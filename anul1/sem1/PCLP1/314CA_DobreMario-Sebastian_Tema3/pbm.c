#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "pbm.h"

int max(const int num1, const int num2)
{
	return (num1 > num2) ? num1 : num2;
}

char *reformat(char *format)
{
	if (!strcmp(format, "P1")) {
		strcpy(format, "P4");
	} else if (!strcmp(format, "P2")) {
		strcpy(format, "P5");
	} else if (!strcmp(format, "P3")) {
		strcpy(format, "P6");
	} else if (!strcmp(format, "P4")) {
		strcpy(format, "P1");
	} else if (!strcmp(format, "P5")) {
		strcpy(format, "P2");
	} else if (!strcmp(format, "P6")) {
		strcpy(format, "P3");
	}

	return format;
}

int checkpixelvalue(const int num, const int limit)
{
	return ((0 > num) || (num > limit));
}

int identifyformat(const char *format)
{
	if (!strcmp(format, "P1")) {
		return P1;
	} else if (!strcmp(format, "P2")) {
		return P2;
	} else if (!strcmp(format, "P3")) {
		return P3;
	} else if (!strcmp(format, "P4")) {
		return P4;
	} else if (!strcmp(format, "P5")) {
		return P5;
	} else if (!strcmp(format, "P6")) {
		return P6;
	}

	return INVALID_FORMAT;
}

RGB_t **alloc_memory(const int size)
{
	RGB_t **p =  (RGB_t **)malloc(size * sizeof(RGB_t *));
	if (!p) {
		fprintf(stdout, "Error: Malloc failed!\n");
		exit(MALLOC);
	}

	for (int i = 0; i < size; i++) {
		p[i] =  (RGB_t *)malloc(size * sizeof(RGB_t));
		if (!p[i]) {
			fprintf(stdout, "Error: Malloc failed!\n");

			while (--i) {
				free(p[i]);
			}
			free(p);
			exit(MALLOC);
		}
	}

	return p;
}

void load_P1(const int height, const int width, const int limit,
			 PBM_t photo, const char *filename,
			 const int headersize)
{
	FILE *in = fopen(filename, "r");
	if (!in) {
		fprintf(stdout, "ERROR: file can't be open!\n");

		free_memory(height, width, photo);

		exit(INVALID_FILE);
	}

	fseek(in, headersize, SEEK_SET);

	for (int i = 0; i < height; i++) {
		for (int j = 0; j < width; j++) {
			fscanf(in, "%d", &photo.p1[i][j]);

			if (checkpixelvalue(photo.p1[i][j], limit)) {
				fprintf(stdout, "ERROR: Bad pixel value!\n");

				free_memory(height, width, photo);
				fclose(in);

				exit(INVALID_PxVALUE);
			}
		}
	}

	fclose(in);
}

void load_P2(const int height, const int width, const int limit,
			 PBM_t photo, const char *filename,
			 const int headersize)
{
	FILE *in = fopen(filename, "r");
	if (!in) {
		fprintf(stdout, "ERROR: file can't be open!\n");

		free_memory(height, width, photo);

		exit(INVALID_FILE);
	}

	fseek(in, headersize, SEEK_SET);

	for (int i = 0; i < height; i++) {
		for (int j = 0; j < width; j++) {
			fscanf(in, "%d", &photo.p2[i][j]);

			if (checkpixelvalue(photo.p2[i][j], limit)) {
				fprintf(stdout, "ERROR: Bad pixel value!\n");

				free_memory(height, width, photo);
				fclose(in);

				exit(INVALID_PxVALUE);
			}
		}
	}

	fclose(in);
}

void load_P3(const int height, const int width, const int limit,
			 PBM_t photo, const char *filename,
			 const int headersize)
{
	FILE *in = fopen(filename, "r");
	if (!in) {
		fprintf(stdout, "ERROR: file can't be open!\n\n");

		free_memory(height, width, photo);

		exit(INVALID_FILE);
	}

	fseek(in, headersize, SEEK_SET);

	for (int i = 0; i < height; i++) {
		for (int j = 0; j < width; j++) {
			fscanf(in, "%d", &photo.p3[i][j].R);
			fscanf(in, "%d", &photo.p3[i][j].G);
			fscanf(in, "%d", &photo.p3[i][j].B);

			if (checkpixelvalue(photo.p3[i][j].R, limit)) {
				fprintf(stdout, "ERROR: Bad pixel value!\n");

				free_memory(height, width, photo);
				fclose(in);

				exit(INVALID_PxVALUE);

			} else if (checkpixelvalue(photo.p3[i][j].G, limit)) {
				fprintf(stdout, "ERROR: Bad pixel value!\n");

				free_memory(height, width, photo);
				fclose(in);

				exit(INVALID_PxVALUE);

			} else if (checkpixelvalue(photo.p3[i][j].B, limit)) {
				fprintf(stdout, "ERROR: Bad pixel value!\n");

				free_memory(height, width, photo);
				fclose(in);

				exit(INVALID_PxVALUE);
			}
		}
	}

	fclose(in);
}

