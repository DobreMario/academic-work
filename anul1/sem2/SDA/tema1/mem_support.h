/* DOBRE Mario-Sebastian - 314CA */
#ifndef MEM_SUPPORT_H
#define MEM_SUPPORT_H

#include <stdio.h>
#include <stdlib.h>
#include "core_browser.h"

void mem_support(browser_t *browser, linked_list_t * pages,
				 FILE *in, FILE *out)
{
	printf("Memory OUT!\n");
	if (browser) freeBrowser(browser);
	if (pages) freePages(pages);
	if (in) fclose(in);
	if (out) fclose(out);
	exit(0);
}
#endif // MEM_SUPPORT_H