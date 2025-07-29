/* DOBRE Mario-Sebastian - 314CA */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "satellite_core.h"

int main(int argc, char *argv[])
{
	if (argc != 4) {
		fprintf(stderr, "Invalid number of parapeters");
		exit(-1);
	}

	if (argv[1][1] != 'c' || argv[1][0] != '-') {
		fprintf(stderr, "Invalid command %s\n", argv[1]);
		exit(-1);
	}

	int idx = argv[1][2] - '0';

	if (idx < 1 || idx > 5) {
		fprintf(stderr, "Invalid command %s\n", argv[1]);
		exit(-1);
	}
	
	idx--;

	FILE *input_file = fopen(argv[2], "r");
	if (!input_file) {
		fprintf(stderr, "Error opening file %s\n", argv[2]);
		exit(-1);
	}
	
	FILE *output_file = fopen(argv[3], "w");
	if (!output_file) {
		fprintf(stderr, "Error opening file %s\n", argv[3]);
		fclose(input_file);
		exit(-1);
	}

	b_tree_t *b_tree = satelit_create_tree(input_file);

	if (!b_tree) {
		fprintf(stderr, "Error creating binary tree\n");
		fclose(input_file);
		fclose(output_file);
		exit(0);
	}

	COMMANDS_LIST[idx](b_tree, input_file, output_file);

	b_tree_free(b_tree, (void (*)(void *))satelit_free);
	fclose(input_file);
	fclose(output_file);
	return 0;
}