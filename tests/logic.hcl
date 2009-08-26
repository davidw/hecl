# logic.hcl -- logic commands

test logic-1 {
    and 0 1
} {0}

test logic-2 {
    and 1 1
} {1}

test logic-3 {
    and 2 4
} {0}

test logic-4 {
    and 1 1 1 1 1 1 0
} {0}

test logic-5 {
    not 0
} {1}

test logic-6 {
    not 1
} {0}

test logic-7 {
    not 2
} {0}

test logic-8 {
    not 0
} {1}

test logic-9 {
    or 0
} {0}

test logic-10 {
    or 1
} {1}

test logic-11 {
    or 0 0
} {0}

test logic-12 {
    or 0 1
} {1}

test logic-13 {
    or 0 1 2 3 4 5
} {7}
