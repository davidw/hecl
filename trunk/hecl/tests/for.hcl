test for-1 {
    set out {}
    for {set i 0} {< $i 10} {incr $i} {
	puts "out is $out"
	append $out $i
    }
    set out
} "0123456789"