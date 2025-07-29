%include "../include/io.mac"

extern printf
global remove_numbers

section .data
	fmt db "%d", 10, 0

section .text

; function signature: 
; void remove_numbers(int *a, int n, int *target, int *ptr_len);

remove_numbers:
	;; DO NOT MODIFY
	push    ebp
	mov     ebp, esp
	pusha

	mov     esi, [ebp + 8] ; source array
	mov     ebx, [ebp + 12] ; n
	mov     edi, [ebp + 16] ; dest array
	mov     edx, [ebp + 20] ; pointer to dest length

	;; DO NOT MODIFY

	

	;; Your code starts here
	
	xor ecx, ecx ; index for loop
	mov dword [edx], 0 ; initialize target length to 0

	loop_start:

		cmp ecx, ebx
		jge end_loop

		; Check if number is odd
		; Skip if odd

		if1_label:

			xor eax, eax
			mov eax, [esi + ecx * 4]
			
			and eax, 1
			cmp eax, 1
			je inc_label

		end_if1 

		; Check if number is a power of 2
		; Skip if it is a power of 2

		if2_label:

			xor eax, eax
			mov eax, [esi + ecx * 4]
			cmp eax, 0
			jle add_element_label

			push eax
			dec eax
			and eax, [esp]
			cmp eax, 0
			pop eax
			je inc_label

		end_if2

		; Copy valid element from source to target and increment result length

		add_element_label:

			xor eax, eax
			push dword [esi + ecx * 4]
			mov eax, dword [edx]
			pop dword [edi + eax * 4]
			inc dword [edx]
			jmp inc_label

		end_add

		; Increment index

		inc_label:
			inc ecx
			jmp loop_start
		end_inc

	end_loop:

	;; Your code ends here
	

	;; DO NOT MODIFY

	popa
	leave
	ret
	
	;; DO NOT MODIFY
