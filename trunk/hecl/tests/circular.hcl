# circular.hcl - funky circular reference

test circular-1 {
    set x 1
    puts ...................
    set y [list &x]
    puts -------------------
    set x &y
    llen $x
} 1
