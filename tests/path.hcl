# path.hcl - path manipulation tests.

test file.split-1 {
    file.split /a/b/c
} {/ a b c}

test file.split-2 {
    file.split a/b/c
} {a b c}

test file.split-3 {
    file.split a
} {a}

test file.split-4 {
    file.split /a
} {/ a}

test file.split-5 {
    file.split ""
} {}

test file.join-1 {
    file.join {a b c}
} {a/b/c}

test file.join-2 {
    file.join {/ a b c}
} {/a/b/c}
