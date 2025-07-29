/* DOBRE Mario-Sebastian - 314CA */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "satellite_core.h"

void c1(b_tree_t *b_tree, FILE *input_file, FILE *output_file) {
	if (!b_tree || !input_file || !output_file) {
		fprintf(stderr, "Error: Invalid arguments passed to c1 (tree, input_file, or output_file is NULL).\n");
		return;
	}

	b_tree_fprint_levelorder(output_file, b_tree, (void (*)(FILE *, void *))satelit_fprint);
}

static b_node_t *__c2_helper(b_node_t *b_node, char code[1001])
{
	if (code[0] == '0' && b_node->left) {

		char tmp[1001];
		strcpy(tmp, code + 1);
		strcpy(code, tmp);

		return __c2_helper(b_node->left, code);
	} else if (code[0] == '1' && b_node->right) {

		char tmp[1001];
		strcpy(tmp, code + 1);
		strcpy(code, tmp);

		return __c2_helper(b_node->right, code);
	}

	return b_node;
}

void c2(b_tree_t *b_tree, FILE *input_file, FILE *output_file) {
	if (!b_tree || !input_file || !output_file) {
		fprintf(stderr, "Error: Invalid arguments passed to c2 (tree, input_file, or output_file is NULL).\n");
		return;
	}

	int n = 0;

	fscanf(input_file, "%d", &n);

	for (int i = 0; i < n; i++) {
		char code[1001];
		fscanf(input_file, " %s", code);
		while (code[0] != '\0') {
			b_node_t *tmp = __c2_helper(b_tree->root, code);
			if (!tmp->left && !tmp->right) {
				satellite_t *sat_tmp = tmp->data;
				fprintf(output_file, "%s  ", sat_tmp->name);
			}
		}
		fprintf(output_file, "\n");
	}
}

static int __c3_helper(b_node_t *b_node, stack_t *path, char *name)
{
	if (!b_node) {
		return 0;
	}

	satellite_t *tmp = b_node->data;

	if (!strcmp(name, tmp->name)) {
		return 1;
	}

	int c = 0;

	st_push(path, &c);
	if (__c3_helper(b_node->left, path, name)) {
		return 1;
	}
	st_pop(path);

	c = 1;
	st_push(path, &c);
	if (__c3_helper(b_node->right, path, name)) {
		return 1;
	}
	st_pop(path);

	return 0;
}

void c3(b_tree_t *b_tree, FILE *input_file, FILE *output_file) {
	if (!b_tree || !input_file || !output_file) {
		fprintf(stderr, "Error: Invalid arguments passed to c3 (tree, input_file, or output_file is NULL).\n");
		return;
	}

	int n = 0;

	fscanf(input_file, "%d", &n);

	stack_t *path = st_create(sizeof(int));

	for (int i = 0; i < n; i++) {
		char name[15];
		fscanf(input_file, " %s", name);
		__c3_helper(b_tree->root, path, name);
	}

	st_print_path(path->list->head, output_file);
	st_free(path);

	fprintf(output_file, "\n");
}

static void __strrev(char *str)
{
	int len =  strlen(str);
	for (int i = 0; i < len / 2; i++) {
		char tmp = str[i];
		str[i] = str[len - i - 1];
		str[len - i - 1] = tmp;
	}
}

static void __st_to_str(stack_t *st, char *str)
{
	int cnt = 0;

	while (!st_is_empty(st)) {
		char tmp = *(int *)st_peek(st) + '0';
		str[cnt++] = tmp;
		st_pop(st);
	}

	str[cnt] = '\0';

	__strrev(str);
}

static char *__c4_helper(b_tree_t *b_tree ,int n, FILE *input_file)
{
	char **paths = malloc(n * sizeof(char *));
	if (!paths) {
		return NULL;
	}
	for (int i = 0; i < n; i++) {
		paths[i] = malloc(1001);
		if(!paths[i]) {
			for (int j = 0; j < i; j++) {
				free(paths[j]);
			}
			free(paths);
			return NULL;
		}
	}
	char *same_path = malloc(1001);
	if (!same_path) {
		for (int i = 0; i < n; i++) {
			free(paths[i]);
		}
		free(paths);
		return NULL;
	}

	for (int i = 0; i < n; i++) {
		stack_t *path = st_create(sizeof(int));
		char name[15];
		fscanf(input_file, " %s", name);
		__c3_helper(b_tree->root, path, name);
		__st_to_str(path, paths[i]);
		st_free(path);
	}

	int i = 1;
	strcpy(same_path, paths[0]);
	while (i < n) {
		int j = 0;
		while (same_path[j] == paths[i][j]) {
			j++;
		}

		same_path[j] = '\0';
		i++;
	}

	for (int i = 0; i < n; i++) {
		free(paths[i]);
	}
	free(paths);

	return same_path;
}

void c4(b_tree_t *b_tree, FILE *input_file, FILE *output_file) {
	if (!b_tree || !input_file || !output_file) {
		fprintf(stderr, "Error: Invalid arguments passed to c4 (tree, input_file, or output_file is NULL).\n");
		return;
	}

	int n;
	fscanf(input_file, "%d", &n);
	char *path = __c4_helper(b_tree, n, input_file);
	if (!path) {
		return;
	}
	b_node_t *tmp = __c2_helper(b_tree->root, path);

	satellite_t *tmp_sat = tmp->data;

	fprintf(output_file, "%s\n", tmp_sat->name);
	free(path);
}

void c5(b_tree_t *b_tree, FILE *input_file, FILE *output_file) {
	if (!b_tree || !input_file || !output_file) {
		fprintf(stderr, "Error: Invalid arguments passed to c5 (tree, input_file, or output_file is NULL).\n");
		return;
	}
}