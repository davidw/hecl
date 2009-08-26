# Test the after command.

test after-1 {
    global x
    after 100 {set x 1}
    after 200 {lappend $x 2}
    after 400
    set x
} {1 2}
