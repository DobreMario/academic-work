// SPDX-License-Identifier: BSD-3-Clause

#include <unistd.h>
#include <internal/syscall.h>
#include <errno.h>
#include <sys/types.h>

int ftruncate(int fd, off_t length)
{
	/* TODO: Implement ftruncate(). */
	int sysret = syscall(__NR_ftruncate, fd, length);
	if (sysret < 0) {
		errno = -sysret;
		return -1;
	}

	return 0;
}
