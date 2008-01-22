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