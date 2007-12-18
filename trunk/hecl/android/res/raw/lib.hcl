# Library of helper procedures.

proc expand {toexpand lst} {
    # This could be replaced by either an expand command, or syntax.
    set res [list]
    set i 0
    foreach el $lst {
	if { = $i $toexpand } {
	    foreach elem $el {
		lappend $res $elem
	    }
	} else {
	    lappend $res $el
	}
	incr $i
    }
    return $res
}

proc basicspinner {context lst args} {
    set aa [arrayadapter -new \
		[list $context \
		     [reslookup android.R.layout.simple_spinner_item] $lst]]

    $aa setdropdownviewresource \
	[reslookup android.R.layout.simple_spinner_dropdown_item]

    set cmd [expand 3 [list spinner -new $context $args]]
    set spinner [eval $cmd]
    $spinner setadapter $aa
    return $spinner
}
