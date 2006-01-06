test hash-1 {
    set foo [hash {a b c d}]
    sort [list [hget &foo a] [hget &foo c]]
} {b d}

test hash-2 {
    set foo [hash {a b c d}]
    hset &foo a 1
    hset &foo c 2
    list [hget &foo a] [hget &foo c]
} {1 2}

test hash-3 {
    hget {a b c d} c
} d

test hash-4 {
    set foo [hash {a b c}]
} {{ERROR {list must have even number of elements}} hash}

test hash-5 {
    set foo [hash {a b c d}]
    hset $foo a 1
    hset $foo x y
    set foo
} {a b c d}