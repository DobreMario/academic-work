/* DOBRE Mario-Sebastian - 314CA */

#ifndef SATELLITE_H
#define SATELLITE_H

#include "binary-tree.h"

/**
 * @brief Structure representing a satellite.
 */
typedef struct {
	char *name;       ///< Name of the satellite.
	int frequency;    ///< Frequency of the satellite.
} satellite_t;

/**
 * @brief Initializes a satellite object.
 *
 * Allocates memory and sets the name and frequency for a new satellite.
 *
 * @param name The name of the satellite.
 * @param frequency The frequency associated with the satellite.
 * @return Pointer to a dynamically allocated satellite_t structure.
 */
satellite_t *satelit_init(char *name, int frequency);

/**
 * @brief Builds a binary tree from a file containing satellite data.
 *
 * This function reads satellite information from the given input file
 * and constructs a binary tree using satellite_t structures.
 *
 * @param input_file File containing satellite data.
 * @return A pointer to the root of the constructed binary tree.
 */
b_tree_t *satelit_create_tree(FILE *input_file);

/**
 * @brief Frees the memory used by a satellite.
 *
 * Releases memory for the satellite name and the structure itself.
 *
 * @param sat Pointer to the satellite to be freed.
 */
void satelit_free(satellite_t *sat);

/**
 * @brief Prints the satellite to stdout.
 *
 * Displays the satellite's name and frequency in a human-readable format.
 *
 * @param sat Pointer to the satellite to be printed.
 */
void satelit_print(satellite_t *sat);

/**
 * @brief Prints the satellite to a file.
 *
 * Similar to `satelit_print`, but outputs to a specified file instead of stdout.
 *
 * @param output_file File where the satellite information will be written.
 * @param sat Pointer to the satellite to print.
 */
void satelit_fprint(FILE *output_file, satellite_t *sat);

/**
 * @brief Compares two binary tree nodes containing satellite data.
 *
 * This function compares two nodes by their satellite frequency first.
 * If the frequencies are different, the node with the smaller frequency is considered smaller.
 * If frequencies are equal, it compares the satellite names lexicographically using `strcmp`.
 *
 * @param node_sat1 Pointer to the first binary tree node (b_node_t) containing satellite_t data.
 * @param node_sat2 Pointer to the second binary tree node (b_node_t) containing satellite_t data.
 * @return Negative value if node_sat1 < node_sat2,
 *         0 if they are equal,
 *         Positive value if node_sat1 > node_sat2.
 */
int cmp_satelit_node(const void *node_sat1, const void *node_sat2);


#endif // SATELLITE_H
