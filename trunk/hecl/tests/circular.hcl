# circular.hcl - funky circular references

test circular-1 {
    set x {a b}
    set y $x
    set ret [lappend $x $y]
    copy $ret
} {{ERROR {reference hard limit - circular reference?}} {copy 5}}

# FIXME - this one crashes the test suite, so I've commented it out
# until I can deal with it properly - davidw.

# test circular-2 {
#     set x {a b}
#     set y $x
#     lappend $x $y
# } {}