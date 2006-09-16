proc globaltestsetfoo {} {
    global foo
    set foo abc
}

proc globaltestreadfoo {} {
    global foo
    set foo
}

proc settwoglobals {} {
    global bee bop
    set bee 1
    set bop 2
}

proc twoglobals {} {
    global bee bop
    set foo [list [set bee] [set bop]]
}

test global-1 {
    globaltestsetfoo
    globaltestreadfoo
} {abc}


test global-2 {
    settwoglobals
    twoglobals
} {1 2}


proc globalincr {} {
    global a_global_variable
    incr $a_global_variable
}

test global-3 {
    upeval 0 {
	set a_global_variable 1
	set testres ""
	lappend $testres [copy $a_global_variable]
	globalincr
	lappend $testres $a_global_variable
	set testres
    }
} {1 2}

test global-4 {
    global g4res

    proc global-4-2 {} { global b
	global g4res
	set b 5
	append $g4res "b = $b"
    }
    proc global-4-1 {} { global b
	global g4res
	global-4-2
	append $g4res "b = $b"
    }

    upeval 0 {
	global-4-1
	list $b $g4res
    }
} {5 {b = 5b = 5}}