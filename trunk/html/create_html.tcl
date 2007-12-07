source tclrivet.tcl

foreach fl [glob *.rvt] {
    set newfile "[file rootname $fl].html"
    # Reset where stdout is going
    close stdout
    set ofl [open $newfile w]
    rivet $fl
}

foreach fl [glob */*.rvt] {
    set newfile "[file rootname $fl].html"
    # Reset where stdout is going
    close stdout
    set ofl [open $newfile w]
    rivet $fl
}

