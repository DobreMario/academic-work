/* DOBRE Mario-Sebastian - 314CA */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "core_browser.h"
#include "mem_support.h" // a header for memory support

int main(void)
{
	linked_list_t *page_list = NULL;
	browser_t *browser = NULL;
	FILE *in = NULL, *out = NULL;
	in = fopen("tema1.in", "rt");
	out = fopen("tema1.out", "wt");
	if (!in || !out) {
		mem_support(browser, page_list, in, out);
	}
	int n = 0, m = 0;
	fscanf(in, "%d", &n);
	page_list = arrayPage(n, in);
	if (!page_list) {
		mem_support(browser, page_list, in, out);
	}
	browser = initBrowser(page_list->head->data);
	if (!browser) {
		mem_support(browser, page_list, in, out);
	}
	fscanf(in, "%d", &m);
	for (int i = 0; i < m; i++) {
		char line[256];
		fscanf(in, " %s", line);
		comm_t f = NULL;
		f = command_translator(line);
		if (!f) {
			char tmp[256];
			if (fscanf(in, "%s", tmp)) {

			}
			fprintf(out, "Invalid command!\n");
		} else {
			if (f(browser, page_list, in, out) == 1) {
				mem_support(browser, page_list, in, out);
			}
		}
	}
	freeBrowser(browser);
	freePages(page_list);
	fclose(in);
	fclose(out);

	return 0;
}
