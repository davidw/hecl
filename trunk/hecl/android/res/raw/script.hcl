## script.hcl - this is a series of Hecl/Android examples
## demonstrating various parts of the API.

# SimpleWidgets --
#
#	This procedure is called to put some simple widgets up on the
#	screen.

proc heclcmd {subcmd target} {
    set hecl [activity]
    $hecl $subcmd $target
}

proc SimpleWidgets {} {
    global context
    global procname
    set procname SimpleWidgets

    set layoutparams [linearlayoutparams -new {FILL_PARENT WRAP_CONTENT}]

    set scroll [scrollview -new $context -layoutparams $layoutparams]

    set swlayout [linearlayout -new $context -layoutparams $layoutparams]
    $swlayout setorientation VERTICAL

    $scroll addview $swlayout
    heclcmd setcontentview $scroll

    $swlayout addview [textview -new $context \
			   -text "This is a textview" \
			   -layoutparams $layoutparams]

    $swlayout addview [button -new $context -text "This is a button" \
			   -layoutparams $layoutparams]

    $swlayout addview [edittext -new $context \
			   -text "This is editable text" \
			   -layoutparams $layoutparams]

    java "android.widget.DigitalClock" "digitalclock"

    $swlayout addview [digitalclock -new $context \
			   -layoutparams $layoutparams]

    java android.widget.ImageButton imagebutton

    set ib [imagebutton -new $context -layoutparams $layoutparams]
    $swlayout addview $ib
    $ib setImageResource [reslookup "R.drawable.buttonhecl"]

}

# WebView --
#
#	Demonstrate the WebView widget.

proc WebView {} {
    global context
    global procname
    set procname WebView

    set layoutparams [linearlayoutparams -new {WRAP_CONTENT WRAP_CONTENT}]

    set layout [linearlayout -new $context -layoutparams $layoutparams]
    $layout setorientation VERTICAL

    java "android.webkit.WebView" webview

    set wv [webview -new $context -layoutparams $layoutparams]
    $layout addview $wv
    heclcmd setcontentview $layout
    # Fetch the Hecl web page, which, unfortunately, isn't all that
    # beautiful ...
    $wv loadurl http://www.hecl.org
    $wv requestfocus
}

# Callback --
#
#	Generic callback proc to hand off to various widgets.

proc Callback {args} {
    set args [lrange $args 1 -1]
    alert "Callback called with arguments: $args"
}

# DatePicker --
#
#	Put a datepicker dialog up on the screen.

proc DatePicker {} {
    global context

    java "android.app.DatePickerDialog" datedialog

    set callback [callback -new [list [list Callback]]]
    set dp [datedialog -new [list $context $callback 2007 10 10 1]]
    $dp show
}

# TimePicker --
#
#	Put a time picker dialog up on the screen.

proc TimePicker {} {
    global context

    java "android.app.TimePickerDialog" timedialog

    set callback [callback -new [list [list Callback]]]
    set tp [timedialog -new \
		[list $context $callback \
		     "It's 5 o'clock somewhere" 5 0 1]]
    $tp show
}

# ProgressDialog --
#
#	Create a "progress bar", and, via the updateProgress proc,
#	update it and finally dismiss it.

proc ProgressDialog {} {
    global context

    java "android.app.ProgressDialog" progressdialog

    set pd [progressdialog show $context "Working..." \
		"This is a progress \"bar\"" 0 0]
    updateProgress $pd 0
}

# updateProgress --
#
#	This proc is called at intervals to update the progress
#	dialog, and then dismiss it when its time is up.  This is done
#	via the after command.

proc updateProgress {pd progress} {
    $pd setprogress [i $progress]
    if { < $progress 10000 } {
	after 1000 [list updateProgress $pd [+ 2000 $progress]]
    } else {
	$pd dismiss
    }
}

# RadioButtons --
#
#	Put some radio buttons up on the screen.  These don't work
#	correctly due to a bug in Android.

