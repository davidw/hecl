stopwatch while-1 {
    set i 1
    time {
	while { < $i 10 } {
	    incr &i
	}
    }
}

stopwatch while-2 {
    set i 1
    time {
	while { < $i 100 } {
	    incr &i
	}
    }
}

stopwatch while-3 {
    set i 1
    time {
	while { < $i 1000 } {
	    incr &i
	}
    }
}

stopwatch while-4 {
    set i 1
    time {
	while { < $i 10000 } {
	    incr &i
	}
    }
}

stopwatch while-5 {
    set i 1
    time {
	while { < $i 10000 } {
	    set i [+ &i 1]
	}
    }
}
