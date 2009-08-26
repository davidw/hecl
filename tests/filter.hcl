
test filter-1 {
    set lst {1 2 3 4 5}
    filter $lst x {= $x 4}
} {4}

test filter-2 {
    set lst {1 2 3 4 5}
    filter $lst x {= $x 8}
} {}

test filter-3 {
    set lst {1 2 3 4 5 4 3 2 1}
    filter $lst x {= $x 4}
} {4 4}
