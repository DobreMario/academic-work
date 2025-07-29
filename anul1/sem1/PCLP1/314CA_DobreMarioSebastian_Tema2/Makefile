CC=gcc
CFLAGS=-Wall -Wextra -std=c99

build:
	$(CC) $(CFLAGS) marching_squares.c fisierPPM.c marching.c comenzi.c -o marching_squares

pack:
	zip -FSr 314CA_DobreMarioSebastian_Tema2.zip README Makefile *.c *.h

clean:
	rm -f marching_squares

.PHONY: pack clean
