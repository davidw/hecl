# choicegroup.hcl --

# choicegroup example

set langlist

proc NewLang {} {
    global sel
    global cg
    set lang [getprop &cg selected]
    setprop &sel text &lang
}

set f [form label "ChoiceGroup example" code {
    stringitem label "Chose from one of the following"
    set cg [choicegroup label "Languages" list {English French German Italian Spanish} callback NewLang]
    set sel [stringitem label "Selected" text ""]
}]

setcurrent &f