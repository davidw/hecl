
test math-1 {
    + 1 1
} {2}

test math-2 {
    + -1 1
} {0}

test math-3 {
    - 10 5
} {5}

test math-4 {
    * 6 7
} {42}

test math-5 {
    / 6 7
} {0.8571428571428571}

test math-6 {
    + 1 2 3
} {6}

test math-7 {
    + 1.0 2.0
} {3}

test double-8 {
    + 1 2.5
} {3.5}

test round-9 {
    round [/ 10 3]
} {3}

test subtract-1 {
    - 10
} {-10}

test subtract-2 {
    - 10 1 2
} {7}
