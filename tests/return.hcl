proc returntest {} {
    set foo 1
    return $foo
    set foo 2
    return $foo
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
} {}

test return-3 {
    set x 10
    puts foo
} {}

test return-4 {
    set x 10
    set x
    set res [puts x]
    append $res [puts y]
    set res
} {}