# circular.hcl - funky circular references

test circular-1 {
    set x 1
    set y [list &x]
    set x &y
    llen $x
} {{ERROR {reference hard limit - circular reference?}}}
