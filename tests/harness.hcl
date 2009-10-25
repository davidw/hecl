# harness.hcl -- test harness procedures.

proc test {name code result} {
    # puts "Running $name"
    catch $code res
    # puts "Code was $code"
    # puts "Result was $res"
    if { eq $res $result } {
	ok $name
    } else {
	fail $name $res $result
    }
}

proc testfiles {files} {
    global ok
    global failed
    # clear success/error list
    set ok [list]
    set failed [list]
    foreach f $files {
	puts "Running $f"
	source $f
    }
}

proc ok {name} {
    global ok
    # puts "$name		ok"
    lappend $ok $name
}

proc fail {name result expected} {
    global failed
    puts "$name FAILED"
    puts "expected: $expected"
    puts "received: $result"
    lappend $failed $name
}

proc totals {} {
    global ok
    global failed
    puts "[llen $ok] tests passed"
    puts "[llen $failed] tests failed: $failed"
}
