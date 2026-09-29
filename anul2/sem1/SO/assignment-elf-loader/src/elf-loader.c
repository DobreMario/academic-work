// SPDX-License-Identifier: BSD-3-Clause

#define _GNU_SOURCE

#include <stdio.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <unistd.h>
#include <sys/mman.h>
#include <fcntl.h>

#include <elf.h>
#include <string.h>
#include <sys/random.h>
#include <sys/utsname.h>
#define STACK_SIZE (8 * 1024 * 1024)

void *map_elf(const char *filename)
{
	// This part helps you store the content of the ELF file inside the buffer.
	struct stat st;
	void *file;
	int fd;

	fd = open(filename, O_RDONLY);
	if (fd < 0) {
		perror("open");
		exit(1);
	}

	fstat(fd, &st);

	file = mmap(NULL, st.st_size, PROT_READ, MAP_PRIVATE, fd, 0);
	if (file == MAP_FAILED) {
		perror("mmap");
		close(fd);
		exit(1);
	}

	return file;
}

void load_and_run(const char *filename, int argc, char **argv, char **envp)
{
	// Contents of the ELF file are in the buffer: elf_contents[x] is the x-th byte of the ELF file.
	void *elf_contents = map_elf(filename);

	Elf64_Ehdr *ehdr = (Elf64_Ehdr *)elf_contents;

	Elf64_Addr load_base = 0;

	if (memcmp(ehdr->e_ident, ELFMAG, SELFMAG) != 0) {
		fprintf(stderr, "Not a valid ELF file\n");
		exit(3);
	}

	if (ehdr->e_ident[EI_CLASS] != ELFCLASS64) {
		fprintf(stderr, "Not a 64-bit ELF\n");
		exit(4);
	}

	Elf64_Phdr *phdr = (Elf64_Phdr *)((char *)ehdr + ehdr->e_phoff);

	if (ehdr->e_type == ET_DYN) {
		uintptr_t min_vaddr = UINTPTR_MAX;
		uintptr_t max_vaddr = 0;

		for (int i = 0; i < ehdr->e_phnum; i++) {
			Elf64_Phdr *p = &phdr[i];

			size_t page_size = sysconf(_SC_PAGESIZE);
			uintptr_t start = p->p_vaddr & ~(page_size - 1);
			uintptr_t end = (p->p_vaddr + p->p_memsz + page_size - 1) & ~(page_size - 1);

			if (p->p_type == PT_LOAD) {
				if (p->p_vaddr < min_vaddr)
					min_vaddr = p->p_vaddr;

				if (p->p_vaddr + p->p_memsz > max_vaddr)
					max_vaddr = p->p_vaddr + p->p_memsz;
			}
		}

		size_t span = max_vaddr - min_vaddr;
		void *mem = mmap(NULL, span,
				PROT_READ | PROT_WRITE,
				MAP_PRIVATE | MAP_ANONYMOUS,
				-1, 0);

		if (mem == MAP_FAILED) {
			perror("mmap ET_DYN");
			exit(5);
		}

		load_base = (Elf64_Addr)mem - (Elf64_Addr)min_vaddr;
	}

	for (int i = 0; i < ehdr->e_phnum; i++) {
		Elf64_Phdr *p = &phdr[i];

		if (p->p_type == PT_LOAD) {

			// Align to page boundaries
			size_t page_size = sysconf(_SC_PAGESIZE);
			uintptr_t start = (load_base + p->p_vaddr) & ~(page_size - 1);
			uintptr_t end = (load_base + p->p_vaddr + p->p_memsz + page_size - 1) & ~(page_size - 1);
			size_t len = end - start;

			void *mem = mmap((void *)start, len,
					PROT_READ | PROT_WRITE | PROT_EXEC,
					MAP_PRIVATE | MAP_ANONYMOUS | MAP_FIXED,
					-1, 0);

			if (mem == MAP_FAILED) {
				perror("mmap PT_LOAD");
				exit(5);
			}

			memcpy((void *)(load_base + p->p_vaddr), (char *)ehdr + p->p_offset, p->p_filesz);

			//BSS zeroing
			if (p->p_memsz > p->p_filesz)
				memset((char *)(load_base + p->p_vaddr) + p->p_filesz, 0, p->p_memsz - p->p_filesz);
		}
	}

	for (int i = 0; i < ehdr->e_phnum; i++) {
		Elf64_Phdr *p = &phdr[i];

		if (p->p_type == PT_LOAD) {

			// Align to page boundaries
			size_t page_size = sysconf(_SC_PAGESIZE);
			uintptr_t start = (load_base + p->p_vaddr) & ~(page_size - 1);
			uintptr_t end = (load_base + p->p_vaddr + p->p_memsz + page_size - 1) & ~(page_size - 1);
			size_t len = end - start;

			// Determine permissions
			int perm = 0;

			if (p->p_flags & PF_R)
				perm |= PROT_READ;

			if (p->p_flags & PF_W)
				perm |= PROT_WRITE;

			if (p->p_flags & PF_X)
				perm |= PROT_EXEC;

			if (mprotect((void *)start, len, perm) < 0) {
				perror("mprotect");
				exit(6);
			}
		}
	}

	void *sp = NULL;

	void *stack_mem = mmap(NULL, STACK_SIZE,
			PROT_READ | PROT_WRITE,
			MAP_PRIVATE | MAP_ANONYMOUS,
			-1, 0);

	if (stack_mem == MAP_FAILED) {
		perror("mmap stack");
		exit(7);
	}

	char *stack_top = (char *)stack_mem + STACK_SIZE;

	int envc = 0;

	while (envp[envc] != NULL)
		envc++;

	// Store argv and envp strings on the stack
	char *stack_argv[argc + 1];
	char *stack_envp[envc + 1];

	for (int i = envc - 1; i >= 0; i--) {
		size_t len = strlen(envp[i]) + 1;

		stack_top -= len;
		memcpy(stack_top, envp[i], len);
		stack_envp[i] = stack_top;
	}

	stack_envp[envc] = NULL;

	for (int i = argc - 1; i >= 0; i--) {
		int len = strlen(argv[i]) + 1;

		stack_top -= len;
		memcpy(stack_top, argv[i], len);
		stack_argv[i] = stack_top;
	}

	stack_argv[argc] = NULL;

	// Align stack pointer to 16 bytes
	stack_top = (char *)((uintptr_t)stack_top & ~0xF);

	// Platform data
	struct utsname arch_info;

	uname(&arch_info);
	const char *platform_str = arch_info.machine;
	size_t platform_len = strlen(platform_str) + 1;

	stack_top -= platform_len;
	memcpy(stack_top, platform_str, platform_len);
	Elf64_Addr platform_addr = (Elf64_Addr)stack_top;

	// Random bytes for AT_RANDOM
	stack_top -= 16;
	getrandom(stack_top, 16, 0);
	Elf64_Addr random_bytes = (Elf64_Addr)stack_top;

	// Align stack pointer to 16 bytes
	stack_top = (char *)((uintptr_t)stack_top & ~0xF);

	Elf64_Addr *auxv_entries = (Elf64_Addr *)stack_top;

	// Set up auxv on stack
	*(--auxv_entries) = 0;
	*(--auxv_entries) = AT_NULL;

	*(--auxv_entries) = platform_addr;
	*(--auxv_entries) = AT_PLATFORM;

	*(--auxv_entries) = (Elf64_Addr)stack_argv[0];
	*(--auxv_entries) = AT_EXECFN;

	*(--auxv_entries) = (Elf64_Addr)random_bytes;
	*(--auxv_entries) = AT_RANDOM;

	Elf64_Addr secure_flag = (getuid() != geteuid() || getgid() != getegid());
	*(--auxv_entries) = secure_flag;
	*(--auxv_entries) = AT_SECURE;

	*(--auxv_entries) = (Elf64_Addr)getegid();
	*(--auxv_entries) = AT_EGID;

	*(--auxv_entries) = (Elf64_Addr)getgid();
	*(--auxv_entries) = AT_GID;

	*(--auxv_entries) = (Elf64_Addr)geteuid();
	*(--auxv_entries) = AT_EUID;

	*(--auxv_entries) = (Elf64_Addr)getuid();
	*(--auxv_entries) = AT_UID;

	*(--auxv_entries) = (Elf64_Addr)(load_base + ehdr->e_entry);
	*(--auxv_entries) = AT_ENTRY;

	*(--auxv_entries) = (Elf64_Addr)0;
	*(--auxv_entries) = AT_FLAGS;		// unused (conform getauxval man)

	*(--auxv_entries) = (Elf64_Addr)ehdr->e_phnum;
	*(--auxv_entries) = AT_PHNUM;

	*(--auxv_entries) = (Elf64_Addr)ehdr->e_phentsize;
	*(--auxv_entries) = AT_PHENT;

	Elf64_Addr load_addr = 0;

	for (int i = 0; i < ehdr->e_phnum; i++) {
		if (phdr[i].p_type == PT_LOAD && phdr[i].p_offset == 0) {
			load_addr = phdr[i].p_vaddr;
			break;
		}
	}

	*(--auxv_entries) = (Elf64_Addr)(load_addr + ehdr->e_phoff + load_base);
	*(--auxv_entries) = AT_PHDR;

	*(--auxv_entries) = (Elf64_Addr)sysconf(_SC_CLK_TCK);
	*(--auxv_entries) = AT_CLKTCK;

	*(--auxv_entries) = (Elf64_Addr)sysconf(_SC_PAGESIZE);
	*(--auxv_entries) = AT_PAGESZ;

	char **new_stack_top = (char **)auxv_entries;

	// Push envp
	for (int i = envc; i >= 0; i--)
		*(--new_stack_top) = stack_envp[i];

	// Push argv
	for (int i = argc; i >= 0; i--)
		*(--new_stack_top) = stack_argv[i];

	// Push argc
	Elf64_Addr *argc_ptr = (Elf64_Addr *)new_stack_top;
	*(--argc_ptr) = (Elf64_Addr)argc;

	sp = (void *)argc_ptr;

	// TODO: Set the entry point and the stack pointer
	void (*entry)() = NULL;

	entry = (void (*)())(load_base + ehdr->e_entry);

	// Transfer control
	__asm__ __volatile__(
			"mov %0, %%rsp\n"
			"xor %%rbp, %%rbp\n"
			"jmp *%1\n"
			:
			: "r"(sp), "r"(entry)
			: "memory"
			);
}

int main(int argc, char **argv, char **envp)
{
	if (argc < 2) {
		fprintf(stderr, "Usage: %s <static-elf-binary>\n", argv[0]);
		exit(1);
	}

	load_and_run(argv[1], argc - 1, &argv[1], envp);
	return 0;
}
