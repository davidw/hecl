
test while-1 {
    set i 0
    set out {}
    while { < $i 10 } {
	append $out " $i"
	set i [+ $i 1]
    }
    set out
} { 0 1 2 3 4 5 6 7 8 9}