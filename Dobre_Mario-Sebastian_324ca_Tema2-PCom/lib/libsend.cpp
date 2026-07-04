#include <pthread.h>
#include <cstdlib>
#include <map>
#include <cstdint>
#include "lib.h"
#include "utils.h"
#include "protocol.h"
#include <cassert>
#include <poll.h>
#include <sys/timerfd.h>

#include <cstring>
#include <unistd.h>

using namespace std;

long long global_window_size = 0;

std::map<int, struct connection *> cons;

struct pollfd data_fds[MAX_CONNECTIONS];
/* Used for timers per connection */
struct pollfd timer_fds[MAX_CONNECTIONS];
int fdmax = 0;
int next_conn_id = 1;

int send_data(int conn_id, char *buffer, int len)
{
	int size = 0;

	pthread_mutex_lock(&cons[conn_id]->con_lock);

	while (size < len) {

		while ((uint16_t)(cons[conn_id]->sender.next_seq - cons[conn_id]->sender.base_seq) >=
				std::min((long long)cons[conn_id]->sender.peer_window, global_window_size)) {
			pthread_cond_wait(&cons[conn_id]->sender.window_cond, &cons[conn_id]->con_lock);
		}

		int chunk_size = std::min(len - size, MAX_DATA_SIZE);

		char segment[MAX_SEGMENT_SIZE];
		memset(segment, 0, sizeof(segment));
		poli_tcp_data_hdr *hdr = (poli_tcp_data_hdr *)segment;
		hdr->protocol_id = POLI_PROTOCOL_ID;
		hdr->conn_id = conn_id;
		hdr->type = FLAG_DATA;
		hdr->seq_num = htons(cons[conn_id]->sender.next_seq);
		hdr->len = htons(chunk_size);

		char *payload = segment + sizeof(poli_tcp_data_hdr);
		std::memcpy(payload, buffer + size, chunk_size);

		cons[conn_id]->sender.unacked_segments[cons[conn_id]->sender.next_seq] = std::string(segment, sizeof(poli_tcp_data_hdr) + chunk_size);

		sendto(cons[conn_id]->sockfd, segment, sizeof(poli_tcp_data_hdr) + chunk_size, 0,
			   (struct sockaddr*)&cons[conn_id]->servaddr, sizeof(cons[conn_id]->servaddr));

		cons[conn_id]->sender.next_seq++;
		size += chunk_size;
	}

	pthread_mutex_unlock(&cons[conn_id]->con_lock);
	return size;
}

void *sender_handler(void *arg)
{
	int res = 0;
	char buf[MAX_SEGMENT_SIZE];

	while (1) {
		int conn_id = -1;
		do {
			res = recv_message_or_timeout(buf, MAX_SEGMENT_SIZE, &conn_id);
		} while(res == -14);

		if (conn_id < 0 || cons.find(conn_id) == cons.end() || cons[conn_id] == nullptr) {
			continue;
		}

		pthread_mutex_lock(&cons[conn_id]->con_lock);

		if (res > 0) {
			poli_tcp_ctrl_hdr *hdr = (poli_tcp_ctrl_hdr *)buf;
			if (hdr->protocol_id == POLI_PROTOCOL_ID) {
				if (hdr->type == FLAG_ACK) {
					uint16_t ack_num = ntohs(hdr->ack_num);
					uint16_t recv_window = ntohs(hdr->recv_window);
					cons[conn_id]->sender.peer_window = recv_window;

					if ((int16_t)(ack_num - cons[conn_id]->sender.base_seq) > 0) {
						while (cons[conn_id]->sender.base_seq != ack_num) {
							cons[conn_id]->sender.unacked_segments.erase(cons[conn_id]->sender.base_seq);
							cons[conn_id]->sender.base_seq++;
						}
						cons[conn_id]->sender.dup_ack_count = 0;
						pthread_cond_signal(&cons[conn_id]->sender.window_cond);
					}
					else if (recv_window > 0) {
						pthread_cond_signal(&cons[conn_id]->sender.window_cond);
					}

					if (ack_num == cons[conn_id]->sender.base_seq) {
						cons[conn_id]->sender.dup_ack_count++;
						if (cons[conn_id]->sender.dup_ack_count == 3) {
							uint16_t curr_seq = cons[conn_id]->sender.base_seq;

							if (cons[conn_id]->sender.unacked_segments.count(curr_seq)) {
								const std::string &seg = cons[conn_id]->sender.unacked_segments[curr_seq];
								sendto(cons[conn_id]->sockfd, seg.c_str(), seg.length(), 0,
									   (struct sockaddr*)&cons[conn_id]->servaddr, sizeof(cons[conn_id]->servaddr));
							}
						}
					}
				} else if (hdr->type == FLAG_SYN_ACK) {
					poli_tcp_ctrl_hdr ack_hdr;
					memset(&ack_hdr, 0, sizeof(ack_hdr));
					ack_hdr.protocol_id = POLI_PROTOCOL_ID;
					ack_hdr.conn_id = conn_id;
					ack_hdr.type = FLAG_ACK;
					sendto(cons[conn_id]->sockfd, &ack_hdr, sizeof(ack_hdr), 0, (struct sockaddr*)&cons[conn_id]->servaddr, sizeof(cons[conn_id]->servaddr));
				}
			}
		} else if (res == -1) {
			for (uint16_t seq = cons[conn_id]->sender.base_seq; seq != cons[conn_id]->sender.next_seq; seq++) {
				if (cons[conn_id]->sender.unacked_segments.count(seq)) {
					const std::string &seg = cons[conn_id]->sender.unacked_segments[seq];
					sendto(cons[conn_id]->sockfd, seg.c_str(), seg.length(), 0,
						   (struct sockaddr*)&cons[conn_id]->servaddr, sizeof(cons[conn_id]->servaddr));
				}
			}
		}

		pthread_mutex_unlock(&cons[conn_id]->con_lock);
	}
	return NULL;
}

