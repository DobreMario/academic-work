// SPDX-License-Identifier: BSD-3-Clause

#include <internal/mm/mem_list.h>
#include <internal/types.h>
#include <internal/essentials.h>
#include <sys/mman.h>
#include <string.h>
#include <stdlib.h>

void *malloc(size_t size)
{
	/* TODO: Implement malloc(). */
	void *addr = NULL;
	addr = mmap(NULL, size, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
	if (addr == MAP_FAILED) {
		return NULL;
	}

	if (mem_list_add(addr, size) == -1) {
		munmap(addr, size);
		return NULL;
	}

	return addr;
}

void *calloc(size_t nmemb, size_t size)
{
	/* TODO: Implement calloc(). */
	void *addr = malloc(nmemb * size);
	if (!addr) {
		return NULL;
	}

	memset(addr, 0, nmemb * size);
	return addr;
}

void free(void *ptr)
{
	/* TODO: Implement free(). */
	struct mem_list *item;
	item = mem_list_find(ptr);
	if (item == NULL) {
		return;
	}

	munmap(ptr, item->len);
	mem_list_del(ptr);
}

void *realloc(void *ptr, size_t size)
{
	/* TODO: Implement realloc(). */
	void *new_addr = malloc(size);
	if (!new_addr) {
		return NULL;
	}

	memcpy(new_addr, ptr, size);
	free(ptr);
	return new_addr;
}

void *reallocarray(void *ptr, size_t nmemb, size_t size)
{
	/* TODO: Implement reallocarray(). */
	return realloc(ptr, nmemb * size);
}
