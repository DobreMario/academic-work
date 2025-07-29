# DOBRE Mario-Sebastian - 314CA
CC=gcc
CFLAGS=-Wall -Wextra -std=c99
EXEC = tema1
VALGRIND = valgrind --leak-check=full --show-leak-kinds=all --track-origins=yes

build:
	$(CC) $(CFLAGS) linked_list.c doubly_linked_list_t.c stack.c page.c tab.c browser.c command.c main.c -o tema1

run: $(EXEC)
	$(VALGRIND) ./$(EXEC)

pack:
	zip -FSr tema1.zip README.md Makefile *.c *.h

clean:
	rm -f tema1

.PHONY: pack clean
