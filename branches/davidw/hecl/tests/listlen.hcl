
test listlen-1 {
    llen {a b c}
} 3

test listlen-2 {
    llen [list a b c d]
} 4

test listlen-3 {
    set lst [list a b "c d" {e f}]
    llen $lst
} 4