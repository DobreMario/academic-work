#include <iostream>
#include <vector>
#include <algorithm>
#include <cstring>
#include <arpa/inet.h>
#include <stdint.h>
#include <stdlib.h>
#include <stdio.h>
#include <unordered_map>
#include <queue>
#include "trie.h"

extern "C"
{
#include "lib.h"
#include "protocols.h"
}

using namespace std;

struct packet_info {
	char packet[MAX_PACKET_LEN];
	size_t len;
	size_t interface;
};

struct mac_info {
	uint8_t mac[6];
};

vector<route_table_entry> rtable;
queue<packet_info *> packets_queue;
unordered_map<uint32_t, mac_info> mac_table;

void process_ipv4(
	TrieNode *trie_root,
	char *packet,
	size_t len,
	size_t interface,
	struct ether_hdr *eth_hdr);

void process_arp(
	TrieNode *trie_root,
	char *packet,
	size_t len,
	size_t interface);

void send_icmp_echo_reply(
	char *packet,
	size_t len,
	size_t interface,
	struct ether_hdr *eth_hdr,
	struct ip_hdr *ip_hdr,
	struct icmp_hdr *icmp_hdr);

void send_icmp_error(
	char *packet,
	size_t len,
	size_t interface,
	struct ether_hdr *eth_hdr,
	struct ip_hdr *ip_hdr,
	uint8_t type,
	uint8_t code);

void process_arp_request(
	size_t interface,
	struct ether_hdr *eth_hdr,
	struct arp_hdr *arp_hdr);

void process_arp_reply(
	TrieNode *trie_root,
	struct arp_hdr *arp_hdr);

void send_arp_request(
	size_t interface,
	uint32_t target_ip);

void enqueue_packet(char *packet, size_t len, size_t interface);
uint8_t *lookup_mac_entry(uint32_t target_ip);
bool compare_routes(const route_table_entry &a, const route_table_entry &b);


int main(int argc, char *argv[])
{
	char packet[MAX_PACKET_LEN];

	init(argv + 2, argc - 2);

	rtable.resize(100000);
	int actual_rtable_len = read_rtable(argv[1], rtable.data());
	rtable.resize(actual_rtable_len);

	TrieNode *trie_root = new TrieNode();
	for (auto &route : rtable) {
		insert_route(trie_root, &route);
	}

	mac_table.clear();

	while (1) {
		size_t interface;
		size_t len;

		interface = recv_from_any_link(packet, &len);
		DIE(interface < 0, "recv_from_any_link");

		struct ether_hdr *eth_hdr = (struct ether_hdr *)packet;

		uint8_t my_mac[6];
		uint8_t broadcast_mac[6] = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};
		get_interface_mac(interface, my_mac);

		bool is_for_me = (memcmp(eth_hdr->ethr_dhost, my_mac, 6) == 0);
		bool is_broadcast = (memcmp(eth_hdr->ethr_dhost, broadcast_mac, 6) == 0);

		if (!is_for_me && !is_broadcast) {
			cout << "[L2] >>> Ignored" << endl;
			continue;
		}

		uint16_t eth_type = ntohs(eth_hdr->ethr_type);

		if (eth_type == 0x0800) {
			process_ipv4(trie_root, packet, len, interface, eth_hdr);
		} else if (eth_type == 0x0806) {
			process_arp(trie_root, packet, len, interface);
		}
	}

	return 0;
}

