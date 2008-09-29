######
# A tiny application showing the usage of some MIDP2.0 ui elements

#################################################
# Create some lcdui.commands
#################################################
set backcmd [lcdui.command -label Back -longlabel Back -type back -priority 1]
set exitcmd [lcdui.command -label Exit -longlabel Exit -type exit -priority 1]
set nocmd [lcdui.command -type screen -label "No" -longlabel "No" -priority 2]
set okcmd [lcdui.command -label Ok -longlabel Ok -type ok -priority 1]
set selectcmd [lcdui.command -label Select -longlabel Select -type item -priority 1]
set smscmd [lcdui.command -label SMS -longlabel SMS -type ok]
set yescmd [lcdui.command -type screen -label "Yes" -longlabel "Yes" -priority 1]

#################################################
# lcdui elements
#################################################

# Read the Hecl logo from the resources...
set logo [lcdui.image -resource /hecl_logo.png]

# A form with various elements...
set datefield [lcdui.date -label "Date/Time"]
set choicegroup [lcdui.choicegroup -label Choice -type popup]
foreach x {c1 c2 c3} {
    $choicegroup append $x
}
set ticker [lcdui.ticker -text "I am a Ticker!"]

set defform [lcdui.form -title "Demo Form" -commandaction backToMainMenu]
$defform configure -ticker $ticker
$defform append [lcdui.textfield -label "TextField" -text "TextField" -uneditable 1]
$defform append [lcdui.textfield -label "Editable TextField" -text "editable text"]
$defform append [lcdui.imageitem -image $logo -anchor center]
$defform append [lcdui.spacer -label spacer1 -minwidth 200 -minheight 2]
$defform append [lcdui.stringitem -text "Stringitem"]
$defform append [lcdui.spacer -label spacer2 -minwidth 200 -minheight 4]
$defform append $datefield
$defform append $choicegroup
$defform append [lcdui.imageitem -image $logo]
$defform append [lcdui.gauge -label "How cool is Hecl?" -interactive 1 \
		     -value 10 -maxvalue 10]
$defform addcommand $backcmd

set deflist [lcdui.list]
$deflist addcommand $selectcmd
$deflist addcommand $backcmd


# The main menu
set mainmenu [lcdui.list -title "Hecl MIDP2.0 Demo" -commandaction mainsel]
$mainmenu addcommand $exitcmd
$mainmenu addcommand $selectcmd
$mainmenu append "List Demo"
$mainmenu append "Form Demo"
$mainmenu append "Font Demo"
$mainmenu append "Call..."
$mainmenu append "Send SMS..."
$mainmenu append "TextBox..."
$mainmenu append "Alert"
$mainmenu append "Canvas"
$mainmenu append "Information"
$mainmenu append "Settings"

set menu1 [lcdui.list -commandaction "menu1sel"]
$menu1 addcommand $backcmd
$menu1 addcommand $selectcmd
$menu1 deleteall
foreach x {1 2 3 4 5 6 7 8 9 10} {$menu1 append "Title \#$x"}

set infoform [lcdui.form -title Information -commandaction "backToMainMenu"]
$infoform append [lcdui.textfield -label "Midlet Version" \
		      -text [midlet.getappproperty "MIDlet-Version"] -uneditable 1]
foreach {l p} {
    "Java Profile" microedition.profiles
    "Java Configuration" microedition.configuration
    "Java Locale" microedition.locale
    "Java Plattform" microedition.platform
    "Java Encoding" microedition.encoding
    "Java Version" java.fullversion
    "MMAPI Snapshot Capable?" supports.video.capture
    "MMAPI Snapshot Format" video.snapshot.encodings} {
    if {= [catch {set p [system.getproperty $p]}] 0} {
	if {> [strlen $p] 0} {
	    $infoform append [lcdui.textfield -label $l -text $p -uneditable 1]
	}
    }
}
$infoform append \
    [lcdui.textfield -label "Snapshot" \
	 -text [midlet.checkpermissions \
		    "javax.microedition.media.control.VideoControl.getSnapshot"] \
	 -uneditable 1]
$infoform append \
    [lcdui.textfield -label "File Access" \
	 -text [midlet.checkpermissions \
		    "javax.microedition.io.Connector.file.read"] \
	 -uneditable 1]
$infoform addcommand $backcmd

# a textbox
set textbox [lcdui.textbox -text "Hello world" -commandaction backToMainMenu]
$textbox addcommand $backcmd


proc canvasEvents {canvas event} {
    global canvasX canvasY

    # If it's not a key release, don't act.
    if { != 6 [$event cget -reason] } {
        return
    }

    set kc [$event cget -keyname]

    if { eq $kc UP } {
	set canvasY [+ $canvasY 10]
    } elseif { eq $kc DOWN } {
	set canvasY [+ $canvasY -10]
    } elseif { eq $kc LEFT } {
	set canvasX [+ $canvasX -10]
    } elseif { eq $kc RIGHT } {
	set canvasX [+ $canvasX 10]
    }

    DrawH [$canvas graphics] $canvasX $canvasY
    $canvas repaint
}

