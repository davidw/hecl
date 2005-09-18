# path.hcl - path manipulation tests.

test filetolist-1 {
    filetolist /a/b/c
} {/ a b c}

test filetolist-2 {
    filetolist a/b/c
} {a b c}

test filetolist-3 {
    filetolist a
} {a}

test filetolist-4 {
    filetolist /a
} {/ a}

test filetolist-5 {
    filetolist ""
} {}

test listtofile-1 {
    listtofile {a b c}
} {a/b/c}

test listtofile-2 {
    listtofile {/ a b c}
} {/a/b/c}
