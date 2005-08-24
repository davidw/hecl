
test incr-1 {
    set x 100
    incr &x
    set x
} {101}

test incr-2 {
    set x 100
    incr &x 10
    set x
} {110}

