#include "marching_squares.h"

int main(void)
{
	char comanda[101];
	scanf("%s", comanda);
	if (strcmp(comanda, "READ") == 0) {
		char s[3];
		int n, m, prag, **r = NULL;
		RGB_t **ppm = NULL;
		scanf("%s", s);
		if (strcmp(s, "P3") == 0) {
			scanf("%d%d%d", &n, &m, &prag);
			ppm = alloc_ppm(n, m);
			scanf_ppm(ppm, n, m, prag);
			printf("Imagine citita cu succes [%d-%d]\n", n, m);
			scanf("%s", comanda);
			while (strcmp(comanda, "EXIT") != 0) {
				comenzi(comanda, &r, &n, &m, &ppm, &prag);
				scanf("%s", comanda);
			}
			myfree_ppm(ppm, n);
		} else {
			printf("Eroare: trebuie sa fie P3\n");
			exit(0);
		}
	} else {
		printf("Mai intai se citeste imaginea\n");
	}
	printf("Gigel a terminat\n");
	return 0;
}
