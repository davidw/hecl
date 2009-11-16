# performance.hcl -- performance tests go here.

# Move to the correct directory.
set destdir [file.join [lrange [file.split [file.current]] 0 -2]]
puts "Running in $destdir"
file.cd $destdir

proc stopwatch {name code} {
    global times
    puts $name
    hset $times $name [time $code]
}

set times [hash {}]

# Actual test files go here.
foreach f [sort {
    loops.hcl sort-performance.hcl
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
