#include "fisierPPM.h"

typedef struct {
	RGB_t a[4][4];
} contur_t;

void init_retea(int ***r, RGB_t **ppm, int *n, int *m);

int **alloc_retea(int n, int m);

void free_retea(int **r, int n);

contur_t init_contur(int r);
