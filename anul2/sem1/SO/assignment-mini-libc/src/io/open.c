// SPDX-License-Identifier: BSD-3-Clause

#include <fcntl.h>
#include <internal/syscall.h>
#include <stdarg.h>
#include <errno.h>

int open(const char *filename, int flags, ...)
{
	/* TODO: Implement open system call. */
	va_list args;
	va_start(args, flags);
	mode_t mode = (flags & O_CREAT) ? va_arg(args, mode_t) : 0;
	va_end(args);

	int sysret = syscall(__NR_open, filename, flags, mode);
	if (sysret < 0) {
		errno = -sysret;
		return -1;
	}

	return sysret;
}
