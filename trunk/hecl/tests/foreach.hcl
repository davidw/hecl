
test foreach-1 {
    set lst {a b c d e}
    set res {}
    foreach el $lst {
	append $res $el
    }
    set res
} {abcde}

test foreach-2 {
    set lst {a b c d e f}
    set res {}
    foreach {m n} $lst {
	append $res "$m+$n"
    }
    set res
} {a+bc+de+f}

test foreach-3 {
    set lst {a b c d e f g}
    set res {}
    foreach {m n} $lst {
	append $res "$m+$n"
    }
    set res
} {{ERROR {Foreach argument list does not match list length}} {foreach 4}}

test foreach-4 {
    set i 0
    set res {}
    set lst {a b c d e f g}
    foreach {m} $lst {
	incr $i
	append $res $i
	continue
	append $res $m
    }
    set res
} {1234567}

test foreach-5 {
    set x 0
    foreach var {} { puts $var }
} {}


proc foreach6cmd {} {
    global foreach6
    foreach v {a b c d e} {
	append $foreach6 "v is $v\n"
    }
    return $foreach6;
}

test foreach-6 {
    global foreach6;
    # make sure var does not exist to allow
    # multiple runs of the test
    set foreach6 "";
    foreach6cmd;
    foreach6cmd;
} {v is a
v is b
v is c
v is d
v is e
v is a
v is b
v is c
v is d
v is e
}

proc fe7 {lst} {
    foreach v $lst {
    }
}

test foreach-7 {
    set locallst {a b c d e}
    fe7 $locallst
    fe7 $locallst
    fe7 $locallst
    set locallst
} {a b c d e}

test foreach-8 {
    set rl {}
    foreach x {1 2 3} {lappend $rl $x}
    set rl
} {1 2 3}