/* DOBRE Mario-Sebastian - 314CA */
#ifndef BROWSER_H
#define BROWSER_H

#include "tab.h"
#include "doubly_linked_list_t.h"

typedef struct browser_t {
	tab_t *santinela; // id = max_id, in rest it will be ptrnull
	tab_t *current;
	doubly_linked_list_t *tab_list;
} browser_t;

browser_t *initBrowser(page_t *page);

void freeBrowser(browser_t *browser);

#endif // BROWSER_H
