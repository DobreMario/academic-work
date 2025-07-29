/* DOBRE Mario-Sebastian - 314CA */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include "doubly_linked_list_t.h"

#define DIE(assertion, call_description)				\
	do {								\
		if (assertion) {					\
			fprintf(stderr, "(%s, %d): ",			\
					__FILE__, __LINE__);		\
			perror(call_description);			\
			exit(errno);				        \
		}							\
	} while (0)

#define MAX_STRING_SIZE 64

doubly_linked_list_t*
dll_create(unsigned int data_size)
{
	
	doubly_linked_list_t *list = malloc(sizeof(doubly_linked_list_t));
	if (!list) {
		fprintf(stderr, "malloc() failed!\n");
		exit(0);
	}
	list->data_size = data_size;
	list->head = NULL;
	list->size = 0;
	return list;
}

dll_node_t*
dll_get_nth_node(doubly_linked_list_t* list, unsigned int n)
{
	
	dll_node_t *tmp = list->head;
	n = n % list->size;
	if (n <= list->size - n) {
		while (n--) {
			tmp = tmp->next;
		}
	} else {
		n = list->size - n;
		while (n--) {
			tmp = tmp->prev;
		}
	}

	return tmp;

}

void
dll_add_nth_node(doubly_linked_list_t* list, unsigned int n, const void* new_data)
{
	

	dll_node_t *new_node;

	new_node = malloc(sizeof(dll_node_t));
	if (!new_node) {
		fprintf(stderr, "malloc() failed\n");
		exit(0);
	}

	new_node->data = malloc(list->data_size);
	if (!new_node->data) {
		fprintf(stderr, "malloc() failed\n");
		exit(0);
	}

	memcpy(new_node->data, new_data, list->data_size);

	if (!list->head) {
		list->head = new_node;
		list->head->next = list->head;
		list->head->prev = list->head;
		list->head->prev = new_node;
		list->size++;
		return;
	}

	dll_node_t *tmp = list->head;

	if (n == 0) {
		dll_node_t *aux = tmp->prev;
		list->head = new_node;
		list->head->next = tmp;
		list->head->prev = tmp->prev;
		aux->next = list->head;
		list->size++;
		return;
	}

	if (n >= list->size) {
		tmp = tmp->prev;
		new_node->prev = tmp;
		tmp->next = new_node;
		new_node->next = list->head;
		list->head->prev = new_node;
		list->size++;
		return;
	}

	if (n <= list->size - n) {
		while (n--) {
			tmp = tmp->next;
		}
	} else {
		n = list->size - n;
		while (n--) {
			tmp = tmp->prev;
		}
	}

	new_node->next = tmp;
	new_node->prev = tmp->prev;
	tmp->prev->next = new_node;
	tmp->prev = new_node;
	list->size++;

	return;
}

dll_node_t*
dll_remove_nth_node(doubly_linked_list_t* list, unsigned int n)
{
	
	if (!list || !list->head) {
		return NULL;
	}

	if (n >= list->size - 1) {
		n = list->size - 1;
	}

	if (n == 0 && list->size == 1) {
		free(list->head->data);
		free(list->head);
		list->head = NULL;
		list->size--;
		return NULL;
	}

	if (n == 0 && list->head && list->head->next) {
		dll_node_t *aux = list->head;
		dll_node_t *tmp = list->head->next;
		tmp->prev = list->head->prev;
		tmp->prev->next = tmp;
		list->head = tmp;
		list->size--;
		return aux;
	}

	dll_node_t *tmp = list->head;

	if (n <= list->size - n) {
		while (n--) {
			tmp = tmp->next;
		}
	} else {
		n = list->size - n;
		while (n--) {
			tmp = tmp->prev;
		}
	}

	dll_node_t *aux = tmp->prev;
	aux->next = tmp->next;
	tmp->next->prev = aux;

	list->size--;

	return tmp;


	
}

unsigned int
dll_get_size(doubly_linked_list_t* list)
{
	
	return list->size;
}

void
dll_free(doubly_linked_list_t** pp_list)
{
	
	for (unsigned int i = 0; i < (*pp_list)->size; i++) {
		dll_node_t *tmp = (*pp_list)->head;
		(*pp_list)->head = (*pp_list)->head->next;
		free(tmp->data);
		free(tmp);
	}

	free((*pp_list));

	(*pp_list) = NULL;
	
}

void
dll_print_int_list(doubly_linked_list_t* list)
{
	if(!list || !list->head) {
		return;
	}
	dll_node_t *tmp = list->head;
	for (unsigned int i = 0; i < list->size; i++) {
		printf("%d ", *(int *)tmp->data);
		tmp = tmp->next;
	}
	printf("\n");
}

void
dll_print_string_list(doubly_linked_list_t* list)
{
	
	if(!list || !list->head) {
		return;
	}
	dll_node_t *tmp = list->head;
	for (unsigned int i = 0; i < list->size; i++) {
		printf("%s ", (char *)(tmp->data));
		tmp = tmp->next;
	}
	printf("\n");
}