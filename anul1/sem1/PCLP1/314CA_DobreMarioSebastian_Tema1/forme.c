//314CA_DobreMario-Sebastian
#include <stdio.h>

#define MIN 1
#define MAX 50

void identificare_forma(char c); // identifica ce tip de forma este

void patrat(int d, int u); // verifica regulile din enunt si afiseaza
// patratul rotit / mesajul corespunzator

void dreptunghi(int l, int i); // verifica regulile din enunt si afiseaza
// dreptunghiul / mesajul corespunzator

void triunghi(int lc, int u); // verifica regulile din enunt si afiseaza
// triunghiul rotit / mesajul corespunzator

void cruce(int d, int u); // verifica regulile din enunt si afiseaza
// crucea rotita / mesajul corespunzator

void fereastra(int d); // verifica regulile din enunt si afiseaza
// fereastra / mesajul corespunzator

int main(void)
{
	short int n;

	scanf("%hd", &n);

	for (int i = 0; i < n; i++) {
		char c;

		scanf(" %c", &c);

		identificare_forma(c);

		if (i != n - 1) {
			printf("\n");
		}
	}

	return 0;
}

void identificare_forma(char c)
{
	if (c == 'p') {
		int d, u; // d = dimensiune, u = unghi

		scanf("%d%d", &d, &u);

		patrat(d, u);
	} else if (c == 'd') {
		int l, i; // l = latime, i = inaltime

		scanf("%d%d", &l, &i);

		dreptunghi(l, i);
	} else if (c == 't') {
		int lc, u; // lc = lungime cateta, u = unghi

		scanf("%d%d", &lc, &u);

		triunghi(lc, u);
	} else if (c == 'c') {
		int d, u; // d = dimensiune, u = unghi

		scanf("%d%d", &d, &u);

		cruce(d, u);
	} else if (c == 'f') {
		int d; // d = dimensiune

		scanf("%d", &d);

		fereastra(d);
	}
	//return;
}

void patrat(int d, int u)
{
	if (MIN <= d && d <= MAX) {
		if (u % 90 == 0) {
			for (int i = 0; i < d; i++) {
				for (int j = 0; j < d; j++) {
					printf("*");
				}
				printf("\n");
			}
		} else if (u % 45 == 0) {
			short int n = 2 * d - 1;
			for (int i = 0; i < d; i++) {
				for (int j = 0; j < n; j++) {
					if (j <= d - 1 + i && j >= d - 1 - i && i < d) {
						printf("*");
					} else {
						printf(" ");
					}
				}
				printf("\n");
			}
			for (int i = d - 2; i >= 0; i--) {
				for (int j = 0; j < n; j++) {
					if (j <= d - 1 + i && j >= d - 1 - i && i < d) {
						printf("*");
					} else {
						printf(" ");
					}
				}
				printf("\n");
			}
		} else {
			printf("Unsupported angle to display shape\n");
		}
	} else {
		printf("Unsupported size to display shape\n");
	}
}

void dreptunghi(int l, int i)
{
	if (MIN <= l && MIN <= i && l <= MAX && i <= MAX) {
		for (int k = 0; k < i; k++) {
			for (int j = 0; j < l; j++) {
				printf("*");
			}
			printf("\n");
		}
	} else {
		printf("Unsupported size to display shape\n");
	}
	//return;
}

void triunghi(int lc, int u)
{
	while (u < 0) {
		u += 360; // aduce unghiul la primul cadran
	}
	if (MIN <= lc && lc <= MAX) {
		if (u % 90 != 0) {
			printf("Unsupported angle to display shape\n");
		} else if ((u / 90) % 4 == 0) {
			for (int i = 0; i < lc; i++) {
				for (int j = 0; j <= i; j++) {
					printf("*");
				}
				printf("\n");
			}
		} else if ((u / 90) % 4 == 1) {
			for (int i = 0; i < lc; i++) {
				for (int j = 0; j < lc - i; j++) {
					printf("*");
				}
				printf("\n");
			}
		} else if ((u / 90) % 4 == 2) {
			for (int i = 0; i < lc; i++) {
				for (int j = 0; j < lc; j++) {
					if (j < i) {
						printf(" ");
					} else {
						printf("*");
					}
				}
				printf("\n");
			}
		} else if ((u / 90) % 4 == 3) {
			for (int i = 0; i < lc; i++) {
				for (int j = 0; j < lc; j++) {
					if (j < lc - i - 1) {
						printf(" ");
					} else {
						printf("*");
					}
				}
				printf("\n");
			}
		}
	} else {
		printf("Unsupported size to display shape\n");
	}
	//return;
}

void cruce(int d, int u)
{
	if (MIN <= d && d <= MAX && (d % 2)) {
		if (u % 90 == 0) {
			for (int i = 0; i < d; i++) {
				for (int j = 0; j < d; j++) {
					if (j == d / 2 || i == d / 2) {
						printf("*");
					} else {
						printf(" ");
					}
				}
				printf("\n");
			}
		} else if (u % 45 == 0) {
			for (int i = 0; i < d; i++) {
				for (int j = 0; j < d; j++) {
					if (j == i || j + i == d - 1) {
						printf("*");
					} else {
						printf(" ");
					}
				}
				printf("\n");
			}
		} else {
			printf("Unsupported angle to display shape\n");
		}
	} else {
		printf("Unsupported size to display shape\n");
	}
	//return;
}

void fereastra(int d)
{
	if (5 <= d && d <= MAX && (d % 2)) {
		for (int i = 0; i < d; i++) {
			for (int j = 0; j < d; j++) {
				if (i == 0 || j == 0) {
					printf("*");
				} else if (i == d - 1 || j == d - 1) {
					printf("*");
				} else if (i == d / 2 || j == d / 2) {
					printf("*");
				} else {
					printf(" ");
				}
			}
			printf("\n");
		}
	} else {
		printf("Unsupported size to display shape\n");
	}
	//return;
}
