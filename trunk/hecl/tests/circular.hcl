# circular.hcl - funky circular references

test circular-1 {
    set x {a b}
    set y $x
    set ret [lappend $x $y]
    copy $ret
} {{ERROR {reference hard limit - circular reference?}} {copy 5}}

test circular-2 {
    set x {a b}
    set y $x
    lappend $x $y
} {}