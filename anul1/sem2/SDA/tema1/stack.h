/* DOBRE Mario-Sebastian - 314CA */
#ifndef STACK_H
#define STACK_H

#include "linked_list.h"

typedef struct {
	struct linked_list_t *list;
}stack_t;

//Standard stack functions
stack_t *
st_create(unsigned int data_size);

unsigned int
st_get_size(stack_t *st);

unsigned int
st_is_empty(stack_t *st);

void *
st_peek(stack_t *st);

void
st_pop(stack_t *st);

void
st_push(stack_t *st, void *new_data);

void
st_clear(stack_t *st);

void
st_free(stack_t *st);

//print page's stack function
void st_page_print(ll_node_t *node, FILE *out);

void st_page_print_r(ll_node_t *node, FILE *out);

#endif // STACK_H