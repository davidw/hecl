proc reverse {tf results} {
    set string [getprop $tf text]
    set newstring ""
    set i [slen $string]
    incr $i -1
    while {> $i -1} {
	set c [sindex $string $i]
	append $newstring $c
	incr $i -1
    }
    setprop $results text $newstring
}

set mainform [form label "Reverse a String" code {
    set tf [textfield label "String:"]
    set results [stringitem label "Results:"]
    cmd label "Reverse" code [list reverse $tf $results]
    cmd label "Exit" code exit type exit;
}]

setcurrent $mainform
