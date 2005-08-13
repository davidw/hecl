proc globaltestsetfoo {} {
    global foo
    set foo abc
}

proc globaltestreadfoo {} {
    global foo
    set foo
}

proc settwoglobals {} {
    global bee bop
    set bee 1
    set bop 2
}

proc twoglobals {} {
    global bee bop
    set foo [list [set bee] [set bop]]
}

test global-1 {
    globaltestsetfoo
    globaltestreadfoo
} {abc}


test global-2 {
    settwoglobals
    twoglobals
} {1 2}