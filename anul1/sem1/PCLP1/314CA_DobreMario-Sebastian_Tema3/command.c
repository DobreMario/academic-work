#include "mylibraries.h"

int findcommand(char *command)
{
	if (!strcmp(command, "LOAD")) {
		return LOAD;
	} else if (!strcmp(command, "SAVE")) {
		return SAVE;
	} else if (!strcmp(command, "SELECT")) {
		return SELECT;
	} else if (!strcmp(command, "CROP")) {
		return CROP;
	} else if (!strcmp(command, "APPLY")) {
		return APPLY;
	} else if (!strcmp(command, "EQUALIZE")) {
		return MYEQUALIZE;
	} else if (!strcmp(command, "HISTOGRAM")) {
		return HISTOGRAM;
	} else if (!strcmp(command, "ROTATE")) {
		return ROTATE;
	} else if (!strcmp(command, "EXIT")) {
		return MYEXIT;
	}

	return INVALID_COMMAND;
}

void load(char *format, int *height, int *width,
		  PBM_t *photo, int *limit, int *x, int *y)
{
	if (photo->p) {
		free_memory(*height, *width, *photo);
	}
	char filename[101];
	scanf("%s", filename);
	load_PBM(format, height, width, photo, limit, filename);
	if (photo->p) {
		fprintf(stdout, "Loaded %s\n", filename);
	} else {
		fprintf(stdout, "Failed to load %s\n", filename);
	}
	x[1] = *width;
	y[1] = *height;
}

void save(char *format, int height, int width,
		  PBM_t photo, int limit)
{
	char savesyntax[256];
	fgets(savesyntax, 256, stdin);
	savesyntax[strlen(savesyntax) - 1] = '\0';
	char *filename = strtok(savesyntax, " ");
	char *ascii = strtok(NULL, " ");
	char type = 't';
	if (!ascii) {
		type = 'b';
	}
	if (!photo.p) {
		fprintf(stdout, "No image loaded\n");
	} else {
		save_PBM(format, height, width, photo, limit, filename, type);
		fprintf(stdout, "Saved %s\n", filename);
	}
}

void myexit(PBM_t photo)
{
	if (!photo.p) {
		fprintf(stdout, "No image loaded\n");
	}
}

void commands(char *command,
			  PBM_t *photo, char *format,
			  int *height, int *width, int *limit,
			  int *x, int *y)
{
	char s[256];
	int comm_nr = findcommand(command);
	switch (comm_nr) {
	case 1:
		load(format, height, width, photo, limit, x, y);
		break;
	case 2:
		save(format, *height, *width, *photo, *limit);
		break;
	case 3:
		select(*photo, *height, *width, x, y);
		break;
	case 4:
		crop(photo, height, width, x, y, format);
		break;
	case 5:
		apply(photo, *height, *width, x, y, format, *limit);
		break;
	case 6:
		equalize(photo, *height, *width, format, *limit);
		break;
	case 7:
		histogram(photo, *height, *width, format);
		break;
	case 8:
		rotate(photo, height, width, x, y, format);
		break;
	case 101:
		myexit(*photo);
		break;
	default:
		if (fgets(s, 256, stdin)) {
			fprintf(stdout, "Invalid command\n");
		} else {
			fprintf(stdout, "Invalid command\n");
		}
		break;
	}
}

void startprogram(PBM_t *photo, int *height, int *width)
{
	photo->p = NULL;
	char s[101], format[101];
	int x[2], y[2];
	x[0] = 0;
	y[0] = 0;
	int run = 1, limit;
	while (run) {
		scanf("%s", s);
		commands(s, photo, format, height, width, &limit, x, y);
		if (strcmp(s, "EXIT") == 0) {
			run = 0;
		}
	}
}

void endprogram(PBM_t photo, int height, int width)
{
	if (photo.p) {
		free_memory(height, width, photo);
	}
}
