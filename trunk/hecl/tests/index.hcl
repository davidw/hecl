
test index-1 {
    lindex [list a b c d] 0
} {a}

test lindex-2 {
    lindex [list a b c d] 10
} {}

test lindex-3 {
    lindex [list a b c d] -1
} {d}

# I don't really like this behavior, as it's impossible to distinguish
# from the above.
test lindex-4 {
    lindex [list a "" c d] 1
} {}