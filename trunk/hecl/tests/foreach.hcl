
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
