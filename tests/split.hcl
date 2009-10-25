
test split-1 {
    split "aaa;bbb;ccc" ";"
} {aaa bbb ccc}

test split-2 {
    split "aaa bbb ccc"
} {aaa bbb ccc}

test split-3 {
    split "aaaxbbbycccxyddd" "xy"
} {aaaxbbbyccc ddd}

test split-4 {
    split "abcdefg" ""
} {a b c d e f g}

