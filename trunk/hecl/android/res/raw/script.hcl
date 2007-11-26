proc SimpleWidgets {} {
    global context

    set swlayout [linearlayout [list $context]]
    $swlayout setorientation VERTICAL
    set tv [textview -text "This is a textview" -layout_width fill_parent \
		-layout_height wrap_content -layout $swlayout]
    set button [button -text "This is a button" -layout_width fill_parent \
		    -layout_height wrap_content -layout $swlayout]
    activity setcontentview $swlayout
}

proc SelectDemo {spinner button} {
    set v [$spinner getselectedview]
    set dest [$v gettext]
    if { eq $dest "Simple Widgets" } {
	SimpleWidgets
    }
}

proc main {} {
    global context
    set context [activity getcontext]
    set layout [linearlayout [list $context]]
    $layout setorientation VERTICAL
    set layoutparams [linearlayoutparams {FILL_PARENT WRAP_CONTENT}]

    set tv [textview -text {Welcome to Hecl on Android.  This is a short tour of all the widgets that currently function.} -layout_width fill_parent \
		-layout_height wrap_content -layout $layout]

    textview -text {Select a widget} -layout_width fill_parent \
	-layout_height wrap_content -layout $layout

    set spinner [spinner  -layout_width fill_parent \
		     -layout_height wrap_content -layout $layout -itemlist \
		     [list "Simple Widgets" "Web View" "Spinner"]]

    set button [button -text "View demo" -layout_width fill_parent \
		    -layout_height wrap_content -layout $layout \
		    -onclick [list SelectDemo $spinner]]

    set pb [progressbar [list $context]]
    $pb setlayoutparams $layoutparams
    $layout addview $pb

    activity setcontentview $layout
}

main