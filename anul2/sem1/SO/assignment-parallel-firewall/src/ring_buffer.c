// SPDX-License-Identifier: BSD-3-Clause

#include "ring_buffer.h"

int ring_buffer_init(so_ring_buffer_t *ring, size_t cap)
{
	/* TODO: implement ring_buffer_init */
	if (!ring || cap == 0) {
		errno = EINVAL;
		return -1;
	}

	unsigned int slot_count = cap / PKT_SZ;

	ring->data = malloc((slot_count) * sizeof(data_buffer_t));
	if (!ring->data)
		return -1;

	ring->read_pos = 0;
	ring->write_pos = 0;
	ring->len = 0;
	ring->cap = slot_count;

	pthread_mutex_init(&ring->mutex, NULL);
	sem_init(&ring->sem_empty, 0, slot_count);
	sem_init(&ring->sem_full, 0, 0);
	ring->stop = 0;
	ring->producer_counter = 0;
	return 0;
}

ssize_t ring_buffer_enqueue(so_ring_buffer_t *ring, void *data, size_t size)
{
	/* TODO: implement ring_buffer_enqueue */
	if (!ring || !data || size == 0) {
		errno = EINVAL;
		return -1;
	}

	sem_wait(&ring->sem_empty);
	pthread_mutex_lock(&ring->mutex);

	if (ring->stop) {
		pthread_mutex_unlock(&ring->mutex);
		sem_post(&ring->sem_empty);
		return -1;
	}

	memcpy(ring->data[ring->write_pos].content, data, size);
	ring->data[ring->write_pos].entry_id = ring->producer_counter++;
	ring->write_pos = (ring->write_pos + 1) % ring->cap;
	ring->len++;

	pthread_mutex_unlock(&ring->mutex);
	sem_post(&ring->sem_full);

	return size;
}

ssize_t ring_buffer_dequeue(so_ring_buffer_t *ring, void *data, size_t size, unsigned long *out_id)
{
	/* TODO: Implement ring_buffer_dequeue */
	if (!ring || !data || size == 0) {
		errno = EINVAL;
		return -1;
	}

	sem_wait(&ring->sem_full);
	pthread_mutex_lock(&ring->mutex);

	if (ring->stop && ring->len == 0) {
		pthread_mutex_unlock(&ring->mutex);
		sem_post(&ring->sem_full);
		return 0;
	}

	if (out_id)
		*out_id = ring->data[ring->read_pos].entry_id;

	memcpy(data, ring->data[ring->read_pos].content, size);

	ring->read_pos = (ring->read_pos + 1) % ring->cap;
	ring->len--;

	pthread_mutex_unlock(&ring->mutex);
	sem_post(&ring->sem_empty);

	return size;
}

void ring_buffer_destroy(so_ring_buffer_t *ring)
{
	/* TODO: Implement ring_buffer_destroy */
	if (!ring)
		return;

	pthread_mutex_destroy(&ring->mutex);
	sem_destroy(&ring->sem_empty);
	sem_destroy(&ring->sem_full);
	free(ring->data);
	ring->data = NULL;
}

void ring_buffer_stop(so_ring_buffer_t *ring)
{
	/* TODO: Implement ring_buffer_stop */
	if (!ring)
		return;
	pthread_mutex_lock(&ring->mutex);
	ring->stop = 1;
	pthread_mutex_unlock(&ring->mutex);

	sem_post(&ring->sem_full);
}
