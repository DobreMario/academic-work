/* DOBRE Mario-Sebastian - 314CA */
#ifndef COMMAND_H
#define COMMAND_H

#include "browser.h"

typedef enum {
	NEW_TAB = 2670,
	CLOSE = 306,
	OPEN = 2319,
	NEXT = 81,
	PREV = 3587,
	PAGE = 1503,
	BACKWARD = 542,
	FORWARD = 3674,
	PRINT = 3792,
	PRINT_HISTORY = 4091
} Command;

typedef int (*comm_t)(browser_t *, linked_list_t *, FILE*, FILE*);

comm_t command_translator(char line[256]);

int new_tab(browser_t *browser, linked_list_t * pages,
			 FILE *in, FILE *out);

int close(browser_t *browser, linked_list_t * pages,
		   FILE *in, FILE *out);

int open(browser_t *browser, linked_list_t * pages,
		  FILE *in, FILE *out);

int next(browser_t *browser, linked_list_t * pages,
		  FILE *in, FILE *out);

int prev(browser_t *browser, linked_list_t * pages,
		  FILE *in, FILE *out);

int page(browser_t *browser, linked_list_t * pages,
		  FILE *in, FILE *out);

int backward(browser_t *browser, linked_list_t * pages,
			  FILE *in, FILE *out);

int forward(browser_t *browser, linked_list_t * pages,
			 FILE *in, FILE *out);

int print(browser_t *browser, linked_list_t * pages,
		   FILE *in, FILE *out);

int print_history(browser_t *browser, linked_list_t * pages,
				   FILE *in, FILE *out);

#endif // COMMAND_H