void process_ipv4(
	TrieNode *trie_root,
	char *packet,
	size_t len,
	size_t interface,
	struct ether_hdr *eth_hdr) {

	struct ip_hdr *ip_hdr = (struct ip_hdr *)(packet + sizeof(struct ether_hdr));
	uint16_t old_sum = ip_hdr->checksum;

	ip_hdr->checksum = 0;
	if (old_sum != htons(checksum((uint16_t *)ip_hdr, sizeof(struct ip_hdr)))) {
		cout << "[IPv4] >>> Wrong checksum" << endl;
		return;
	}

	ip_hdr->checksum = old_sum;

	struct icmp_hdr *icmp_hdr = (struct icmp_hdr *)(packet +
								sizeof(struct ether_hdr) +
								sizeof(struct ip_hdr));

	if (ip_hdr->proto == 1 && icmp_hdr->mtype == 8 &&
		ip_hdr->dest_addr == inet_addr(get_interface_ip(interface))) {
		send_icmp_echo_reply(packet, len, interface, eth_hdr, ip_hdr, icmp_hdr);
		return;
	}

	if (ip_hdr->ttl <= 1) {
		send_icmp_error(packet, len, interface, eth_hdr, ip_hdr, 11, 0);
		cout << "[IPv4] >>> TTL expired" << endl;
		return;
	}

	struct route_table_entry *best_route = get_best_route_trie(trie_root,
															   ip_hdr->dest_addr);
	if (!best_route) {
		send_icmp_error(packet, len, interface, eth_hdr, ip_hdr, 3, 0);
		cout << "[IPv4] >>> Route not found" << endl;
		return;
	}

	uint16_t old_ttl = ip_hdr->ttl;
	ip_hdr->ttl -= 1;
	ip_hdr->checksum = ~(~ip_hdr->checksum + ~((uint16_t)old_ttl) +
					   (uint16_t)ip_hdr->ttl) - 1;

	uint8_t *dest_mac = lookup_mac_entry(best_route->next_hop);
	if (!dest_mac) {
		cout << "[ARP] >>> MAC not found" << endl;
		enqueue_packet(packet, len, best_route->interface);
		send_arp_request(best_route->interface, best_route->next_hop);
		return;
	}

	memcpy(eth_hdr->ethr_dhost, dest_mac, 6);
	get_interface_mac(best_route->interface, eth_hdr->ethr_shost);

	send_to_link(len, packet, best_route->interface);

	cout << "[IPv4] >>> Forward successful from IP: "
		 << inet_ntoa(*(struct in_addr *)&ip_hdr->source_addr)
		 << " to IP: "
		 << inet_ntoa(*(struct in_addr *)&ip_hdr->dest_addr) << endl;
}

void process_arp(
	TrieNode *trie_root,
	char *packet,
	size_t len,
	size_t interface) {

	struct ether_hdr *eth_hdr = (struct ether_hdr *)packet;
	struct arp_hdr *arp_hdr = (struct arp_hdr *)(packet + sizeof(struct ether_hdr));

	uint16_t op = ntohs(arp_hdr->opcode);

	if (op == 1) {
		process_arp_request(interface, eth_hdr, arp_hdr);
	} else if (op == 2) {
		process_arp_reply(trie_root, arp_hdr);
	}
}

void send_icmp_echo_reply(
	char *packet,
	size_t len,
	size_t interface,
	struct ether_hdr *eth_hdr,
	struct ip_hdr *ip_hdr,
	struct icmp_hdr *icmp_hdr) {

	icmp_hdr->mtype = 0;
	icmp_hdr->mcode = 0;
	icmp_hdr->check = 0;
	icmp_hdr->check = htons(checksum((uint16_t *)icmp_hdr,
							ntohs(ip_hdr->tot_len) - sizeof(struct ip_hdr)));

	uint32_t tmp_source = ip_hdr->source_addr;
	ip_hdr->source_addr = ip_hdr->dest_addr;
	ip_hdr->dest_addr = tmp_source;

	ip_hdr->checksum = 0;
	ip_hdr->checksum = htons(checksum((uint16_t *)ip_hdr, sizeof(struct ip_hdr)));

	uint8_t tmp_mac[6];
	memcpy(tmp_mac, eth_hdr->ethr_shost, 6);
	memcpy(eth_hdr->ethr_shost, eth_hdr->ethr_dhost, 6);
	memcpy(eth_hdr->ethr_dhost, tmp_mac, 6);

	send_to_link(len, packet, interface);
	cout << "[ICMP] >>> Echo reply sent" << endl;
}