proc RadioButtons {} {
    global procname
    set procname RadioButtons
    global context

    set layoutparams [radiogrouplayoutparams -new {FILL_PARENT WRAP_CONTENT}]

    set layout [linearlayout -new $context]
    $layout setorientation VERTICAL

    # Since this is added to the linearlayout, it has to have
    # linearlayoutparams
    set radiogroup \
	[radiogroup -new $context \
	     -layoutparams [linearlayoutparams -new {FILL_PARENT FILL_PARENT}]]

    $radiogroup setorientation VERTICAL

    $layout addview $radiogroup

    # FIXME - broken - but it's Google's fault!
    $radiogroup addview [radiobutton -new $context \
			     -text "Android" -layoutparams $layoutparams]
    $radiogroup addview [radiobutton -new $context \
			     -text "JavaME" -layoutparams $layoutparams]
    $radiogroup addview [radiobutton -new $context \
			     -text "Flash Lite" -layoutparams $layoutparams]
    heclcmd setcontentview $layout
}

# CheckBoxes --
#
#	Put some checkboxes up on the screen, and, through the
#	CheckBoxCallback proc, make sure that only two of the three
#	are selected.

proc CheckBoxes {} {
    global procname
    set procname CheckBoxes
    global context

    set layoutparams [linearlayoutparams -new {FILL_PARENT WRAP_CONTENT}]

    set layout [linearlayout -new $context]
    $layout setorientation VERTICAL

    $layout addview [textview -new $context -text "Pick any two:" \
			 -layoutparams $layoutparams]

    set cb1 [checkbox -new $context \
		 -text "Fast" -layoutparams $layoutparams]
    set cb2 [checkbox -new $context \
		 -text "Cheap" -layoutparams $layoutparams]
    set cb3 [checkbox -new $context \
		 -text "Good" -layoutparams $layoutparams]

    set callback [callback -new [list [list CheckBoxCallback $cb1 $cb2 $cb3]]]

    foreach cb [list $cb1 $cb2 $cb3] {
	$cb setoncheckedchangelistener $callback
	$layout addview $cb
    }

    heclcmd setcontentview $layout
}

# CheckBoxCallback --
#
#	This is the callback for the CheckBox code.  It ensures that
#	the user can only select two out of the three options.

proc CheckBoxCallback {cb1 cb2 cb3 checkbox ischecked} {
    # Only do something if the checkbox has been checked, rather than
    # unchecked.
    if { = 1 $ischecked } {
	set total 0
	foreach cb [list $cb1 $cb2 $cb3] {
	    incr $total [$cb ischecked]
	}
	# If they've checked the third of three, uncheck it.
	if { = $total 3 } {
	    $checkbox setchecked 0
	}
    }
}

# Spinner --
#
#	Displays a spinner, and links it to a textview via a callback,
#	SpinnerCallback.

proc Spinner {} {
    global procname
    set procname Spinner
    global context

    set layoutparams [linearlayoutparams -new {FILL_PARENT WRAP_CONTENT}]

    set layout [linearlayout -new $context]
    $layout setorientation VERTICAL

    set spinner [basicspinner $context [list "Wheel" "Of" "Fortune!"] \
		     -layoutparams $layoutparams]
    $layout addview $spinner
    # requestfocus is necessary or you won't be able to access the
    # spinner if it's redisplayed.  I think this is an Android bug.
    $spinner requestfocus

    set selected [textview -new $context -text "Currently selected: Wheel" \
		      -layoutparams $layoutparams]
    $layout addview $selected

    set callback [callback -new [list [list SpinnerCallback $selected]]]
    $spinner setonitemselectedlistener $callback

    heclcmd setcontentview $layout
}

# SpinnerCallback --
#
#	Display the currently selected spinner item.

proc SpinnerCallback {textview parent view position id} {
    set text [$view gettext]
    $textview settext "Currently selected: $text"
}

