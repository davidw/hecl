
test ref-1 {
    set foo x
    set bar &foo
    set foo y
    set bar
} y

test ref-2 {
    set foo x
    set bar [ref foo]
    set foo y
    set bar
} y

test ref-3 {
    set foo x
    set bar $foo
    set foo y
    set bar
} x

test ref-4 {
    set foo a
    set bar b
    set baz [list &foo &bar]
    set foo x
    set baz
} {x b}

test ref-5 {
    set foo x
set str "
set foo x
set bar &foo
set foo y"
    eval $str
    set bar
} x
