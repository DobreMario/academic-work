typedef struct {
	int R, G, B;
} RGB_t;

RGB_t **alloc_ppm(int n, int m);

void scanf_ppm(RGB_t **ppm, int n, int m, int prag);

void printf_ppm(RGB_t **ppm, int n, int m);

int ver_color(RGB_t **ppm, int n, int m, int prag);

void resize_ppm(RGB_t ***ppm, int *n, int *m);

int med_px(RGB_t **ppm, int i, int j);

void myfree_ppm(RGB_t **ppm, int n);
