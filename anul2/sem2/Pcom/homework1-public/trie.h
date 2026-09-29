#ifndef TRIE_H
#define TRIE_H

#include <stdint.h>

extern "C" {
	#include "lib.h"
}

struct TrieNode {
	TrieNode* left;
	TrieNode* right;
	struct route_table_entry* route;

	TrieNode() {
		left = NULL;
		right = NULL;
		route = NULL;
	}
};

void insert_route(TrieNode* root, struct route_table_entry* route);
struct route_table_entry* get_best_route_trie(TrieNode* root, uint32_t ip_dest);
void free_trie(TrieNode* root);

#endif // TRIE_H