void send_icmp_error(
	char *packet,
	size_t len,
	size_t interface,
	struct ether_hdr *eth_hdr,
	struct ip_hdr *ip_hdr,
	uint8_t type,
	uint8_t code) {

	char new_packet[MAX_PACKET_LEN];

	struct ether_hdr *new_eth_hdr = (struct ether_hdr *)new_packet;
	memcpy(new_eth_hdr->ethr_dhost, eth_hdr->ethr_shost, 6);
	get_interface_mac(interface, new_eth_hdr->ethr_shost);
	new_eth_hdr->ethr_type = htons(0x0800);

	struct ip_hdr *new_ip_hdr = (struct ip_hdr *)(new_packet + sizeof(struct ether_hdr));
	new_ip_hdr->ver = 4;
	new_ip_hdr->ihl = 5;
	new_ip_hdr->tos = 0;
	new_ip_hdr->tot_len = htons(sizeof(struct ip_hdr) + sizeof(struct icmp_hdr) +
								sizeof(struct ip_hdr) + 8);
	new_ip_hdr->id = htons(4);
	new_ip_hdr->frag = 0;
	new_ip_hdr->ttl = 64;
	new_ip_hdr->proto = 1;
	new_ip_hdr->source_addr = inet_addr(get_interface_ip(interface));
	new_ip_hdr->dest_addr = ip_hdr->source_addr;
	new_ip_hdr->checksum = 0;
	new_ip_hdr->checksum = htons(checksum((uint16_t *)new_ip_hdr,
										  sizeof(struct ip_hdr)));

	struct icmp_hdr *icmp_hdr = (struct icmp_hdr *)(new_packet +
								sizeof(struct ether_hdr) + sizeof(struct ip_hdr));
	icmp_hdr->mtype = type;
	icmp_hdr->mcode = code;
	icmp_hdr->check = 0;
	icmp_hdr->un_t.echo_t.id = 0;
	icmp_hdr->un_t.echo_t.seq = 0;

	char *payload = (char *)(new_packet + sizeof(struct ether_hdr) +
							 sizeof(struct ip_hdr) + sizeof(struct icmp_hdr));
	memcpy(payload, ip_hdr, sizeof(struct ip_hdr) + 8);

	size_t new_packet_len = sizeof(struct ether_hdr) + sizeof(struct ip_hdr) +
							sizeof(struct icmp_hdr) + sizeof(struct ip_hdr) + 8;

	icmp_hdr->check = htons(checksum((uint16_t *)icmp_hdr,
							sizeof(struct icmp_hdr) + sizeof(struct ip_hdr) + 8));

	send_to_link(new_packet_len, new_packet, interface);
}

void process_arp_request(
	size_t interface,
	struct ether_hdr *eth_hdr,
	struct arp_hdr *arp_hdr) {

	uint32_t target_ip = arp_hdr->tprotoa;
	uint32_t sender_ip = arp_hdr->sprotoa;

	if (target_ip != inet_addr(get_interface_ip(interface))) {
		return;
	}

	char arp_reply[MAX_PACKET_LEN];
	struct ether_hdr *reply_eth_hdr = (struct ether_hdr *)arp_reply;
	struct arp_hdr *reply_arp_hdr = (struct arp_hdr *)(arp_reply +
									sizeof(struct ether_hdr));

	memcpy(reply_eth_hdr->ethr_dhost, eth_hdr->ethr_shost, 6);
	get_interface_mac(interface, reply_eth_hdr->ethr_shost);
	reply_eth_hdr->ethr_type = htons(0x0806);

	reply_arp_hdr->hw_type = htons(1);
	reply_arp_hdr->proto_type = htons(0x0800);
	reply_arp_hdr->hw_len = 6;
	reply_arp_hdr->proto_len = 4;
	reply_arp_hdr->opcode = htons(2);

	get_interface_mac(interface, reply_arp_hdr->shwa);
	reply_arp_hdr->sprotoa = inet_addr(get_interface_ip(interface));
	memcpy(reply_arp_hdr->thwa, arp_hdr->shwa, 6);
	reply_arp_hdr->tprotoa = sender_ip;

	send_to_link(sizeof(struct ether_hdr) + sizeof(struct arp_hdr),
				 arp_reply, interface);

	cout << "[ARP] >>> ARP reply sent to IP: "
		 << inet_ntoa(*(struct in_addr *)&sender_ip) << endl;
}

