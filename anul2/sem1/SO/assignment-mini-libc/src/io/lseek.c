// SPDX-License-Identifier: BSD-3-Clause

#include <unistd.h>
#include <internal/syscall.h>
#include <errno.h>
#include <sys/types.h>

off_t lseek(int fd, off_t offset, int whence)
{
	/* TODO: Implement lseek(). */

	int sysret = syscall(__NR_lseek, fd, offset, whence);
	if (sysret < 0) {
		errno = -sysret;
		return -1;
	}
	return sysret;
}
