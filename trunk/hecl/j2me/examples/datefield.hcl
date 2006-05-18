# datefield.hcl --
# datefield example

set f [form label Date code {
    set df [datefield label "Date/Time"]
    set si [stringitem label "Integer representation:"]
    cmd label show code {
	set i [getprop $df date]
	setprop $si text $i
	set minusoneweek [- $i 604800]
	setprop $dfowa date $minusoneweek
	setprop $weekagoi text $minusoneweek
    }
    string "One week earlier:"
    set dfowa [datefield]
    set weekagoi [stringitem label "Week earlier integer representation:"]
}];

setcurrent $f