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

set a_global_variable 1

proc globalincr {} {
    global a_global_variable
    incr $a_global_variable
}

# Broken - needs upeval command to work at a global level to work
# completely.
test global-3 {
    set testres ""
    lappend $testres $a_global_variable
    globalincr
    lappend $testres $a_global_variable
    set testres
} {}