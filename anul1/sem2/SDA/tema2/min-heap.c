/* DOBRE Mario-Sebastian - 314CA */
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "satellite_core.h"

#define DIE(assertion, call_description)                                       \
	do {                                                                       \
		if (assertion) {                                                       \
			fprintf(stderr, "(%s, %d): ", __FILE__, __LINE__);                 \
			perror(call_description);                                          \
			exit(errno);                                                       \
		}                                                                      \
	} while (0)

#define GO_UP(x) (((x) - 1) >> 1)

#define GO_LEFT(x) (((x) << 1) + 1)

#define GO_RIGHT(x) (((x) << 1) + 2)

heap_t *heap_create(int (*cmp_f)(const void *, const void *), size_t data_size)
{
	heap_t *heap;

	heap = malloc(sizeof(*heap));
	DIE(heap == NULL, "heap malloc");

	heap_t heap_struct = (heap_t){.arr = malloc(5 * data_size),
								  .capacity = 5,
								  .data_size = data_size,
								  .size = 0,
								  .cmp = cmp_f};
	DIE(heap_struct.arr == NULL, "heap->arr malloc");
	*heap = heap_struct;
	return heap;
}

void heap_swap(void *a, void *b, size_t size) {
	void *tmp = malloc(size);
	if(!tmp) {
		return;
	}
	memcpy(tmp, a, size);

	memcpy(a, b, size);

	memcpy(b, tmp, size);

	free(tmp);
}

static void __heap_insert_fix(heap_t *heap, int pos)
{
	while (pos > 0) {
		int p = GO_UP(pos);
		if (heap->cmp((char*)heap->arr + p * heap->data_size,
					  (char*)heap->arr + pos * heap->data_size) > 0) {
			heap_swap((char*)heap->arr + p * heap->data_size,
					  (char*)heap->arr + pos * heap->data_size,
					  heap->data_size);
			pos = p;
		} else {
			break;
		}
	}
}

void heap_insert(heap_t *heap, void *data)
{
	char *arr = heap->arr;

	memcpy(&arr[heap->size * heap->data_size], data, heap->data_size);
	__heap_insert_fix(heap, heap->size);

	heap->size++;
	if (heap->size == heap->capacity) {
		heap->capacity *= 2;

		heap->arr = realloc(heap->arr, heap->capacity * heap->data_size);
		DIE(heap->arr == NULL, "heap->arr realloc");
	}
}

void *heap_top(heap_t *heap)
{
	if (heap->size == 0)
		return NULL;
	return heap->arr;
}

static void __heap_pop_fix(heap_t *heap, int pos)
{
	int left = GO_LEFT(pos);
	int right = GO_RIGHT(pos);
	int smallest = pos;

	if (left < heap->size &&
		heap->cmp((char*)heap->arr + left * heap->data_size,
				  (char*)heap->arr + smallest * heap->data_size) < 0) {
		smallest = left;
	}

	if (right < heap->size &&
		heap->cmp((char*)heap->arr + right * heap->data_size,
				  (char*)heap->arr + smallest * heap->data_size) < 0) {
		smallest = right;
	}

	if (smallest != pos) {
		heap_swap((char*)heap->arr + pos * heap->data_size,
				  (char*)heap->arr + smallest * heap->data_size,
				  heap->data_size);

		__heap_pop_fix(heap, smallest);
	}
}

void heap_pop(heap_t *heap)
{
	char *arr = heap->arr;
	memcpy(&arr[0], &arr[(heap->size - 1) * heap->data_size], heap->data_size);
	heap->size--;

	__heap_pop_fix(heap, 0);
}

int heap_empty(heap_t *heap) 
{ 
	return heap->size <= 0;
}

void heap_free(heap_t *heap)
{
	if (!heap) {
		return;
	}

	free(heap->arr);
	free(heap);
}

static void __satelit_heap_free(b_node_t *b_node, void (*free_data)(satellite_t *))
{
	if (!b_node)
		return;

	__satelit_heap_free(b_node->left, free_data);
	__satelit_heap_free(b_node->right, free_data);
	free_data(b_node->data);
	free(b_node);
}

void satellite_heap_free(heap_t *heap)
{
	if (!heap) {
		return;
	}

	for (int i = 0; i < heap->size; i++) {
		b_node_t *tmp = *(b_node_t **)((char *)heap->arr + i * heap->data_size);
		__satelit_heap_free(tmp, satelit_free);
	}

	free(heap->arr);
	free(heap);
}