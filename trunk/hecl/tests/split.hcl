
test split-1 {
    split "aaa;bbb;ccc" ;
} {aaa bbb ccc}

test split-2 {
    split "aaa bbb ccc"
} {aaa bbb ccc}

