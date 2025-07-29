/* DOBRE Mario-Sebastian - 314CA */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "command.h"

unsigned long hash(const char *str)
{
	unsigned long hash = 5381;
	int c;

	while ((c = *str++)) {
		hash = (((hash << 5) + hash) + c) % ((1 << 12) - 1);
	}

	return hash;
}

comm_t command_translator(char line[256])
{
	unsigned long id = hash(line);
	switch (id) {
	case 2670:
		return new_tab;
	case 306:
		return close;
	case 2319:
		return open;
	case 81:
		return next;
	case 3587:
		return prev;
	case 1503:
		return page;
	case 542:
		return backward;
	case 3674:
		return forward;
	case 3792:
		return print;
	case 4091:
		return print_history;
	default:
		return NULL;
	}

	return NULL;
}

int new_tab(browser_t *browser, linked_list_t *pages,
			FILE *in, FILE *out)
{
	if (!pages || !browser ||
		!in || !out) {
		return 0;
	}

	browser->santinela->id++;
	tab_t *t = createTab(browser->santinela->id, pages->head->data);
	if (!t) {
		return 1;
	}

	dll_add_nth_node(browser->tab_list, browser->tab_list->size, t);
	browser->current = browser->tab_list->head->prev->data;
	free(t);
	return 0;
}

int close(browser_t *browser, linked_list_t *pages,
		  FILE *in, FILE *out)
{
	if (!pages || !browser ||
		!in || !out) {
		return 0;
	}

	if (browser->current->id == 0) {
		fprintf(out, "403 Forbidden\n");
		return 0;
	}

	unsigned int cnt = 0;
	dll_node_t *node = browser->tab_list->head;
	while (node->data != browser->current) {
		node = node->next;
		cnt++;
	}
	node = node->prev;
	dll_node_t *tmp = dll_remove_nth_node(browser->tab_list, cnt);
	freeTab(tmp->data);
	free(tmp);

	if (node == browser->tab_list->head) {
		node = node->prev;
	}
	browser->current = node->data;

	return 0;
}

int open(browser_t *browser, linked_list_t *pages,
		 FILE *in, FILE *out)
{
	if (!pages || !browser ||
		!in || !out) {
		return 0;
	}

	unsigned int id = 0;
	fscanf(in, "%u", &id);

	dll_node_t *node = browser->tab_list->head->next;
	tab_t *tmp = node->data;
	while (node != browser->tab_list->head &&
		   ((unsigned int)tmp->id) != id) {
		node = node->next;
		tmp = node->data;
	}

	if (node == browser->tab_list->head) {
		fprintf(out, "403 Forbidden\n");
		return 0;
	}

	browser->current = tmp;
	return 0;
}

int next(browser_t *browser, linked_list_t *pages,
		 FILE *in, FILE *out)
{
	if (!pages || !browser ||
		!in || !out) {
		return 0;
	}

	dll_node_t *node = browser->tab_list->head;
	while (node->data != browser->current) {
		node = node->next;
	}
	node = node->next;

	if (node->data == browser->santinela) {
		node = node->next;
	}

	browser->current = node->data;

	return 0;
}

int prev(browser_t *browser, linked_list_t *pages,
		 FILE *in, FILE *out)
{
	if (!pages || !browser ||
		!in || !out) {
		return 0;
	}

	dll_node_t *node = browser->tab_list->head;
	while (node->data != browser->current) {
		node = node->next;
	}
	node = node->prev;

	if (node->data == browser->santinela) {
		node = node->prev;
	}

	browser->current = node->data;

	return 0;
}

int page(browser_t *browser, linked_list_t *pages,
		 FILE *in, FILE *out)
{
	if (!pages || !browser ||
		!in || !out) {
		return 0;
	}

	unsigned int id = 0;
	fscanf(in, "%u", &id);

	ll_node_t *node = pages->head;
	page_t *tmp = node->data;
	while (node && ((unsigned int)tmp->id) != id) {
		node = node->next;
		if (node) {
			tmp = node->data;
		}
	}

	if (!node) {
		fprintf(out, "403 Forbidden\n");
		return 0;
	}

	st_free(browser->current->forwardStack);
	browser->current->forwardStack = st_create(sizeof(page_t));
	st_push(browser->current->backwardStack, browser->current->currentPage);
	browser->current->currentPage = tmp;

	return 0;
}

int backward(browser_t *browser, linked_list_t *pages,
			 FILE *in, FILE *out)
{
	if (!pages || !browser ||
		!in || !out) {
		return 0;
	}

	if (!browser->current->backwardStack
		|| !browser->current->backwardStack->list
		|| !browser->current->backwardStack->list->head) {
		fprintf(out, "403 Forbidden\n");
		return 0;
	}

	st_push(browser->current->forwardStack, browser->current->currentPage);
	page_t *p = st_peek(browser->current->backwardStack);
	ll_node_t *node = pages->head;

	browser->current->currentPage = node->data;
	while (browser->current->currentPage->id != p->id) {
		node = node->next;
		browser->current->currentPage = node->data;
	}
	// This block is used because after popping, the current page would point to NULL.

	st_pop(browser->current->backwardStack);
	return 0;
}

int forward(browser_t *browser, linked_list_t *pages,
			FILE *in, FILE *out)
{
	if (!pages || !browser ||
		!in || !out) {
		return 0;
	}

	if (!browser->current->forwardStack
		|| !browser->current->forwardStack->list
		|| !browser->current->forwardStack->list->head) {
		fprintf(out, "403 Forbidden\n");
		return 0;
	}

	st_push(browser->current->backwardStack, browser->current->currentPage);
	page_t *p = st_peek(browser->current->forwardStack);
	ll_node_t *node = pages->head;

	browser->current->currentPage = node->data;
	while (browser->current->currentPage->id != p->id) {
		node = node->next;
		browser->current->currentPage = node->data;
	}
	// This block is used because after popping, the current page would point to NULL.
	st_pop(browser->current->forwardStack);

	return 0;
}

int print(browser_t *browser, linked_list_t *pages,
		  FILE *in, FILE *out)
{
	if (!pages || !browser ||
		!in || !out) {
		return 0;
	}

	dll_node_t *node = browser->tab_list->head;
	while (node->data != browser->current) {
		node = node->next;
	}

	for (unsigned int i = 0; i < browser->tab_list->size; i++) {
		if (node != browser->tab_list->head) {
			tab_t *t = node->data;
			fprintf(out, "%d ", t->id);
		}
		node = node->next;
	}
	fprintf(out, "\n");
	fprintf(out, "%s\n", browser->current->currentPage->description);

	return 0;
}

int print_history(browser_t *browser, linked_list_t *pages,
				  FILE *in, FILE *out)
{
	if (!pages || !browser ||
		!in || !out) {
		return 0;
	}

	unsigned int id = 0;
	fscanf(in, "%u", &id);

	dll_node_t *node = browser->tab_list->head->next;
	tab_t *tmp = node->data;
	while (node != browser->tab_list->head &&
		   ((unsigned int)tmp->id) != id) {
		node = node->next;
		tmp = node->data;
	}

	if (node == browser->tab_list->head) {
		fprintf(out, "403 Forbidden\n");
		return 0;
	}

	st_page_print_r(tmp->forwardStack->list->head, out);
	fprintf(out, "%s\n", tmp->currentPage->url);
	st_page_print(tmp->backwardStack->list->head, out);

	return 0;
}
