
test string-1 {
    slen "foo"
} 3

test string-2 {
    sindex "foo" 1
} o

test string-3 {
    sindex "foo" 10
} ""

test string-4 {
    set foo foobar
    slen $foo
} 6