/* DOBRE Mario-Sebastian - 314CA */
#define _POSIX_C_SOURCE 200809L // i used it for getline()
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "linked_list.h"
#include "page.h"

page_t *initPage(unsigned int id, char url[50], char *description)
{
	page_t *page = malloc(sizeof(page_t));
	if (!page) {
		fprintf(stderr, "malloc() failed\n");
		return NULL;
	}

	page->description = malloc(strlen(description) + 1);
	if (!page->description) {
		free(page);
		fprintf(stderr, "malloc() failed\n");
		return NULL;
	}

	memcpy(&page->id, &id, sizeof(int));
	memcpy(page->url, url, strlen(url) + 1);
	memcpy(page->description, description, strlen(description) + 1);

	return page;
}

linked_list_t *arrayPage(unsigned int n, FILE *in)
{
	linked_list_t *Pages_list = ll_create(sizeof(page_t));
	for (unsigned int i = 0; i <= n; i++) {
		unsigned int id;
		char url[50], *description = NULL;
		if (i > 0) {
			size_t len = 0;
			fscanf(in, "%d %s %s", &id, url, description);
			getline(&description, &len, in);
			if (description[strlen(description) - 1] == '\n') {
				description[strlen(description) - 1] = '\0';
			}
		} else {
			id = __FIRST_ID__;
			strcpy(url, __FIRST_URL__);
			description = malloc(17);
			if (!description) {
				fprintf(stderr, "malloc() failed\n");
				return NULL;
			}
			strcpy(description, __FIRST_DESCRIPTION__);
		}
		page_t *page = initPage(id, url, description);
		ll_add_nth_node(Pages_list, Pages_list->size, page);
		free(description);
		free(page);
	}

	return Pages_list;
}

void printPage(page_t *page, FILE *out)
{
	if (!page) {
		fprintf(out, "SANTINELA ~> NULL\n");
		return;
	}
	fprintf(out, "id = %d\n", page->id);
	fprintf(out, "url = %s\n", page->url);
	fprintf(out, "descriere = %s\n", page->description);
}

void printPages(linked_list_t *p, FILE *out)
{
	ll_node_t *node = p->head;
	while (node) {
		page_t *page = node->data;
		fprintf(out, "id = %d\n", page->id);
		fprintf(out, "url = %s\n", page->url);
		fprintf(out, "descriere = %s\n", page->description);
		node = node->next;
	}
}

void freePage(void *data)
{
	page_t *page = data;
	free(page->description);
}

void freePages(linked_list_t *p)
{
	ll_node_t *node = p->head;
	while (node) {
		freePage(node->data);
		free(node->data);
		ll_node_t *tmp = node;
		node = node->next;
		free(tmp);
	}
	free(p);
}
