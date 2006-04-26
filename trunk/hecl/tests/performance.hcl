# performance.hcl -- performance tests go here.

# Move to the current working directory.
cd [listtofile [lrange [filetolist [currentfile]] 0 -2]]


proc stopwatch {name code} {
    global times
    puts $name
    hset $times $name [time $code]
}

set times [hash {}]

# Actual test files go here.
foreach f [sort {
    loops.hcl
}] {
    source $f
}

proc hashsort {h} {
    set keys {}
    foreach {k v} $h {
	lappend $keys $k
    }
    set keys [sort $keys]
    set retval {}
    foreach k $keys {
	lappend $retval $k
	lappend $retval [hget $h $k]
    }
    return $retval
}

foreach {k v} [hashsort $times] {
    puts "Test $k took	$v	milliseconds"
}