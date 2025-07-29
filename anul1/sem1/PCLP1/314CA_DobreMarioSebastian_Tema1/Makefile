# Copyright 2020 Darius Neatu <neatudarius@gmail.com>

# compiler setup
CC=gcc
CFLAGS=-Wall -Wextra -std=c99

# define targets
TARGETS=jocuri_societate forme circuits segment_display

build: $(TARGETS)

jocuri_societate: jocuri_societate.c
	$(CC) $(CFLAGS) jocuri_societate.c -o jocuri_societate

forme: forme.c
	$(CC) $(CFLAGS) forme.c -o forme

circuits: circuits.c
	$(CC) $(CFLAGS) circuits.c -o circuits

segment_display: segment_display.c
	$(CC) $(CFLAGS) segment_display.c -o segment_display

pack:
	zip -FSr 314CA_DobreMarioSebastian_Tema1.zip README Makefile *.c

clean:
	rm -f $(TARGETS)

.PHONY: pack clean