proc changeFont {fontsize switchfont pickfont fonts txt sizestringitem fontstringitem cmd form} {
    set f [lindex $fonts [$pickfont selection get]]
    if { eq $cmd $switchfont } {
	$fontstringitem configure -font $f
	# This seems to be necessary, at least in the emulator, in order
	# for the change to actually take effect.
	$fontstringitem configure -text $txt
    } elseif { eq $cmd $fontsize } {
	$sizestringitem configure -text [lcdui.font $f stringwidth $txt]
    }
}

# Font demo

proc createFontForm {} {
    set txt "Hello World"
    set fontform [lcdui.form -title "Font Demo"]
    set fonts [sort [lcdui.font names]]
    $fontform setcurrent
    set pickfont [lcdui.choicegroup -label "Pick a font:" -type popup]
    $fontform append $pickfont
    foreach f $fonts {
	$pickfont append $f
    }
    set fontstringitem [lcdui.stringitem -label "Sample text:" -text $txt]
    set sizestringitem [lcdui.stringitem -label "Size (pixels):" -text "This is filled in when you use the 'Font size' command"]
    $fontform append $fontstringitem
    $fontform append $sizestringitem

    set switchfont [lcdui.command -label "Switch font" -longlabel "Switch font"]
    set fontsize [lcdui.command -label "Font size" -longlabel "Font size (pixels)"]
    $fontform addcommand $switchfont
    $fontform addcommand $fontsize
    $fontform configure -commandaction \
	[list changeFont $fontsize $switchfont $pickfont \
	     $fonts $txt $sizestringitem $fontstringitem]
}

# A canvas
set canvasX 10
set canvasY 10

set canvas [lcdui.canvas -title "Test Canvas" -commandaction backToMainMenu -eventhandler canvasEvents]
$canvas addcommand $backcmd

# Draw the H

proc DrawH {graphics x y} {
    global WIDTH
    global HEIGHT
    set y2 [+ $y 70]
    set x2 [+ $x 70]
    set halfy [+ $y 40]

    $graphics color white
    $graphics frect {0 0} [list $WIDTH $HEIGHT]
    $graphics color black
    $graphics frect [list $x $y] [list 10 80]
    $graphics frect [list $x2 $y] [list 10 80]
    $graphics frect [list $x $halfy] [list 80 10]
}

# Settings

proc settingsDemo {} {
    global backcmd
    set form [lcdui.form -title "Settings Demo" -commandaction menu1sel]
    $form setcurrent
    $form addcommand $backcmd

    foreach s {"-color"
	"-alphalevels"
	"-alertimagewidth"
	"-alertimageheight"
	"-listimagewidth"
	"-listimageheight"
	"-choiceimagewidth"
	"-choiceimageheight"
	"-bg"
	"-fg"
	"-hilightbg"
	"-hilightfg"
	"-border"
	"-hilightborder"
	"-borderstyle"
	"-hilightborderstyle"} {
	$form append [lcdui.stringitem -label "[strtrim $s -]:" -text [lcdui.settings cget $s]]
    }
}

proc main {} {
    global mainmenu
    $mainmenu setcurrent
}

proc mainsel {cmd d} {
    global exitcmd
    if {eq $cmd $exitcmd} {
	midlet.exit
    }
    set idx [lindex [$d selection get] 0]
    if {< $idx 0} {
	return
    }
    if {= $idx 0} {
	global menu1
	$menu1 setcurrent
    } elseif {= $idx 1} {
	global defform
	$defform setcurrent
    } elseif {= $idx 2} {
	createFontForm
    } elseif {= $idx 3} {
	# Call...
	midlet.platformrequest "tel:+4369911259952"
    } elseif {= $idx 4} {
	# Send SMS...
	midlet.platformrequest "sms:+4369911259952"
    } elseif {= $idx 5} {
	# Textbox
	global textbox
	$textbox setcurrent
    } elseif {= $idx 6} {
	# Alert
	[lcdui.alert -type confirmation -title Alert\
	     -commandaction backToMainMenu \
	     -text "This is the alert message!" -timeout forever] setcurrent
    } elseif {= $idx 7} {
	# Canvas
	global canvas
	$canvas setcurrent
	set g [$canvas graphics]
	global canvasX canvasY WIDTH HEIGHT
	set WIDTH [$canvas cget -width]
	set HEIGHT [$canvas cget -height]
	DrawH $g $canvasX $canvasY
	$canvas repaint
    } elseif {= $idx 8} {
	global infoform
	$infoform setcurrent
    } elseif {= $idx 9} {
	settingsDemo
    }
}

proc menu1sel {cmd d} {
    global backcmd selectcmd mainmenu {lcdui.select_command}
    if {eq $cmd $backcmd} {
	$mainmenu setcurrent
    } elseif {or [eq $cmd $selectcmd] [eq $cmd ${lcdui.select_command}]} {
	$d configure -title "Title \#[ + 1 [$d selection get]]"

	#puts "selected index=[$d selection get]"
    }
}

proc backToMainMenu {cmd d} {
    global mainmenu
    $mainmenu setcurrent; 
}

#proc showsmsform {back} {
#    global textbox backcmd okcmd
#    setBackmenu $back
#    $textbox configure -title "Schreibe SMS" -maxlength 120
#    $textbox configure -commandaction "handlesmscmd"
#    $textbox addcommand $backcmd
#    $textbox addcommand $okcmd
#    $textbox setcurrent
#}

### Local Variables:
### mode:tcl
### coding:utf-8
### End:

main