void process_arp_reply(TrieNode *trie_root, struct arp_hdr *arp_hdr) {
	mac_info new_entry;
	memcpy(new_entry.mac, arp_hdr->shwa, 6);
	mac_table[arp_hdr->sprotoa] = new_entry;

	struct in_addr ip_print;
	ip_print.s_addr = arp_hdr->sprotoa;
	cout << "[ARP] >>> ARP reply processed for IP: "
		 << inet_ntoa(ip_print) << endl;

	queue<packet_info *> temp_queue;

	while (!packets_queue.empty()) {
		struct packet_info *info = packets_queue.front();
		packets_queue.pop();

		struct ether_hdr *eth_hdr = (struct ether_hdr *)info->packet;
		struct ip_hdr *ip_hdr = (struct ip_hdr *)(info->packet +
								sizeof(struct ether_hdr));

		struct route_table_entry *best_route = get_best_route_trie(trie_root,
																   ip_hdr->dest_addr);

		if (best_route) {
			uint8_t *dest_mac = lookup_mac_entry(best_route->next_hop);

			if (dest_mac) {
				memcpy(eth_hdr->ethr_dhost, dest_mac, 6);
				get_interface_mac(best_route->interface, eth_hdr->ethr_shost);

				send_to_link(info->len, info->packet, best_route->interface);

				delete info;
				continue;
			}
		}

		temp_queue.push(info);
	}

	packets_queue = temp_queue;
}

void send_arp_request(size_t interface, uint32_t target_ip) {
	char arp_packet[MAX_PACKET_LEN];
	struct ether_hdr *eth_hdr = (struct ether_hdr *)arp_packet;
	struct arp_hdr *arp_hdr = (struct arp_hdr *)(arp_packet + sizeof(struct ether_hdr));

	uint8_t broadcast_mac[6] = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};
	memcpy(eth_hdr->ethr_dhost, broadcast_mac, 6);
	get_interface_mac(interface, eth_hdr->ethr_shost);
	eth_hdr->ethr_type = htons(0x0806);

	arp_hdr->hw_type = htons(1);
	arp_hdr->proto_type = htons(0x0800);
	arp_hdr->hw_len = 6;
	arp_hdr->proto_len = 4;
	arp_hdr->opcode = htons(1);

	get_interface_mac(interface, arp_hdr->shwa);
	arp_hdr->sprotoa = inet_addr(get_interface_ip(interface));
	memset(arp_hdr->thwa, 0, 6);
	arp_hdr->tprotoa = target_ip;

	send_to_link(sizeof(struct ether_hdr) + sizeof(struct arp_hdr),
				 arp_packet, interface);

	cout << "[ARP] >>> ARP request sent for IP: "
		 << inet_ntoa(*(struct in_addr *)&target_ip) << endl;
}

void enqueue_packet(char *packet, size_t len, size_t interface) {
	struct packet_info *info = new packet_info();
	memcpy(info->packet, packet, len);
	info->len = len;
	info->interface = interface;
	packets_queue.push(info);
}

uint8_t *lookup_mac_entry(uint32_t target_ip) {
	auto it = mac_table.find(target_ip);
	if (it != mac_table.end()) {
		return it->second.mac;
	}
	return NULL;
}

bool compare_routes(const route_table_entry &a, const route_table_entry &b) {
	uint32_t mask_a = ntohl(a.mask);
	uint32_t mask_b = ntohl(b.mask);

	if (mask_a != mask_b) {
		return mask_a > mask_b;
	}
	return ntohl(a.prefix) > ntohl(b.prefix);
}
