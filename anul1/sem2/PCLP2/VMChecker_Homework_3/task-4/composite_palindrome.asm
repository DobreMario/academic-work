section .data
    ; int print for debugging
    int_print db "%d", 10, 0
    ; string print for debugging
    str_print db "%s", 10, 0


section .bss
    ; int len
    len resd 1
    ; int max_total_len
    max_len resd 1
    ; char *temp
    temp resd 1
    ; best palindrome found so far (char *best)
    best resd 1
    ; length of best palindrome found (int best_len)
    best_len resd 1
    ; count of valid palindrome subsets (int total_subs)
    total_subs resd 1
    ; length of current concatenated string (int temp_len)
    temp_len resd 1
    ; index variable used in loops (int i)
    i resd 1
    ; total number of subsets = 2^len (int total_subsets)
    total_subsets resd 1
    ; current subset bitmask (int mask)
    mask resd 1
    ; length of current word (int word_len)
    word_len resd 1

section .text
    ; External C functions
    extern malloc, free, strlen, memcpy, strcpy, strcmp, printf
    ; Function from task 1
    global check_palindrome
    ; Task 2 function + auxiliary functions
    global __calc_max_len, __buffers_allocator, __calc_total_subsets, composite_palindrome

check_palindrome:
    ; create a new stack frame
    enter 0, 0
    xor eax, eax
    push ebx
    push ecx

    ; [ebp + 8] = str
    mov ebx, [ebp + 8]
    ; [ebp + 12] = len
    mov ecx, [ebp + 12]
    dec ecx
    ; edx = left index (starting from 0)
    xor edx, edx

while:
    ; compare left and right pointers
    cmp dl, cl
    jge end_while

    ; compare characters at positions edx and ecx
    mov eax, [ebx + edx]
    cmp al, [ebx + ecx]
    jne return_zero

    inc edx
    dec ecx
    jmp while

end_while:
    xor eax, eax
    ; return 1
    mov al, 1
    jmp end

return_zero:
    ; return 0
    xor eax, eax

end:
    pop ecx
    pop ebx
    leave
    ret


__calc_max_len:
    ; create a new stack frame
    enter 0, 0

    push esi
    push edi
    push ebx

    ; i = 0
    xor edx, edx
    ; [ebp + 8] = strs
    mov esi, [ebp + 8]
    ; max_total_len = 0
    xor eax, eax
    xor ebx, ebx

calc_loop:
    ; while (i < len)
    cmp edx, [ebp + 12]
    jge done_len

    ; edi = strs[i]
    mov edi, [esi]
    ; strlen(strs[i])
    push edx
    push edi
    call strlen
    ; clean up stack
    add esp, 4
    pop edx

    ; max_total_len += strlen(strs[i])
    add ebx, eax
    ; move to next string
    add esi, 4
    ; i++
    inc edx
    jmp calc_loop

done_len:
    ; return max_total_len
    xor eax, eax
    mov eax, ebx

    pop ebx
    pop edi
    pop esi

    leave
    ret


__calc_total_subsets:
    ; create a new stack frame
    enter 0, 0

    push esi
    push edi
    push ebx

    xor eax, eax
    xor ecx, ecx
    ; eax = 1 (base value for shift)
    mov eax, 1
    ; ecx = len
    mov cl, byte [ebp + 8]
    ; eax = 1 << len (compute total subsets)
    shl eax, cl

    pop ebx
    pop edi
    pop esi

    leave
    ret


__buffers_alocator:
    ; create a new stack frame
    enter 0, 0

    push esi
    push edi
    push ebx

    ; temp = malloc(max_total_len + 1)
    mov eax, [ebp + 8]
    push eax
    call malloc
    ; clean up the stack
    add esp, 4
    mov [temp], eax

    ; best = malloc(1)
    push dword 1
    call malloc
    ; clean up the stack
    add esp, 4
    mov [best], eax

    mov ebx, [best]
    ; best[0] = '\0'
    mov byte [ebx], 0

    ; best_len = 0
    mov dword [best_len], 0

    xor eax, eax

    pop ebx
    pop edi
    pop esi

    leave
    ret


__brute_force:
    ; create a new stack frame
    enter 0, 0

    push esi
    push edi
    push ebx

    ; [ebp + 8] = strs
    mov esi, [ebp + 8]

    ; mask = 1
    xor ecx, ecx
    inc ecx

