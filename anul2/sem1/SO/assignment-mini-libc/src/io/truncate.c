// SPDX-License-Identifier: BSD-3-Clause

#include <unistd.h>
#include <internal/syscall.h>
#include <errno.h>
#include <sys/types.h>

int truncate(const char *path, off_t length)
{
	/* TODO: Implement truncate(). */
	int sysret = syscall(__NR_truncate, path, length);
	if (sysret < 0) {
		errno = -sysret;
		return -1;
	}

	return 0;
}
