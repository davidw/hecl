
test set-1 {
    set foo 1
    set foo
} 1

test set-2 {
    set foo x
    set bar $foo
    set foo y
    set bar
} x

proc zealousref {} {
    set myvar ""
    set myvar "more $myvar"
    return &myvar
}

test set-3 {
    zealousref
    zealousref
    zealousref
} {more }