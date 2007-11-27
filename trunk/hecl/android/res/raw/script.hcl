proc SimpleWidgets {} {
    global context
    set swlayout [linearlayout -new $context]
    set layoutparams [linearlayoutparams -new {FILL_PARENT WRAP_CONTENT}]
    $swlayout setorientation VERTICAL

    set tv [textview -new $context -text "This is a textview" -layoutparams $layoutparams]
    $swlayout addview $tv

    set button [button -new $context -text "This is a button" -layoutparams $layoutparams]
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
    set layout [linearlayout -new $context]
    $layout setorientation VERTICAL
    set layoutparams [linearlayoutparams -new {FILL_PARENT WRAP_CONTENT}]

    set tv [textview -new $context -text {Welcome to Hecl on Android.  This is a short tour of all the widgets that currently function.} -layoutparams $layoutparams]

    textview -new $context -text {Select a widget} -layoutparams $layoutparams

    set ala [arrayadapter -new [list \
				    $context \
				    [reslookup android.R.layout.simple_spinner_item] \
				    [list "Simple Widgets" "Web View" "Spinner"]]]
    $ala setdropdownviewresource [reslookup android.R.layout.simple_spinner_dropdown_item]

    set spinner [spinner -new $context]
    $spinner setadapter $ala
    $spinner setlayoutparams $layoutparams
    $layout addview $spinner

    set button [button -new $context -text "View demo" \
		    -layoutparams $layoutparams \
		    -onclicklistener [callback -new [list [list SelectDemo $spinner]]]]
    $layout addview $button

    set pb [progressbar -new $context]
    $pb setlayoutparams $layoutparams
    $layout addview $pb

    activity setcontentview $layout
}

main