mask_for:
    ; for (mask < total_subsets)
    cmp ecx, [total_subsets]
    jge end_mask_for
    mov [mask], ecx

    ; temp_len = 0
    xor ecx, ecx
    mov [temp_len], ecx

    mov eax, [temp]
    ; temp[0] = '\0'
    mov byte [eax], 0

i_for:
    ; for (i < len), [ebp + 12] = len
    cmp ecx, [ebp + 12]
    jge end_i_for
    mov [i], ecx

    xor ecx, ecx
    xor eax, eax
    inc eax
    mov ecx, [i]
    shl eax, cl
    mov ecx, [mask]
    and eax, ecx
    ; if (mask & (1 << i))
    cmp eax, 0
    jne skip_set

    mov ecx, [i]
    ; word_len = strlen(strs[i])
    mov eax, [esi + 4 * ecx]
    push eax
    call strlen
    ; clean up stack
    add esp, 4
    mov [word_len], eax

    ; ebx = temp + temp_len
    mov ebx, [temp]
    add ebx, [temp_len]

    mov ecx, [i]
    ; eax = strs[i]
    mov eax, [esi + 4 * ecx]

    ; memcpy(temp + temp_len, strs[i], word_len)
    push dword [word_len]
    push eax
    push ebx
    call memcpy
    ; clean up stack
    add esp, 12

    ; temp_len += word_len
    mov eax, [temp_len]
    add eax, [word_len]
    mov [temp_len], eax

skip_set:
    mov eax, [temp]
    add eax, [temp_len]
    ; temp[temp_len] = '\0'
    mov byte [eax], 0

    push dword [temp_len]
    mov eax, [temp]
    push eax
    call check_palindrome
    ; clean up stack
    add esp, 8
    ; if (check_palindrome(temp))
    cmp eax, 0
    je end_if_palindrome

    ; if (temp_len > best_len || ...
    mov eax, [temp_len]
    mov ebx, [best_len]
    cmp eax, ebx
    jg update_best
    jne end_if_palindrome

    ; ... || (temp_len == best_len && strcmp(temp, best) < 0)

    ; strcmp(temp, best)
    mov eax, [best]
    push eax
    mov eax, [temp]
    push eax
    call strcmp
    ; clean up stack
    add esp, 8
    ; strcmp(temp, best) < 0
    cmp eax, 0
    jge end_if_palindrome

update_best:
    ; free(best)
    mov eax, [best]
    push eax
    call free
    ; clean up stack
    add esp, 4

    ; best = malloc(temp_len + 1)
    mov eax, [temp_len]
    inc eax
    push eax
    call malloc
    ; clean up stack
    add esp, 4
    mov [best], eax

    ; strcpy(best, temp)
    mov eax, [temp]
    push eax
    mov eax, [best]
    push eax
    call strcpy
    ; clean up stack
    add esp, 8

    ; best_len = temp_len
    mov eax, [temp_len]
    mov [best_len], eax

end_if_palindrome:
    ; i++
    mov ecx, [i]
    inc ecx
    jmp i_for

end_i_for:
    ; mask++
    mov ecx, [mask]
    inc ecx
    jmp mask_for

end_mask_for:
    ; free(temp)
    mov eax, [temp]
    push eax
    call free
    ; clean up stack
    add esp, 4

    ; return best
    mov eax, [best]

    pop ebx
    pop edi
    pop esi

    leave
    ret


composite_palindrome:
    ; create a new stack frame
    enter 0, 0

    push esi
    push edi
    push ebx

    xor edx, edx
    xor eax, eax

    ; [ebp + 8] = strs
    mov esi, [ebp + 8]
    ; [ebp + 12] = len
    mov ebx, [ebp + 12]

    push ebx
    push esi
    call __calc_max_len
    ; clean up stack
    add esp, 8

    mov [max_len], eax

    xor eax, eax

    push dword [max_len]
    call __buffers_alocator
    ; clean up stack
    add esp, 4

    xor eax, eax

    push ebx
    call __calc_total_subsets
    ; clean up stack
    add esp, 4

    mov [total_subsets], eax

    push ebx
    push esi
    call __brute_force
    ; clean up stack
    add esp, 4

    pop ebx
    pop edi
    pop esi

    leave
    ret

