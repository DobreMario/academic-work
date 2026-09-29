#include <internal/syscall.h>
#include <time.h>
#include <internal/types.h>
#include <errno.h>

unsigned int sleep(unsigned int seconds)
{
    struct timespec req, rem;
    req.tv_sec = seconds;
    req.tv_nsec = 0;

    if (nanosleep(&req, &rem) == -1) {
        return rem.tv_sec;
    }


    return 0;
}