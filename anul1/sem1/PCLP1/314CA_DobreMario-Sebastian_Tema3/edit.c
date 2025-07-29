#include "mylibraries.h"

//support functions
int identifyformat_for_editing(const char *format)
{
	if (!strcmp(format, "P1") || !strcmp(format, "P4") ||
		!strcmp(format, "P2") || !strcmp(format, "P5")) {
		return P1245;
	} else if (!strcmp(format, "P3") || !strcmp(format, "P6")) {
		return P36;
	}

	return INVALID_FORMAT;
}

void px_correction(int *px, int limit)
{
	if (*px < 0) {
		*px = 0;
	} else if (*px > limit) {
		*px = limit;
	}
}

int aprox(double num)
{
	num *= 10;
	int nr = (int)num;
	if (nr % 10 < 5) {
		return nr / 10;
	}

	return (nr / 10 + 1);
}

void myswap(int *num1, int *num2)
{
	int aux = *num1;
	*num1 = *num2;
	*num2 =  aux;
}

void init_h(PBM_t photo, int h[256], int height, int width)
{
	for (int i = 0; i < height; i++) {
		for (int j = 0; j < width; j++) {
			h[photo.p_simple[i][j]]++;
		}
	}
}

//selection functions
int check_coordonate(int coordonate, int poz, int height, int width)
{
	if ((0 > coordonate || coordonate > height) && poz) {
		return 0;
	} else if ((0 > coordonate || coordonate > width) && !poz) {
		return 0;
	}

	return 1;
}

int check_selection(int *coordonates, int height, int width)
{
	for (int i = 0; i < 4; i++) {
		if (!check_coordonate(coordonates[i], (i % 2), height, width)) {
			return 0;
		}
	}

	if (coordonates[0] == coordonates[2] || coordonates[1] == coordonates[3]) {
		return 0;
	}

	return 1;
}

void select(PBM_t photo, int height, int width, int *x, int *y)
{
	char s[256];
	fgets(s, 256, stdin);
	s[strlen(s) - 1] = '\0';

	char cpy[256];
	strcpy(cpy, s + 1);
	strcpy(s, cpy);

	if (!photo.p) {
		fprintf(stdout, "No image loaded\n");
	} else {
		if (strstr(s, "ALL")) {
			x[0] = 0;
			x[1] = width;
			y[0] = 0;
			y[1] = height;
			fprintf(stdout, "Selected ALL\n");
		} else if ((s[0] < '0' || '9' < s[0]) && s[0] != '-') {
			printf("Invalid command\n");
		} else {
			int n[4] = {0};
			char *p = strtok(s, " ");
			int cnt = 0;

			while (p) {
				int negative = 0;
				if (p[0] == '-') {
					p++;
					negative = 1;
				}

				int nr = (int)strlen(p);
				for (int i = 0; i < nr; i++) {
					if (p[i] < '0' || '9' < p[i]) {
						printf("Invalid command\n");
						return;
					}
					n[cnt] = n[cnt] * 10 + p[i] - '0';
				}

				if (negative) {
					n[cnt] *= -1;
				}

				cnt++;
				p = strtok(NULL, " ");
			}

			if (cnt < 4) {
				printf("Invalid command\n");
				return;
			}

			if (n[0] > n[2]) {
				int aux = n[0];
				n[0] = n[2];
				n[2] = aux;
			}
			if (n[1] > n[3]) {
				int aux = n[1];
				n[1] = n[3];
				n[3] = aux;
			}

			if (check_selection(n, height, width)) {
				x[0] = n[0];
				x[1] = n[2];
				y[0] = n[1];
				y[1] = n[3];
				fprintf(stdout, "Selected %d %d %d %d\n", x[0], y[0],
						x[1], y[1]);
			} else {
				fprintf(stdout, "Invalid set of coordinates\n");
			}
		}
	}
}

//crop functions
void crop_for_p36(PBM_t *photo, int *height, int *width, int *x, int *y,
				  PBM_t cropped, int newheight, int newidth)
{
	for (int i = 0; i < newheight; i++) {
		for (int j = 0; j < newidth; j++) {
			cropped.p_RGB[i][j].R = photo->p_RGB[y[0] + i][x[0] + j].R;
			cropped.p_RGB[i][j].G = photo->p_RGB[y[0] + i][x[0] + j].G;
			cropped.p_RGB[i][j].B = photo->p_RGB[y[0] + i][x[0] + j].B;
		}
	}
	free_memory(*height, *width, *photo);
	x[0] = 0;
	y[0] = 0;
	y[1] = newheight;
	x[1] = newidth;
	*height = newheight;
	*width = newidth;
	photo->p_RGB = cropped.p_RGB;
}

