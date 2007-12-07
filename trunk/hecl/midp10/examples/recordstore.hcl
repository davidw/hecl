# recordstore.hcl
#
# Newer version by Nils-Arne Dahlberg
# uses rms.* commands
# Also added an exit-command
#
# recordstore example - save stuff to the phone's memory and retrieve
# it later, even if you've quit the application.

proc saveit {} {
    global existing
    global rsn
    global tf
    rms.put [getprop $rsn text] [getprop $tf text]

    set lst {}

    foreach k [rms.list] {
	lappend $lst "Name: $k Size: [rms.size $k] Available: [rms.sizeavail $k]"
    }

    setprop $existing text [join $lst "\n"]
}

proc getit {} {
    global rsn
    global tf
    setprop $tf text [rms.get [getprop $rsn text]]
}

set f [form label SaveIt code {
    set existing [stringitem label "Existing:" text [rms.list]]
    set rsn [textfield label "Name:"]
    set tf [textfield label "Write it:"]
    cmd label "Save it" code saveit
    cmd label "Fetch it" code getit
    cmd label exit type exit code exit
}]

setcurrent $f
