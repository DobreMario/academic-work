// SPDX-License-Identifier: BSD-3-Clause

#include <pthread.h>
#include <fcntl.h>
#include <unistd.h>

#include "consumer.h"
#include "ring_buffer.h"
#include "packet.h"
#include "utils.h"

typedef struct pkt_info_t {
	unsigned long timestamp;
	unsigned long hash;
	unsigned long id;
	so_action_t action;
} pkt_info_t;

void *consumer_thread(void *arg)
{
	/* TODO: implement consumer thread */
	so_consumer_ctx_t *ctx = (so_consumer_ctx_t *) arg;
	char buffer[PKT_SZ];
	ssize_t len;

	struct so_packet_t *pkt;
	pkt_info_t p_info;
	unsigned long id;

	while (true) {
		len = ring_buffer_dequeue(ctx->producer_rb, buffer, PKT_SZ, &id);

		if (len <= 0)
			break;

		pkt = (struct so_packet_t *)buffer;
		p_info.action = process_packet(pkt);
		p_info.hash = packet_hash(pkt);
		p_info.timestamp = pkt->hdr.timestamp;
		p_info.id = id;

		pthread_mutex_lock(&ctx->write_mutex);

		while (ctx->expected_id != p_info.id)
			pthread_cond_wait(&ctx->condition, &ctx->write_mutex);

		fprintf(ctx->out_file, "%s %016lx %lu\n", ((p_info.action == PASS) ? "PASS" : "DROP"), p_info.hash, p_info.timestamp);
		fflush(ctx->out_file);
		ctx->expected_id++;
		pthread_cond_broadcast(&ctx->condition);
		pthread_mutex_unlock(&ctx->write_mutex);
	}

	return NULL;
}

int create_consumers(pthread_t *tids,
					 int num_consumers,
					 struct so_ring_buffer_t *rb,
					 const char *out_filename)
{
	so_consumer_ctx_t *ctxs = malloc(sizeof(so_consumer_ctx_t));

	if (!ctxs)
		return -1;

	ctxs->producer_rb = rb;
	ctxs->out_file = fopen(out_filename, "w");
	if (!ctxs->out_file) {
		free(ctxs);
		return -1;
	}

	ctxs->expected_id = 0;
	pthread_mutex_init(&ctxs->write_mutex, NULL);
	pthread_cond_init(&ctxs->condition, NULL);

	for (int i = 0; i < num_consumers; i++) {
		/*
		 * TODO: Launch consumer threads
		 **/
		int rc = pthread_create(&tids[i], NULL, consumer_thread, ctxs);

		if (rc != 0) {
			fclose(ctxs->out_file);
			free(ctxs);
			return -1;
		}
	}

	return num_consumers;
}
