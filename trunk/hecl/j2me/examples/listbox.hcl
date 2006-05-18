# listbox.hcl --
# listbox example

proc reverseoptions {listb} {
    set newlist {}
    set l [getprop $listb selected]
    foreach v $l {
	lappend $newlist [not $v]
    }
    setprop $listb selected $newlist
}

set lb [listbox label "Listbox Example" type multiple code {
    for {set i 0} {< $i 10} {incr $i} {
	string "Option $i"
    }
    cmd label "Reverse Options" code {reverseoptions $lb}
}]

setcurrent $lb
