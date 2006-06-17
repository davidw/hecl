# Hecl Browser

#set mainurl http://builder.hecl.org/browser/start
#set mainurl http://eugene:3000/browser/main
set baseurl http://eugene:3000/browser

# Run a file from a URL.
proc websource {url} {
    upeval [http $url]
}

proc RunHeclet {script} {
    if { catch $script err } {
	set last [lindex $err 0]
	set errcode [lindex $last 0]
	if { eq $errcode "HECLET_EXIT" } {
	    alert text "Error!" time 2000
	} else {
	    set errmsg [lindex $last 1]
	    throw $errmsg $errcode
	}
    }
}

proc fetchrun {} {
    global listofhashes
    global main
    global baseurl
    set sel [getprop $main selected]
    set h [lindex $listofhashes $sel]
    set id [hget $h id]
    RunHeclet [http ${baseurl}/fetch/${id}]
}

proc main {} {
    set main [form label "HeclBrowser" code {
	string "Fetching a list of programs ..."
    }]

    setcurrent $main
}

main

set listofhashes [http ${baseurl}/start]

set main [listbox label "Available scripts" code {
    cmd label "Fetch and run" code fetchrun
}]

setcurrent $main

foreach h $listofhashes {
    string "[hget $h name]"
}
