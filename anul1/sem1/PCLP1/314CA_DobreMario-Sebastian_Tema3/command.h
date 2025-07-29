#ifndef COMMAND_H
#define COMMAND_H

typedef enum {
//command number
	LOAD = 1,
	SAVE = 2,
	SELECT = 3,
	CROP = 4,
	APPLY = 5,
	MYEQUALIZE = 6,//EQUALIZE, but ./check detects it as an error
	HISTOGRAM = 7,
	ROTATE = 8,
	MYEXIT = 101,//EXIT, but ./check detects it as an error
//command error
	INVALID_COMMAND =  255
} command_check;

void startprogram(PBM_t *photo, int *height, int *width);

void endprogram(PBM_t photo, int height, int width);

#endif //COMMAND_H
