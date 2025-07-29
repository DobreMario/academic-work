> **Author**: DOBRE Mario-Sebastian
> **Group**: 314CA

# Task 1

## Description

The assembly function, `remove_numbers`, filters a list of integers by removing:
1. Odd numbers.
2. Numbers that are powers of 2 (e.g., 1, 2, 4, 8, ...).

Numbers that are **even** and **not powers of 2** will be kept in the destination list.

### Parameters:
- `a` – Source list of numbers (pointer to a 32-bit array).
- `n` – Number of elements in the source list.
- `target` – Memory address where the function will write the result.
- `ptr_len` – Memory address where the function will write the length of the new list.

### Logic

1. **Removing odd numbers**: Check if the least significant bit is 1. If so, skip the number.
2. **Checking powers of 2**: Use the operation `x & (x - 1)` to determine if a number is a power of 2. If the result is 0, the number is a power of 2 and is skipped.
3. **Adding valid elements**: Numbers that pass both checks are added to the destination list. The stack is used to temporarily manage values and add elements to the destination list.

---

# Task 2 ~ Subtask 1

## Description

The function `check_events` validates an array of events based on specific rules for the year, month, and day fields. The `event` structure is defined as compact, according to the problem requirements.

### Parameters:
- `events` – Pointer to an array of `event` structures.
- `len` – Number of elements in the array.

### Logic

1. **Year validation**: The year must be between 1990 and 2030 (inclusive). If the year is outside this range, the event is marked as invalid.
2. **Month validation**: The month must be between 1 and 12 (inclusive). If the month is outside this range, the event is marked as invalid.
3. **Day validation**: The day must be valid for the respective month. A `days_in_month` array is used to determine the number of days in each month.
4. **Marking validity**: If all checks are passed, the event is marked as valid. Otherwise, it is marked as invalid.
5. **Iterating through events**: The function iterates through the array of events and applies the checks to each event.

---

# Task 2 ~ Subtask 2

## Description

This subtask contains two main functions: `event_cmp` and `sort_events`. These functions are used to compare and sort an array of events based on specific criteria.

### Function 1: `event_cmp`

The `event_cmp` function compares two events based on the following criteria, in order of priority:
1. **Validity**: Events with `valid = 0` are considered greater than those with `valid = 1`.
2. **Year**: Events with smaller years are considered smaller.
3. **Month**: Events with smaller months are considered smaller.
4. **Day**: Events with smaller days are considered smaller.
5. **Name**: Events are compared lexicographically by name.

### Function 2: `sort_events`

The `sort_events` function sorts an array of events in ascending order using a selection sort algorithm. The `event_cmp` function is used to determine the order of events.

#### Steps:
1. **Outer loop**: Iterates through each event in the array.
2. **Inner loop**: Finds the smallest event in the unsorted portion of the array.
3. **Swap**: Swaps the current event with the smallest event found.

---

# Task 3

## Description

The assembly function, `base64`, implements the Base64 encoding algorithm. It takes a source array of bytes and encodes it into a Base64 string, storing the result in a destination array. The function also calculates the length of the encoded string.

### Parameters:
- `source` – Pointer to the input array to be encoded.
- `n` – Number of bytes in the input array.
- `dest` – Pointer to the output array where the Base64 encoded string will be stored.
- `dest_len` – Pointer to an integer where the length of the encoded string will be stored.

### Logic

1. **Base64 alphabet**: Base64 encoding uses a predefined alphabet of 64 characters: `ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/`.
2. **Encoding process**: Input bytes are processed in groups of 3. Each group of 3 bytes is combined into a 24-bit value, which is then split into four 6-bit groups. These groups are mapped to characters in the Base64 alphabet.
3. **Output length**: The length of the encoded string is calculated and stored in the `dest_len` variable.

---

# Task 4

## Description

This task implements three assembly functions to validate the rows, columns, and 3x3 blocks of a Sudoku grid. Each function checks whether the respective section of the Sudoku grid contains all digits from 1 to 9 without repetition.

### Logic

1. **Row validation (`check_row`)**: The function validates a specific row in the Sudoku grid. A frequency array is used to track the occurrences of digits.
2. **Column validation (`check_column`)**: The function validates a specific column in the Sudoku grid. The same logic as row validation is applied, but it skips 9 elements to move to the next row.
3. **Block validation (`check_box`)**: The function validates a specific 3x3 block in the Sudoku grid. A `start_idx` array is used to determine the starting position of the block.

### Example
For a valid Sudoku grid:
```
5 3 4 | 6 7 8 | 9 1 2
6 7 2 | 1 9 5 | 3 4 8
1 9 8 | 3 4 2 | 5 6 7
------+------+------
8 5 9 | 7 6 1 | 4 2 3
4 2 6 | 8 5 3 | 7 9 1
7 1 3 | 9 2 4 | 8 5 6
------+------+------
9 6 1 | 5 3 7 | 2 8 4
2 8 7 | 4 1 9 | 6 3 5
3 4 5 | 2 8 6 | 1 7 9
```
- `check_row` will return `1` for all rows.
- `check_column` will return `1` for all columns.
- `check_box` will return `1` for all 3x3 blocks.

---

