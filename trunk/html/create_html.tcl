#!/usr/bin/tclsh

source tclrivet.tcl

proc genfile {fl} {
    set newfile "[file rootname $fl].html"
    puts stderr "Generating $newfile"
    # Reset where stdout is going
    close stdout
    set ofl [open $newfile w]
    rivet $fl
}

foreach fl [glob *.rvt] {
    genfile $fl
}

foreach fl [glob */*.rvt] {
    genfile $fl
}

