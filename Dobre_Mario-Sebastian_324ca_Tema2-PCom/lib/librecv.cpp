#include <pthread.h>
#include <cstdlib>
#include <map>
#include <cstdint>
#include "lib.h"
#include "utils.h"
#include "protocol.h"
#include <poll.h>
#include <cassert>
#include <sys/timerfd.h>

#include <cstring>
#include <unistd.h>

using namespace std;

int listen_fd = -1;
int next_conn_id = 1;
int max_recv_buffer;

std::map<int, struct connection *> cons;

struct pollfd data_fds[MAX_CONNECTIONS];
/* Used for timers per connection */
struct pollfd timer_fds[MAX_CONNECTIONS];
int fdmax = 0;

int recv_data(int conn_id, char *buffer, int len)
{
	int size = 0;

	pthread_mutex_lock(&cons[conn_id]->con_lock);

	while (cons[conn_id]->recv.buffer.empty()) {
		pthread_cond_wait(&cons[conn_id]->recv.data_cond, &cons[conn_id]->con_lock);
	}

	size = std::min((int)cons[conn_id]->recv.buffer.size(), len);

	std::copy(cons[conn_id]->recv.buffer.begin(),
			  cons[conn_id]->recv.buffer.begin() + size,
			  buffer);

	cons[conn_id]->recv.buffer.erase(cons[conn_id]->recv.buffer.begin(),
									  cons[conn_id]->recv.buffer.begin() + size);

	poli_tcp_ctrl_hdr ack_hdr;
	ack_hdr.protocol_id = POLI_PROTOCOL_ID;
	ack_hdr.conn_id = conn_id;
	ack_hdr.type = FLAG_ACK;
	ack_hdr.ack_num = htons(cons[conn_id]->recv.expected_seq);

	int used_space = cons[conn_id]->recv.buffer.size();
	int free_space = max_recv_buffer - used_space;
	ack_hdr.recv_window = htons(free_space / MAX_DATA_SIZE);

	sendto(cons[conn_id]->sockfd, &ack_hdr, sizeof(ack_hdr), 0,
		   (struct sockaddr*)&cons[conn_id]->servaddr, sizeof(cons[conn_id]->servaddr));

	pthread_mutex_unlock(&cons[conn_id]->con_lock);

	return size;
}

void *receiver_handler(void *arg)
{
	char segment[MAX_SEGMENT_SIZE];
	int res;

	while (1) {
		int conn_id = -1;
		do {
			res = recv_message_or_timeout(segment, MAX_SEGMENT_SIZE, &conn_id);
		} while(res == -14);

		if (res > 0 && conn_id >= 0 && cons.find(conn_id) != cons.end() && cons[conn_id] != nullptr) {
			poli_tcp_data_hdr *hdr = (poli_tcp_data_hdr *)segment;

			if (hdr->protocol_id != POLI_PROTOCOL_ID || hdr->type != FLAG_DATA) {
				continue;
			}

			pthread_mutex_lock(&cons[conn_id]->con_lock);

			uint16_t seq_num_host = ntohs(hdr->seq_num);
			uint16_t len_host = ntohs(hdr->len);

			if (seq_num_host == cons[conn_id]->recv.expected_seq) {
				char *payload = segment + sizeof(poli_tcp_data_hdr);
				cons[conn_id]->recv.buffer.insert(cons[conn_id]->recv.buffer.end(), payload, payload + len_host);
				cons[conn_id]->recv.expected_seq++;

				while (cons[conn_id]->recv.out_of_order.count(cons[conn_id]->recv.expected_seq)) {
					const std::string& next_payload = cons[conn_id]->recv.out_of_order[cons[conn_id]->recv.expected_seq];
					cons[conn_id]->recv.buffer.insert(cons[conn_id]->recv.buffer.end(), next_payload.begin(), next_payload.end());
					cons[conn_id]->recv.out_of_order.erase(cons[conn_id]->recv.expected_seq);
					cons[conn_id]->recv.expected_seq++;
				}

				pthread_cond_signal(&cons[conn_id]->recv.data_cond);
			}
			else if ((int16_t)(seq_num_host - cons[conn_id]->recv.expected_seq) > 0) {
				char *payload = segment + sizeof(poli_tcp_data_hdr);
				cons[conn_id]->recv.out_of_order[seq_num_host] = std::string(payload, len_host);
			}

			poli_tcp_ctrl_hdr ack_hdr;
			ack_hdr.protocol_id = POLI_PROTOCOL_ID;
			ack_hdr.conn_id = conn_id;
			ack_hdr.type = FLAG_ACK;
			ack_hdr.ack_num = htons(cons[conn_id]->recv.expected_seq);
			int used_space = cons[conn_id]->recv.buffer.size();
			int free_space = max_recv_buffer - used_space;
			ack_hdr.recv_window = htons(free_space / MAX_DATA_SIZE);

			sendto(cons[conn_id]->sockfd, &ack_hdr, sizeof(ack_hdr), 0,
				   (struct sockaddr*)&cons[conn_id]->servaddr, sizeof(cons[conn_id]->servaddr));

			pthread_mutex_unlock(&cons[conn_id]->con_lock);
		}
	}
	return NULL;
}

