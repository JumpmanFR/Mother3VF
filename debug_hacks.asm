debug_hacks:
define state_location $02004111
define current_input_reader $02005C00
define big_text_trigger $02014304
define normal_overworld_reader $00
define debug_overworld_reader $0F

define debug_mode_flag $020143A0

.enable_debug:
push {lr}
ldr  r1,=#{debug_mode_flag}
mov  r2,#0x42
strb r2,[r1,#0]
pop  {pc}


// ==============================================
// This hack checks if a specific shortcut is being pressed.
// If it is, load the overworld debug menu.
// ==============================================
.switch_loading:
push {lr}

ldr  r1,=#{debug_mode_flag}
ldrb r2,[r1,#0]
cmp  r2,#0x42
bne  +

ldr  r1,=#0x4000130
ldrh r1,[r1,#0]
mvn  r1,r1
ldr  r2,=#{shortcut}         // Is the shortcut being pressed?
and  r1,r2
cmp  r1,r2
bne  +

add  sp,#4
bl   $8039C04                // Special loading
mov  r1,#1

b    .switch_loading_end
+

mov  r12,r0
mov  r2,r12
mov  r1,#0

.switch_loading_end:
ldr  r0,=#{state_location}   // Abuse the EMPTY party member's space
strb r1,[r0,#0]
strb r1,[r0,#1]
pop  {pc}

// ==============================================
// This hack checks if a specific shortcut is being pressed.
// If it is, change the input reading "layer".
// r2 contains which input to respond to.
// ==============================================
.switch_change_input_reader:
push {r0-r1,lr}

ldr  r1,=#{state_location}
ldrb r0,[r1,#0]
cmp  r0,#0                   // Do only if the state is set
beq  .switch_change_input_reader_end
ldrb r0,[r1,#1]              // If it's 
cmp  r0,#1
bne  +
mov  r0,#0
strb r0,[r1,#1]
b    .switch_change_input_reader_success
+
ldr  r1,=#0x2018CCA
ldrh r1,[r1,#0]
and  r1,r2
cmp  r1,r2
bne  .switch_change_input_reader_end

.switch_change_input_reader_success:
ldr  r1,=#{current_input_reader}
ldrb r0,[r1,#0]
cmp  r0,#{normal_overworld_reader}
bne  +
mov  r0,#{debug_overworld_reader}
b    .switch_change_input_reader_store
+
mov  r0,#{normal_overworld_reader}

.switch_change_input_reader_store:
strb r0,[r1,#0]

.switch_change_input_reader_end:
pop  {r0-r1,pc}

// ==============================================
// This hack checks if a specific shortcut is being pressed.
// If it is, change the input reading "layer".
// Reads input while inside the debug mode.
// ==============================================
.switch_change_input_reader_debug:
push {r2,lr}
ldr  r2,=#{shortcut_change_reader_to_normal}
bl   .switch_change_input_reader
mov  r0,#3
and  r0,r1
pop  {r2,pc}

// ==============================================
// This hack checks if a specific shortcut is being pressed.
// If it is, change the input reading "layer".
// Reads input while in the overworld.
// ==============================================
.switch_change_input_reader_normal:
push {r2,lr}
ldr  r2,=#{shortcut_change_reader_to_debug}
bl   .switch_change_input_reader
bl   $802706C
pop  {r2,pc}

// ==============================================
// This hack checks if a specific shortcut is being pressed.
// If it is, change the input reading "layer".
// Reads input while running.
// ==============================================
.switch_change_input_reader_running:
push {r2,lr}
ldr  r2,=#{shortcut_change_reader_to_debug}
bl   .switch_change_input_reader
mov  r0,#0xF0
and  r0,r1
pop  {r2,pc}

// ==============================================
// This hack prevents changing the input reading layer
// if the debug menu was closed.
// ==============================================
.remove_input_change:
push {lr}
mov  r0,#0
ldr  r1,=#{state_location}
strb r0,[r1,#0]
bl   $8039CDC
pop  {pc}

// ==============================================
// This hack makes it so text is forced into a specific position.
// ==============================================
define main_font_width $8D1CE78
define normal_space $7040
define saturn_space $709E
define num_characters $2027CB3
.force_text_into_pos:
push {r0,r2-r4,lr}
mov  r0,#3
mov  r4,#3
mov  r1,r2
bl   $8009C4C                // Get text's position
ldr  r2,=#{main_font_width}
mov  r3,#0
-
ldrb r1,[r0,#0]
cmp  r1,#0
beq  +
ldrb r1,[r2,r1]
add  r3,r3,r1
add  r0,#4
add  r4,#1
b    -
+
mov  r1,r5
sub  r1,r1,r3
mov  r3,r0
mov  r0,r1
mov  r1,#3
push {r2-r3}
swi  #6
pop  {r2-r3}                 // Get how many spaces this would require
cmp  r1,#0                   // r0 = Amount of normal spaces
beq  +                       // r1 = Amount of saturn spaces
cmp  r1,#1
bne  .mod_2

sub  r0,#3
mov  r1,#2
b    +

.mod_2:
sub  r0,#1
mov  r1,#1
+

push {r0}
ldr  r2,=#{num_characters}   // Update the total number of characters
ldrb r0,[r2,#0]
add  r0,r0,r1
strb r0,[r2,#0]
pop  {r0}
push {r1}
ldrb r1,[r2,#0]
add  r1,r1,r0
strb r1,[r2,#0]
pop  {r1}

ldr  r2,=#{normal_space}
-
cmp  r0,#0
ble  +
str  r2,[r3,#0]
add  r3,#4
add  r4,#1
sub  r0,#1
b    -
+
ldr  r2,=#{saturn_space}
-
cmp  r1,#0
ble  +
str  r2,[r3,#0]
add  r3,#4
add  r4,#1
sub  r1,#1
b    -
+
mov  r1,r4                   // Return in r1 where to put the text
pop  {r0,r2-r4,pc}

// ==============================================
// This hack makes it so the number of characters per line is even
// ==============================================
define no_width $70EB
.fix_odd:
push {r0-r3,lr}
mov  r0,#0
mov  r1,r2
bl   $8009C4C                // Get text's position
ldr  r2,=#{num_characters}
ldrb r2,[r2,#0]
ldrb r1,[r0,#0]
mov  r3,#0
str  r3,[r0,#0]
sub  r1,r2,r1                // Get the characters in this line
mov  r2,#1
mov  r3,r1
and  r3,r2
cmp  r3,#0
beq  .fix_odd_end            // Is it odd?

add  r1,#3
lsl  r1,r1,#2
add  r0,r0,r1
ldr  r1,=#{no_width}         // If it is, add a no width character to fix this
str  r1,[r0,#0]
ldr  r2,=#{num_characters}
ldrb r1,[r2,#0]
add  r1,#1
strb r1,[r2,#0]

.fix_odd_end:
pop  {r0-r3,pc}

// ==============================================
// This hack makes it so the line is cleared before printing.
// ==============================================
.blank_line:
push {r0-r2,lr}
mov  r0,#0
mov  r1,r2
bl   $8009C4C                // Get text's position
ldr  r2,=#{num_characters}
ldrb r2,[r2,#0]
str  r2,[r0,#0]
add  r0,#4 
mov  r2,#0
mov  r1,#0xFE
-
cmp  r1,#0
beq  +
str r2,[r0,#0]
add  r0,#4
sub  r1,#1
b    -
+
pop  {r0-r2,pc}

// ==============================================
// This hack makes it so map names are properly
// printed.
// ==============================================
.print_proper_map_name:
push {lr}
add  sp,#-0x108
push {r0}
ldrh r0,[r4,#0]
ldr  r1,=#0x3E7
cmp  r0,r1
bne  +
pop  {r0}
mov  r1,#0
sub  r1,#1
str  r1,[sp,#4]
add  r0,sp,#4
b    .print_proper_map_name_after_sanity_check
+
pop  {r0}
.print_proper_map_name_after_sanity_check:
mov  r4,r0
ldr  r0,=#0x600B540          // Clean the graphics' zone - Top row
ldr  r1,=#0x600B7A0-0x600B540
mov  r5,r1
ldr  r2,=#0xDDDDDDDD
mov  r6,r2
bl   $8001B54
ldr  r0,=#0x600B940          // Clean the graphics' zone - Bottom row
mov  r1,r5
mov  r2,r6
bl   $8001B54
add  r0,sp,#8
mov  r6,r0
mov  r1,#1
lsl  r1,r1,#8
mov  r2,#0
sub  r2,r2,#1
bl   $8001B54                // Setup the zone
mov  r0,r4
bl   $80025D8                // Count the letters (Stops at a newline)
mov  r2,r0
sub  r2,r2,r4
lsl  r2,r2,#0x18
lsr  r2,r2,#0x18
mov  r0,r4
mov  r1,r6
cmp  r2,#0                   // Is there anything to print?
beq  +
lsr  r2,r2,#1
bl   $8090F78                // Do the copy
+
ldrh r0,[r6,#0]
ldr  r1,=#0xFFFF
cmp  r0,r1                   // Is there anything to print?
beq  +
mov  r3,#1
neg  r3,r3
str  r7,[sp,#0]
mov  r0,r6
bl   .force_text_into_pos_wrapper_0_map
bl   $8039A18                // Print

+
add  sp,#0x108
pop  {pc}

// ==============================================
// This hack makes it so letters are not processed twice
// ==============================================
.remove_useless_writes:
push {lr}
ldr  r1,=#{big_text_trigger}
ldrb r1,[r1,#0]
cmp  r1,#1
bne  +
mov  r6,#0
b    .remove_useless_writes_end
+
add  r0,r9
ldrb r6,[r0,#0]
.remove_useless_writes_end:
pop  {pc}

// ==============================================
// This hack is a wrapper for line 0 which makes it so
// the line is cleared before printing.
// ==============================================
.blank_line_wrapper_0:
push {lr}
mov  r2,#0
bl   .blank_line
mov  r1,#3
pop  {pc}

// ==============================================
// This hack is a wrapper for line 1 which makes it so
// the line is cleared before printing.
// ==============================================
.blank_line_wrapper_1:
push {lr}
mov  r2,#1
bl   .blank_line
mov  r1,#3
pop  {pc}

// ==============================================
// This hack is a wrapper for line 2 which makes it so
// the line is cleared before printing.
// ==============================================
.blank_line_wrapper_2:
push {lr}
mov  r2,#2
bl   .blank_line
mov  r1,#3
pop  {pc}

// ==============================================
// This hack is a wrapper which makes it so
// the number of characters per line is even.
// ==============================================
.fix_odd_end_wrapper:
push {lr}
push {r2}
add  sp,#-4
push {r0}
ldr  r0,[sp,#4+4+8]
str  r0,[sp,#4]
pop  {r0}
bl   $8039A18
add  sp,#4
pop  {r2}
bl   .fix_odd
pop  {pc}

// ==============================================
// This hack is a wrapper which makes it so
// the number of characters per line is even.
// ==============================================
.fix_odd_end_wrapper_alt:
push {lr}
push {r2}
add  sp,#-8
push {r0}
ldr  r0,[sp,#4+8+8]
str  r0,[sp,#4]
ldr  r0,[sp,#4+8+0xC]
str  r0,[sp,#8]
pop  {r0}
bl   $8039A74
add  sp,#8
pop  {r2}
bl   .fix_odd
pop  {pc}

// ==============================================
// This hack is a wrapper which makes it so
// the number of characters per line is even.
// Used when there is a jump to the next element.
// ==============================================
.fix_odd_end_wrapper_special_0:
push {lr}
mov  r2,#0
bl   .fix_odd
bl   $8039B24
pop  {pc}

// ==============================================
// This hack is a wrapper which makes it so
// the number of characters per line is even.
// Used when there is a jump to the next element.
// ==============================================
.fix_odd_end_wrapper_special_1:
push {lr}
mov  r2,#1
bl   .fix_odd
bl   $8039B24
pop  {pc}

// ==============================================
// This hack is a wrapper for line 0 which makes it so
// text is forced into a specific position.
// ==============================================
define target_pixels $68
.force_text_into_pos_wrapper_0:
push {r5,lr}
mov  r5,#{target_pixels}
mov  r2,#0
bl   .force_text_into_pos
pop  {r5,pc}

// ==============================================
// This hack is a wrapper for line 0 which makes it so
// text is forced into a specific position.
// ==============================================
define target_pixels_map $30
.force_text_into_pos_wrapper_0_map:
push {r5,lr}
mov  r5,#{target_pixels_map}
mov  r2,#0
bl   .force_text_into_pos
pop  {r5,pc}

// ==============================================
// This hack is a wrapper for line 1 which makes it so
// text is forced into a specific position.
// ==============================================
.force_text_into_pos_wrapper_1:
push {r5,lr}
mov  r5,#{target_pixels}
mov  r2,#1
bl   .force_text_into_pos
pop  {r5,pc}

// ==============================================
// This hack is a wrapper for line 2 which makes it so
// text is forced into a specific position.
// ==============================================
.force_text_into_pos_wrapper_2:
push {r5,lr}
mov  r5,#{target_pixels}
mov  r2,#2
bl   .force_text_into_pos
pop  {r5,pc}

// ==============================================
// This hack prevents changing the input reading layer
// if the debug menu was closed.
// ==============================================
.fix_vwf_debug_top:
push {lr}
mov  r9,r0
ldr  r1,=#{current_input_reader}
ldrb r1,[r1,#0]
cmp  r1,#0xF
bne  +
mov  r0,#0
ldr  r1,=#{big_text_trigger}
strb r0,[r1,#0]
+
mov  r1,#0xC8
pop  {pc}


// ==============================================
// This hack prevents changing the input reading layer
// if the debug menu was closed.
// ==============================================
.fix_vwf_debug_bottom:
push {lr}
ldr  r1,=#{current_input_reader}
ldrb r1,[r1,#0]
cmp  r1,#0xF
bne  +
mov  r0,#1
ldr  r1,=#{big_text_trigger}
strb r0,[r1,#0]
+
ldr  r0,[r4,#0]
lsl  r0,r0,#0x14
pop  {pc}