# HeclEditor --
#
#	Edit and run simple Hecl scripts.

proc HeclEditor {} {
    global context

    set procname HeclEditor
    global context

    set layoutparams [linearlayoutparams -new {FILL_PARENT WRAP_CONTENT}]

    set layout [linearlayout -new $context]
    $layout setorientation VERTICAL

    $layout addview [textview -new $context \
			 -text "Hecl evaluator: enter code below and hit 'eval' to evaluate" \
			 -layoutparams $layoutparams]

    # The default script:
    set script {proc AddOne {num} {
    return [+ $num 1]
}
AddOne 41}

    set editor [edittext -new $context -text $script -layoutparams $layoutparams]
    $layout addview $editor

    set eval [button -new $context -text "Eval" -layoutparams $layoutparams]
    $layout addview $eval

    set results [textview -new $context -text "Results:" -layoutparams $layoutparams]
    $layout addview $results

    set callback [callback -new [list [list EditCallback $editor $results]]]
    $eval setonclicklistener $callback
    heclcmd setcontentview $layout
}

# EditCallback --
#
#	Run the script and display the results.

proc EditCallback {editor results button} {
    set res "Results: [eval [$editor gettext]]"
    $results settext $res
}

# Contacts --
#
#	Display the phone's contact list.

proc Contacts {} {
    global context

    set procname Contacts
    global context

    set layoutparams [linearlayoutparams -new {FILL_PARENT WRAP_CONTENT}]

    set layout [linearlayout -new $context]
    $layout setorientation VERTICAL

    set cursor [query content://contacts/people/]

    while { $cursor next } {
	set name [$cursor getstring 6]
	set number [$cursor getstring 5]
	$layout addview [textview -new $context \
			     -layoutparams $layoutparams \
			     -text "Who: $name Number: $number"]
    }

    heclcmd setcontentview $layout
}

# TaskList --
#
#	Display a list of taks, and let the user switch between them.

proc TaskList {} {
    global context

    set procname TaskList
    global context

    set layoutparams [linearlayoutparams -new {FILL_PARENT WRAP_CONTENT}]

    set layout [linearlayout -new $context]
    $layout setorientation VERTICAL

    # Create ourselves some commands to access Android internals.
    java android.app.ActivityManagerNative activitymanagernative
    java android.app.IActivityManager iactivitymanager
    java {android.app.IActivityManager$TaskInfo} taskinfo
    java android.content.ComponentName componentname

    # Utilized to contain the result of gettasks
    java java.util.List javalist
    set am [activitymanagernative getdefault]
    set tasks [$am gettasks 10 0 [null]]
    set len [$tasks size]
    set tasklist [list]
    for {set i 0} {< $i $len} {incr $i} {
	set baseactivity [[$tasks get $i] -field baseactivity]
	lappend $tasklist "Task: [$baseactivity getpackagename]"
    }

    $layout addview [textview -new $context -layoutparams $layoutparams \
			 -text "Currently running tasks"]

    set lview [basiclist $context $tasklist -layoutparams $layoutparams]
    $layout addview $lview
    $lview requestfocus

    set callback [callback -new [list [list SelectTask]]]
    $lview setonitemclicklistener $callback

    heclcmd setcontentview $layout
}

proc SelectTask {parent view position id} {
    [activitymanagernative getdefault] movetasktofront $position
}


# SelectScripts --
#
#	Display the phone's contact list.

proc SelectScripts {} {
    global context

    set procname SelectScripts
    global context

    set layoutparams [linearlayoutparams -new {FILL_PARENT WRAP_CONTENT}]

    set layout [linearlayout -new $context -layoutparams $layoutparams]
    $layout setorientation VERTICAL

    set cursor [query content://org.hecl.android.Scripts/scripts]

    $layout addview [textview -new $context \
			 -layoutparams $layoutparams \
			 -text "Available scripts:"]

    set scriptlist [list]
    while { $cursor next } {
	lappend $scriptlist [$cursor getstring 1]
    }

    set lview [basiclist $context $scriptlist -layoutparams $layoutparams]
    $layout addview $lview
    $lview requestfocus

    heclcmd setcontentview $layout
}

# Activity --
#
#	Create a new Activity that is independent of this one.
#	newActivity is defined in lib.tcl

proc Activity {} {
    global context

    set script {
	set context [activity]
	set layoutparams [linearlayoutparams -new {FILL_PARENT WRAP_CONTENT}]

	set layout [linearlayout -new $context -layoutparams $layoutparams]
	$layout setorientation VERTICAL
	$layout addview [textview -new $context \
			     -layoutparams $layoutparams \
			     -text "Hello World"]

	$context setcontentview $layout
	$context settitle "My new SubHecl!"
    }

    newActivity $context $script
}

# SelectDemo --
#
#	Select which demo to display.

proc SelectDemo {parent view position id} {
    set dest [$view gettext]
    if { eq $dest "Hecl Editor" } {
	HeclEditor
    } elseif {eq $dest "New Activity"} {
	Activity
    } elseif {eq $dest "Hecl Scripts"} {
	SelectScripts
    } elseif { eq $dest "Contacts" } {
	Contacts
    } elseif { eq $dest "Simple Widgets" } {
	SimpleWidgets
    } elseif {eq $dest "Web View"} {
	WebView
    } elseif {eq $dest "Date Picker"} {
	DatePicker
    } elseif {eq $dest "Progress Dialog"} {
	ProgressDialog
    } elseif {eq $dest "Radio Buttons"} {
	RadioButtons
    } elseif {eq $dest "CheckBoxes"} {
	CheckBoxes
    } elseif {eq $dest "Spinner"} {
	Spinner
    } elseif {eq $dest "Time Picker"} {
	TimePicker
    } elseif {eq $dest "Task List"} {
	TaskList
    }
}

# viewCode --
#
#	View the code of the proc that is currently displayed.

proc viewCode {} {
    global procname
    global context

    set layout [linearlayout -new $context]
    $layout setorientation VERTICAL
    set layoutparams [linearlayoutparams -new {FILL_PARENT WRAP_CONTENT}]

    set text [intro proccode $procname]
    $layout addview [edittext -new $context \
			 -text [s $text] \
			 -layoutparams $layoutparams]

    set procname viewCode
    heclcmd setcontentview $layout
}

# main --
#
#	The initial, main screen.

proc main {} {
    global context
    global procname

    set procname main
    set layout [linearlayout -new $context]
    $layout setorientation VERTICAL
    set layoutparams [linearlayoutparams -new {FILL_PARENT WRAP_CONTENT}]

    set tv [textview -new $context -text {Welcome to Hecl on Android.  This is a short tour of all the widgets that currently function.} -layoutparams $layoutparams]

    $layout addview $tv

    set lview [basiclist $context [list "Simple Widgets" "Web View" "Date Picker" \
				       "Time Picker" "Progress Dialog" "Spinner" \
				       "Radio Buttons" "CheckBoxes" "Contacts" "Task List" \
				       "Hecl Editor" "New Activity" "Hecl Scripts" ] \
		   -layoutparams $layoutparams]

    $lview requestfocus
    $layout addview $lview

    set callback [callback -new [list [list SelectDemo]]]
    $lview setonitemclicklistener $callback

    heclcmd setcontentview $layout

    # Used to set up a callback for when the menu is requested by the
    # user, and it's necessary to set it up.
    menusetup {m} {
	$m add 0 0 "View Source"
	$m add 0 1 "Main Screen"
    }

    # Sets up the actual callback code for when a menu item is
    # selected.
    menucallback {mi} {
	set id [$mi getid]
	if { = $id 1 } {
	    main
	} elseif { = $id 0 } {
	    viewCode
	}
    }
}

# This is used everywhere, so making it a global is no big deal.
set context [activity]

# Start things running.
main
