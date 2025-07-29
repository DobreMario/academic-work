/* DOBRE Mario-Sebastian - 314CA */
#ifndef PAGE_H
#define PAGE_H

#include "linked_list.h"

/* Next defines are for the first page <3 */
#define __FIRST_ID__ 0
#define __FIRST_URL__ "https://acs.pub.ro/"
#define __FIRST_DESCRIPTION__ "Computer Science"

typedef struct {
	unsigned int id;
	char url[50];
	char *description;
}page_t;

page_t *initPage(unsigned int id, char url[50], char *description);

linked_list_t *arrayPage(unsigned int n, FILE *in);

void printPage(page_t *page, FILE *in);

void printPages(linked_list_t *p, FILE *in);

void freePage(void *data);

void freePages(linked_list_t *p);

#endif // PAGE_H