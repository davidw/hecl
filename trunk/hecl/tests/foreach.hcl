
test foreach-1 {
    set lst {a b c d e}
    set res {}
    foreach el $lst {
	append &res $el
    }
    set res
} {abcde}

test foreach-2 {
    set lst {a b c d e f}
    set res {}
    foreach {m n} $lst {
	append &res "$m+$n"
    }
    set res
} {a+bc+de+f}

test foreach-3 {
    set lst {a b c d e f g}
    set res {}
    foreach {m n} $lst {
	append &res "$m+$n"
    }
    set res
} {{ERROR {Foreach argument list does not match list length}} foreach}

test foreach-4 {
    set i 0
    set res {}
    set lst {a b c d e f g}
    foreach {m} $lst {
	incr &i
	append &res $i
	continue
	append &res $m
    }
    set res
} {1234567}
