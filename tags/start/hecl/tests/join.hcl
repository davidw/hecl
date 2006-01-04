
test join-1 {
    join [list aaa bbb ccc]
} {aaa bbb ccc}

test join-2 {
    join [list aaa bbb ccc] ""
} {aaabbbccc}

test join-3 {
    join [list aaa bbb ccc] "xyz"
} {aaaxyzbbbxyzccc}

