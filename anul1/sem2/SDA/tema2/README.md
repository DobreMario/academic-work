# Huffman Tree & Satellites Project

> **Author**: DOBRE Mario-Sebastian  
> **Group**: 314CA

## 📌 Table of Contents
- [📄 Project Overview](#-project-overview)
- [📁 File Structure](#-file-structure)
- [⚙️ Main Functionalities](#️-main-functionalities)
- [🧠 Data Structures Used](#-data-structures-used)
- [🏗️ Command System](#-command-system)
- [📚 API Overview](#-api-overview)
- [🛠️ Helper Functions](#-helper-functions)
- [🔧 How to Compile & Run](#-how-to-compile--run)
- [📝 Example Input & Output](#-example-input--output)
- [🔍 Step-by-Step Example Explanation](#-step-by-step-example-explanation)
- [📞 Contact](#-contact)
- [🙌 Feedback](#-feedback)

---

## 📄 Project Overview

This project implements a system for managing and processing satellites using advanced data structures such as min-heaps and binary trees (Huffman Tree). The program reads satellite data, builds a Huffman tree, and executes various commands on it, all in a modular and efficient way.

---

## 📁 File Structure

- `binary-tree.[c|h]` – Binary tree implementation and management.
- `min-heap.[c|h]` – Generic min-heap implementation.
- `satellite.[c|h]` – Satellite structures and functions (see below).
- `commands.[c|h]` – Command implementations for tree operations (see below).
- `main.c` – Entry point, handles argument parsing and command execution.
- `queue.[c|h]`, `stack.[c|h]`, `linked_list.[c|h]` – Auxiliary data structures.
- `Makefile` – For easy compilation.
- `input.in`, `output.out` – Test files.

---

## ⚙️ Main Functionalities

- Read satellite data from file and build a Huffman tree.
- Traverse the tree in various ways (BFS, encoding, decoding, LCA, etc.).
- Efficient memory management for dynamic structures.
- Defensive programming: all functions check pointer/resource validity.

---

## 🧠 Data Structures Used

- **Min-Heap** – for building the Huffman tree.
- **Binary Tree** – for representing the Huffman tree.
- **Stack & Queue** – for traversals and encoding/decoding.
- **Custom Structs** – for satellites and tree nodes.

---

## 🏗️ Command System

All supported commands are defined in `commands.h` and implemented in `commands.c`. The command system interprets user input and updates the tree state accordingly.

- `c1` – Print the tree in BFS (level order).
- `c2` – Decode and print leaves based on binary codes.
- `c3` – Print the binary encoding for given nodes.
- `c4` – Find the lowest common ancestor (LCA) for given nodes.
- `c5` – Reserved for future commands.

A function pointer vector is used for clean and modular command dispatch:
```c
#define COMMANDS_LIST ((command_func_t[]){c1, c2, c3, c4, c5})
// Usage:
COMMANDS_LIST[idx](b_tree, input_file, output_file);
```

---

## 📚 API Overview

### `satellite.h`

Defines the satellite structure and its associated functions:

- **`satellite_t`**  
  Represents a satellite with a name (`char *name`) and frequency (`int frequency`).

- **Key Functions:**
  - `satellite_t *satelit_init(char *name, int frequency);`  
    Allocates and initializes a satellite.
  - `b_tree_t *satelit_create_tree(FILE *input_file);`  
    Builds a binary tree from file data.
  - `void satelit_free(satellite_t *sat);`  
    Frees a satellite's memory.
  - `void satelit_print(satellite_t *sat);`  
    Prints satellite data to stdout.
  - `void satelit_fprint(FILE *output_file, satellite_t *sat);`  
    Prints satellite data to a file.
  - `int cmp_satelit_node(const void *node_sat1, const void *node_sat2);`  
    Comparison function for heap nodes.

### `commands.h`

Defines the command functions for tree operations:

- `void c1(b_tree_t *tree, FILE *input_file, FILE *output_file);`
- `void c2(b_tree_t *tree, FILE *input_file, FILE *output_file);`
- `void c3(b_tree_t *tree, FILE *input_file, FILE *output_file);`
- `void c4(b_tree_t *tree, FILE *input_file, FILE *output_file);`
- `void c5(b_tree_t *tree, FILE *input_file, FILE *output_file);`

---

## 🛠️ Helper Functions

Each main command (`c1`-`c5`) uses internal static helper functions (prefixed with `__`), visible only in `commands.c`, for modular and clear code:

- **For `c1`**:  
  Uses direct tree traversal functions, no custom helpers.

- **For `c2`**:  
  - `static b_node_t *__c2_helper(b_node_t *b_node, char code[1001])` – Recursively traverses the tree based on a binary code to reach a leaf.

- **For `c3`**:  
  - `static int __c3_helper(b_node_t *b_node, stack_t *path, char *name)` – Recursively finds a node by name and builds its binary path.

- **For `c4`**:  
  - `static char *__c4_helper(b_tree_t *b_tree, int n, FILE *input_file)` – Builds and compares binary paths for multiple nodes to find the common prefix (LCA).
  - `static void __strrev(char *str)` – Reverses a string (used for encoding).
  - `static void __st_to_str(stack_t *st, char *str)` – Converts a stack of binary steps to a string.

- **For `c5`**:  
  No helpers yet; reserved for future use.

---

## 🔧 How to Compile & Run

```bash
make
```

To run:
```bash
./tema2 -c1 input.in output.out
```
Where `-c1` to `-c5` selects the desired command.

---

## 📞 Contact

For questions, suggestions, or bug reports:

- 📧 **Email**: mario.dobre@stud.acs.upb.ro

---

## 🙌 Feedback

This project was a great opportunity to apply advanced data structures and memory management in C.  
Any feedback on:

- 🧠 **Implementation logic** and correctness  
- 🧹 **Code structure and readability**  
- 🧮 **Dynamic memory management**  

is welcome! Thank you! 🚀

---
