#include <stdio.h>
#include <stdlib.h>
#include "fisierPPM.h"

RGB_t **alloc_ppm(int n, int m)
{
	RGB_t **ppm = (RGB_t **)malloc(n * sizeof(RGB_t *));
	if (!ppm) {
		printf("Eroare: Malloc a esuat:(\n)");
		exit(0);
	}
	for (int i = 0; i < n; i++) {
		ppm[i] = (RGB_t *)malloc(m * sizeof(RGB_t));
		if (!ppm[i]) {
			printf("Eroare: Malloc a esuat:(\n)");
			while (--i) {
				free(ppm[i]);
			}
			free(ppm);
			exit(0);
		}
	}

	return ppm;
}

int ver_color(RGB_t **ppm, int i, int j, int prag)
{
	if (ppm[i][j].R > prag) {
		return 1;
	} else if (ppm[i][j].G > prag) {
		return 1;
	} else if (ppm[i][j].B > prag) {
		return 1;
	}

	return 0;
}

void scanf_ppm(RGB_t **ppm, int n, int m, int prag)
{
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < m; j++) {
			if (scanf("%d", &ppm[i][j].R) != 1) {
				printf("Eroare: eroare citire pixeli\n");
				exit(0);
			}
			if (scanf("%d", &ppm[i][j].G) != 1) {
				printf("Eroare: eroare citire pixeli\n");
				exit(0);
			}
			if (scanf("%d", &ppm[i][j].B) != 1) {
				printf("Eroare: eroare citire pixeli\n");
				exit(0);
			}
			if (ver_color(ppm, i, j, prag)) {
				printf("Eroare: valoare pixel necorespunzatoare\n");
				exit(0);
			}
		}
	}
}

void printf_ppm(RGB_t **ppm, int n, int m)
{
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < m; j++) {
			printf("%d %d %d\n", ppm[i][j].R, ppm[i][j].G, ppm[i][j].B);
		}
	}
}

void myfree_ppm(RGB_t **ppm, int n)
{
	for (int i = 0; i < n; i++) {
		free(ppm[i]);
	}

	free(ppm);
}

void resize_ppm(RGB_t ***ppm, int *n, int *m)
{
	RGB_t **ppm_ex = alloc_ppm((*n) * 4, (*m) * 4);
	for (int i = 0; i < (*n); i++) {
		for (int j = 0; j < (*m); j++) {
			for (int u = 0; u < 4; u++) {
				for (int k = 0; k < 4; k++) {
					ppm_ex[i * 4 + u][j * 4 + k] = (*ppm)[i][j];
				}
			}
		}
	}
	myfree_ppm(*ppm, *n);
	(*n) *= 4;
	(*m) *= 4;
	(*ppm) = alloc_ppm((*n), (*m));
	for (int i = 0; i < (*n); i++) {
		for (int j = 0; j < (*m); j++) {
			(*ppm)[i][j] = ppm_ex[i][j];
		}
	}
	myfree_ppm(ppm_ex, *n);
}

int med_px(RGB_t **ppm, int i, int j)
{
	return (ppm[i][j].R + ppm[i][j].G + ppm[i][j].B) / 3;
}
