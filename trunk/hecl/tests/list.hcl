test list-1 {
    set bar [list a b   c]
    set bar
} {a b c}


test list-2 {
    set bar {a b c}
    set bar
} {a b c}

test list-3 {
    set bar [list a   b c]
    set bar
} {a b c}

test list-4 {
    set bar [list a b c]
    set foo &bar
    lappend &bar d
    set foo
} {a b c d}

