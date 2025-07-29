/* DOBRE Mario-Sebastian - 314CA */
#ifndef TAB_H
#define TAB_H

#include "page.h"
#include "stack.h"
#include "doubly_linked_list_t.h"

typedef struct {
	int id;
	page_t *currentPage;
	stack_t *backwardStack, *forwardStack;
}tab_t;

tab_t *createTab(int id, page_t *page);

void printTab(tab_t *tab, FILE *out);

void printTabs(doubly_linked_list_t *t, FILE *out);

void freeTab(tab_t *tab);

void freeTabList(doubly_linked_list_t *t);

#endif // TAB_H