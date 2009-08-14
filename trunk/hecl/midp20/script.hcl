# vim: ts=2 sw=2 et filetype=tcl
# TODO: Save to RMS
# TODO: Canvas based editor

set :mf [lcdui.textbox -title "Hecl IDE" -maxlength 4096 -uneditable 1 \
	     -text "# Please wait..."]
$:mf setcurrent

# Abbreviated to save keystroke on mobile
proc abbr {} {
    upeval {
	global /alert /canvas /choice /cmd /date /font /form /gauge /img /imgit \
	    /list /set /spc /strit /txtbox /txt /ticker /add /rm /show \
	    /act: /eh: /sc: /ll:
    }
}
set /alert  lcdui.alert
set /canvas lcdui.canvas
set /choice lcdui.choicegroup
set /cmd    lcdui.command
set /date   lcdui.date
set /font   lcdui.font
set /form   lcdui.form
set /gauge  lcdui.gauge
set /img    lcdui.image
set /imgit  lcdui.imageitem
set /list   lcdui.list
set /set    lcdui.settings
set /spc    lcdui.spacer
set /strit  lcdui.stringitem
set /txtbox lcdui.textbox
set /txt    lcdui.textfield
set /ticker lcdui.ticker
set /add    addcommand
set /rm     removecommand
set /show   setcurrent
set /act:   -commandaction
set /eh:    -eventhandler
set /sc:    -selectcommand
set /ll:    -longlabel

# Stuff to make abbr defaults
rename proc proc:orig
proc:orig proc {name arglist body} {
				    proc:orig $name $arglist "abbr\n$body"
				}

abbr

# Capture puts output
rename puts DEBUG
set :out ""
proc:orig puts {txt} {
    global :out
    append $:out "$txt\n"
}

# Anonymous proc generator
set :ac 0
proc:orig : {args body} {
    global :ac
    set :ac [1+ $:ac]
    set name ":anon:$:ac"
    proc $name $args "abbr\n$body"
    return $name
}

set :ex [hash {}]
set :exkeys {}

# EXAMPLES
proc:orig :AddSample {name code} {
    global :ex :exkeys
    hset $:ex $name [strtriml $code]
    lappend $:exkeys $name
}

:AddSample "Hello World" {
    # See Examples for more
    puts "Hello World"
}

:AddSample "List" {
    set sel [$/cmd -label Select $/ll: Select -type item]
    set back [$/cmd -label Back $/ll: Back -type back]
    set lst {One Two Three Four Five Six}
    set menu [$/list $/sc: $sel $/act: [: {cmd menu} {
	global sel lst
	if {eq $cmd $sel} {
	    set index [$menu selection get]
	    showmsg Selection "You choose [lindex $lst $index]" {
		global menu
		$menu $/show
	    }
	} else {
	    exit
	}
    }]]
    foreach name $lst {
	$menu append $name
    }
    $menu $/add $sel
    $menu $/add $back
    $menu $/show
}

:AddSample Form {
    set form [$/form -title "Demo Form" $/act: [: {cmd form} {
	exit
    }]]
    $form configure -ticker [$/ticker -text "I am a Ticker!"]
    $form append [$/txt -label "TextField" -text "You can't edit this" \
		      -uneditable 1]
    $form append [$/txt -label "Editable TextField" -text "Change me"]
    set logo [$/img -resource /hecl_logo.png]
    $form append [$/imgit -image $logo -anchor center]
    $form append [$/spc -label spacer1 -minwidth 200 -minheight 2]
    $form append [$/strit -text "StringItem"]
    $form append [$/spc -label spacer2 -minwidth 200 -minheight 4]
    $form append [$/date -label "Date/Time"]
    set choice [$/choice -label Choice -type popup]
    foreach item {One Two Three Four} {
	$choice append $item
    }
    $form append $choice
    $form append [$/imgit -image $logo]
    $form append [$/gauge -label "How cool is Hecl?" -interactive 1 \
		      -value 10 -maxvalue 10]
    $form $/add [$/cmd -label Back $/ll: Back -type back]
    $form $/show
}

