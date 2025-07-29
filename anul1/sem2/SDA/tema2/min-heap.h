/* DOBRE Mario-Sebastian - 314CA */
#ifndef MIN_HEAP_H
#define MIN_HEAP_H

typedef struct {
	void *arr; // heap array
	int size; // heap size
	int capacity; // heap capacity
	size_t data_size; // size of each element
	int (*cmp)(const void *key1, const void *key2); // comparison function
} heap_t;

heap_t *heap_create(int (*cmp_f)(const void *, const void *), size_t data_size);

void heap_insert(heap_t *heap, void *data);

void *heap_top(heap_t *heap);

void heap_pop(heap_t *heap);

int heap_empty(heap_t *heap);

void heap_free(heap_t *heap);

// ---------- HOMEWORK FUNCTIONS ----------

void satellite_heap_free(heap_t * heap);


#endif // MIN_HEAP_H