int wait4connect(uint32_t ip, uint16_t port)
{
	char buf[MAX_SEGMENT_SIZE];
	struct sockaddr_in cliaddr;
	socklen_t clilen = sizeof(cliaddr);

	while (1) {
		int n = recvfrom(listen_fd, buf, MAX_SEGMENT_SIZE, 0, (struct sockaddr *)&cliaddr, &clilen);
		if (n > 0) {
			poli_tcp_ctrl_hdr *hdr = (poli_tcp_ctrl_hdr *)buf;
			if (hdr->protocol_id == POLI_PROTOCOL_ID && hdr->type == FLAG_SYN) {
				break;
			}
		}
	}

	int conn_id = next_conn_id++;

	struct connection *con = cons[conn_id];
	con->sockfd = socket(AF_INET, SOCK_DGRAM, 0);
	con->conn_id = conn_id;
	con->servaddr = cliaddr;

	struct sockaddr_in dynamic_addr;
	memset(&dynamic_addr, 0, sizeof(dynamic_addr));
	dynamic_addr.sin_family = AF_INET;
	dynamic_addr.sin_addr.s_addr = INADDR_ANY;
	dynamic_addr.sin_port = 0;

	bind(con->sockfd, (struct sockaddr *)&dynamic_addr, sizeof(dynamic_addr));
	socklen_t len = sizeof(dynamic_addr);
	getsockname(con->sockfd, (struct sockaddr *)&dynamic_addr, &len);

	struct timeval tv;
	tv.tv_sec = 0; tv.tv_usec = 100000;
	setsockopt(con->sockfd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));

	char synack_buf[MAX_SEGMENT_SIZE];
	memset(synack_buf, 0, sizeof(synack_buf));
	poli_tcp_ctrl_hdr *synack_hdr = (poli_tcp_ctrl_hdr *)synack_buf;
	synack_hdr->protocol_id = POLI_PROTOCOL_ID;
	synack_hdr->conn_id = conn_id;
	synack_hdr->type = FLAG_SYN_ACK;
	synack_hdr->ack_num = 0;

	uint16_t port_payload = dynamic_addr.sin_port;
	char *payload_ptr = synack_buf + sizeof(poli_tcp_ctrl_hdr);
	memcpy(payload_ptr, &port_payload, sizeof(uint16_t));

	int synack_size = sizeof(poli_tcp_ctrl_hdr) + sizeof(uint16_t);

	while (1) {
		sendto(con->sockfd, synack_buf, synack_size, 0, (struct sockaddr *)&cliaddr, clilen);

		char ack_buf[MAX_SEGMENT_SIZE];
		int n = recvfrom(con->sockfd, ack_buf, MAX_SEGMENT_SIZE, MSG_PEEK, NULL, NULL);

		if (n > 0) {
			poli_tcp_ctrl_hdr *ack_hdr = (poli_tcp_ctrl_hdr *)ack_buf;
			if (ack_hdr->type == FLAG_ACK) {
				recvfrom(con->sockfd, ack_buf, MAX_SEGMENT_SIZE, 0, NULL, NULL);
				break;
			} else if (ack_hdr->type == FLAG_DATA) {
				break;
			} else {
				recvfrom(con->sockfd, ack_buf, MAX_SEGMENT_SIZE, 0, NULL, NULL);
			}
		}
	}

	tv.tv_sec = 0; tv.tv_usec = 0;
	setsockopt(con->sockfd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));

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

void init_receiver(int recv_buffer_bytes)
{
	max_recv_buffer = recv_buffer_bytes;
	for(int i = 0; i < MAX_CONNECTIONS; i++) {
		struct connection* c = new connection();
		c->sockfd = -1;
		pthread_mutex_init(&c->con_lock, NULL);
		cons[i] = c;
	}

	listen_fd = socket(AF_INET, SOCK_DGRAM, 0);
	struct sockaddr_in servaddr;
	memset(&servaddr, 0, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = INADDR_ANY;
	servaddr.sin_port = htons(8032);

	bind(listen_fd, (struct sockaddr *)&servaddr, sizeof(servaddr));

	pthread_t thread1;
	int ret = pthread_create(&thread1, NULL, receiver_handler, NULL);
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
