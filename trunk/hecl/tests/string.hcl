
test string-1 {
    strlen "foo"
} 3

test string-2 {
    strindex "foo" 1
} o

test string-3 {
    strindex "foo" 10
} ""

test string-4 {
    set foo foobar
    strlen $foo
} 6