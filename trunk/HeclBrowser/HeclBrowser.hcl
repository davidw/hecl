# Hecl Browser

set baseurl http://builder.hecl.org/browser
#set baseurl http://eugene:3000/browser

# Run a file from a URL.
proc websource {url} {
    upeval [http $url]
}

proc RunHeclet {script} {
    if { catch $script err } {
	set last [lindex $err 0]
	set errcode [lindex $last 0]
	if { eq $errcode "HECLET_EXIT" } {
	    return
	} else {
	    set errmsg [lindex $last 1]
	    alert text "Error: $errmsg" time 2000
	    return
	}
    }
}

proc fetchrun {script_id} {
    global baseurl
    RunHeclet [http ${baseurl}/fetch/${script_id}]
}

proc main {} {
    set main [form label "HeclBrowser" code {
	string "Fetching a list of programs ..."
    }]

    setcurrent $main
}

main

RunHeclet [http ${baseurl}/main]