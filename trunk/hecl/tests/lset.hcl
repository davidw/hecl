
test lset-1 {
    set lst {a b c}
    lset $lst 1 x
    set lst
} {a x c}

# lset with no fourth argument deletes the element.

test lset-2 {
    set lst {a b c}
    lset $lst 1
    set lst
} {a c}