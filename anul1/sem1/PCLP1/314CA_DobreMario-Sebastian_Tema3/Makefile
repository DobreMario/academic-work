CC=gcc
CFLAGS=-Wall -Wextra -std=c99

build:
	$(CC) $(CFLAGS) pbm.c edit.c command.c main.c -o image_editor

pack:
	zip -FSr 314CA_DobreMario-Sebastian_Tema3.zip README Makefile *.c *.h

clean:
	rm -f image_editor

.PHONY: pack clean
