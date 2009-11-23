set fname "file:///SDCard/hecl.hcl"

set mainform [lcdui.form]

set a [lcdui.alert -text "Loading $fname ..."]
$a setcurrent
# set err ""
# catch {
#     source $fname
# } err

# if { ne "" $err } {
#     $a configure -text $err
# }