void crop_for_p1245(PBM_t *photo, int *height, int *width, int *x, int *y,
					PBM_t cropped, int newheight, int newidth)
{
	for (int i = 0; i < newheight; i++) {
		for (int j = 0; j < newidth; j++) {
			cropped.p_simple[i][j] = photo->p_simple[y[0] + i][x[0] + j];
		}
	}
	free_memory(*height, *width, *photo);
	x[0] = 0;
	y[0] = 0;
	y[1] = newheight;
	x[1] = newidth;
	*height = newheight;
	*width = newidth;
	photo->p_simple = cropped.p_simple;
}

void crop(PBM_t *photo, int *height, int *width, int *x, int *y,
		  char *format)
{
	if (!(*photo).p) {
		fprintf(stdout, "No image loaded\n");
	} else if ((x[1] ==  *width) && (y[1] == *height) &&
			   (x[0] ==  0) && (y[0] == 0)) {
		fprintf(stdout, "Image cropped\n");
	} else {
		PBM_t cropped;
		cropped.p = NULL;
		int newheight = y[1] - y[0];
		int newidth = x[1] - x[0];
		int size = ((newheight > newidth) ? newheight : newidth);
		cropped.p = alloc_memory(size);

		if (identifyformat_for_editing(format)) {
			crop_for_p36(photo, height, width, x, y,
						 cropped, newheight, newidth);
		} else {
			crop_for_p1245(photo, height, width, x, y,
						   cropped, newheight, newidth);
		}

		fprintf(stdout, "Image cropped\n");
	}
}

//apply functions
int check_apply(char *s)
{
	if (!strcmp(s, "EDGE")) {
		return MYEDGE;
	} else if (!strcmp(s, "SHARPEN")) {
		return SHARPEN;
	} else if (!strcmp(s, "BLUR")) {
		return BLUR;
	} else if (!strcmp(s, "GAUSSIAN_BLUR")) {
		return GAUSSIAN_BLUR;
	} else {
		return INVALID_PARAMETER;
	}
}

void supportmatrix(RGB_t **p_RGB, RGB_t tmp[3][3], int i, int j)
{
	for (int u = 0; u < 3; u++) {
		for (int k = 0 ; k < 3; k++) {
			tmp[u][k] = p_RGB[i - 1 + u][j - 1 + k];
		}
	}
}

RGB_t newpixelvalue(RGB_t **p_RGB, int i, int j, int ker[3][3])
{
	RGB_t tmp[3][3] = {0};
	RGB_t sum = {0, 0, 0};
	supportmatrix(p_RGB, tmp, i, j);
	for (int u = 0; u < 3; u++) {
		for (int k = 0; k < 3; k++) {
			sum.R += ker[u][k] * tmp[u][k].R;
			sum.G += ker[u][k] * tmp[u][k].G;
			sum.B += ker[u][k] * tmp[u][k].B;
		}
	}

	return sum;
}

void setmp_var(int *tmpheight, int *tmpwidth, int *x, int *y,
			   PBM_t *edited)
{
	*tmpheight = y[1] - y[0];
	*tmpwidth = x[1] - x[0];
	int size = ((*tmpheight > *tmpwidth) ? *tmpheight : *tmpwidth);
	edited->p_RGB = alloc_memory(size);
}

void setpx(PBM_t edited, RGB_t **p_RGB, int *x, int *y,
		   int height, int width, int ker[3][3],
		   int tmpheight, int tmpwidth, int limit)
{
	for (int i = 0; i < tmpheight; i++) {
		for (int j = 0; j < tmpwidth; j++) {
			int RGB_i = y[0] + i, RGB_j = x[0] + j;
			if (RGB_i == 0 || RGB_j == 0 ||
				RGB_i == height - 1 || RGB_j == width - 1) {
				edited.p_RGB[i][j] = p_RGB[RGB_i][RGB_j];
			} else {
				edited.p_RGB[i][j] = newpixelvalue(p_RGB, RGB_i, RGB_j, ker);
			}

			px_correction(&edited.p_RGB[i][j].R, limit);
			px_correction(&edited.p_RGB[i][j].G, limit);
			px_correction(&edited.p_RGB[i][j].B, limit);
		}
	}

}

