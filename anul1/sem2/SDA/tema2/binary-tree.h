/* DOBRE Mario-Sebastian - 314CA */
#ifndef BINARY_TREE_H
#define BINARY_TREE_H

#include <stdio.h>

typedef struct b_node_t b_node_t;
struct b_node_t
{
	b_node_t *left;
	b_node_t *right;
	void *data;
};

typedef struct b_tree_t b_tree_t;
struct b_tree_t
{
	b_node_t *root;
	size_t data_size;
};

b_tree_t *b_tree_create(size_t data_size);

b_node_t *b_node_create(void *data, size_t data_size);

void b_tree_insert(b_tree_t *b_tree, void *data);

void b_tree_print_preorder(b_tree_t *b_tree, void (*print_data)(void *));

void b_tree_print_inorder(b_tree_t *b_tree, void (*print_data)(void *));

void b_tree_print_postorder(b_tree_t *b_tree, void (*print_data)(void *));

void b_tree_print_levelorder(b_tree_t *b_tree, void (*print_data)(void *));

void b_tree_free(b_tree_t *b_tree, void (*free_data)(void *));

// ---------- HOMEWORK FUNCTIONS ----------

void b_tree_fprint_levelorder(FILE *output_file, b_tree_t *b_tree,
							  void (*print_data)(FILE*, void *)); 

#endif // BINARY_TREE_H