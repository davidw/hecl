
test sort-1 {
    set foo [sort {f a sd d d e q f j}]
    set foo
} {a d d e f f j q sd}

test sort-2 {
    set foo [sort { 5 6 7 1 1 2 3 4 2 1}]
    set foo
} {1 1 1 2 2 3 4 5 6 7}

test sort-3 {
    set foo [sort {5 6 7 10 100 20 3 4 2 1}]
    set foo
} {1 2 3 4 5 6 7 10 20 100}