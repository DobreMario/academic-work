//314CA_DobreMario-Sebastian
#include <stdio.h>

#define NMAX 10001

void initializari(unsigned int vs[NMAX], unsigned int *cnt, long *HP);
// functia initializeaza jucatorul

void sortare_shields(unsigned int v[NMAX], unsigned int n);
// sortez shield-urile in oridine descrescatoare

void bossfight(unsigned int vs[NMAX], unsigned int *cnt, long *HP);
// functie pentru bossfight

int shield_folosit(unsigned int v[NMAX], unsigned int *n, unsigned int dmg);
// elimina shield-ul folosit

int main(void)
{
	unsigned int n, vs[NMAX] = {0}, cnt = 0;
	long HP;

	scanf("%ld%u", &HP, &n);

	for (unsigned int i = 0; i < n; i++) {
		initializari(vs, &cnt, &HP);
	}

	printf("Initial health points: %ld\n", HP);

	sortare_shields(vs, cnt);

	unsigned int m;

	scanf("%u", &m);

	for (unsigned int i = 0; i < m && HP > 0; i++) {
		bossfight(vs, &cnt, &HP);
	}

	if (HP > 0) {
		printf("Foe Vanquished!\n");
	} else {
		printf("You died.\n");
	}

	return 0;
}

void initializari(unsigned int vs[NMAX], unsigned int *cnt, long *HP)
{
	char tip;
	unsigned int valoare;

	scanf(" %c%u", &tip, &valoare);

	if (tip == 'H') {
		*HP += valoare;
	} else if (tip == 'S') {
		vs[(*cnt)++] = valoare;
	} else {
		printf("Invalid item type.\n");
	}
	// return;
}

void sortare_shields(unsigned int v[NMAX], unsigned int n)
{
	for (unsigned int i = 0; i < n; i++) {
		for (unsigned int j = i; j < n; j++) {
			if (v[i] < v[j]) {
				unsigned int aux = v[i];
				v[i] = v[j];
				v[j] = aux;
			}
		}
	}
}

int shield_folosit(unsigned int v[NMAX], unsigned int *n, unsigned int dmg)
{
	for (unsigned int i = 0; i < *n; i++) {
		if (v[i] <= dmg) {
			unsigned int shield = v[i];
			for (unsigned int j = i + 1; j < *n; j++) {
				v[j - 1] = v[j];
			}
			(*n)--;
			return shield;
		}
	}
	(*n)--;
	return v[*n];
}

void bossfight(unsigned int vs[NMAX], unsigned int *cnt, long *HP)
{
	unsigned int dmg, shield = 0;

	scanf("%u", &dmg);

	if (*cnt > 0) {
		shield = shield_folosit(vs, cnt, dmg);
	}
	*HP -= (shield > dmg) ? 0 : dmg - shield;
	if (*HP < 0) {
		*HP = 0;
	}

	printf("%ld", *HP);

	if (shield > 0) {
		printf(" %u\n", shield);
	} else {
		printf("\n");
	}
}
