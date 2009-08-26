
test incr-1 {
    set x 100
    incr $x
    set x
} {101}

test incr-2 {
    set x 100
    incr $x 10
    set x
} {110}

test incr-3 {
    proc incrtmp {} {
	global tmp;
	incr $tmp;
    }

    proc p2 {} {
	global tmp;
	
	set tmp 0
	incrtmp;
	incrtmp;
    }
    p2;
    p2;
    global tmp;
    set tmp
} {2}

