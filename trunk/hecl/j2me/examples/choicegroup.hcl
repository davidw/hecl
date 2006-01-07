# choicegroup.hcl --

# choicegroup example

set langlist {English French German Italian Spanish}

proc NewLang {} {
    global sel
    global cg
    global langlist
    set lang [getprop &cg selected]
    setprop &sel text [lindex &langlist &lang]
}

set f [form label "ChoiceGroup example" code {
    stringitem label "Chose from one of the following"
    set cg [choicegroup label "Languages" list &langlist callback NewLang]
    set sel [stringitem label "Selected" text ""]
}]

setcurrent &f