void load_P4(const int height, const int width, const int limit,
			 PBM_t photo, const char *filename,
			 const int headersize)
{
	FILE *in = fopen(filename, "r");
	if (!in) {
		fprintf(stdout, "ERROR: file can't be open!\n");

		free_memory(height, width, photo);

		exit(INVALID_FILE);
	}

	fseek(in, headersize + 1, SEEK_SET);

	for (int i = 0; i < height; i++) {
		for (int j = 0; j < width; j++) {
			fread(&photo.p4[i][j], sizeof(int), 1, in);

			if (checkpixelvalue(photo.p4[i][j], limit)) {
				fprintf(stdout, "ERROR: Bad pixel value!\n");

				free_memory(height, width, photo);
				fclose(in);

				exit(INVALID_PxVALUE);
			}
		}
	}
	fclose(in);
}

void load_P5(const int height, const int width, const int limit,
			 PBM_t photo, const char *filename,
			 const int headersize)
{
	FILE *in = fopen(filename, "r");
	if (!in) {
		fprintf(stdout, "ERROR: file can't be open!\n");

		free_memory(height, width, photo);

		exit(INVALID_FILE);
	}

	fseek(in, headersize + 1, SEEK_SET);

	for (int i = 0; i < height; i++) {
		for (int j = 0; j < width; j++) {
			unsigned char c = 0;
			fread(&c, sizeof(unsigned char), 1, in);
			photo.p5[i][j] = (int)c;

			if (checkpixelvalue(photo.p5[i][j], limit)) {
				fprintf(stdout, "ERROR: Bad pixel value!\n");

				free_memory(height, width, photo);
				fclose(in);

				exit(INVALID_PxVALUE);
			}
		}
	}
	fclose(in);
}

void load_P6(const int height, const int width, const int limit,
			 PBM_t photo, const char *filename,
			 const int headersize)
{
	FILE *in = fopen(filename, "r");
	if (!in) {
		fprintf(stdout, "ERROR: file can't be open!\n");
		free_memory(height, width, photo);
		exit(INVALID_FILE);
	}

	fseek(in, headersize + 1, SEEK_SET);

	for (int i = 0; i < height; i++) {
		for (int j = 0; j < width; j++) {
			unsigned char r = 0, g = 0, b = 0;

			fread(&r, sizeof(unsigned char), 1, in);
			fread(&g, sizeof(unsigned char), 1, in);
			fread(&b, sizeof(unsigned char), 1, in);

			photo.p6[i][j].R = (int)r;
			photo.p6[i][j].G = (int)g;
			photo.p6[i][j].B = (int)b;

			if (checkpixelvalue(photo.p6[i][j].R, limit)) {
				fprintf(stdout, "ERROR: Bad pixel value!\n");

				free_memory(height, width, photo);
				fclose(in);

				exit(INVALID_PxVALUE);

			} else if (checkpixelvalue(photo.p6[i][j].G, limit)) {
				fprintf(stdout, "ERROR: Bad pixel value!\n");

				free_memory(height, width, photo);
				fclose(in);

				exit(INVALID_PxVALUE);

			} else if (checkpixelvalue(photo.p6[i][j].B, limit)) {
				fprintf(stdout, "ERROR: Bad pixel value!\n");

				free_memory(height, width, photo);
				fclose(in);

				exit(INVALID_PxVALUE);
			}
		}
	}
	fclose(in);
}

void load_PBM(char *format, int *height, int *width,
			  PBM_t *photo, int *limit, const char *filename)
{
	FILE *in = fopen(filename, "r");

	if (!in) {
		photo->p = NULL;
	} else {
		fscanf(in, "%s%d%d%d", format, width, height, limit);
		const int size = max(*height, *width);

		photo->p = alloc_memory(size);
		const int headersize = ftell(in);

		fclose(in);
		int type = identifyformat(format);
		switch (type) {
		case 1:
			load_P1(*height, *width, *limit, *photo, filename, headersize);
			break;
		case 2:
			load_P2(*height, *width, *limit, *photo, filename, headersize);
			break;
		case 3:
			load_P3(*height, *width, *limit, *photo, filename, headersize);
			break;
		case 4:
			load_P4(*height, *width, *limit, *photo, filename, headersize);
			break;
		case 5:
			load_P5(*height, *width, *limit, *photo, filename, headersize);
			break;
		case 6:
			load_P6(*height, *width, *limit, *photo, filename, headersize);
			break;
		default:
			fprintf(stdout, "ERROR: Invalid PBM format!\n");
			free_memory(*height, *width, *photo);
			exit(INVALID_FORMAT);
		}
	}
}

void save_P1(const char *format, const int height, const int width,
			 const PBM_t photo, const int limit, const char *filename)
{
	FILE *out =  fopen(filename, "w");
	if (!out) {
		fprintf(stdout, "ERROR: file can't be open!\n");

		free_memory(height, width, photo);

		exit(INVALID_FILE);
	}
	fprintf(out, "%s\n%d %d\n%d\n", format, width, height, limit);
	for (int i = 0; i < height; i++) {
		for (int j = 0; j < width; j++) {
			fprintf(out, "%d ", photo.p1[i][j]);
		}
		fprintf(out, "\n");
	}
	fclose(out);
}

