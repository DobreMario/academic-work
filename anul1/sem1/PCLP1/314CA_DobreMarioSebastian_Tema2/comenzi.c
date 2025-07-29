#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "marching.h"

void comenzi(char comanda[101], int ***r, int *n, int *m,
			 RGB_t ***ppm, int *prag)
{
	if (strcmp(comanda, "READ") == 0) {
		myfree_ppm(*ppm, *n);

		char s[3];
		scanf("%s", s);

		if (strcmp(s, "P3") == 0) {
			scanf("%d%d%d", &(*n), &(*m), &(*prag));

			*ppm = alloc_ppm(*n, *m);
			scanf_ppm(*ppm, *n, *m, *prag);

			printf("Imagine citita cu succes [%d-%d]\n", *n, *m);
		} else {
			printf("Eroare: trebuie sa fie P3\n");
			exit(0);
		}
	} else if (strcmp(comanda, "RESIZE") == 0) {
		resize_ppm(&(*ppm), &(*n), &(*m));

		printf("Imagine redimensionata cu succes [%d-%d]\n", *n, *m);
	} else if (strcmp(comanda, "WRITE") == 0) {
		printf("P3\n%d %d\n%d\n", *n, *m, *prag);

		printf_ppm(*ppm, *n, *m);
	} else if (strcmp(comanda, "GRID") == 0) {
		int rn = (*n), rm = (*m);
		init_retea(&(*r), *ppm, &rn, &rm);

		printf("Grid calculat cu succes [%d-%d]\n", rn - 1, rm - 1);

		for (int i = 0; i < rn - 1; i++) {
			for (int j = 0; j < rm - 1; j++) {
				printf("%d ", (*r)[i][j]);
			}
			printf("\n");
		}
		free_retea(*r, (*n) / 4 + 1);
	} else if (strcmp(comanda, "INIT_CONTUR") == 0) {
		for (int i = 0; i < 16; i++) {
			contur_t con = init_contur(i);

			printf("P3\n4 4\n%d\n", *prag);

			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 4; k++) {
					printf("%d %d ", con.a[j][k].R, con.a[j][k].G);
					printf("%d\n", con.a[j][k].B);
				}
			}
		}
	} else if (strcmp(comanda, "MARCH") == 0) {
		printf("Marching Squares aplicat cu succes [%d-%d]\n", *n, *m);

		int rn = (*n), rm = (*m);
		init_retea(&(*r), *ppm, &rn, &rm);

		for (int i = 0; i < (*n) / 4; i++) {
			for (int j = 0; j < (*m) / 4; j++) {
				int nr = (*r)[i][j] * 8 + (*r)[i][j + 1] * 4;
				nr += (*r)[i + 1][j + 1] * 2 + (*r)[i + 1][j];

				contur_t con = init_contur(nr);

				for (int u = 0; u < 4; u++) {
					for (int k = 0; k < 4; k++) {
						(*ppm)[i * 4 + u][j * 4 + k] = con.a[u][k];
					}
				}
			}
		}

		free_retea(*r, (*n) / 4 + 1);
	}
}
