
test search-1 {
    set lst {1 2 3 4 5}
    search $lst x {= $x 4}
} {4}

test search-2 {
    set lst {1 2 3 4 5}
    search $lst x {= $x 8}
} {}

test search-3 {
    set lst {1 2 3 4 5 4 3 2 1}
    search $lst x {= $x 4}
} {4}

test search-4 {
    set lst {9 10 11 12 13}
    set idx 0
    search $lst x {
	incr $idx
	= $x 11
    }
    set idx
} {3}