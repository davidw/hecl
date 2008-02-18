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

# basicspinner --
#
#	Create a simple spinner using default pieces.

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

# basiclist --
#
#	Create a simple listview using default pieces.

proc basiclist {context lst args} {
    set aa [arrayadapter -new \
		[list $context \
		     [reslookup android.R.layout.simple_list_item_1] \
		     $lst]]

    set cmd [expand 3 [list listview -new $context $args]]
    set lview [eval $cmd]

    $lview setadapter $aa
    return $lview
}

# newActivity --
#
#	Create a new activity from the old $context, and execute $code
#	in it.

proc newActivity {context code} {
    set h [subhecl -new [list]]
    set intent [intent -new [list]]
    $intent setclass $context [$h getclass]
    $h setmailbox $code
    $context startActivity $intent
}

# contentQuery --
#
#	Run a query and return a cursor object.

proc contentQuery {uri} {
    java android.net.Uri uri
    return [[activity] managedQuery [uri parse $uri] [null] [null] [null]]
}