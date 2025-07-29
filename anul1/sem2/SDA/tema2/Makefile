# DOBRE Mario-Sebastian - 314CA
CC=gcc
CFLAGS=-Wall -Wextra -std=c99
EXEC = tema1
VALGRIND = valgrind --leak-check=full --show-leak-kinds=all --track-origins=yes

build:
	$(CC) $(CFLAGS) linked_list.c stack.c queue.c min-heap.c binary-tree.c satellite.c commands.c main.c -o tema2

run: $(EXEC)
	$(VALGRIND) ./$(EXEC)

pack:
	zip -FSr tema2.zip README.md Makefile *.c *.h

clean:
	rm -f tema2

.PHONY: pack clean
