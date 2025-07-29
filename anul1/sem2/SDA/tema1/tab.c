/* DOBRE Mario-Sebastian - 314CA */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "tab.h"
#include "doubly_linked_list_t.h"

tab_t *createTab(int id, page_t *page)
{
	tab_t *tab = malloc(sizeof(tab_t));
	if (!tab) {
		fprintf(stderr, "malloc() failed!\n");
		return NULL;
	}
	tab->id = id;
	tab->currentPage = page;
	tab->backwardStack = st_create(sizeof(page_t));
	tab->forwardStack = st_create(sizeof(page_t));
	return tab;
}

void printTab(tab_t *tab, FILE *out)
{
	fprintf(out, "TAB ~ id = %d\n", tab->id);
	printPage(tab->currentPage, out);
}

void freeTab(tab_t *tab)
{
	st_free(tab->backwardStack);
	st_free(tab->forwardStack);
	free(tab);
}

void printTabs(doubly_linked_list_t *t, FILE *out)
{
	dll_node_t *node = t->head;
	for (unsigned int i = 0; i < t->size; i++) {
		tab_t *tab = node->data;
		printTab(tab, out);
		node = node->next;
	}
}

void freeTabList(doubly_linked_list_t *t)
{
	dll_node_t *node = t->head;
	for (unsigned int i = 0; i < t->size; i++) {
		tab_t *tab = node->data;
		freeTab(tab);
		dll_node_t *tmp = node;
		node = node->next;
		free(tmp);
	}
	free(t);
}
