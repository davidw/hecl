
test proc-1 {
    set a 1
    proc foo {a b} {
	set a [+ $a 1]
	+ $a $b
    }
    set res "$a [foo $a 5]"
    set res
} {1 7}

test proc-2 {
    proc badcommand {a b} {}
    badcommand a b c d
} {{ERROR {proc badcommand has too many arguments}} badcommand}

test proc-3 {
    proc badcommand {a b} {}
    badcommand a
} {{ERROR {proc badcommand doesn't have enough arguments}} badcommand}