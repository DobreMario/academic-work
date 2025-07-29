%include "../include/io.mac"

extern printf
global check_row
global check_column
global check_box
; you can declare any helper variables in .data or .bss
section .data
	start_idx db 0, 3, 6, 27, 30, 33, 54, 57, 60

section .bss
	num_freq resb 9


section .text


; int check_row(char* sudoku, int row);
check_row:
	;; DO NOT MODIFY
	push    ebp
	mov     ebp, esp
	push ebx
	push ecx
	push edx
	push esi
	push edi

	mov     esi, [ebp + 8]  ; char* sudoku, pointer to 81-long char array
	mov     edx, [ebp + 12]  ; int row 
	;; DO NOT MODIFY
   
	;; Freestyle starts here

	; Initialize frequency array to track occurrences of numbers 1-9
	mov edi, num_freq
	xor ecx, ecx
	for_row_setup:
		cmp ecx, 9
		jge end_for_row_setup
		mov byte [edi + ecx], 0
		inc ecx
		jmp for_row_setup
	end_for_row_setup:

	; Calculate the starting index for the row in the Sudoku grid
	xor eax, eax
	mov eax, edx
	imul eax, 9
	add esi, eax

	; Loop through each cell in the row to validate numbers
	xor ecx, ecx
	for_row_check:
		cmp ecx, 9
		jge end_for_row_check

		; Check if the number is within the valid range (1-9)
		xor eax, eax
		mov al, byte [esi + ecx]
		sub al, 1

		cmp al, 8
		ja not_valid_row
		cmp al, 0
		jb not_valid_row

		; Check if the number has already been encountered in this row
		xor ebx, ebx
		mov bl, byte [num_freq + eax]
		cmp ebx, 0
		jne not_valid_row

		; Mark the number as encountered in the frequency array
		mov byte [num_freq + eax], 1
		jmp next_num_row

		not_valid_row:
			mov eax, 2
			jmp return_2_row

		next_num_row:
			inc ecx
			jmp for_row_check

	end_for_row_check:

	; Return 1 if the row is valid, otherwise return 2
	mov eax, 1

	return_2_row:

	;; MAKE SURE TO LEAVE YOUR RESULT IN EAX BY THE END OF THE FUNCTION
	;; Remember: OK = 1, NOT_OKAY = 2
	;; ex. if this row is okay, by this point eax should contain the value 1 

	;; Freestyle ends here
end_check_row:
	;; DO NOT MODIFY

	pop edi
	pop esi
	pop edx
	pop ecx
	pop ebx
	leave
	ret
	
	;; DO NOT MODIFY

; int check_column(char* sudoku, int column);
check_column:
	;; DO NOT MODIFY
	push    ebp
	mov     ebp, esp
	push ebx
	push ecx
	push edx
	push esi
	push edi

	mov     esi, [ebp + 8]  ; char* sudoku, pointer to 81-long char array
	mov     edx, [ebp + 12]  ; int column 
	;; DO NOT MODIFY
   
	;; Freestyle starts here

	; Initialize frequency array to track occurrences of numbers 1-9
	mov edi, num_freq
	xor ecx, ecx
	for_col_setup:
		cmp ecx, 9
		jge end_for_col_setup
		mov byte [edi + ecx], 0
		inc ecx
		jmp for_col_setup
	end_for_col_setup:

	; Loop through each cell in the column to validate numbers
	xor ecx, ecx

	for_col_check:
		cmp ecx, 9
		jge end_for_col_check

		; Calculate the index for the current cell in the column
		xor eax, eax
		mov eax, ecx
		imul eax, 9
		add eax, edx
		mov al, byte [esi + eax]
		sub al, 1

		; Check if the number is within the valid range (1-9)
		cmp al, 8
		ja not_valid_col
		cmp al, 0
		jb not_valid_col

		; Check if the number has already been encountered in this column
		xor ebx, ebx
		mov bl, byte [num_freq + eax]
		cmp ebx, 0
		jne not_valid_col

		; Mark the number as encountered in the frequency array
		mov byte [num_freq + eax], 1
		jmp next_num_col

		not_valid_col:
			mov eax, 2
			jmp return_2_col

		next_num_col:
			inc ecx
			jmp for_col_check

	end_for_col_check:

	; Return 1 if the column is valid, otherwise return 2
	mov eax, 1

	return_2_col:
	

	;; MAKE SURE TO LEAVE YOUR RESULT IN EAX BY THE END OF THE FUNCTION
	;; Remember: OK = 1, NOT_OKAY = 2
	;; ex. if this column is okay, by this point eax should contain the value 1 

	;; Freestyle ends here
