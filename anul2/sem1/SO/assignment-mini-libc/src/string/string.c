// SPDX-License-Identifier: BSD-3-Clause

#include <string.h>

char *strcpy(char *destination, const char *source)
{
	/* TODO: Implement strcpy(). */
	if (!source) {
		return NULL;
	}

	char *dest;
	for (dest =  destination; *source != 0; dest++, source++) {
		*dest = *source;
	}
	*dest = '\0';
	return destination;
}

char *strncpy(char *destination, const char *source, size_t len)
{
	/* TODO: Implement strncpy(). */
	if (!source) {
		return NULL;
	}
	char *dest;
	size_t i;
	for (i = 0, dest = destination; i < len && *source != 0; i++, dest++, source++) {
		*dest = *source;
	}

	for (; i < len; i++, dest++) {
		*dest = '\0';
	}

	return destination;
}

char *strcat(char *destination, const char *source)
{
	/* TODO: Implement strcat(). */
	if (!source) {
		return NULL;
	}

	char *dest = destination;
	while(*dest != '\0') {
		dest++;
	}
	for (; *source != 0; dest++, source++) {
		*dest = *source;
	}
	*dest = '\0';

	return destination;
}

char *strncat(char *destination, const char *source, size_t len)
{
	/* TODO: Implement strncat(). */
	if (!source) {
		return NULL;
	}

	char *dest = destination;
	size_t i;
	while(*dest != '\0') {
		dest++;
	}
	for (i = 0; i < len && *source != 0; i++, dest++, source++) {
		*dest = *source;
	}
	*dest = '\0';
	return destination;
}

int strcmp(const char *str1, const char *str2)
{
	/* TODO: Implement strcmp(). */
	if (!str1 && !str2) {
		return 0;
	}
	if (!str1) {
		return -1;
	}
	if (!str2) {
		return 1;
	}

	while(*str1 != '\0' && *str2 != '\0' && !(*str1 - *str2)) {
		str1++; str2++;
	}

	return (*str1 - *str2);
}

int strncmp(const char *str1, const char *str2, size_t len)
{
	/* TODO: Implement strncmp(). */
	if ((!str1 && !str2) || len == 0) {
		return 0;
	}
	if (!str1) {
		return -1;
	}
	if (!str2) {
		return 1;
	}

	while(*str1 != '\0' && *str2 != '\0' && !(*str1 - *str2) && --len) {
		str1++; str2++;
	}

	return (*str1 - *str2);
}

size_t strlen(const char *str)
{
	size_t i = 0;

	for (; *str != '\0'; str++, i++)
		;

	return i;
}

char *strchr(const char *str, int c)
{
	/* TODO: Implement strchr(). */
	if (!str) {
		return NULL;
	}

	const char *iter;
	for (iter = str; *iter != '\0'; iter++) {
		if ((*iter) == c) {
			return (char *)iter;
		}
	}

	if (c == '\0') {
		return (char *)iter;
	}

	return NULL;
}

char *strrchr(const char *str, int c)
{
	/* TODO: Implement strrchr(). */
	if (!str) {
		return NULL;
	}

	const char *iter = str + strlen(str);
	while(iter != (str - 1)) {
		if ((*iter) == c) {
			return (char *) iter;
		}
		iter--;
	}
	return NULL;
}

char *strstr(const char *haystack, const char *needle)
{
	/* TODO: Implement strstr(). */
	if (!haystack || !needle) {
		return NULL;
	}

	size_t len = strlen(needle);
	const char *iter;
	for (iter = haystack; *iter != '\0'; iter++) {
		if (*iter == *needle && !strncmp(iter, needle, len)) {
			return (char *)iter;
		}
		
	}
	return NULL;
}

char *strrstr(const char *haystack, const char *needle)
{
	/* TODO: Implement strrstr(). */
	if (!haystack || !needle) {
		return NULL;
	}

	size_t len = strlen(needle);
	const char *iter = haystack + strlen(haystack);
	while(iter != (haystack - 1)) {
		if (*iter == *needle && !strncmp(iter, needle, len)) {
			return (char *)iter;
		}
		iter--;
	}
	return NULL;
}

void *memcpy(void *destination, const void *source, size_t num)
{
	/* TODO: Implement memcpy(). */
	if (!destination || !source || !num) {
		return destination;
	}

	unsigned char *dest = destination;
	const unsigned char *src = source;
	for (size_t i = 0; i < num; i++) {
		*(dest + i) = *(src + i);
	}

	return destination;
}

void *memmove(void *destination, const void *source, size_t num)
{
	/* TODO: Implement memmove(). */
	if (!destination || !source || !num) {
		return destination;
	}

	unsigned char *dest = destination;
	const unsigned char *src = source;

	if (dest > src && dest < src + (num)) {
		for (size_t i = num; i > 0; i--) {
		 *(dest + i - 1) = *(src + i - 1);
		}

		return destination;
	}

	for (size_t i = 0; i < num; i++) {
		*(dest + i) = *(src + i);
	}
	return destination;
}

int memcmp(const void *ptr1, const void *ptr2, size_t num)
{
	/* TODO: Implement memcmp(). */
	if (!ptr1) {
		return -1;
	}

	if (!ptr2) {
		return 1;
	}

	if (!num) {
		return 0;
	}

	const unsigned char *p1 = ptr1;
	const unsigned char *p2 = ptr2;
	while (num--)
	{
		if (*p1 - *p2) {
			return (int)(*p1) - (int)(*p2);
		}

		p1++;
		p2++;
	}
	

	return 0;
}

void *memset(void *source, int value, size_t num)
{
	/* TODO: Implement memset(). */
	if (!source) {
		return NULL;
	}

	if (!num) {
		return source;
	}

	unsigned char *src = source;
	for (size_t i = 0; i < num; i++) {
		*(src + i) = value;
	}

	return source;
}
