# Homework 3 - IOCLA

**Author:** Dobre Mario-Sebastian
**Group:** 314CA

---

## Task 1: `sortari.asm` 🧩

- Nodes are stored in a vector, each with two fields:
  - `.val` – node value (int)
  - `.next` – pointer to the next node
- Nodes are linked in increasing order using the `.next` field.
- The node with value 1 is returned as the head of the list.
- Complexity: O(n²), acceptable due to the uniqueness and consecutiveness of values.

---

## Task 2: `operatii.asm` ✂️📋

### Goal  
Extract words from a string using delimiters and sort them by length and lexicographically.

---

### Functions

- `get_words`
  Iterates through the string, replaces delimiters with `'\0'`, and stores addresses of detected words.

- `sort`
  Sorts the vector of word pointers using a custom comparator `my_strcmp`.

- `my_strcmp`
  First compares word lengths, then lexicographically if lengths are equal.

---

## Task 3: `kfib.asm` 🔢✨

### Goal  
Recursively computes the n-th term in the KFib sequence, defined as the sum of the previous K terms.

### Mathematical Definition  

\[
KFib(n) = 
egin{cases}
0, & n < K \
1, & n = K \
\sum_{i=1}^{K} KFib(n - i), & n > K
\end{cases}
\]

### 🧠 Thought Process  
I chose to iterate from `n-K` to `n-1` for the recursive calls. Trying from `n` down caused index confusion and segmentation faults.

> **"What doesn’t kill you makes you stronger… unless it’s a segmentation fault."** 💀

---

## Task 4 - `composite_palindrome.asm` 🔄🔍

### 🔧 Pre-ASM: Temporary C File  
I first wrote a C version to clearly understand the logic before converting it to assembly. It helped avoid mistakes related to register management and recursive calls.

---

### 📚 Math & Logic

- Goal: Find the longest concatenation of words that forms a **palindrome**.
- If multiple with the same length, pick the lexicographically smallest.
- There are \(2^n\) possible subsets from `n` words.
- For each subset, check if the concatenation is a palindrome.

---

### 🧠 Implementation Logic

1. Generate all subsets using a **bitmask** (from 1 to \(2^n - 1\)).
2. Concatenate words from the current subset.
3. Check if the result is a palindrome (by comparing characters from ends to center).
4. If it’s a palindrome and better than previous, store as the result.

---

### check_palindrome 🔍

- Compares characters from the start and end of the string.
- Returns 1 if it’s a palindrome, 0 otherwise.

---

### composite_palindrome 🧩

- Calculates max possible concatenation length.
- Allocates buffers for testing and storing the best result.
- Uses **bitmask** to generate and test all subsets.
- Returns the best palindrome found.
