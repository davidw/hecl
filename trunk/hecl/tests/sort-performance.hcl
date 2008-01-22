# Bubble sort implementation.

stopwatch bubblesort-1 {
    set B {27 67 70 58 48 88 45 86 55 22 74 25 14 69 2 52 42 47 72 0
	78 39 89 16 40 62 95 18 61 91 23 92 98 38 76 36 41 26 64 34 54 65 24
	33 99 19 12 50 82 44 75 71 4 35 56 66 17 37 10 46 6 29 31 3 90 84}

    time {
	set A [copy $B]
	set count [llen $A]
	set swapped true
	while {$swapped} {
	    set swapped false
	    for {set i 0} {< $i [- $count 1]} {incr $i} {
		set j [+ $i 1]
		if {> [lindex $A $i] [lindex $A $j]} {
		    set tmp [lindex $A $j]
		    lset $A $j [lindex $A $i]
		    lset $A $i $tmp
		    set swapped true
		}
	    }
	}
    } 100
}