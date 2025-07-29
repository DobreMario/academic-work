/* DOBRE Mario-Sebastian - 314CA */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "page.h"
#include "linked_list.h"
#include "stack.h"

stack_t *
st_create(unsigned int data_size)
{
	
    stack_t *stack = (stack_t *)malloc(sizeof(stack_t));
    if (!stack) {
        printf("malloc() failed!\n");
        exit(0);
    }
    stack->list = ll_create(data_size);
    return stack;
}

unsigned int
st_get_size(stack_t *st)
{
	
	return st->list->size;
}

unsigned int
st_is_empty(stack_t *st)
{
	
    if (!st || !st->list || !st->list->head) {
        return 1;
    }
	return 0;
}

void *
st_peek(stack_t *st)
{
    
    if (!st || !st->list || !st->list->head) {
        return NULL;
    }
	return st->list->head->data;
}

void
st_pop(stack_t *st)
{
	
    if (!st || !st->list || !st->list->head) {
        return;
    }
    ll_node_t *tmp = ll_remove_nth_node(st->list, 0);
    free(tmp->data);
    free(tmp);
    
}

void
st_push(stack_t *st, void *new_data)
{
	
    ll_add_nth_node(st->list, 0, new_data);
}

void
st_clear(stack_t *st)
{
    ll_free(&(st->list));
}

void
st_free(stack_t *st)
{
    
    st_clear(st);
    free(st);
    st = NULL;
}

void
st_page_print(ll_node_t *node, FILE *out) // // Recursive print of the list in normal order
{
	if (!node || !out) {
		return;
	}

	page_t *tmp = node->data;
	fprintf(out, "%s\n", tmp->url);

	st_page_print(node->next, out);
	
}

void
st_page_print_r(ll_node_t *node, FILE *out) // // Recursive print of the list in reverse order
{
	if (!node || !out) {
		return;
	}

	st_page_print(node->next, out);

	page_t *tmp = node->data;
	fprintf(out, "%s\n", tmp->url);
}
