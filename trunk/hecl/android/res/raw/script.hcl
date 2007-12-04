proc SimpleWidgets {} {
    global context
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
    set layoutparams [linearlayoutparams -new {WRAP_CONTENT WRAP_CONTENT}]

    set layout [linearlayout -new $context -layoutparams $layoutparams]
    $layout setorientation VERTICAL

    set wv [webview -new $context -layoutparams $layoutparams]
    $layout addview $wv
    activity setcontentview $layout
    $wv loadurl http://www.hecl.org
}

proc DatePicker {} {
    global context
    set layoutparams [linearlayoutparams -new {WRAP_CONTENT WRAP_CONTENT}]

    set datepicker [datepicker -new [list $context [null] [null]] \
			-layoutparams $layoutparams]
    $datepicker init [i 2007] [i 11] [i 10] [i 0] [null]
    activity setcontentview $datepicker
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
    }
}

proc main {} {
    global context
    set context [activity getcontext]
    set layout [linearlayout -new $context]
    $layout setorientation VERTICAL
    set layoutparams [linearlayoutparams -new {FILL_PARENT WRAP_CONTENT}]

    set tv [textview -new $context -text {Welcome to Hecl on Android.  This is a short tour of all the widgets that currently function.} -layoutparams $layoutparams]

    textview -new $context -text {Select a widget} -layoutparams $layoutparams

    set ala [arrayadapter -new [list \
				    $context \
				    [reslookup android.R.layout.simple_spinner_item] \
				    [list "Simple Widgets" "Web View" "Date Picker" "Spinner"]]]
    $ala setdropdownviewresource [reslookup android.R.layout.simple_spinner_dropdown_item]

    set spinner [spinner -new $context]
    $spinner setadapter $ala
    $spinner setlayoutparams $layoutparams
    $layout addview $spinner

    set button [button -new $context -text "View demo" \
		    -layoutparams $layoutparams \
		    -onclicklistener [callback -new [list [list SelectDemo $spinner]]]]
    $layout addview $button

    activity setcontentview $layout
}

main