end_check_column:
	;; DO NOT MODIFY

	pop edi
	pop esi
	pop edx
	pop ecx
	pop ebx
	leave
	ret
	
	;; DO NOT MODIFY


; int check_box(char* sudoku, int box);
check_box:
	;; DO NOT MODIFY
	push    ebp
	mov     ebp, esp
	push ebx
	push ecx
	push edx
	push esi
	push edi

	mov     esi, [ebp + 8]  ; char* sudoku, pointer to 81-long char array
	mov     edx, [ebp + 12]  ; int box 
	;; DO NOT MODIFY
   
	;; Freestyle starts here

	; Initialize frequency array to track occurrences of numbers 1-9
	mov edi, num_freq
	xor ecx, ecx
	for_box_setup:
		cmp ecx, 9
		jge end_for_box_setup
		mov byte [edi + ecx], 0
		inc ecx
		jmp for_box_setup
	end_for_box_setup:

	; Loop through each cell in the 3x3 box to validate numbers
	xor ecx, ecx

	for_box_check:

		cmp ecx, 18
		jg end_for_box_check

		; Calculate the index for the current cell in the box
		xor eax, eax
		mov al, byte [start_idx + edx]
		add eax, ecx
		mov al, byte [esi + eax]
		sub al, 1

		; Check if the number is within the valid range (1-9)
		cmp al, 8
		ja not_valid_box
		cmp al, 0
		jb not_valid_box

		; Check if the number has already been encountered in this box
		xor ebx, ebx
		mov bl, byte [num_freq + eax]
		cmp ebx, 0
		jne not_valid_box

		; Mark the number as encountered in the frequency array
		mov byte [num_freq + eax], 1


		; Calculate the index for the next cell in the box
		xor eax, eax
		mov al, byte [start_idx + edx]
		add eax, ecx
		mov al, byte [esi + eax + 1]
		sub al, 1

		; Check if the number is within the valid range (1-9)
		cmp al, 8
		ja not_valid_box
		cmp al, 0
		jb not_valid_box

		; Check if the number has already been encountered in this box
		xor ebx, ebx
		mov bl, byte [num_freq + eax]
		cmp ebx, 0
		jne not_valid_box

		; Mark the number as encountered in the frequency array
		mov byte [num_freq + eax], 1

		; Calculate the index for the next cell in the box
		xor eax, eax
		mov al, byte [start_idx + edx]
		add eax, ecx
		mov al, byte [esi + eax + 2]
		sub al, 1

		; Check if the number is within the valid range (1-9)
		cmp al, 8
		ja not_valid_box
		cmp al, 0
		jb not_valid_box

		; Check if the number has already been encountered in this box
		xor ebx, ebx
		mov bl, byte [num_freq + eax]
		cmp ebx, 0
		jne not_valid_box

		; Mark the number as encountered in the frequency array
		mov byte [num_freq + eax], 1

		next_num_box:
			add ecx, 9
			jmp for_box_check

		not_valid_box:
			mov eax, 2
			jmp return_2_box

	end_for_box_check:

	; Return 1 if the box is valid, otherwise return 2
	mov eax, 1
	return_2_box:



	;; MAKE SURE TO LEAVE YOUR RESULT IN EAX BY THE END OF THE FUNCTION
	;; Remember: OK = 1, NOT_OKAY = 2
	;; ex. if this box is okay, by this point eax should contain the value 1 

	;; Freestyle ends here
end_check_box:
	;; DO NOT MODIFY

	pop edi
	pop esi
	pop edx
	pop ecx
	pop ebx
	leave
	ret
	
	;; DO NOT MODIFY
