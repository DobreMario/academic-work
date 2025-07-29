#ifndef PBM_H
#define PBM_H

typedef enum {
//PBM: format type
	P1 = 1,
	P2 = 2,
	P3 = 3,
	P4 = 4,
	P5 = 5,
	P6 = 6,
//PBM: function value
	SUCCES = 0,
	INVALID_FILE = 127,
	INVALID_PxVALUE = 226,
	MALLOC = 245,
	INVALID_FORMAT = 255,
} PBM_check;

typedef struct {
	int R, G, B;
} RGB_t;

typedef union {
//for alloc and free memory
	RGB_t **p;
//for read and write
	int **p1, **p2, **p4, **p5;
	RGB_t **p3, **p6;
//for crop and apply
	int **p_simple;
	RGB_t **p_RGB;
} PBM_t;

RGB_t **alloc_memory(const int size);

void free_memory(const int height, const int width, PBM_t photo);

void load_PBM(char *format, int *height, int *width,
			  PBM_t *photo, int *limit, const char *filename);

void save_PBM(char *format, const int height, const int width,
			  const PBM_t photo, const int limit, const char *filename,
			  const char type);

#endif //PBM_H