void setpx_blur(PBM_t edited, RGB_t **p_RGB, int *x, int *y,
				int height, int width, int ker[3][3],
				int tmpheight, int tmpwidth, int limit,
				double blur_const)
{
	for (int i = 0; i < tmpheight; i++) {
		for (int j = 0; j < tmpwidth; j++) {
			int RGB_i = y[0] + i, RGB_j = x[0] + j;
			if (RGB_i == 0 || RGB_j == 0 ||
				RGB_i == height - 1 || RGB_j == width - 1) {
				edited.p_RGB[i][j] = p_RGB[RGB_i][RGB_j];
			} else {
				edited.p_RGB[i][j] = newpixelvalue(p_RGB, RGB_i, RGB_j, ker);

				double r = (double)edited.p_RGB[i][j].R * blur_const;
				double g = (double)edited.p_RGB[i][j].G * blur_const;
				double b = (double)edited.p_RGB[i][j].B * blur_const;

				edited.p_RGB[i][j].R = aprox(r);
				edited.p_RGB[i][j].G = aprox(g);
				edited.p_RGB[i][j].B = aprox(b);
			}

			px_correction(&edited.p_RGB[i][j].R, limit);
			px_correction(&edited.p_RGB[i][j].G, limit);
			px_correction(&edited.p_RGB[i][j].B, limit);
		}
	}

}

void copypx(PBM_t edited, RGB_t **p_RGB, int tmpheight, int tmpwidth,
			int *x, int *y)
{
	for (int i = 0; i < tmpheight; i++) {
		for (int j = 0; j < tmpwidth; j++) {
			int RGB_i = y[0] + i, RGB_j = x[0] + j;
			p_RGB[RGB_i][RGB_j] = edited.p_RGB[i][j];
		}
	}
}

void edge(RGB_t **p_RGB, int height, int width,
		  int *x, int *y, int limit)
{
	int ker[3][3] = EDGE_INIT;
	PBM_t edged;
	int tmpheight, tmpwidth;
	setmp_var(&tmpheight, &tmpwidth, x, y, &edged);

	setpx(edged, p_RGB, x, y, height, width, ker,
		  tmpheight, tmpwidth, limit);

	copypx(edged, p_RGB, tmpheight, tmpwidth, x, y);

	free_memory(tmpheight, tmpwidth, edged);

	fprintf(stdout, "APPLY EDGE done\n");
}

void sharpen(RGB_t **p_RGB, int height, int width,
			 int *x, int *y, int limit)
{
	int ker[3][3] = SHARPEN_INIT;
	PBM_t sharped;
	int tmpheight, tmpwidth;
	setmp_var(&tmpheight, &tmpwidth, x, y, &sharped);

	setpx(sharped, p_RGB, x, y, height, width, ker,
		  tmpheight, tmpwidth, limit);

	copypx(sharped, p_RGB, tmpheight, tmpwidth, x, y);

	free_memory(tmpheight, tmpwidth, sharped);

	fprintf(stdout, "APPLY SHARPEN done\n");
}

void blur(RGB_t **p_RGB, int height, int width,
		  int *x, int *y, int limit)
{
	int ker[3][3] = BOX_BLUR_INIT;
	double ker_const = BOX_BLUR_INIT_CONST;
	PBM_t blured;
	int tmpheight, tmpwidth;
	setmp_var(&tmpheight, &tmpwidth, x, y, &blured);

	setpx_blur(blured, p_RGB, x, y, height, width, ker,
			   tmpheight, tmpwidth, limit, ker_const);

	copypx(blured, p_RGB, tmpheight, tmpwidth, x, y);

	free_memory(tmpheight, tmpwidth, blured);

	fprintf(stdout, "APPLY BLUR done\n");
}

void gaussian_blur(RGB_t **p_RGB, int height, int width,
				   int *x, int *y, int limit)
{
	int ker[3][3] = GAUSSIAN_BLUR_INIT;
	double ker_const = GAUSSIAN_BLUR_CONST;
	PBM_t gaussianed;
	int tmpheight, tmpwidth;
	setmp_var(&tmpheight, &tmpwidth, x, y, &gaussianed);

	setpx_blur(gaussianed, p_RGB, x, y, height, width, ker,
			   tmpheight, tmpwidth, limit, ker_const);

	copypx(gaussianed, p_RGB, tmpheight, tmpwidth, x, y);

	free_memory(tmpheight, tmpwidth, gaussianed);

	fprintf(stdout, "APPLY GAUSSIAN_BLUR done\n");
}

