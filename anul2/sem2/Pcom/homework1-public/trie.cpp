#include "trie.h"
#include <arpa/inet.h>

void insert_route(TrieNode* root, struct route_table_entry* route) {
	TrieNode* curr = root;
	uint32_t prefix = ntohl(route->prefix);
	uint32_t mask = ntohl(route->mask);

	for (int i = 31; i >= 0; i--) {
		if ((mask & (1U << i)) == 0) {
			break;
		}

		int bit = (prefix >> i) & 1;

		if (bit == 0) {
			if (curr->left == NULL) {
				curr->left = new TrieNode();
			}
			curr = curr->left;
		} else {
			if (curr->right == NULL) {
				curr->right = new TrieNode();
			}
			curr = curr->right;
		}
	}
	
	curr->route = route;
}

struct route_table_entry* get_best_route_trie(TrieNode* root, uint32_t ip_dest) {
	TrieNode* curr = root;
	struct route_table_entry* best_match = NULL;
	uint32_t ip = ntohl(ip_dest);

	for (int i = 31; i >= 0; i--) {
		if (curr->route != NULL) {
			best_match = curr->route;
		}

		int bit = (ip >> i) & 1;

		if (bit == 0) {
			if (curr->left == NULL) {
				break;
			}
			curr = curr->left;
		} else {
			if (curr->right == NULL) {
				break;
			}
			curr = curr->right;
		}
	}

	if (curr != NULL && curr->route != NULL) {
		best_match = curr->route;
	}

	return best_match;
}

void free_trie(TrieNode* root) {
	if (root == NULL) {
		return;
	}
	free_trie(root->left);
	free_trie(root->right);
	delete root;
}
