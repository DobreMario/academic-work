section .text
extern qsort
extern strcmp
extern strlen
global sort
global get_words
global my_strcmp

my_strcmp:
    ; create a new stack frame
    enter 0, 0
    xor eax, eax

    push ebx
    push edx

    ; get first string
    mov ebx, [ebp + 8]
    mov ebx, [ebx]
    push ebx
    call strlen
    ; clean up stack
    add esp, 4

    push eax
    xor eax, eax

    ; get second string
    mov edx, [ebp + 12]
    mov edx, [edx]
    push edx
    call strlen

    ; clean up stack
    add esp, 4

    sub eax, [esp]
    ; compare lengths
    cmp eax, 0
    jg return_less
    jl return_greater

    ; compare lexicographically
    ; get first string
    mov ebx, [ebp + 8]
    ; get second string
    mov edx, [ebp + 12]
    mov ebx, [ebx]
    mov edx, [edx]
    push edx
    push ebx
    call strcmp
    ; clean up stack
    add esp, 8
    jmp cmp_done

return_less:
    ; return -1
    mov eax, -1
    jmp cmp_done

return_greater:
    ; return 1
    mov eax, 1

cmp_done:
    ; restore stack
    add esp, 4
    pop edx
    pop ebx
    leave
    ret

;; sort(char **words, int number_of_words, int size)
; functia va trebui sa apeleze qsort pentru soratrea cuvintelor
; dupa lungime si apoi lexicografix
sort:
    ; create a new stack frame
    enter 0, 0
    xor eax, eax

    ; get function arguments
    ; [ebp + 8] = words
    mov ebx, [ebp + 8]
    ; [ebp + 12] = number_of_words
    mov ecx, [ebp + 12]
    ; [ebp + 16] = size
    mov edx, [ebp + 16]


    ; call qsort
    push my_strcmp
    push edx
    push ecx
    push ebx
    call qsort
    ; clean up stack
    add esp, 16

    leave
    ret

;; get_words(char *s, char **words, int number_of_words)
; separa stringul s in cuvinte si salveaza cuvintele in wo
get_words:
    ; create a new stack frame
    enter 0, 0
    xor eax, eax

    ; get function arguments
    ; [ebp + 8] = s
    mov esi, [ebp + 8]
    ; [ebp + 12] = words
    mov edi, [ebp + 12]
    ; [ebp + 16] = number_of_words
    mov ecx, [ebp + 16]


    ; initialize counters
    xor ebx, ebx
    xor edx, edx

for:
    ; read character
    mov al, [esi]
    ; check for end of string (null terminator)
    cmp al, 0
    je end_for

    ; check delimiters
    cmp al, ' '
    je end_word
    cmp al, ','
    je end_word
    cmp al, '.'
    je end_word
    ; ASCII code for newline (\n)
    cmp al, 10
    je end_word

    ; if not inside a word
    cmp edx, 0
    jne skip

    ; store pointer to word start
    mov [edi + ebx * 4], esi
    inc ebx

    ; set inside-word flag to 1
    mov edx, 1

skip:
    inc esi
    jmp for

end_word:
    ; replace delimiter with null terminator
    mov byte [esi], 0
    inc esi

    ; reset inside-word flag to 0
    mov edx, 0
    jmp for


end_for:
    leave
    ret
