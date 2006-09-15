
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

test if-4 {
    set foo ""
    set a 1
    set b 2
    if {< $a 0} {
	set foo a
    } elseif {> $a $b} {
	set foo b
    }
    set foo
} {}

test if-5 {
    set foo ""
    set a 10
    if { < $a 20 } {
	set foo bar
    }
    set foo
} {bar}

test if-6 {
    set foo ""
    set a 1000

    if { < $a 10 } {
	set foo 1
    } elseif { < $a 100 } {
	set foo 2
    } elseif { < $a 200 } {
	set foo 3
    } else {
	set foo 4
    }

    set foo
} {4}

test if-7 {
     set i 1;
     if {> $i 4} {set foo 4} elseif {> $i 2} {set foo 2} else {set foo 1}
} {1}

test if-8 {
     set i 1;
     if {> $i 4} {set foo 4} elseif {> $i 2} {set foo 2} {set foo 1}
} {{ERROR {missing "else/elseif" in "if"}} {if 3}}

test if-9 {
     set i 1;
     if {> $i 4} {set foo 4} else {set foo 2} {set foo 1}
} {{ERROR {malformed "else"}} {if 3}}

test if-10 {
     set i 1;
     if {> $i 4} {set foo 4} {set foo 2}
} {{ERROR {missing "else/elseif" in "if"}} {if 3}}

test if-11 {
     set i 1;
     if {> $i 4} {set foo 4} else
} {{ERROR {malformed "else"}} {if 3}}
