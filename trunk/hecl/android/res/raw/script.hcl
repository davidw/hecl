proc SimpleWidgets {} {
    global context
    global procname
    set procname SimpleWidgets

    set layoutparams [linearlayoutparams -new {FILL_PARENT WRAP_CONTENT}]

    # set swlayout [linearlayout -new $context]
    set scroll [scrollview -new $context -layoutparams $layoutparams]

    set swlayout [linearlayout -new $context -layoutparams $layoutparams]
    $swlayout setorientation VERTICAL

    $scroll addview $swlayout

    $swlayout addview [textview -new $context \
			   -text "This is a textview" \
			   -layoutparams $layoutparams]

    $swlayout addview [button -new $context -text "This is a button" \
			   -layoutparams $layoutparams]

    $swlayout addview [edittext -new $context \
			   -text "This is editable text" \
			   -layoutparams $layoutparams]

    activity setcontentview $scroll
}

proc WebView {} {
    global context
    global procname
    set procname WebView

    set layoutparams [linearlayoutparams -new {WRAP_CONTENT WRAP_CONTENT}]

    set layout [linearlayout -new $context -layoutparams $layoutparams]
    $layout setorientation VERTICAL

    set wv [webview -new $context -layoutparams $layoutparams]
    $layout addview $wv
    activity setcontentview $layout
    $wv loadurl http://www.hecl.org
}

proc datecallback {args} {
    log "datecallback"
}

proc DatePicker {} {
    global context

    set callback [callback -new [list [list datecallback]]]
    set dp [datedialog -new [list $context $callback [i 2007] [i 10] [i 10] [i 1]]]
    $dp show
}

proc TimePicker {} {
    global context

    set callback [callback -new [list [list datecallback]]]
    set tp [timedialog -new [list $context $callback "It's 5 O'clock Somewhere" [i 5] [i 0] [i 1]]]
    $dt show
}


proc SelectDemo {spinner button} {
    set v [$spinner getselectedview]
    set dest [$v gettext]
    if { eq $dest "Simple Widgets" } {
	SimpleWidgets
    } elseif {eq $dest "Web View"} {
	WebView
    } elseif {eq $dest "Date Picker"} {
	DatePicker
    } elseif {eq $dest "Time Picker"} {
	TimePicker
    }

}

proc viewCode {} {
    global procname

    set context [activity getcontext]
    set layout [linearlayout -new $context]
    $layout setorientation VERTICAL
    set layoutparams [linearlayoutparams -new {FILL_PARENT WRAP_CONTENT}]

    set text [intro proccode $procname]
    $layout addview [edittext -new $context \
			 -text [s $text] \
			 -layoutparams $layoutparams]

    set procname viewCode
    activity setcontentview $layout
}

proc main {} {
    global context
    global procname

    set procname main
    set layout [linearlayout -new $context]
    $layout setorientation VERTICAL
    set layoutparams [linearlayoutparams -new {FILL_PARENT WRAP_CONTENT}]

    set tv [textview -new $context -text {Welcome to Hecl on Android.  This is a short tour of all the widgets that currently function.} -layoutparams $layoutparams]

    $layout addview $tv

    set ala [arrayadapter -new \
		 [list $context \
		      [reslookup android.R.layout.simple_spinner_item] \
		      [list "Simple Widgets" "Web View" "Date Picker" "Time Picker" "Spinner"]]]

    $ala setdropdownviewresource [reslookup android.R.layout.simple_spinner_dropdown_item]

    set spinner [spinner -new $context]
    $spinner setadapter $ala
    $spinner setlayoutparams $layoutparams
    $layout addview $spinner

    set button [button -new $context -text "View demo" \
		    -layoutparams $layoutparams \
		    -onclicklistener [callback -new [list [list SelectDemo $spinner]]]]
    $layout addview $button

    $spinner requestfocus
    activity setcontentview $layout

    menusetup {m} {
	$m add [i 0] [i 0] "View Source"
	$m add [i 0] [i 1] "Main Screen"
    }

    menucallback {mi} {
	set id [$mi getid]
	if { = $id 1 } {
	    main
	} elseif { = $id 0 } {
	    viewCode
	}
    }
}

set context [activity getcontext]

main

#newmain