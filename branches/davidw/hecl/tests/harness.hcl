
proc test {name code result} {
    # puts "Running $name: "
    catch $code res
    if { = $res $result } {
	ok $name
    } else {
	fail $name $res $result
    }
}

proc testfiles {files} {
    foreach f $files {
	sourcehere $f
    }
}

proc ok {name} {
    global ok
    puts "$name ok"
    lappend &ok $name
}

proc fail {name result expected} {
    global failed
    puts "$name FAILED"
    puts "expected: $expected"
    puts "received: $result"
    lappend &failed $name
}

proc totals {} {
    global ok
    global failed
    puts "[llen $ok] tests passed"
    puts "[llen $failed] tests failed: $failed"
}