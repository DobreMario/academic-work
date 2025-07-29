# Browser Core Project

> **Author**: DOBRE Mario-Sebastian  
> **Group**: 314CA

## 📌 Content
- [📄 Project Overview](#-project-overview)
- [📁 File Structure](#-file-structure)
- [⚙️ Functionalities](#️-functionalities)
- [🧠 Data Structures Used](#-data-structures-used)
- [🏗️ Command Implementation](#-command-implementation)
- [📚 API Overview](#-api-overview)
- [🔧 How to Compile](#-how-to-compile)
- [📝 Example of Input and Output](#-example-of-input-and-output)
- [🔍 Step-by-Step Explanation for Example Input](#-step-by-step-explanation-for-example-input)
- [📞 Contact](#-contact)



## 📄 Project Overview

This architecture simulates how a modern web browser manages browsing history, allowing multiple tabs to be open simultaneously and enabling navigation between pages within each tab. The system is designed to highlight the internal structure of a browser, including features such as opening, closing, and switching tabs, navigating forward and backward through pages, as well as maintaining per-tab browsing history.

---

## 📁 File Structure

### Header Files

- `linked_list.h`, `doubly_linked_list_t.h`, `stack.h`  
    ~> *Imported from lab resources. Used for core data structure implementations.*

- `command.h`  
    ~> *Functions for implementing command parsing and execution.*

- `page.h`, `tab.h`, `browser.h`  
    ~> *Define the structures and functions used to model browser components.*

- `core_browser.h`  
    ~> *Main header file that brings together all modules.*

---

## ⚙️ Functionalities

- Add and switch between tabs
- Navigate to different pages
- Go back/forward in navigation history
- Manage open pages and tabs

---

## 🧠 Data Structures Used

- **Linked Lists** – for managing tabs and pages
- **Stacks** – for implementing back/forward navigation and history
- **Custom Structs** – for pages, tabs and browser instance

---

## 🏗️ Command Implementation

All supported commands are defined in `command.h` and implemented in their
corresponding source files. The command system interprets user input and
updates the browser state accordingly.

---

## 📚 API Overview

### `command.h`

The `command.h` file defines the command system used to interpret and execute user-input commands. Each command is represented by a hashed value and is mapped to a specific function responsible for its execution.

- **`Command`**  

Enumeration representing hashed command names. Each value corresponds to a hash of a string (e.g., `"NEW_TAB"` becomes `2670`), making it harder to reverse engineer the actual command names.

#### Typedefs

- **`comm_t`**  
    Function pointer type for the command functions:
    ```c
    typedef void (*comm_t)(browser_t *, linked_list_t *, FILE*, FILE*);

## Function Parameters

Each function associated with a command receives the following parameters:

- **`browser_t *browser`**: A pointer to the browser instance.
- **`linked_list_t *pages`**: A linked list containing the stored pages 
- **`FILE *in`**: The input file stream (used for reading commands).
- **`FILE *out`**: The output file stream (used for printing results or error messages).

## Functions

### `comm_t command_translator(char line[256])`
- **Description**: This function receives a command line as input, computes the hash of that command, and returns a pointer to the corresponding function that will execute that command.

### `void new_tab(...)`
- **Description**: Creates a new tab in the browser. The new tab becomes the current tab, and its ID is calculated by incrementing the ID of the last tab in the list. The tab is set to the default page.

### `void close(...)`
- **Description**: Closes the current tab and updates the browser to set the tab to the left of the closed one as the current tab. Closing the tab with ID 0 (the default tab) is not allowed, and in case of an error, an appropriate error message will be displayed.

### `void open(...)`
- **Description**: Opens a tab with a specified ID. If the tab with that ID does not exist, an error message will be displayed.

### `void next(...)`
- **Description**: Moves the current tab to the next tab in the list of tabs. If there is a tab after the current one, it becomes active.

### `void prev(...)`
- **Description**: Moves the current tab to the previous tab in the list of tabs. If there is a tab before the current one, it becomes active.

### `void page(...)`
- **Description**: Opens a page with a specified ID in the current tab. If the page does not exist, an error message will be displayed. The current page is saved in the "Backward" stack, and the "Forward" stack is emptied.

### `void backward(...)`
- **Description**: Accesses the last page from the "Backward" stack for the current tab. The current page is saved in the "Forward" stack. If the "Backward" stack is empty, an error message will be displayed.

### `void forward(...)`
- **Description**: Accesses the last page from the "Forward" stack for the current tab. The current page is saved in the "Backward" stack. If the "Forward" stack is empty, an error message will be displayed.

### `void print(...)`
- **Description**: Prints the IDs of all open tabs in the browser, starting from the current tab and continuing to the right. Additionally, it prints the description of the current page for the current tab on a new line.

### `void print_history(...)`
- **Description**: Prints the browsing history for a tab with the specified ID. The history is displayed in the following order: the "Forward" stack (from first to last added), the current page, and the "Backward" stack (from last to first added). If the tab does not exist, an error message will be displayed.

---

### `browser.h`

Defines the main `browser_t` structure and the functions used to
initialize and destroy the browser instance.

#### Structures

- `browser_t`  
    Represents the browser instance.
    - `tab_t *santinela` – Sentinel tab, used as a marker (`id = max_id`), otherwise `NULL`.
    - `tab_t *current` – Pointer to the currently active tab.
    - `doubly_linked_list_t *tab_list` – List of all tabs.

#### Functions

- `browser_t *initBrowser(page_t *page)`  
    Initializes a browser with a given page loaded in the first tab. Allocates memory and sets default state.

- `void freeBrowser(browser_t *browser)`  
    Frees all memory associated with the browser, including its tabs and loaded pages.

---

### `tab.h`

Defines the `tab_t` structure and the functions used to create, manage, and destroy browser tabs.

#### Structures

- **`tab_t`**  
  Represents a single tab in the browser.
  - `int id` – The unique identifier for the tab.
  - `page_t *currentPage` – Pointer to the current page loaded in the tab.
  - `stack_t *backwardStack` – Stack for navigating backward in the page history.
  - `stack_t *forwardStack` – Stack for navigating forward in the page history.

#### Functions

- **`tab_t *createTab(int id, page_t *page)`**  
  Creates a new tab with a given `id` and loads the specified `page` in it. This tab will have an initial backward and forward navigation stack.

- **`void printTab(tab_t *tab, FILE *out)`**  
  Prints information about the given tab, including the tab's ID and the current page loaded in that tab.

- **`void printTabs(doubly_linked_list_t *t, FILE *out)`**  
  Prints the list of all tabs in the browser, starting from the current tab and moving to the right.

- **`void freeTab(tab_t *tab)`**  
  Frees the memory associated with a single tab, including its page and navigation stacks.

- **`void freeTabList(doubly_linked_list_t *t)`**  
  Frees the memory of all tabs in the browser by iterating over the list of tabs and calling `freeTab` for each one.

---

### `page.h`

Defines the `page_t` structure and the functions used to manage pages in the browser.

#### Defines

- **`__FIRST_ID__`**  
  Defines the ID of the first page (initial page). Set to `0`.

- **`__FIRST_URL__`**  
  Defines the URL of the first page. Set to `"https://acs.pub.ro/"`.

- **`__FIRST_DESCRIPTION__`**  
  Defines the description of the first page. Set to `"Computer Science"`.

#### Structures

- **`page_t`**  
  Represents a single page in the browser.
  - `unsigned int id` – Unique identifier for the page.
  - `char url[50]` – The URL of the page.
  - `char *description` – A description of the page.

#### Functions

- **`page_t *initPage(unsigned int id, char url[50], char *description)`**  
  Initializes a page with the specified ID, URL, and description.

- **`linked_list_t *arrayPage(unsigned int n, FILE *in)`**  
  Reads `n` pages from the input file and stores them in a linked list.

- **`void printPage(page_t *page, FILE *in)`**  
  Prints the information of a single page, including its ID, URL, and description.

- **`void printPages(linked_list_t *p, FILE *in)`**  
  Prints the list of all pages in the linked list.

- **`void freePage(void *data)`**  
  Frees the memory allocated for a single page.

- **`void freePages(linked_list_t *p)`**  
  Frees the memory of all pages in the linked list.

---

### `main.c`

Acts as the entry point for the browser simulation. This file is responsible for initializing the environment, processing input commands, and managing the overall program flow.

#### Responsibilities

- Opens input and output files: `tema1.in` and `tema1.out`.
- Loads an initial set of pages using `arrayPage()`.
- Initializes the browser with the default page.
- Reads and executes a number of commands defined in the input file.
- Routes each command through `command_translator()` to resolve and execute the appropriate function.
- Manages proper memory deallocation and file cleanup.

#### Execution Flow

1. Opens input/output files with error handling.
2. Reads the number of pages (`n`) and loads them into a `linked_list_t` using `arrayPage()`.
3. Initializes a `browser_t` instance using the first page in the list.
4. Reads the number of commands (`m`) and executes them one by one:
   - Reads each command keyword.
   - Translates the keyword into a function pointer using `command_translator()`.
   - Executes the command with `browser`, `page_list`, `in`, and `out` as arguments.
5. Cleans up memory and closes file descriptors.

#### Key Functions Used

- `arrayPage(int n, FILE *in)`  
    Reads `n` pages from input and stores them in a linked list.

- `initBrowser(page_t *page)`  
    Initializes a browser instance with a given page loaded into the first tab.

- `command_translator(char line[256])`  
    Maps a string command to its respective function pointer.

- Command execution functions from `command.h`, such as:
    - `new_tab()`, `close()`, `open()`, `page()`
    - `next()`, `prev()`, `backward()`, `forward()`
    - `print()`, `print_history()`

- `freeBrowser(browser_t *browser)`  
    Frees all memory used by the browser and its associated tabs/pages.

- `freePages(linked_list_t *p)`  
    Frees memory used by the list of pages.

---

## 👑 `mem_support.h` — The Guardian of Memory in Critical Moments

The **`mem_support.h`** header acts as an emergency safeguard for memory management during the execution of the program. Its main function, `mem_support()`, is designed to be called **mid-execution** whenever a critical failure occurs — such as a failed dynamic memory allocation — to ensure the program exits cleanly without memory leaks:

- Frees the main `browser_t` structure, if it has already been allocated
- Deallocates the `linked_list_t` page list, if it exists
- Closes any open input/output files (`FILE *in`, `FILE *out`)
- Exits the program gracefully using `exit(0)`

This function provides a **controlled and safe exit strategy** for exceptional scenarios, preventing the program from continuing in an invalid or unstable state. It's a critical tool for robust and defensive programming.

> 👑 “It’s not a game over — it’s a clean exit.”

---

## 🔧 How to Compile

```bash
make
```
---

## 📝 Example of Input and Output

### Input

```bash
4
1
https://www.stanford.edu/
Stanford University
2
https://www.ox.ac.uk/
University of Oxford
3
https://www.harvard.edu/
Harvard University
4
https://www.mit.edu/
MIT
20
PAGE 1
PRINT
NEW_TAB
PAGE 2
PRINT
NEXT
PRINT
CLOSE
PRINT
NEW_TAB
PAGE 3
BACKWARD
PRINT
FORWARD
PRINT
OPEN 0
PRINT
NEXT
PRINT
PRINT_HISTORY 0
```

### Expected Output

```bash
0 
Stanford University
1 0 
University of Oxford
0 1 
Stanford University
403 Forbidden
0 1 
Stanford University
2 0 1 
Computer Science
2 0 1 
Harvard University
0 1 2 
Stanford University
1 2 0 
University of Oxford
https://www.stanford.edu/
https://acs.pub.ro/
```
---

## 🔍 Step-by-Step Explanation for Example Input

### `PAGE 1`  
Opens the page with ID `1` (`Stanford University`) in the current tab (`Tab 0`).  
The initial page (`Computer Science`) is saved to the **Backward** stack.

---

### `PRINT`  
Displays the current state of the browser tabs and the current page in `Tab 0`.

---

### `NEW_TAB`  
Creates a new tab (`Tab 1`) which becomes the active one.  
The new tab is initialized with the default page (`Computer Science`).

---

### `PAGE 2`  
Opens the page with ID `2` (`University of Oxford`) in `Tab 1`.  
The previous page is saved in the **Backward** stack.

---

### `PRINT`  
Prints the active tabs and the current page in `Tab 1`.

---

### `NEXT`  
Moves focus to the next tab in the list (wraps around to `Tab 0`).

---

### `PRINT`  
Prints the state of the browser with `Tab 0` as the active tab.

---

### `CLOSE`  
Attempts to close the current tab (`Tab 0`).  
If it cannot be closed (e.g., it's the only tab or default one), an error message is shown.  
Otherwise, focus shifts to a neighboring tab.

---

### `PRINT`  
Shows the updated tab state after attempting to close `Tab 0`.

---

### `NEW_TAB`  
Creates `Tab 2`, sets it as the current tab, and loads the default page.

---

### `PAGE 3`  
Loads the page with ID `3` (`Harvard University`) into `Tab 2`.  
The current page is pushed to the **Backward** stack.

---

### `BACKWARD`  
Navigates back to the previous page in `Tab 2` (`Computer Science`).  
`Harvard University` is pushed to the **Forward** stack.

---

### `PRINT`  
Displays the current tab list and the active page after going back.

---

### `FORWARD`  
Moves forward in the history to `Harvard University`.

---

### `PRINT`  
Prints the current page and active tab after forward navigation.

---

### `OPEN 0`  
Switches focus to the tab with ID `0` if it exists.

---

### `PRINT`  
Shows the state of the browser with `Tab 0` now active.

---

### `NEXT`  
Moves focus to the next tab (`Tab 1`).

---

### `PRINT`  
Displays the active tab (`Tab 1`) and the corresponding page.

---

### `PRINT_HISTORY 0`  
Prints the browsing history of `Tab 0`.  
It includes:
- Forward stack (displayed from bottom to top)
- Current page
- Backward stack (displayed in logical order, from the most recent to the oldest)

---


## 📞 Contact

If you have any questions, suggestions, or would like to report issues related to this project, feel free to get in touch!

- 📧 **Email**: mario.dobre@stud.acs.upb.ro

---

## 🙌 Feedback

This project was a great opportunity to apply concepts related to dynamic data structures and memory management in C. I welcome any feedback regarding:

- 🧠 **Implementation logic** and functional correctness  
- 🧹 **Code structure and readability** (naming, organization, modularity)  
- 🧮 **Dynamic memory management** – whether there are more efficient or safer ways to handle allocation/deallocation in certain parts of the project  

I'm open to suggestions and improvements, whether they're about coding style, performance optimization, or overall clarity.

Thank you! 🐴 _(Certified Pony Lover)_

---
