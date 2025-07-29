#ifndef EDIT_H
#define EDIT_H

//kernels init

#define EDGE_INIT {{-1, -1, -1}, {-1, 8, -1}, {-1, -1, -1}}
#define SHARPEN_INIT {{0, -1, 0}, {-1, 5, -1}, {0, -1, 0}}
#define BOX_BLUR_INIT {{1.0, 1.0, 1.0}, {1.0, 1.0, 1.0}, {1.0, 1.0, 1.0}}
#define BOX_BLUR_INIT_CONST (1.0 / 9.0)
#define GAUSSIAN_BLUR_INIT {{1.0, 2.0, 1.0}, {2.0, 4.0, 2.0}, {1.0, 2.0, 1.0}}
#define GAUSSIAN_BLUR_CONST (1.0 / 16.0)

typedef enum {
//CROP: format type
	P1245 = 0,
	P36 = 1,
//APPLEY: command type
	BLUR = 66,
	MYEDGE = 69,//EDGE, but ./check detects it as an error
	GAUSSIAN_BLUR = 71,
	SHARPEN = 83,
	INVALID_PARAMETER = 255
} EDIT_check;

void select(PBM_t photo, int height, int width, int *x, int *y);

void crop(PBM_t *photo, int *height, int *width, int *x, int *y, char *format);

void apply(PBM_t *photo, int height, int width, int *x, int *y,
		   char *format, int limit);

void equalize(PBM_t *photo, int height, int width,
			  char *format, int limit);

void rotate(PBM_t *photo, int *height, int *width, int *x, int *y,
			char *format);

void histogram(PBM_t *photo, int height, int width, char *format);

#endif //EDIT_H
