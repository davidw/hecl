proc stopwatch {name code} {
    global times
    puts $name
    hset &times $name [time $code]
}

set times [hash {}]

foreach f [sort {
    loops.hcl
}] {
    sourcehere $f
}

foreach {k v} $times {
    puts "$k	$v"
}