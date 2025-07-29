/* DOBRE Mario-Sebastian - 314CA */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "satellite.h"
#include "min-heap.h"

satellite_t *satelit_init(char *name, int frequency)
{
	satellite_t *sat = malloc(sizeof(satellite_t));
	if (!sat) {
		return NULL;
	}
	sat->name = malloc(strlen(name) + 1);
	if (!sat->name) {
		free(sat);
		return NULL;
	}
	strcpy(sat->name, name);
	sat->frequency = frequency;
	return sat;
}

static heap_t *__get_file_data(FILE *input_file)
{
	heap_t *heap = heap_create(cmp_satelit_node, sizeof(b_node_t));

	int n, frequency;
	char name[16];

	fscanf(input_file, "%d", &n);

	for (int i = 0; i < n; i++) {
		fscanf(input_file, "%d %s", &frequency, name);
		satellite_t *tmp_sat = satelit_init(name, frequency);
		b_node_t *tmp_node = b_node_create(tmp_sat, sizeof(satellite_t));
		heap_insert(heap, tmp_node);
		free(tmp_sat);
		free(tmp_node);
	}

	return heap;
}

static b_node_t * __get_node(void *node)
{
	b_node_t *child = malloc(sizeof(b_node_t));
	if (!child) {
		return NULL;
	}
	memcpy(child, node, sizeof(b_node_t));

	return child;
}

b_tree_t *satelit_create_tree(FILE *input_file)
{
	b_tree_t *b_tree = b_tree_create(sizeof(satellite_t));
	if (!b_tree) {
		return NULL;
	}

	heap_t *tmp_heap = __get_file_data(input_file);

	if (tmp_heap->size == 0) {
		free(b_tree);
		heap_free(tmp_heap);
		return NULL;
	}

	while(tmp_heap->size != 1) {

		b_node_t *left_child = __get_node(heap_top(tmp_heap));
		if (!left_child) {
			free(b_tree);
			satellite_heap_free(tmp_heap);
			return NULL;
		}
		satellite_t *left = left_child->data;
		heap_pop(tmp_heap);

		b_node_t *right_child = __get_node(heap_top(tmp_heap));
		if (!right_child) {
			free(left_child);
			free(b_tree);
			satellite_heap_free(tmp_heap);
			return NULL;
		}
		satellite_t *right = right_child->data;
		heap_pop(tmp_heap);

		int size = strlen(left->name) + strlen(right->name) + 1;
		int sum = left->frequency + right->frequency;
		char *tmp_name = malloc(size);
		if (!tmp_name) {
			return NULL;
		}
		strcpy(tmp_name, left->name);
		strcat(tmp_name, right->name);

		satellite_t *root_sat = satelit_init(tmp_name, sum);
		b_node_t *root = b_node_create(root_sat, sizeof(satellite_t));

		root->left = left_child;
		root->right = right_child;

		heap_insert(tmp_heap, root);

		free(root);
		free(root_sat);
		free(tmp_name);
	}

	b_tree->root = __get_node(heap_top(tmp_heap));
	if (!b_tree->root) {
		free(b_tree);
		heap_free(tmp_heap);
		return NULL;
	}

	heap_free(tmp_heap);

	return b_tree;
}

void satelit_free(satellite_t *sat)
{
	free(sat->name);
	free(sat);
}

void satelit_fprint(FILE *output_file, satellite_t *sat)
{
	fprintf(output_file, "%d-%s ", sat->frequency, sat->name);
}

void satelit_print(satellite_t *sat)
{
	printf("%d-%s ", sat->frequency, sat->name);
}

int cmp_satelit_node(const void *node_sat1, const void *node_sat2)
{
	const b_node_t *node1 = (const b_node_t *)node_sat1;
	const b_node_t *node2 = (const b_node_t *)node_sat2;
	const satellite_t *sat1 = (const satellite_t *)node1->data;
	const satellite_t *sat2 = (const satellite_t *)node2->data;
	if (sat1->frequency < sat2->frequency) {
		return -1;
	} else if (sat1->frequency > sat2->frequency) {
		return 1;
	} else {
		return strcmp(sat1->name, sat2->name);
	}
}