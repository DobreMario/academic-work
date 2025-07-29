//314CA_DobreMario-Sebastian
#include <stdio.h>

#define NMAX 1001

void initializare_leds(char a[NMAX][NMAX], short int n,
					   short int m, short int len, short int w);
// modularizare initializari

void umplere(char a[NMAX][NMAX], short int n, short int m);
// umple matricea cu 0

void aprindere_leds(char a[NMAX][NMAX], short int n, short int m,
					short int digits);
// aprinde segmentele corespunzatoare pentru fiecare digits citit

void comenzi_leds(char a[NMAX][NMAX], short int n,
				  short int m, short int len, short int w);
// jocul lui Gigel cu ledurile

void segment_a(char a[NMAX][NMAX], short int m, short int w);
// initializarea segmentului a

void segment_b(char a[NMAX][NMAX], short int n,
			   short int m, short int len, short int w);
// initializarea segmentului b

void segment_c(char a[NMAX][NMAX], short int n,
			   short int m, short int len, short int w);
// initializarea segmentului c

void segment_d(char a[NMAX][NMAX], short int n, short int m, short int w);
// initializarea segmentului d

void segment_e(char a[NMAX][NMAX], short int n, short int len, short int w);
// initializarea segmentului e

void segment_f(char a[NMAX][NMAX], short int n, short int len, short int w);
// initializarea segmentului f

void segment_g(char a[NMAX][NMAX], short int m, short int len, short int w);
// initializarea segmentului g

void printf_leds(char a[NMAX][NMAX], short int n, short int m);
// printeaza matricea de leduri

void tranzitie_sus(char a[NMAX][NMAX], short int n, short int m, short int t);
// mutare leduri in sus

void tranzitie_jos(char a[NMAX][NMAX], short int n, short int m, short int t);
// mutare leduri in jos

void tranzitie_stg(char a[NMAX][NMAX], short int n, short int m, short int t);
// mutare leduri in stanga

void tranzitie_drp(char a[NMAX][NMAX], short int n, short int m, short int t);
// mutare leduri in dreapta

void digit_0(char a[NMAX][NMAX], short int n, short int m);
// aprinde ledul in forma de 0

void digit_1(char a[NMAX][NMAX], short int n, short int m);
// aprinde ledul in forma de 1

void digit_2(char a[NMAX][NMAX], short int n, short int m);
// aprinde ledul in forma de 2

void digit_3(char a[NMAX][NMAX], short int n, short int m);
// aprinde ledul in forma de 3

void digit_4(char a[NMAX][NMAX], short int n, short int m);
// aprinde ledul in forma de 4

void digit_5(char a[NMAX][NMAX], short int n, short int m);
// aprinde ledul in forma de 5

void digit_6(char a[NMAX][NMAX], short int n, short int m);
// aprinde ledul in forma de 6

void digit_7(char a[NMAX][NMAX], short int n, short int m);
// aprinde ledul in forma de 7

void digit_8(char a[NMAX][NMAX], short int n, short int m);
// aprinde ledul in forma de 8

void digit_9(char a[NMAX][NMAX], short int n, short int m);
// aprinde ledul in forma de 9

int main(void)
{
	short int n, m, len;

	scanf("%hd%hd%hd", &n, &m, &len);

	short int w = len / 3;

	if ((2 * len + 3 * w == n) && (len + 2 * w == m)) {
		char a[NMAX][NMAX];

		comenzi_leds(a, n, m, len, w);
	} else {
		// ?? //
	}

	return 0;
}

void initializare_leds(char a[NMAX][NMAX], short int n,
					   short int m, short int len, short int w)
{
	umplere(a, n, m);

	segment_a(a, m, w);

	segment_b(a, n, m, len, w);

	segment_c(a, n, m, len, w);

	segment_d(a, n, m, w);

	segment_e(a, n, len, w);

	segment_f(a, n, len, w);

	segment_g(a, m, len, w);

	//return;
}

void umplere(char a[NMAX][NMAX], short int n, short int m)
{
	for (short int i = 0; i < n; i++) {
		for (short int j = 0; j < m; j++) {
			a[i][j] = '0';
		}
	}

	//return;
}

void segment_a(char a[NMAX][NMAX], short int m, short int w)
{
	for (short int i = 0; i < w; i++) {
		for (short int j = 0; j < m; j++) {
			if ((j >= w && j < m - w)) {
				a[i][j] = 'a';
			} else {
				a[i][j] = '0';
			}
		}
	}

	//return;
}

