proc stopwatch {name code} {
    global times
    hset &times $name [time $code]
}

set times [hash {}]

stopwatch while-1 {
    set i 1
    time {
	while { < $i 100 } {
#	    puts $i
	    set i [+ $i 1]
	}
    }
}

puts $times