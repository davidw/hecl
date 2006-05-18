# form.hcl --

# form example, with stringitem, string, and textfield.

proc newitem {type} {
    global tf
    $type label [getprop $tf text]
}

set f [form label "Form Example" code {
    string "This is an example of a lcdui 'form'\n You can create new items dynamically by specifying the name below and selecting the appropriate command"
    set tf [textfield label "Item label:"]
    cmd label "choicegroup" code {newitem choicegroup}
    cmd label "datefield" code {newitem datefield}
    cmd label "gauge" code {newitem gauge}
    cmd label "textfield" code {newitem textfield}
    cmd label exit type exit code exit
}]

setcurrent $f