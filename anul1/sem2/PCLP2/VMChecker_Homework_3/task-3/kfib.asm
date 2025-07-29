section .text
global kfib

kfib:
    ; Create a new stack frame
    enter 0, 0
    xor eax, eax
    xor esi, esi

    ; [ebp + 8] = n
    mov ecx, [ebp + 8]

    ; [ebp + 12] = K
    cmp ecx, [ebp + 12]
    jl return_zero
    je return_one

    ; [ebp + 12] = K
    sub ecx, [ebp + 12]

for:
    ; [ebp + 8] = n
    cmp ecx, [ebp + 8]
    jge end_for

    push ecx
    push esi

    ; [ebp + 12] = K
    push dword [ebp + 12]
    ; ecx = n - K
    push ecx
    call kfib
    ; clean up stack
    add esp, 8

    pop esi
    pop ecx

    add esi, eax

    inc ecx
    jmp for

end_for:
    mov eax, esi
    jmp end

return_zero:
    ; return 0 if n < K
    mov eax, 0
    jmp end

return_one:
    ; return 1 if n == K
    mov eax, 1

end:
    leave
    ret