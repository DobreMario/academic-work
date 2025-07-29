/* DOBRE Mario-Sebastian - 314CA */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "browser.h"

browser_t *initBrowser(page_t *page)
{
	browser_t *browser = malloc(sizeof(browser_t));
	if (!browser) {
		fprintf(stderr, "malloc() failed!\n");
		return NULL;
	}

	browser->tab_list = dll_create(sizeof(tab_t));
	tab_t *santinela = createTab(0, NULL);
	dll_add_nth_node(browser->tab_list, 0, santinela);
	browser->santinela = browser->tab_list->head->data;
	tab_t *tab_0 = createTab(0, page);
	dll_add_nth_node(browser->tab_list, 1, tab_0);
	browser->current = browser->tab_list->head->next->data;
	free(tab_0);
	free(santinela);

	return browser;
}

void freeBrowser(browser_t *browser)
{
	freeTabList(browser->tab_list);
	free(browser);
}

