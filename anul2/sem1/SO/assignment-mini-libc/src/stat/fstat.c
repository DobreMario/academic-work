// SPDX-License-Identifier: BSD-3-Clause

#include <sys/stat.h>
#include <internal/syscall.h>
#include <errno.h>

int fstat(int fd, struct stat *st)
{
	/* TODO: Implement fstat(). */
	int sysret = syscall(__NR_fstat, fd, st);
	if (sysret < 0) {
		errno = -sysret;
		return -1;
	}

	return 0;
}
