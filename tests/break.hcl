test break-1 {
    set i 0
    set res ""
    while true {
	if { > $i 10 } {
	    break
	}
	set res "$res $i"
	set i [+ $i 1]
    }
    set res
} { 0 1 2 3 4 5 6 7 8 9 10}

test break-2 {
    set res "x"
    break
    set res "${res}y"
    set res
} {{BREAK } {break 3}}