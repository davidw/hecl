
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
} {{ERROR {proc badcommand has too many arguments}} {badcommand 3}}

test proc-3 {
    proc badcommand {a b} {}
    badcommand a
} {{ERROR {proc badcommand doesn't have enough arguments}} {badcommand 3}}

test proc-4 {
    proc f1 {} {return hello}
    proc f2 {} {return world}
    proc f3 {f} {$f}
    set res ""
    append $res [f3 f1]
    append $res [f3 f2]
    set res
} {helloworld}

test rename-1 {
    proc f1 {} {return fred}
    set r [f1]
    rename f1 newcmd
    append $r [newcmd]
    set r
} {fredfred}