void apply(PBM_t *photo, int height, int width, int *x, int *y,
		   char *format, int limit)
{
	char s[256];
	fgets(s, 256, stdin);
	s[strlen(s) - 1] = '\0';

	char cpy[256];
	strcpy(cpy, s + 1);
	strcpy(s, cpy);

	if (!(*photo).p) {
		fprintf(stdout, "No image loaded\n");
	} else {
		int par_nr = check_apply(s);
		switch (par_nr) {
		case 66:
			if (identifyformat_for_editing(format)) {
				blur(photo->p, height, width, x, y, limit);
			} else {
				fprintf(stdout, "Easy, Charlie Chaplin\n");
			}
			break;
		case 69:
			if (identifyformat_for_editing(format)) {
				edge(photo->p, height, width, x, y, limit);
			} else {
				fprintf(stdout, "Easy, Charlie Chaplin\n");
			}
			break;
		case 71:
			if (identifyformat_for_editing(format)) {
				gaussian_blur(photo->p, height, width, x, y, limit);
			} else {
				fprintf(stdout, "Easy, Charlie Chaplin\n");
			}
			break;
		case 83:
			if (identifyformat_for_editing(format)) {
				sharpen(photo->p, height, width, x, y, limit);
			} else {
				fprintf(stdout, "Easy, Charlie Chaplin\n");
			}
			break;
		default:
			if (strlen(s) > 0) {
				fprintf(stdout, "APPLY parameter invalid\n");
			} else {
				fprintf(stdout, "Invalid command\n");
			}
			break;
		}
	}
}

//equalize functions
void equalize_px(int h[256], double area)
{
	int sum[256] = {0};

	for (int i = 0; i < 256; i++) {
		for (int j = 0; j <= i; j++) {
			sum[i] += h[j];
		}
	}

	for (int i = 0; i < 256; i++) {
		double px = 255.0 / area * sum[i];
		h[i] = aprox(px);
	}
}

void set_equalized_px(PBM_t *photo, int h[256], double area,
					  int height, int width, int limit)
{
	equalize_px(h, area);
	for (int i = 0; i < height; i++) {
		for (int j = 0; j < width; j++) {
			int px = photo->p_simple[i][j];
			photo->p_simple[i][j] = h[px];
			px_correction(&photo->p_simple[i][j], limit);
		}
	}
}

void equalize(PBM_t *photo, int height, int width,
			  char *format, int limit)
{
	if (!(*photo).p) {
		fprintf(stdout, "No image loaded\n");
	} else if (!identifyformat_for_editing(format)) {

		int h[256] = {0};

		init_h(*photo, h, height, width);

		double area = height * width;

		set_equalized_px(photo, h, area, height, width, limit);

		printf("Equalize done\n");
	} else {
		printf("Black and white image needed\n");
	}
}

//rotate functions
int checkangle(int *angle)
{
	if (*angle < 0) {
		*angle += 360;
	}

	if (*angle >= 0 && *angle <= 360 && *angle % 90 == 0) {
		return 1;
	}

	return 0;
}

void transposed_rgb(PBM_t *photo, int size, int *x, int *y,
					int newheight, int newidth)
{
	PBM_t trans;
	trans.p_RGB = alloc_memory(size);
	for (int i = 0; i < newheight; i++) {
		for (int j = 0; j < newidth; j++) {
			int RGB_i =  y[0] + i;
			int RGB_j = x[0] + j;
			trans.p_RGB[j][i] = photo->p_RGB[RGB_i][RGB_j];
		}
	}

	if (newheight != newidth) {
		free_memory(newheight, newidth, *photo);
		*photo = trans;
	} else {
		for (int i = 0; i < newidth; i++) {
			for (int j = 0; j < newheight; j++) {
				int RGB_i =  y[0] + i;
				int RGB_j = x[0] + j;
				photo->p_RGB[RGB_i][RGB_j] = trans.p_RGB[i][j];
			}
		}
		free_memory(size, size, trans);
	}
}

void transposed_simple(PBM_t *photo, int size, int *x, int *y,
					   int newheight, int newidth)
{

	PBM_t trans;
	trans.p = alloc_memory(size);
	for (int i = 0; i < newheight; i++) {
		for (int j = 0; j < newidth; j++) {
			int simp_i =  y[0] + i;
			int simp_j = x[0] + j;
			trans.p_simple[j][i] = photo->p_simple[simp_i][simp_j];
		}
	}

	if (newheight != newidth) {
		free_memory(newheight, newidth, *photo);
		*photo = trans;
	} else {
		for (int i = 0; i < newheight; i++) {
			for (int j = 0; j < newidth; j++) {
				int simp_i =  y[0] + i;
				int simp_j = x[0] + j;
				photo->p_simple[simp_i][simp_j] = trans.p_simple[i][j];
			}
		}
		free_memory(size, size, trans);
	}
}

