/* DOBRE Mario-Sebastian - 314CA */
#ifndef COMMANDS_H
#define COMMANDS_H

#include "binary-tree.h"

/**
 * @brief Executes command 1: prints the binary tree in BFS order.
 *
 * This function performs a level-order (Breadth-First Search) traversal of the binary tree.
 * All nodes located on the same level are printed on the same line, separated by spaces.
 * Output is written exclusively to the output_file.
 *
 * @param tree The binary tree containing data of type satellite_t.
 * @param input_file Input file (not used for this command).
 * @param output_file File to which the BFS traversal result is printed.
 */
void c1(b_tree_t *tree, FILE *input_file, FILE *output_file);

/**
 * @brief Executes command 2: decodes and prints leaves from binary codes.
 *
 * This function continues reading from the input file, parsing binary-encoded paths
 * (e.g., "010110"). A '0' means left, and a '1' means right. If the code represents a valid
 * path to a leaf node, the leaf's value is printed.
 * Output is written exclusively to the output_file.
 *
 * @param tree The binary tree containing data of type satellite_t.
 * @param input_file Input file containing binary-encoded paths.
 * @param output_file Output file where leaf node values are printed.
 */
void c2(b_tree_t *tree, FILE *input_file, FILE *output_file);

/**
 * @brief Executes command 3: prints the binary encoding of given nodes.
 *
 * This function continues reading node values from the input file and outputs their
 * binary encoding (e.g., "010110") based on their path in the tree (0 = left, 1 = right).
 * Output is written exclusively to the output_file.
 *
 * @param tree The binary tree containing data of type satellite_t.
 * @param input_file Input file with node values to encode.
 * @param output_file Output file for encoded paths.
 */
void c3(b_tree_t *tree, FILE *input_file, FILE *output_file);

/**
 * @brief Executes command 4: finds the lowest common ancestor (LCA) of given nodes.
 *
 * This function reads pairs or sets of node values from the input file and determines
 * their lowest common ancestor in the tree. The result is printed to the output file.
 * Output is written exclusively to the output_file.
 *
 * @param tree The binary tree containing data of type satellite_t.
 * @param input_file Input file with node values to analyze.
 * @param output_file Output file for LCA results.
 */
void c4(b_tree_t *tree, FILE *input_file, FILE *output_file);

/**
 * @brief Executes command 5.
 *
 * Currently unspecified. This function may be used for a future command.
 *
 * @param tree The binary tree containing data of type satellite_t.
 * @param input_file Input file.
 * @param output_file Output file.
 */
void c5(b_tree_t *tree, FILE *input_file, FILE *output_file);

/**
 * @brief Function pointer type for commands operating on a binary tree and file streams.
 *
 * Each function of this type requires a binary tree with satellite_t data,
 * and two file pointers for input and output.
 */
typedef void (*command_func_t)(b_tree_t *tree, FILE *input_file, FILE *output_file);

/**
 * @brief Array of command function pointers.
 *
 * Contains all defined commands (c1 to c5), accessible by index.
 */
#define COMMANDS_LIST ((command_func_t[]){c1, c2, c3, c4, c5})

#endif // COMMANDS_H
