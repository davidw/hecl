proc SimpleWidgets {} {
    global context
    set swlayout [linearlayout [list $context]]
    set layoutparams [linearlayoutparams {FILL_PARENT WRAP_CONTENT}]
    $swlayout setorientation VERTICAL

    set tv [textview [list $context] -text "This is a textview"]
    $tv setlayoutparams $layoutparams
    $swlayout addview $tv

    set button [button [list $context] -text "This is a button"]
    $button setlayoutparams $layoutparams
    $swlayout addview $button

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

    set tv [textview [list $context] -text {Welcome to Hecl on Android.  This is a short tour of all the widgets that currently function.} -layoutparams $layoutparams]

    textview [list $context] -text {Select a widget} -layoutparams $layoutparams

    set ala [arrayadapter [list \
			       $context \
			       [reslookup android.R.layout.simple_spinner_item] \
			       [list "Simple Widgets" "Web View" "Spinner"]]]
    $ala setdropdownviewresource [reslookup android.R.layout.simple_spinner_dropdown_item]
    set spinner [spinner [list $context]]
    $spinner setadapter $ala
    $spinner setlayoutparams $layoutparams
    $layout addview $spinner

    set button [button [list $context] -text "View demo" \
		    -layoutparams $layoutparams \
		    -onclicklistener [callback [list [list SelectDemo $spinner]]]]
    $layout addview $button

    set pb [progressbar [list $context]]
    $pb setlayoutparams $layoutparams
    $layout addview $pb

    activity setcontentview $layout
}

main