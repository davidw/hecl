
test set-1 {
    set foo 1
    set foo
} 1

test set-2 {
    set foo x
    set bar [copy $foo]
    set foo y
    set bar
} x

proc zealousref {} {
    set myvar ""
    set myvar "more $myvar"
    return $myvar
}

proc zealousref2 {} {
    set refvar "a b c d"
    set refvar "more $refvar"
    return $refvar
}

test set-3 {
    zealousref
    zealousref
    zealousref
} {more }

test set-4 {
    zealousref2
    zealousref2
    zealousref2
} {more a b c d}

test unset-1 {
    set a 1
    unset a
    set a
} {{ERROR {Variable a does not exist}} {set 4}}

# This refers to the EMPTYTHING defined in Thing.java
test emptything-1 {
    set a [puts a]
    append $a x
    set b [puts b]
} {}
