
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

test rename-2 {
    proc rn2 {} {return barney}
    set r [rn2]
    rename rn2 rn2new
    catch {
	append $r [rn2]
    } foo
    append $r [rn2new]
    list $r $foo
} {barneybarney {{ERROR {Command 'rn2' does not exist} 2}}}

proc varargproc {a b args} {
    return "$a $b $args"
}

test varargs-1 {
    varargproc x
} {{ERROR {proc varargproc doesn't have enough arguments}} {varargproc 2}}

test varargs-2 {
    varargproc x y
} {x y }

test varargs-3 {
    varargproc x y z
} {x y z}

test varargs-4 {
    varargproc x y 1 2 3 4 5 6
} {x y 1 2 3 4 5 6}

test recurse-1 {
    proc Recurse {o} {
	if {> $o 1} {
	    set res "<"
	    append $res [Recurse [- $o 1]]
	    append $res ">"
	    return $res
	} else {
	    return $o
	}
    }
    Recurse 5
} {<<<<1>>>>}