:AddSample Font {
    proc ChangeFont {switchfont pickfont fonts txt sizesi fontsi cmd form} {
	set f [lindex $fonts [$pickfont selection get]]
	if { eq $cmd $switchfont } {
	    $fontsi configure -font $f
	    # This seems to be necessary, at least in the emulator, in order
	    # for the change to actually take effect.
	    $fontsi configure -text $txt
	    $sizesi configure -text [$/font $f stringwidth $txt]
	} else {
	    exit
	}
    }

    set txt "Hello World"
    set form [$/form -title "Font Demo"]
    set fonts [sort [$/font names]]
    $form setcurrent
    set pickfont [$/choice -label "Pick a font:" -type popup]
    $form append $pickfont
    foreach f $fonts {
	$pickfont append $f
    }
    set fontsi [$/strit -label "Sample text:" -text $txt]
    set sizesi [$/strit -label "Size (pixels):"]
    $form append $fontsi
    $form append $sizesi

    set switchfont [$/cmd -label "Switch font" $/ll: "Switch font"]
    set exit [$/cmd -label "Exit" $/ll: "Exit" -type exit]
    $form $/add $switchfont
    $form $/add $exit
    $form configure $/act: [list ChangeFont $switchfont $pickfont $fonts $txt $sizesi $fontsi]
    ChangeFont $switchfont $pickfont $fonts $txt $sizesi $fontsi $switchfont $form
}

:AddSample Call {
    # Call...
    midlet.platformrequest "tel:+393488866859"
}

:AddSample SMS {
    # Send SMS...
    midlet.platformrequest "sms:+393488866859"
}

:AddSample TextBox {
    set textbox [$/txtbox -text "Hello world" $/act: [: {cmd textbox} {exit}]]
    $textbox $/add [$/cmd -label Exit $/ll: Exit -type exit]
    $textbox $/show
}

:AddSample Alert {
    [$/alert -type confirmation -timeout forever \
	 -title Alert -text "This is the alert message!" \
	 $/act: [: {c a} {exit}]] $/show
}

:AddSample Information {
    set form [$/form -title Information $/act: [: {c f} {exit}]]

    $form append [$/txt -label "Midlet Version" -text [midlet.getappproperty "MIDlet-Version"] -uneditable 1]

    set plist {
	"Java Profile" microedition.profiles
	"Java Configuration" microedition.configuration
	"Java Locale" microedition.locale
	"Java Plattform" microedition.platform
	"Java Encoding" microedition.encoding
	"Java Version" java.fullversion
	"MMAPI Snapshot Capable?" supports.video.capture
	"MMAPI Snapshot Format" video.snapshot.encodings
    }
    foreach {l p} $plist {
	if {= [catch {set p [system.getproperty $p]}] 0} {
	    if {> [strlen $p] 0} {
		$form append [$/txt -maxlength 256 -label $l -text [strrange $p 0 255] -uneditable 1]
	    }
	}
    }

    $form append [$/txt -label "Snapshot" -text [midlet.checkpermissions "javax.microedition.media.control.VideoControl.getSnapshot"] -uneditable 1]

    $form append [$/txt -label "File Access" -text [midlet.checkpermissions "javax.microedition.io.Connector.file.read"] -uneditable 1]

    $form $/add [$/cmd -label Exit $/ll: Exit -type exit]
    $form $/show
}

:AddSample Settings {
    set form [$/form -title "Settings Demo" $/act: [: {c f} {exit}]]

    foreach s {
	"-color"
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
	"-hilightborderstyle"
    } {
	$form append [$/strit -label "[strtrim $s -]:" -text [$/set cget $s]]
    }

    $form $/add [$/cmd -label Exit $/ll: Exit -type exit]
    $form $/show
}

