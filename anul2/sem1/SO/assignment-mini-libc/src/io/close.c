// SPDX-License-Identifier: BSD-3-Clause

#include <unistd.h>
#include <internal/syscall.h>
#include <stdarg.h>
#include <errno.h>

int close(int fd)
{
	/* TODO: Implement close(). */

	int sysret = syscall(__NR_close, fd);

	if (sysret < 0) {
		errno = -sysret;
		return -1;
	}

	return sysret;
}
