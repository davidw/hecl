proc returntest {} {
    set foo 1
    return &foo
    set foo 2
    return &foo
}

test return-1 {
    returntest
} 1

proc emptyreturn {} {
    set x 1
    return
}

test return-2 {
    emptyreturn
} 1