//314CA_DobreMario-Sebastian
#include <stdio.h>

#define NMAX 1001
#define ERROR 0.00000001
// penru a calcula cu o precizie de 8 zecimale

void validare_lege(char l[3]);
// verifica daca legea este valida

void legeaI(unsigned int n, unsigned int w);
// legea I a lui Kirchhoff

void legea_II(unsigned int n, unsigned int w);
// legea II a lui Kirchhoff

int ver_egal(double a, double b);
// operatia modul verifica daca cele doua numere sunt egale

int main(void)
{
	char l[3];// initializarea legei

	scanf("%s", l);// citire lege

	validare_lege(l);

	return 0;
}

void validare_lege(char l[3])
{
	if (l[0] == 'I' && l[1] == '\0') {
		unsigned int n, w;

		scanf("%d%d", &n, &w);// n = numar noduri, w = numar fire folosite

		legeaI(n, w);

		return;
	} else if (l[0] == 'I' && l[1] == 'I' && l[2] == '\0') {
		unsigned int n, w;

		scanf("%d%d", &n, &w);//n = numar noduri, w = numar fire

		legea_II(n, w);

		return;
	}

	printf("Legile existente sunt doar Legea 1 si Legea a 2-a");

	//return;
}

void legeaI(unsigned int n, unsigned int w)
{
	double v_in[NMAX] = {0}, v_out[NMAX] = {0};
	double ver_in[NMAX] = {0}, ver_out[NMAX] = {0};
	// v_in si v_out reprezinta sumele de intensitati din legea lui Kirchhoff,
	//iar ver_in si ver_out sunt vectori de verificare a nodurilor 'deschise'
	for (unsigned int i = 0; i < w; i++) {
		unsigned int n_in, n_out;
		// n_in = nodul de intare, n_out = nodul de iesire
		double c;
		// curentul prin fir

		scanf("%d%d%lf", &n_in, &n_out, &c);

		if (c > 1.0 * 0) {
			v_in[n_in] += c;
			v_out[n_out] += c;
			ver_in[n_in] = 1;
			ver_out[n_out] = 1;
			// daca curentul este pozitiv atunci curentul circula n_in --> n_out
		} else {
			v_in[n_in] += c;
			v_out[n_out] += c;
			ver_in[n_out] = 1;
			ver_out[n_in] = 1;
			// daca curentul este negativ atunci curentul circula n_out --> n_in
		}
	}
	for (unsigned int i = 1; i <= n; i++) {
		if (!ver_in[i] || !ver_out[i]) {
			printf("Circuitul este deschis in nodul %d.", i);
			return;
	}
	}
	for (unsigned int i = 1; i <= n; i++) {
		if (!ver_egal(v_in[i], v_out[i])) {
			printf("Legea 1 a lui Kirchhoff nu se respecta pentru egalitatea");
			printf(" %.9fA = %.9fA in nodul %d.", v_out[i], v_in[i], i);
			return;
		}
	}
	printf("Legea 1 a lui Kirchhoff se respecta pentru circuitul dat.");
	//return;
}

void legea_II(unsigned int n, unsigned int w)
{
	double vc = 0, vs = 0;
	// vc si vs reprezinta suma celor doua tensiuni din legea lui Kirchhoff
	for (unsigned int i = 1; i <= w; i++) {
		unsigned int n_in, n_out, nr_c;
		// n_in = nodul de intare, n_out = nodul de iesire
		//nr_c = numar componete
		double cpf;
		// cpf = curent prin fir

		scanf("%d%d%lf%d", &n_in, &n_out, &cpf, &nr_c);

		if (1 <= n_in && n_in <= n && 1 <= n_out && n_out <= n) {
			for (unsigned int j = 0; j < nr_c; j++) {
				char comp; // comp = simbol componeta
				double val; // valoarea componentei

				scanf(" %c%lf", &comp, &val);

				if (comp == 'R' && val > 1.0 * 0) {
					vc += val * cpf;
					// rezintenta nu poate sa aiba o valoare negativa
				} else if (comp == 'E') {
					if (val > 1.0 * 0) {
						vs += val;
						// tensiunea nu poate sa aiba o valoare negativa
					} else {
						printf("Sursa de tensiune nu poate fi negativa.\n");
						return;
					}
				} else {
					printf("Componenta dorita nu exista.\n");
				}
			}
		}
	}
	if (ver_egal(vc, vs)) {
		printf("Legea a 2-a lui Kirchhoff se respecta pentru circuitul dat.\n");
		return;
	}

	printf("Legea a 2-a lui Kirchhoff nu se respecta pentru egalitatea");
	printf(" %.9lfV = %.9lfV.\n", vc, vs);

	//return;
}

int ver_egal(double a, double b)
{
	if (a > b) {
		double dif = a - b;
		return dif <= ERROR;
	}
	double dif = b - a;
	return dif <= ERROR;
	// stuctura de decizie simuleaza comparatia |a-b| <= ERROR
}
