#ifndef __TIME_H__
#define __TIME_H__

struct timespec
{
	long tv_sec;
	long tv_nsec;  
};

unsigned int sleep(unsigned int seconds);
int nanosleep(const struct timespec *req, struct timespec *rem);

#endif // __TIME_H__
