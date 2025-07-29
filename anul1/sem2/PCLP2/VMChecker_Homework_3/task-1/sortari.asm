section .text
global sort

;   struct node {
;    int val;
;    struct node* next;
;   };

struc node
.val resd 1
.next resd 1
endstruc

;; struct node* sort(int n, struct node* node);
;   The function will link the nodes in the array
;   in ascending order and will return the address
;   of the new found head of the list
; @params:
;   n -> the number of nodes in the array
;   node -> a pointer to the beginning in the array
;   @returns:
;   the address of the head of the sorted list


sort:
; create a new stack frame
enter 0, 0
xor eax, eax
xor esi, esi
xor ecx, ecx

; ecx = n
mov ecx, [ebp + 8]
; esi = node
mov esi, [ebp + 12]

for1:
; compare with 0 (loop end condition)
cmp ecx, 0
jle end_for1

; save outer loop counter
push ecx

xor ecx, ecx
; ecx = number of nodes
mov ecx, [ebp + 8]
; edi = pointer to nodes array
mov edi, [ebp + 12]

xor ebx, ebx
; ebx = current node value
mov ebx, [esi + node.val]
; we are searching for value + 1
inc ebx

for2:
; compare with 0 (loop end condition)
cmp ecx, 0
jle end_for2

xor edx, edx
mov edx, [edi + node.val]

; found node with value == current + 1
cmp edx, ebx
je set

dec ecx
; node_size = 8 (sizeof(struct node))
add edi, 8
jmp for2

end_for2:
; compare with 0 (no match found)
cmp ecx, 0
je inc_label

set:
; set 'next' pointer to the matching node
mov [esi + node.next], edi

inc_label:
pop ecx
; node_size = 8 (sizeof(struct node))
add esi, 8
dec ecx
jmp for1

end_for1:
; eax = pointer to nodes array
mov eax, [ebp + 12]
xor ebx, ebx
; we are searching for the node with value 1
mov bl, 1
; ecx = number of nodes
mov ecx, [ebp + 8]

for:
; compare with 0 (loop end condition)
cmp ecx, 0
je end_for

cmp [eax + node.val], ebx
je end_for

; node_size = 8 (sizeof(struct node))
add eax, 8
dec ecx
jmp for

end_for:
leave
ret