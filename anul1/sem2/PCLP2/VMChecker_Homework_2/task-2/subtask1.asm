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
	days_in_month db 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31

section .text
	global check_events
	extern printf

check_events:
	;; DO NOT MODIFY
	enter 0,0
	pusha

	mov ebx, [ebp + 8]      ; events
	mov ecx, [ebp + 12]     ; length
	;; DO NOT MODIFY

	;; Your code starts here

	   start_loop:

		cmp ecx, 0
		je end_loop

		; check year
		cmp word [ebx + event.date_year], 1990
		jl invalid
		cmp word [ebx + event.date_year], 2030
		jg invalid

		; check month
		cmp byte [ebx + event.date_month], 0
		jl invalid
		cmp byte [ebx + event.date_month], 12
		jg invalid

		; check day
		movzx eax, byte [ebx + event.date_month]
		dec eax
		movzx eax, byte [days_in_month + eax]
		cmp byte [ebx + event.date_day], 0
		jle invalid
		cmp byte [ebx + event.date_day], al
		jg invalid

		jmp valid

		; invalid setup

		invalid:
			mov byte [ebx + event.valid], 0
			jmp inc_event
		end_invalid:

		; valid setup

		valid:
			mov byte [ebx + event.valid], 1
			jmp inc_event
		end_valid:
	
		inc_event:
			add ebx, 36
			dec ecx
			jmp start_loop
		end_inc_event:
	
	end_loop:

	;; Your code ends here

	;; DO NOT MODIFY
	popa
	leave
	ret
	;; DO NOT MODIFY