void save_P2(const char *format, const int height, const int width,
			 const PBM_t photo, const int limit, const char *filename)
{
	FILE *out =  fopen(filename, "w");
	if (!out) {
		free_memory(height, width, photo);

		exit(INVALID_FILE);
	}
	fprintf(out, "%s\n%d %d\n%d\n", format, width, height, limit);
	for (int i = 0; i < height; i++) {
		for (int j = 0; j < width; j++) {
			fprintf(out, "%d ", photo.p2[i][j]);
		}
		fprintf(out, "\n");
	}
	fclose(out);
}

void save_P3(const char *format, const int height, const int width,
			 const PBM_t photo, const int limit, const char *filename)
{
	FILE *out =  fopen(filename, "w");
	if (!out) {
		fprintf(stdout, "ERROR: file can't be open!\n");

		free_memory(height, width, photo);

		exit(INVALID_FILE);
	}
	fprintf(out, "%s\n%d %d\n%d\n", format, width, height, limit);
	for (int i = 0; i < height; i++) {
		for (int j = 0; j < width; j++) {
			fprintf(out, "%d ", photo.p3[i][j].R);
			fprintf(out, "%d ", photo.p3[i][j].G);
			fprintf(out, "%d ", photo.p3[i][j].B);
		}
		fprintf(out, "\n");
	}
	fclose(out);
}

void save_P4(const char *format, const int height, const int width,
			 const PBM_t photo, const int limit, const char *filename)
{
	FILE *out =  fopen(filename, "w");
	if (!out) {
		fprintf(stdout, "ERROR: file can't be open!\n");

		free_memory(height, width, photo);

		exit(INVALID_FILE);
	}
	fprintf(out, "%s\n%d %d\n%d\n", format, width, height, limit);
	for (int i = 0; i < height; i++) {
		for (int j = 0; j < width; j++) {
			unsigned char c = (unsigned char)photo.p4[i][j];
			fwrite(&c, sizeof(unsigned char), 1, out);
		}
	}
	fclose(out);
}

void save_P5(const char *format, const int height, const int width,
			 const PBM_t photo, const int limit, const char *filename)
{
	FILE *out =  fopen(filename, "w");
	if (!out) {
		fprintf(stdout, "ERROR: file can't be open!\n");

		free_memory(height, width, photo);

		exit(INVALID_FILE);
	}
	fprintf(out, "%s\n%d %d\n%d\n", format, width, height, limit);
	for (int i = 0; i < height; i++) {
		for (int j = 0; j < width; j++) {
			unsigned char c = (unsigned char)photo.p5[i][j];
			fwrite(&c, sizeof(unsigned char), 1, out);
		}
	}
	fclose(out);
}

void save_P6(const char *format, const int height, const int width,
			 const PBM_t photo, const int limit, const char *filename)
{
	FILE *out =  fopen(filename, "w");
	if (!out) {
		fprintf(stdout, "ERROR: file can't be open!\n");

		free_memory(height, width, photo);

		exit(INVALID_FILE);
	}
	fprintf(out, "%s\n%d %d\n%d\n", format, width, height, limit);
	for (int i = 0; i < height; i++) {
		for (int j = 0; j < width; j++) {

			unsigned char r = (unsigned char)photo.p6[i][j].R;
			unsigned char g = (unsigned char)photo.p6[i][j].G;
			unsigned char b = (unsigned char)photo.p6[i][j].B;

			fwrite(&r, sizeof(unsigned char), 1, out);
			fwrite(&g, sizeof(unsigned char), 1, out);
			fwrite(&b, sizeof(unsigned char), 1, out);

		}
	}
	fclose(out);
}

void save_PBM(char *format, const int height, const int width,
			  const PBM_t photo, const int limit, const char *filename,
			  const char type)
{
	int ptype =  identifyformat(format);
	if (ptype > 3 && type == 't') {
		ptype -= 3;
		format = reformat(format);
	} else if (ptype <= 3 && type == 'b') {
		ptype += 3;
		format = reformat(format);
	}

	switch (ptype) {
	case 1:
		save_P1(format, height, width, photo, limit, filename);
		break;
	case 2:
		save_P2(format, height, width, photo, limit, filename);
		break;
	case 3:
		save_P3(format, height, width, photo, limit, filename);
		break;
	case 4:
		save_P4(format, height, width, photo, limit, filename);
		break;
	case 5:
		save_P5(format, height, width, photo, limit, filename);
		break;
	case 6:
		save_P6(format, height, width, photo, limit, filename);
		break;
	default:
		fprintf(stdout, "ERROR: PBM can't be save!\n");
		break;
	}
}

void free_memory(const int height, const int width, PBM_t photo)
{
	const int size = max(height, width);
	for (int i = 0; i < size; i++) {
		free(photo.p[i]);
	}
	free(photo.p);
}