void segment_b(char a[NMAX][NMAX], short int n,
			   short int m, short int len, short int w)
{
	for (int i = 0; i < n - len - w; i++) {
		for (int j = w + len; j < m; j++) {
			if (i >= w && i < n - 2 * w - len) {
				a[i][j] = 'b';
			} else {
				a[i][j] = '0';
			}
		}
	}

	//return;
}

void segment_c(char a[NMAX][NMAX], short int n,
			   short int m, short int len, short int w)
{
	for (int i = n - len - w; i < n; i++) {
		for (int j = w + len; j < m; j++) {
			if (i >= n - len - w && i < n - w) {
				a[i][j] = 'c';
			} else {
				a[i][j] = '0';
			}
		}
	}

	//return;
}

void segment_d(char a[NMAX][NMAX], short int n, short int m, short int w)
{
	for (short int i = n - w; i < n; i++) {
		for (short int j = 0; j < m; j++) {
			if (j >= w && j < m - w) {
				a[i][j] = 'd';
			} else {
				a[i][j] = '0';
			}
		}
	}

	//return;
}

void segment_e(char a[NMAX][NMAX], short int n, short int len, short int w)
{
	for (int i = n - len - w; i < n; i++) {
		for (int j = 0; j < w; j++) {
			if (i >= n - len - w && i < n - w) {
				a[i][j] = 'e';
			} else {
				a[i][j] = '0';
			}
		}
	}

	//return;
}

void segment_f(char a[NMAX][NMAX], short int n, short int len, short int w)
{
	for (int i = 0; i < n - len - w; i++) {
		for (int j = 0; j < w; j++) {
			if (i >= w && i < n - 2 * w - len) {
				a[i][j] = 'f';
			} else {
				a[i][j] = '0';
			}
		}
	}

	//return;
}

void segment_g(char a[NMAX][NMAX], short int m, short int len, short int w)
{
	for (short int i = w + len; i < 2 * w + len; i++) {
		for (short int j = 0; j < m; j++) {
			if (j >= w && j < m - w) {
				a[i][j] = 'g';
			} else {
				a[i][j] = '0';
			}
		}
	}

	//return;
}

void printf_leds(char a[NMAX][NMAX], short int n, short int m)
{
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < m; j++) {
			printf("%c ", a[i][j]);
		}
		printf("\n");
	}
	printf("\n");

	//return;
}

void comenzi_leds(char a[NMAX][NMAX], short int n,
				  short int m, short int len, short int w)
{
	char c;
	long t;
	short int oX, oY;

	scanf(" %c", &c);

	while (c != 'Q') {
		if (c == 'F') {
			short int digit;
			oX = 0; oY = 0;

			scanf("%hd", &digit);

			if (0 <= digit && digit <= 9) {
				initializare_leds(a, n, m, len, w);
				aprindere_leds(a, n, m, digit);
			} else {
				printf("The input given is not a digit.\n");
			}
		} else if (c == 'W') {
			scanf("%ld", &t);

			t %= n;
			oY += t;
		} else if (c == 'A') {
			scanf("%ld", &t);

			t %= m;
			oX -= t;
		} else if (c == 'S') {
			scanf("%ld", &t);

			t %= n;
			oY -= t;
		} else if (c == 'D') {
			scanf("%ld", &t);

			t %= m;
			oX += t;
		} else if (c == 'P') {
			if (oY > 0) {
				tranzitie_sus(a, n, m, oY);
				oY = 0;
			} else if (oY < 0) {
				oY *= (-1);

				tranzitie_jos(a, n, m, oY);
				oY = 0;
			}
			if (oX > 0) {
				tranzitie_drp(a, n, m, oX);
				oX = 0;
			} else if (oX < 0) {
				oX *= (-1);
				tranzitie_stg(a, n, m, oX);
				oX = 0;
			}
			printf_leds(a, n, m);
		} else {
			short int digit;

			scanf("%hd", &digit);
			printf("Invalid command.\n");
		}

		scanf(" %c", &c);
	}

	//return;
}

void aprindere_leds(char a[NMAX][NMAX], short int n, short int m,
					short int digits)
{
	if (digits == 0) {
		digit_0(a, n, m);
	} else if (digits == 1) {
		digit_1(a, n, m);
	} else if (digits == 2) {
		digit_2(a, n, m);
	} else if (digits == 3) {
		digit_3(a, n, m);
	} else if (digits == 4) {
		digit_4(a, n, m);
	} else if (digits == 5) {
		digit_5(a, n, m);
	} else if (digits == 6) {
		digit_6(a, n, m);
	} else if (digits == 7) {
		digit_7(a, n, m);
	} else if (digits == 8) {
		digit_8(a, n, m);
	} else if (digits == 9) {
		digit_9(a, n, m);
	}

	//return;
}