:AddSample Canvas {
    proc DrawH {g x y} {
	global WIDTH HEIGHT
	set y2 [+ $y 70]
	set x2 [+ $x 70]
	set halfy [+ $y 40]

	$g color white
	$g frect {0 0} [list $WIDTH $HEIGHT]
	$g color black
	$g frect [list $x $y] [list 10 80]
	$g frect [list $x2 $y] [list 10 80]
	$g frect [list $x $halfy] [list 80 10]
    }

    set X 0
    set Y 0
    set c [$/canvas -title "Test Canvas" $/act: [: {cmd canvas} {exit}] \
	       $/eh: [: {canvas event} {
		   global X Y
		   # Don't act unless key press or key repeat
		   set reason [$event cget -reason]
		   if {or [< $reason 5] [> $reason 7]} {return}

		   set kc [$event cget -keyname]
		   if {eq $kc UP} {
		       set Y [+ $Y 10]
		   } elseif {eq $kc DOWN} {
		       set Y [- $Y 10]
		   } elseif {eq $kc LEFT} {
		       set X [- $X 10]
		   } elseif {eq $kc RIGHT} {
		       set X [+ $X 10]
		   }
		   DrawH [$canvas graphics] $X $Y
		   $canvas flush
	       }]]
    $c $/add [$/cmd -label Exit $/ll: Exit -type exit]
    $c $/show
    set WIDTH [$c cget -width]
    set HEIGHT [$c cget -height]
    DrawH [$c graphics] $X $Y
    $c repaint
}

:AddSample "Canvas Events" {
    set Y 0
    proc LineY {} {
	global HEIGHT Y LINEH
	- $HEIGHT [* $Y $LINEH]
    }

    proc EraseLn {g} {
	global Y MAXLINE LINEH WIDTH
	set Y [- $Y 1]
	if {< $Y 0} {set Y $MAXLINE}
	$g color white
	$g frect [list 0 [- [LineY] $LINEH]] [list $WIDTH $LINEH]
	$g color black
    }

    proc PrintLn {g txt} {
	global Y MAXLINE
	if {= $Y 0} {
	    $g clear
	}
	$g string [list 0 [LineY]] $txt nw
	set Y [1+ $Y]
	if {> $Y $MAXLINE} {set Y 0}
    }

    set LAST ""
    set REPEAT 0
    set c [$/canvas -title "Canvas Events" -fullscreen 1 \
	       $/eh: [: {c e} {
		   global LAST REPEAT
		   set reason [$e cget -reason]
		   if {or [> $reason 7] [< $reason 2]} {return}
		   set keycode [$e cget -keycode]
		   set keyname [$e cget -keyname]
		   set ga [$e cget -gameaction]
		   set x [$e cget -x]
		   set y [$e cget -y]
		   set width [$e cget -width]
		   set height [$e cget -height]
		   set line "$reason $keycode $keyname $ga ${x}x$y ${width}x$height"

		   set g [$c graphics]
		   if {eq $line $LAST} {
		       if {> $REPEAT 0} {EraseLn $g}
		       PrintLn $g "Repeat [incr $REPEAT] time"
		   } else {
		       PrintLn $g $line
		       set REPEAT 0
		   }
		   set LAST $line
		   $c flush
		   set KEY_0 48
		   if {= $keycode $KEY_0} {
		       exit
		   }
	       }]]
    $c $/show
    set g [$c graphics]
    set WIDTH [$g cget -clipwidth]
    set HEIGHT [$g cget -clipheight]
    set LINEH [$/font [$g cget -font] cget -height]
    set MAXLINE [/ $HEIGHT $LINEH]
    PrintLn $g "Canvas ${WIDTH}x$HEIGHT, $MAXLINE lines $LINEH pixel each"
    PrintLn $g "Press keys to test, Press 0 to quit"
    $c repaint
}
# END OF EXAMPLES

set :src ""
set :err ""

