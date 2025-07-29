%include "../include/io.mac"

extern printf
global base64

section .data
	alphabet db 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
	bit_mask db 0
	fmt db "%d", 10, 0

section .text

base64:
	;; DO NOT MODIFY

    push ebp
    mov ebp, esp
	pusha

	mov esi, [ebp + 8] ; source array
	mov ebx, [ebp + 12] ; n
	mov edi, [ebp + 16] ; dest array
	mov edx, [ebp + 20] ; pointer to dest length

	;; DO NOT MODIFY


	; -- Your code starts here --

	; Set bit_mask to 0x3F (binary: 111111) to isolate 6 bits
	; This is used to extract Base64 indices
	xor eax, eax
	mov eax, 1
	shl eax, 6
	sub eax, 1
	mov [bit_mask], eax

	xor eax, eax

	; Initialize destination length to 0
	mov dword [edx], 0

	; Initialize loop counter
	mov ecx, 0
	for:
		; Loop through the source array in chunks of 3 bytes
		; Convert each chunk into 4 Base64 characters
		cmp ecx, ebx
		jge end_for

		; Collect 3 bytes from the source array
		mov al, byte [esi + ecx]
		shl eax, 8
		or al, byte [esi + ecx + 1]
		shl eax, 8
		or al, byte [esi + ecx + 2]

		; Push the collected 3 bytes onto the stack
		push eax
		xor eax, eax

		; Extract the first 6 bits (highest) and map to Base64 alphabet
		mov eax, [esp]
		shr eax, 18
		and eax, [bit_mask]
		mov al, byte [alphabet + eax]
		mov [edi], al

		; Extract the next 6 bits and map to Base64 alphabet
		xor eax, eax
		mov eax, [esp]
		shr eax, 12
		and eax, [bit_mask]
		mov al, byte [alphabet + eax]
		mov [edi + 1], al

		; Extract the third 6 bits and map to Base64 alphabet
		xor eax, eax
		mov eax, [esp]
		shr eax, 6
		and eax, [bit_mask]
		mov al, byte [alphabet + eax]
		mov [edi + 2], al

		; Extract the last 6 bits (lowest) and map to Base64 alphabet
		xor eax, eax
		mov eax, [esp]
		and eax, [bit_mask]
		mov al, byte [alphabet + eax]
		mov [edi + 3], al

		; Move to the next chunk: increment pointers and counters
		add edi, 4
		add ecx, 3
		add dword [edx], 4
		add esp, 4

		jmp for
	end_for:
	; End of the loop
	
	; -- Your code ends here --


	;; DO NOT MODIFY

	popa
	leave
	ret

	;; DO NOT MODIFY