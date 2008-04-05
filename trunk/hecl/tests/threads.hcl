# This test exists to test how well threads behave.  If the eval
# method isn't synchronized, this test crashes and burns.

proc bangbang {} {
   after 100 bangbang
}

proc threadmainproc {} {
    set starttime [clock seconds]
    while { true } {
	global foobar
	incr $foobar
	set now [clock seconds]
	if { > [- $now $starttime] 10 } {
	    return "everything ok"
	}
    }
}

test threads-1 {
    puts "Running threads-1 test - please wait about 15 seconds to see if it completes successfully"
    global foobar
    set foobar 1
    after 100 bangbang
    threadmainproc
} {everything ok}

test threads-2 {
    puts "Running threads-2 test - this one takes about 10 seconds"

    proc repeatit {} {
	global http_event_test
	lappend $http_event_test [hget [http.geturl http://hecl.org] ncode]
	after 1 repeatit
    }

    proc bye {} {
	tnotify forever
    }

    set e [after 10000 bye]
    set f [after 1 repeatit]

    global http_event_test
    set http_event_test [list]
    twait forever

    puts "repeatit ran [llen $http_event_test] times"

    set myres [< 3 [llen $http_event_test]]
    set myres
} {1}
