test plus {
     +
} {0}

test imath-1 {
    + 1 1
} {2}

test imath-2 {
    + -1 1
} {0}

test imath-3 {
    - 10 5
} {5}

test imath-4 {
    * 6 7
} {42}

test imath-5 {
    / 6 7
} {0}

test imath-6 {
    + 1 2 3
} {6}

test double-5 {
    / 6 7.0
} {0.8571428571428571}

test double-7 {
    + 1.0 2.0
} {3.0}

test double-8 {
    + 1 2.5
} {3.5}

test round-9 {
    round [/ 10. 3]
} {3}

test sub-1 {
     -
} {0}

test sub-2 {
    - 10
} {-10}

test sub-3 {
     - 10 1
} {9}

test sub-3 {
    - 10 1 2
} {7}

test min-1 {
    min 10 20
} {10}

test min-2 {
    min 10000000 20000000
} {10000000}

test min-3 {
    min 3.14156 2.14
} {2.14}

test max-1 {
    max 10 20
} {20}

test max-2 {
    max 10000000 20000000
} {20000000}

test max-3 {
    max 3.14156 2.14
} {3.14156}