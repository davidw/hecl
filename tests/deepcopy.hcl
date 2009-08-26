# Tests the deep copying of different object types.

test deepcopy-int-1 {
    set i 0
    set j [copy $i]
    incr $i
    incr $j
    list $i $j
} {1 1}

test deepcopy-string-1 {
    set i "hello"
    set j [copy $i]
    append $j " world"
    list $i $j
} {hello {hello world}}

test deepcopy-codething-1 {
    set res {}
    set i 0
    set loop {incr $i}

    append $res [copy $loop]

    while { < $i 100 } $loop

    append $res [copy $loop]

    set res
} {incr $iincr ${i}}