int setup_connection(uint32_t ip, uint16_t port)
{
	int conn_id = next_conn_id++;
	struct connection *con = cons[conn_id];
	con->sockfd = socket(AF_INET, SOCK_DGRAM, 0);
	con->conn_id = conn_id;

	memset(&con->servaddr, 0, sizeof(con->servaddr));
	con->servaddr.sin_family = AF_INET;
	con->servaddr.sin_addr.s_addr = ip;
	con->servaddr.sin_port = port;

	poli_tcp_ctrl_hdr syn_hdr;
	memset(&syn_hdr, 0, sizeof(syn_hdr));
	syn_hdr.protocol_id = POLI_PROTOCOL_ID;
	syn_hdr.conn_id = conn_id;
	syn_hdr.type = FLAG_SYN;

	struct timeval tv;
	tv.tv_sec = 0; tv.tv_usec = 500000;
	setsockopt(con->sockfd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));

	while (1) {
		sendto(con->sockfd, &syn_hdr, sizeof(syn_hdr), 0,
			   (struct sockaddr*)&con->servaddr, sizeof(con->servaddr));

		char buf[MAX_SEGMENT_SIZE];
		struct sockaddr_in reply_addr;
		socklen_t reply_len = sizeof(reply_addr);

		int n = recvfrom(con->sockfd, buf, MAX_SEGMENT_SIZE, 0, (struct sockaddr*)&reply_addr, &reply_len);
		if (n >= (int)(sizeof(poli_tcp_ctrl_hdr) + sizeof(uint16_t))) {
			poli_tcp_ctrl_hdr *synack = (poli_tcp_ctrl_hdr *)buf;

			if (synack->protocol_id == POLI_PROTOCOL_ID && synack->type == FLAG_SYN_ACK) {
				uint16_t received_port;
				memcpy(&received_port, buf + sizeof(poli_tcp_ctrl_hdr), sizeof(uint16_t));

				con->servaddr.sin_port = received_port;
				break;
			}
		}
	}

	tv.tv_sec = 0; tv.tv_usec = 0;
	setsockopt(con->sockfd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));

	poli_tcp_ctrl_hdr ack_hdr;
	memset(&ack_hdr, 0, sizeof(ack_hdr));
	ack_hdr.protocol_id = POLI_PROTOCOL_ID;
	ack_hdr.conn_id = conn_id;
	ack_hdr.type = FLAG_ACK;
	sendto(con->sockfd, &ack_hdr, sizeof(ack_hdr), 0, (struct sockaddr*)&con->servaddr, sizeof(con->servaddr));

	data_fds[fdmax].fd = con->sockfd;
	data_fds[fdmax].events = POLLIN;

	timer_fds[fdmax].fd = timerfd_create(CLOCK_REALTIME,  0);
	timer_fds[fdmax].events = POLLIN;
	struct itimerspec spec;
	spec.it_value.tv_sec = 0; spec.it_value.tv_nsec = 50000000;
	spec.it_interval.tv_sec = 0; spec.it_interval.tv_nsec = 50000000;
	timerfd_settime(timer_fds[fdmax].fd, 0, &spec, NULL);

	fdmax++;
	return conn_id;
}

void init_sender(int speed, int delay)
{
	long long bandwidth_bps = (long long)speed * 1000000;
	long long bandwidth_Bps = bandwidth_bps / 8;
	double rtt_seconds = (double)delay / 1000.0;
	long long bdp_bytes = (long long)(bandwidth_Bps * rtt_seconds);
	global_window_size = (bdp_bytes / MAX_DATA_SIZE) + 2;

	if (global_window_size < 2) {
		global_window_size = 2;
	} else if (global_window_size > 64) {
		global_window_size = 64;
	}

	for(int i = 0; i < MAX_CONNECTIONS; i++) {
		struct connection* c = new connection();
		c->sockfd = -1;
		pthread_mutex_init(&c->con_lock, NULL);
		cons[i] = c;
	}

	pthread_t thread1;
	int ret = pthread_create(&thread1, NULL, sender_handler, NULL);
	assert(ret == 0);
}

// Cleanup function to free resources when the protocol is terminated
void cleanup_connections() {
	for (auto const& [id, c] : cons) {
		if (c != nullptr) {
			pthread_mutex_destroy(&c->con_lock);
			if (c->sockfd != -1) {
				close(c->sockfd);
			}
			delete c;
		}
	}
	cons.clear();
}

struct ProtocolCleaner {
	~ProtocolCleaner() {
		cleanup_connections();
	}
} global_cleaner;
