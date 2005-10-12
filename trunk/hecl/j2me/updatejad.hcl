# updatejad.hcl

# Uses the template.jad file to update the specified (usually
# Hecl.jad) file with the correct file size.

set jadfile [lindex &argv 1]
set jarfile [lindex &argv 2]

proc usage {} {
    global argv
    puts "Usage: [lindex &argv 0] jadfile jarfile"
    exit
}

if { or [eq &jadfile ""] [eq &jarfile ""] } {
    usage
}

set templatefile [lrange [filetolist [currentfile]] 0 -2]
lappend &templatefile template.jad
set templatefile [listtofile $templatefile]

puts "Updating [lindex &argv 1]"

set filesize [filesize &jarfile]
puts "Jar file &jarfile is &filesize bytes"
set template [readall &templatefile]
# This is a trick to 'subst' the contents of $template.
eval "write &jadfile \"&template\""
