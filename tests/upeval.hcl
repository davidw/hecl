
proc stackframe {} {
    upeval { incr $foo }
}

test upeval-1 {
    set foo 1
    stackframe
    set foo
} {2}

proc do {code while condition} {
    upeval $code
    while { upeval $condition } {
	upeval $code
    }
}

test upeval-2 {
    set x 100
    set foo ""
    do {
	append $foo $x
	incr $x
    } while {< $x 10}
    set foo
} {100}

proc globalset {} {
    global upeval3
    set upeval3 100
}

test upeval-3 {
    globalset
    upeval 0 {
	set upeval3
    }
} {100}

test upeval-4 {
    set foo 1
    catch {
	upeval { bleagh }
    } err
    list $foo $err
} {1 {{ERROR {Command 'bleagh' does not exist} 1} {upeval 2}}}