set :showmain {
    global :mf
    $:mf $/show
}
set :adc $:showmain
set :ok [$/cmd -label OK $/ll: OK -type ok]
set :de [$/cmd -label Edit $/ll: Edit -type cancel]
set :def [$/txtbox $/act: [: {cmd form} {
    global :df
    $:df $/show
}]]
set :df [$/alert -title Hecl -type info $/act: [: {cmd alert} {
    global :ok :adc :de :def
    if {eq $cmd $:ok} {
	eval $:adc
    } elseif {eq $cmd $:de} {
	$:def conf -text [$alert cget -text]
	$:def conf -title [$alert cget -title]
	$:def $/show
    }
}]]
$:def $/add $:ok
$:df $/add $:ok
$:df $/add $:de

proc showmsg {title text args} {
    global :adc :showmain :df
    set argc [llen $args]
    if {= $argc 0} {
	set :adc $:showmain
    } elseif {= $argc 1} {
	set :adc [lindex $args 0]
    } else {
	throw "showmsg title text ?code?"
    }
    $:df conf -title $title
    $:df conf -text $text
    $:df $/show
}

set :s:ac 0
set :s:fm 0
proc:orig :Setup {} {
    global :out :ac :s:ac :s:fm
    set :out ""
    set :s:ac [copy $:ac]
    system.gc
    set :s:fm [runtime.freememory]
}

# Exit to IDE
proc exit {} {
    global :ac :s:ac :out :mf :outc
    set :ac [copy $:s:ac]

    if {strlen $:out} {
	$:mf $/add $:outc
	showmsg Output $:out {:ShowMem}
    } else {
	:ShowMem
    }
}

proc :ShowMem {} {
    global :s:fm
    set total [runtime.totalmemory]
    set mb [long [- $total $:s:fm]]
    system.gc
    set ma [long [- $total [runtime.freememory]]]
    set mc [long [- $ma $mb]]
    showmsg Memory "Total: $total\nBefore: $mb\nAfter: $ma\nChanged: $mc"
}

proc :LoadExample {name} {
    global :src :ex :mf
    set :src [hget $:ex $name]
    $:mf conf -text $:src
}

# Example List
set :sel [$/cmd -label Select $/ll: Select -type item]
set :sf [$/list -title "Examples:" $/sc: $:sel $/act: [: {cmd lst} {
    global :sel :exkeys :mf
    if {eq $cmd $:sel} {
	:LoadExample [lindex $:exkeys [$lst selection get]]
    }
    $:mf $/show
}]]
foreach name $:exkeys {
    $:sf append $name
}
$:sf $/add $:sel
$:sf $/add [$/cmd -label Back $/ll: Back -type back]

# Main
set :run  [$/cmd -label Run $/ll: "Run the code" -type ok]
set :undo [$/cmd -label Undo $/ll: "Undo edit" -type cancel]
set :errc  [$/cmd -label Errors $/ll: "Show errors" -type item]
set :outc  [$/cmd -label Output $/ll: "Show output" -type item -priority 2]
set :sample [$/cmd -label Examples $/ll: Examples -type item -priority 3]
set :exit [$/cmd -label Exit $/ll: "Exit Hecl" -type item -priority 4]

$:mf conf $/act: [: {cmd main} {
    global :src :err :out :run :undo :errc :outc :sample :exit
    if {eq $cmd $:run} {
	set :src [$main cget -text]
	:Setup
	$main $/rm $:errc
	$main $/rm $:outc
	if {
	    catch {
		upeval 0 $:src
		after 100
		if {$main cget -isshown} {exit}
	    } :err
	} {
	    $main $/add $:errc
	    showmsg Errors $:err {exit}
	}
    } elseif {eq $cmd $:undo} {
	$main conf -text $:src
    } elseif {eq $cmd $:errc} {
	showmsg Errors $:err
    } elseif {eq $cmd $:outc} {
	showmsg Output $:out
    } elseif {eq $cmd $:sample} {
	global :sf
	$:sf $/show
    } elseif {eq $cmd $:exit} {
	midlet.exit
    }
}]
$:mf $/add $:run
$:mf $/add $:undo
$:mf $/add $:sample
$:mf $/add $:exit
:LoadExample "Hello World"
$:mf conf -uneditable 0

#DEBUG [join [sort [intro commands]] "\n"]