void digit_0(char a[NMAX][NMAX], short int n, short int m)
{
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < m; j++) {
			if (a[i][j] == 'g' || a[i][j] == '0') {
				a[i][j] = ' ';
			} else {
				a[i][j] = '^';
			}
		}
	}

	//return;
}

void digit_1(char a[NMAX][NMAX], short int n, short int m)
{
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < m; j++) {
			if (a[i][j] == 'b' || a[i][j] == 'c') {
				a[i][j] = '^';
			} else {
				a[i][j] = ' ';
			}
		}
	}

	//return;
}

void digit_2(char a[NMAX][NMAX], short int n, short int m)
{
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < m; j++) {
			if (a[i][j] == 'f' || a[i][j] == 'c' || a[i][j] == '0') {
				a[i][j] = ' ';
			} else {
				a[i][j] = '^';
			}
		}
	}

	//return;
}

void digit_3(char a[NMAX][NMAX], short int n, short int m)
{
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < m; j++) {
			if (a[i][j] == 'f' || a[i][j] == 'e' || a[i][j] == '0') {
				a[i][j] = ' ';
			} else {
				a[i][j] = '^';
			}
		}
	}

	//return;
}

void digit_4(char a[NMAX][NMAX], short int n, short int m)
{
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < m; j++) {
			if (a[i][j] == '0') {
				a[i][j] = ' ';
			} else if (a[i][j] == 'a' || a[i][j] == 'e' || a[i][j] == 'd') {
				a[i][j] = ' ';
			} else {
				a[i][j] = '^';
			}
		}
	}

	//return;
}

void digit_5(char a[NMAX][NMAX], short int n, short int m)
{
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < m; j++) {
			if (a[i][j] == 'b' || a[i][j] == 'e' || a[i][j] == '0') {
				a[i][j] = ' ';
			} else {
				a[i][j] = '^';
			}
		}
	}

	//return;
}

void digit_6(char a[NMAX][NMAX], short int n, short int m)
{
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < m; j++) {
			if (a[i][j] == 'b' || a[i][j] == '0') {
				a[i][j] = ' ';
			} else {
				a[i][j] = '^';
			}
		}
	}

	//return;
}

void digit_7(char a[NMAX][NMAX], short int n, short int m)
{
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < m; j++) {
			if (a[i][j] == 'b' || a[i][j] == 'c' || a[i][j] == 'a') {
				a[i][j] = '^';
			} else {
				a[i][j] = ' ';
			}
		}
	}

	//return;
}

void digit_8(char a[NMAX][NMAX], short int n, short int m)
{
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < m; j++) {
			if (a[i][j] == '0') {
				a[i][j] = ' ';
			} else {
				a[i][j] = '^';
			}
		}
	}

	//return;
}

void digit_9(char a[NMAX][NMAX], short int n, short int m)
{
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < m; j++) {
			if (a[i][j] == 'e' || a[i][j] == '0') {
				a[i][j] = ' ';
			} else {
				a[i][j] = '^';
			}
		}
	}

	//return;
}

void tranzitie_sus(char a[NMAX][NMAX], short int n, short int m, short int t)
{
	while (t--) {
		for (short int j = 0; j < m; j++) {
			char cpy = a[0][j];
			for (short int i = 1; i < n; i++) {
				a[i - 1][j] = a[i][j];
			}
			a[n - 1][j] = cpy;
		}
	}

	//return;
}

void tranzitie_jos(char a[NMAX][NMAX], short int n, short int m, short int t)
{
	while (t--) {
		for (short int j = 0; j < m; j++) {
			char cpy = a[n - 1][j];
			for (short int i = n - 2; i >= 0; i--) {
				a[i + 1][j] = a[i][j];
			}
			a[0][j] = cpy;
		}
	}

	//return;
}

void tranzitie_drp(char a[NMAX][NMAX], short int n, short int m, short int t)
{
	while (t--) {
		for (short int i = 0; i < n; i++) {
			char cpy = a[i][m - 1];
			for (short int j = m - 2; j >= 0; j--) {
				a[i][j + 1] = a[i][j];
			}
			a[i][0] = cpy;
		}
	}

	//return;
}

void tranzitie_stg(char a[NMAX][NMAX], short int n, short int m, short int t)
{
	while (t--) {
		for (short int i = 0; i < n; i++) {
			char cpy = a[i][0];
			for (short int j = 1; j < m; j++) {
				a[i][j - 1] = a[i][j];
			}
			a[i][m - 1] = cpy;
		}
	}

	//return;
}
