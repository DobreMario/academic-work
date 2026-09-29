#include <stdio.h>
#include <errno.h>
#include <stddef.h>
#include <internal/syscall.h>

int puts(const char *str)
{
    if (!str) {
        errno = EINVAL;
        return -1;
    }

    size_t len = 0;
    const char *ptr = str;
    while (*ptr != '\0') {
        len++;
        ptr++;
    }

    int sysret = syscall(__NR_write, 1, str, len);
    if (sysret == -1) {
        errno = EIO;
        return -1;
    }
    sysret = syscall(__NR_write, 1, "\n", 1);

    return sysret;
}