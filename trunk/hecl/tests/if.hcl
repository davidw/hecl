
test if-1 {
    if { true } {
	set foo 1
    } else {
	set foo 2
    }
    set foo
} {1}

test if-2 {
    if { > 0 1 } {
	set foo 1
    } else {
	set foo 2
    }
    set foo
} {2}

test if-3 {
    set foo 10
    if { < $foo 10 } {
	set foo 1
    } elseif { < $foo 100 } {
	set foo 2
    } else {
	set foo 3
    }
    set foo
} {2}