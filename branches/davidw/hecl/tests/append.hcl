
test append-1 {
    set foo "bar"
    append foo x
} {barx}

test append-2 {
    append foo x
} {x}

test append-3 {
    append foo x y z
} {xyz}