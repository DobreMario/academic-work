#include <internal/syscall.h>
#include <time.h>
#include <internal/types.h>
#include <errno.h>

int nanosleep(const struct timespec *req, struct timespec *rem) {
	int sysret = syscall(__NR_nanosleep, req, rem);
	if (sysret < 0) {
		errno = -sysret;
		return -1;
	}

	return 0;
}