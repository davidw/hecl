# script.hcl -- Hecl example script.

# Display the error frame with text $txt
proc err {txt} {
    global errf
    setcurrent $errf
    string $txt
};

# Run the main script.
proc run {} {
    global main
    if { catch {upeval [getprop $main text]} problem } {
	err $problem
    }
}

# Create a 'back' button - backto is the screen to return to.
proc makebackbutton {backto} {
    return [list cmd label Back code [list setcurrent $backto] type back]
}

# Helper for form eval
proc formeval {textfield results} {
    setprop $results text \
	[eval [getprop $textfield text]]
}

# Callback for fast/cheap/good choicegroup

proc fastcheapgood {choicegroup} {
    set sel [getprop $choicegroup selected]
    if { < 2 [llen $sel] } {
	setprop $choicegroup selected {}
    }
}

# Create a form.
proc makeform {} {
    global lbox
    set newform [form label "MIDP Form" code {
	stringitem label "An example form"
	set tf [textfield label "Eval hecl code:"]
	set results [stringitem label "Results:"]

	gauge label "How cool is Hecl?" maxval 10 val 10

	datefield label "Select date/time"

	set fcg [choicegroup label "Chose any two:" list {Cheap Fast Good} type multiple]
	setprop $fcg callback [list fastcheapgood $fcg]

	cmd label "Eval" code [list formeval $tf $results]
	eval [makebackbutton $lbox]
    }]
    setcurrent $newform
}

# listbox helper
proc listboxcallback {lst} {
    global newlistbox
    set selected [getprop $newlistbox selected]
    set newlist ""
    foreach i $selected {
	lappend $newlist [lindex $lst $i]
    }

    setprop $newlistbox label "Selected: [join $newlist]"
}

# Create a listbox
proc makelistbox {} {
    global lbox
    global newlistbox
    set lbl [list Apples Bananas Oranges Pears]
    set newlistbox \
	[listbox label "MIDP ListBox" list $lbl callback \
	     [list listboxcallback $lbl] code {
	    eval [makebackbutton $lbox]
	}]
    setcurrent $newlistbox
}

# Create a textbox
proc maketextbox {} {
    global lbox
    set newtextbox [textbox label "MIDP TextBox" code {
	eval [makebackbutton $lbox]
    } text "This is an example of a TextBox"]
    setcurrent $newtextbox
}

# Create an alert
proc makealert {} {
    set newalert [alert label "MIDP Alert" text "This is an example of an Alert!"]
    setcurrent $newalert
}

set screenhash [hash {
    0 form
    1 listbox
    2 textbox
    3 alert
}]

# Create a new gui element depending on what's selected.
proc choose {} {
    global lbox
    global screenhash
    set sel [getprop $lbox selected]
    set cmd "make[hget $screenhash $sel]"
    $cmd
}

# View source for examples.
proc viewsource {} {
    global lbox
    global screenhash
    set sel [getprop $lbox selected]
    set screen [hget $screenhash $sel]
    set code [intro proccode "make$screen"]
    set tb [textbox label "Source for $screen example" text $code code {
	eval [makebackbutton $lbox]
    }]
    setcurrent $tb
}

# Initial script to run.
set script {# 'Run' to continue...
set lbox [listbox label "Hecl examples" code {
    string "Create form"
    string "Create listbox"
    string "Create textbox"
    string "Create alert"
    cmd label "Run" code choose
    cmd label "See source" code viewsource
    cmd label {Exit} type exit code exit
} type exclusive]
setcurrent $lbox
# 'Run' to continue...
}

# Error report form
set errf [form label Error code {
    cmd label Back type back code {setcurrent $main}
}];

# Initial textbox
set main [textbox label Hecl code {
    cmd label Run code run ;
    cmd label "View errors" code {setcurrent $errf} ;
} len 900 text $script];

setcurrent $main;
