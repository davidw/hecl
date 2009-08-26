# hash.hcl - hash tests

test hash-1 {
    set foo [hash {a b c d}]
    sort [list [hget $foo a] [hget $foo c]]
} {b d}

test hash-2 {
    set foo [hash {a b c d}]
    hset $foo a 1
    hset $foo c 2
    list [hget $foo a] [hget $foo c]
} {1 2}

test hash-3 {
    hget {a b c d} c
} d

test hash-4 {
    set foo [hash {a b c}]
} {{ERROR {list must have even number of elements} 2} {hash 1}}

test hash-5 {
    set foo [hash {a b c d}]
    hset [copy $foo] a 1
    hset [copy $foo] x y
    sort [join $foo]
} {a b c d}

test hash-6 {
    set count 0
    set myhash [hash {a 1}]
    while { < $count 4 } {
	incr $count
	hset $myhash $count $count
	set myhash [hash { }]
    }
    set myhash
} {}