void transposed(PBM_t *photo, int *x, int *y,
				char *format, int newheight, int newidth)
{
	int size = ((newheight > newidth) ? newheight : newidth);
	if (identifyformat_for_editing(format)) {
		transposed_rgb(photo, size, x, y, newheight, newidth);
	} else {
		transposed_simple(photo, size, x, y, newheight, newidth);
	}
}

void supportforpermutation_rgb(PBM_t *photo, int *x, int *y)
{
	int newidth = x[1] - x[0];
	for (int i = y[0]; i < y[1]; i++) {
		for (int j = 0; j < newidth / 2; j++) {
			RGB_t aux = photo->p_RGB[i][x[0] + j];
			photo->p_RGB[i][x[0] + j] = photo->p_RGB[i][x[1] - j - 1];
			photo->p_RGB[i][x[1] - j - 1] = aux;
		}
	}
}

void supportforpermutation_simple(PBM_t *photo, int *x, int *y)
{
	int newidth = x[1] - x[0];
	for (int i = y[0]; i < y[1]; i++) {
		for (int j = 0; j < newidth / 2; j++) {
			int aux = photo->p_simple[i][x[0] + j];
			photo->p_simple[i][x[0] + j] = photo->p_simple[i][x[1] - j - 1];
			photo->p_simple[i][x[1] - j - 1] = aux;
		}
	}
}

void permutation_columns(PBM_t *photo, int *x, int *y,
						 char *format)
{
	if (identifyformat_for_editing(format)) {
		supportforpermutation_rgb(photo, x, y);
	} else {
		supportforpermutation_simple(photo, x, y);
	}
}

void rotate(PBM_t *photo, int *height, int *width, int *x, int *y,
			char *format)
{
	int angle = 0;
	if (scanf("%d", &angle) != 1) {
		printf("Invalid command!\n");
		return;
	}
	if (!(*photo).p) {
		fprintf(stdout, "No image loaded\n");
		return;
	}
	int startangle = angle;
	int newheight = y[1] - y[0];
	int newidth = x[1] - x[0];
	if (newheight != *height && newidth != *width &&
		newheight != newidth) {
		printf("The selection must be square\n");
		return;
	} else if (!checkangle(&angle)) {
		printf("Unsupported rotation angle\n");
	} else {
		for (int i = 0; i < angle / 90; i++) {
			transposed(photo, x, y, format, newheight, newidth);

			if (newheight == *height && newidth == *width) {
				myswap(height, width);
				myswap(&y[1], &x[1]);
				myswap(&newheight, &newidth);
			}

			permutation_columns(photo, x, y, format);
		}
		printf("Rotated %d\n", startangle);
	}
}

//histogram functions
void calculatebins(int h[256], int y)
{
	if (y == 256) {
		return;
	}

	int newh[256] = {0};

	int sec = 256 / y;
	for (int i  = 0; i < 256; i += sec) {
		for (int j = 0; j < sec; j++) {
			newh[i / sec] += h[i + j];
		}
	}

	for (int i = 0; i < y; i++) {
		h[i] = newh[i];
	}
}

int max_frecv(int h[256], int y)
{
	int maxi = h[0];
	for (int i = 1; i < y; i++) {
		if (maxi < h[i]) {
			maxi = h[i];
		}
	}

	return maxi;
}

void bins_printf(int h[256], int x, int y)
{
	double stars = 0;
	int max = max_frecv(h, y);
	for (int i = 0; i < y; i++) {
		stars = (double)h[i] / max * x;
		int nr_stars = (int)stars;
		printf("%d\t|\t", nr_stars);
		while (nr_stars--) {
			printf("*");
		}
		printf("\n");
	}
}

void histogram(PBM_t *photo, int height, int width, char *format)
{
	int x = -1, y = -1, invalid_check;
	scanf("%d%d", &x, &y);
	int h[256] = {0};
	if (!photo->p) {
		fprintf(stdout, "No image loaded\n");
		return;
	}
//(y & (y - 1)) -> check if it is a power of 2
	if (y <= 0 || (y & (y - 1)) || scanf("%d", &invalid_check) == 1) {
		printf("Invalid command\n");
		return;
	}
	if (identifyformat_for_editing(format)) {
		printf("Black and white image needed\n");
		return;
	}
	init_h(*photo, h, height, width);

	calculatebins(h, y);

	bins_printf(h, x, y);
}
