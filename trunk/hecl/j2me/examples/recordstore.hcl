# recordstore.hcl

# recordstore example - save stuff to the phone's memory and retrieve
# it later, even if you've quit the application.

proc saveit {} {
    global existing
    global rsn
    global tf
    rs_put [getprop $rsn text] [getprop $tf text]

    set lst {}
    foreach k [rs_list] {
	lappend $lst "Name: $k Size: [rs_size $k] Available: [rs_sizeavail $k]"
    }

    setprop $existing text [join $lst "\n"]
}

proc getit {} {
    global rsn
    global tf
    setprop $tf text [rs_get [getprop $rsn text]]
}

set f [form label SaveIt code {
    set existing [stringitem label "Existing:" text [rs_list]]
    set rsn [textfield label "Name:"]
    set tf [textfield label "Write it:"]
    cmd label "Save it" code saveit
    cmd label "Fetch it" code getit
}]

setcurrent $f
