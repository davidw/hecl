proc globaltestsetfoo {} {
    global foo
    set foo abc
}

proc globaltestreadfoo {} {
    global foo
    set foo
}

test global-1 {
    globaltestsetfoo
    globaltestreadfoo
} {abc}
