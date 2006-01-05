# textbox.hcl --

# textbox example

set tb [textbox label "TextBox Example" text "Blah

blah

blah blah" code {
    cmd label "Exit" code exit
}]

setcurrent &tb
