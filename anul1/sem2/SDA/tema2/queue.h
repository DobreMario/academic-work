/* DOBRE Mario-Sebastian - 314CA */
#ifndef QUEUE_H
#define QUEUE_H

typedef struct queue_t queue_t;
struct queue_t
{
    unsigned int max_size;
    unsigned int size;
    unsigned int data_size;
    unsigned int read_idx;
    unsigned int write_idx;
    void **buff;
};

queue_t *q_create(unsigned int data_size, unsigned int max_size);

unsigned int q_get_size(queue_t *q);

unsigned int q_is_empty(queue_t *q);

void *q_front(queue_t *q);

int q_dequeue(queue_t *q);

int q_enqueue(queue_t *q, void *new_data);

void q_clear(queue_t *q);

void q_free(queue_t *q);


#endif // QUEUE_H