# heclfile.hcl - this lets us run a default file or select another
# one.

set fname "file:///root1/hecl.hcl"

set err ""
catch {
    source $fname
} err

if { ne "" $err } {
    set a [lcdui.alert -text "Error loading $fname : $err"]
    $a setcurrent
}
