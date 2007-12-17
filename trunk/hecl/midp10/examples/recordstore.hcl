# recordstore.hcl
# newer version by Nils-Arne Dahlberg - nilsarne@linux.nu
# recordstore example - save stuff to the phone's memory and retrieve
# it later, even if you've quit the application.

# Saving to recordstore is not always that easy.
# We need to consider the following:

# 1/ rms.set can only set an existing recordstore and index, but will
# create a non-existing recordstore

# 2/ rms.add will create a new index, beginning at 1 So, we try to set
# the record, if this fails we add a record. This should work, if
# rms.add fails, it probably means that we can't access the
# recordstore at all.
proc saveit {} {
    global existing
    global rsn
    global tf
    if {catch {rms.set [getprop $rsn text] [getprop $tf text]}} {
	rms.add [getprop $rsn text] [getprop $tf text]
    }

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
