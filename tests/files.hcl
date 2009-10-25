# files.hcl - hecl file implementation

test write-1 {
    set fn "/tmp/hecl-test-[clock seconds]"
    set f [open $fn w]
    $f write "hello world"
    $f close

    set f [open $fn]
    $f read
} {hello world}

test write-2 {
    set fn "/tmp/hecl-test-[clock seconds]"
    set f [open $fn w]
    $f write "hello world"
    $f close

    file.size $fn
} {11}

test write-3 {
    set fn "/tmp/hecl-test-[clock seconds]"
    set f [open $fn w]
    for {set i 0} {<= $i 1000} {incr $i} {
	$f writeln "hello world $i"
    }
    $f close

    file.size $fn
} {15907}

test write-read-1 {
    set fn "/tmp/hecl-test-[clock seconds]"
    set f [open $fn w]
    for {set i 0} {<= $i 1000} {incr $i} {
	$f writeln "hello world $i"
    }
    $f close

    set f [open $fn]
    set data [$f read]
    $f close
    set i 0
    set errors 0
    # See if what we have written matches what we read
    foreach line [split $data "\n"] {
	set sline [split $line]
	if { < 2 [llen $sline] } {
	    if { != [lindex $sline 2] $i } {
		incr $errors
	    }
	}
	incr $i
    }

    set errors
} {0}

test readln-1 {
    set fn "/tmp/hecl-test-[clock seconds]"
    set f [open $fn w]
    for {set i 0} {<= $i 1000} {incr $i} {
	$f writeln "hello world $i"
    }
    $f close

    set f [open $fn]
    set i 0
    set line ""
    while { true } {
	set lline $line
	set line [$f readln]
	if { eq $line "" } {
	    break
	}
	incr $i
    }
    list $i $lline
} {1001 {hello world 1000}}
