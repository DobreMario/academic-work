%include "../include/io.mac"

; declare your structs here

struc event
	.name resb 31
	.valid resb 1
	.date_day resb 1
	.date_month resb 1
	.date_year resw 1
endstruc

section .data
	events_num dd 0 ; number of events

section .text
	global sort_events
	global event_cmp
	extern printf

event_cmp:
	enter 0, 0
	push ebx
	push ecx
	push edx

	mov ebx, [ebp + 8]
	mov edx, [ebp + 12]

	; cmp the two events

		; by valid

	xor eax, eax
	mov al, byte [ebx + event.valid]
	cmp al, byte [edx + event.valid]
	jl return_1
	jg return_minus_1

		; by year

	xor eax, eax
	mov ax, word [ebx + event.date_year]
	cmp ax, word [edx + event.date_year]
	jl return_minus_1
	jg  return_1


		; by month

	mov al, byte [ebx + event.date_month]
	cmp al, byte [edx + event.date_month]
	jl return_minus_1
	jg return_1

		; by day

	mov al, byte [ebx + event.date_day]
	cmp al, byte [edx + event.date_day]
	jl return_minus_1
	jg return_1

		; by name

	lea ebx, [ebx + event.name]
	lea edx, [edx + event.name]

	xor ecx, ecx
	strcmp:
		cmp ecx, 31
		jge end_strmp
		xor eax, eax
		mov al, [ebx]
		cmp al, [edx]
		jg return_1
		jl return_minus_1
		inc ebx
		inc edx
		inc ecx
		jmp strcmp
	end_strmp:

	; up is a try to remake the strcmp function :P

	mov eax, 0	; equal
	jmp end_event_cmp

	return_minus_1:
		mov eax, -1	; less :,(
		jmp end_event_cmp

	return_1:
		mov eax, 1 ; greater B)
		jmp end_event_cmp

end_event_cmp:
	pop edx
	pop ecx
	pop ebx
	leave
	ret

sort_events:
	;; DO NOT MODIFY
	enter 0, 0
	pusha

	mov ebx, [ebp + 8]      ; events - adresa primului eveniment
	mov ecx, [ebp + 12]     ; length - numărul de evenimente
	;; DO NOT MODIFY

	;; Your code starts here

	mov [events_num], ecx	;store the number of events

	xor ecx, ecx

	for1:

		cmp ecx, [events_num]
		jge end_for1

		mov edx, ebx	; edx points to the current event
		mov edi, ebx	; edi points to the minimum event found so far

		mov esi, ecx	; save the index of the for1 loop
		inc ecx

		for2:
			; for2 loop: find the smallest event in the remaining list
			cmp ecx, [events_num]
			jge end_for2

			add edx, event_size

			push edi
			push edx
			call event_cmp
			add esp, 8

			cmp eax, 0
			jg skip_min

			mov edi, edx

			skip_min:
			inc ecx
			jmp for2

		end_for2:

		xor ecx, ecx

		swap_mem:
			; swap the events
			cmp ecx, event_size
			jge end_swap_mem

			xor eax, eax
			mov al, [edi + ecx]
			xchg [ebx + ecx], al
			mov [edi + ecx], al

			inc ecx
			jmp swap_mem

		end_swap_mem:

		add ebx, event_size
		mov ecx, esi	; restore the outer loop index
		inc ecx
		jmp for1

	end_for1:

	;; DO NOT MODIFY
	popa
	leave
	ret
	;; DO NOT MODIFY
