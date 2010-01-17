# vim: ts=2 sw=2 et filetype=tcl
# TODO: Save to RMS
# TODO: Canvas based editor

set SourceCode [lcdui.textbox -title "Hecl IDE" -maxlength 4096 -uneditable 1 \
		    -text "# Please wait..."]
$SourceCode setcurrent

# These are shortcuts so that there is less typing when entering code
# on the phone.
alias lcdui.alert	/alert
alias lcdui.canvas	/canvas
alias lcdui.choicegroup	/choice
alias lcdui.command	/cmd
alias lcdui.date	/date
alias lcdui.font	/font
alias lcdui.form	/form
alias lcdui.gauge	/gauge
alias lcdui.image	/img
alias lcdui.imageitem	/imgit
alias lcdui.list	/list
alias lcdui.spacer	/spc
alias lcdui.stringitem	/strit
alias lcdui.textbox	/txtbox
alias lcdui.textfield	/txt
alias lcdui.ticker	/ticker

# Capture puts output
rename puts DEBUG
set :out ""
proc puts {txt} {
    global :out
    append $:out "$txt\n"
}

# Anonymous proc generator
set :ac 0
proc : {args body} {
    global :ac
    set :ac [1+ $:ac]
    set name ":anon:$:ac"
    proc $name $args $body
    return $name
}

# Hash table with all of the examples
set :ex [hash {}]
set :exkeys {}

# Add some sample code.
proc AddSample {name code} {
    global :ex :exkeys
    hset $:ex $name [strtriml $code]
    lappend $:exkeys $name
}

# The following "AddSample"s are all examples of UI screens and
# widgets:

AddSample "Hello World" {
# See Examples for more
puts "Hello World"
}

AddSample "List" {
set sel [/cmd -label Select -longlabel Select -type item]
set back [/cmd -label Back -longlabel Back -type back]
set lst {One Two Three Four Five Six}
set menu [/list -selectcommand $sel -commandaction [: {cmd menu} {
    global sel lst
    if {eq $cmd $sel} {
	set index [$menu selection get]
	showmsg Selection "You choose [lindex $lst $index]" {
	    global menu
	    $menu setcurrent
	}
    } else {
	done
    }
}]]
foreach name $lst {
    $menu append $name
}
$menu addcommand $sel
$menu addcommand $back
$menu setcurrent
}

AddSample Form {
set form [/form -title "Demo Form" -commandaction [: {cmd form} {
    done
}]]
$form configure -ticker [/ticker -text "I am a Ticker!"]
$form append [/txt -label "TextField" -text "You can't edit this" \
		  -uneditable 1]
$form append [/txt -label "Editable TextField" -text "Change me"]
set logo [/img -resource /hecl_logo.png]
$form append [/imgit -image $logo -anchor center]
$form append [/spc -label spacer1 -minwidth 200 -minheight 2]
$form append [/strit -text "StringItem"]
$form append [/spc -label spacer2 -minwidth 200 -minheight 4]
$form append [/date -label "Date/Time"]
set choice [/choice -label Choice -type popup]
foreach item {One Two Three Four} {
    $choice append $item
}
$form append $choice
$form append [/imgit -image $logo]
$form append [/gauge -label "How cool is Hecl?" -interactive 1 \
		  -value 10 -maxvalue 10]
$form addcommand [/cmd -label Back -longlabel Back -type back]
$form setcurrent
}

AddSample Font {
proc ChangeFont {switchfont pickfont fonts txt sizesi fontsi cmd form} {
    set f [lindex $fonts [$pickfont selection get]]
    if { eq $cmd $switchfont } {
	$fontsi configure -font $f
	# This seems to be necessary, at least in the emulator, in order
	# for the change to actually take effect.
	$fontsi configure -text $txt
	$sizesi configure -text [/font $f stringwidth $txt]
    } else {
	done
    }
}

set txt "Hello World"
set form [/form -title "Font Demo"]
set fonts [sort [/font names]]
$form setcurrent
set pickfont [/choice -label "Pick a font:" -type popup]
$form append $pickfont
foreach f $fonts {
    $pickfont append $f
}
set fontsi [/strit -label "Sample text:" -text $txt]
set sizesi [/strit -label "Size (pixels):"]
$form append $fontsi
$form append $sizesi

set switchfont [/cmd -label "Switch font" -longlabel "Switch font"]
set exit [/cmd -label "Exit" -longlabel "Exit" -type exit]
$form addcommand $switchfont
$form addcommand $exit
$form configure -commandaction [list ChangeFont $switchfont $pickfont $fonts $txt $sizesi $fontsi]
ChangeFont $switchfont $pickfont $fonts $txt $sizesi $fontsi $switchfont $form
}

AddSample Call {
# Call...
midlet.platformrequest "tel:+393488866859"
}

AddSample SMS {
# Send SMS...
midlet.platformrequest "sms:+393488866859"
}

AddSample "Vibrate / Backlight" {
midlet.vibrate 2000
midlet.flashbacklight 2000
}

AddSample TextBox {
set textbox [/txtbox -text "Hello world" -commandaction [: {cmd textbox} {done}]]
$textbox addcommand [/cmd -label Exit -longlabel Exit -type exit]
$textbox setcurrent
}

AddSample Alert {
[/alert -type confirmation -timeout forever \
     -title Alert -text "This is the alert message!" \
     -commandaction [: {c a} {done}]] setcurrent
}

# Only add this if we have the location.get command:
if { < 0 [llen [search [intro commands] x {eq $x location.get}]] } {
AddSample LocationAPI {
set locationform [lcdui.form -title "Location Information" -commandaction [: {c f} {done}]]
$locationform addcommand [/cmd -label Exit -longlabel Exit -type exit]
proc LocationError {err} {
    [/alert -title "Location Error" -text $err] setcurrent
}
proc LocationCallback {lf results} {
    foreach {k v} $results {
	$lf append [lcdui.textfield -label $k -text $v]
    }
}
location.get -callback [list LocationCallback $locationform] -timeout 60 -onerror LocationError
$locationform setcurrent
}
}

AddSample Information {
set form [/form -title Information -commandaction [: {c f} {done}]]

$form append [/txt -label "Midlet Version" -text [midlet.getappproperty "MIDlet-Version"] -uneditable 1]

set plist {
    "Java Profile" microedition.profiles
    "Java Configuration" microedition.configuration
    "Java Locale" microedition.locale
    "Java Plattform" microedition.platform
    "Java Encoding" microedition.encoding
    "Java Version" java.fullversion
    "MMAPI Snapshot Capable?" supports.video.capture
    "MMAPI Snapshot Format" video.snapshot.encodings
    "Location Version" microedition.location.version
}
foreach {l p} $plist {
    if {= [catch {set p [system.getproperty $p]}] 0} {
	if {> [strlen $p] 0} {
	    $form append [/txt -label $l -text $p -uneditable 1]
	}
    }
}

$form append [/txt -label "Snapshot" -text [midlet.checkpermissions "javax.microedition.media.control.VideoControl.getSnapshot"] -uneditable 1]

$form append [/txt -label "File Access" -text [midlet.checkpermissions "javax.microedition.io.Connector.file.read"] -uneditable 1]

$form addcommand [/cmd -label Exit -longlabel Exit -type exit]
$form setcurrent
}



AddSample Settings {
set form [/form -title "Settings Demo" -commandaction [: {c f} {done}]]

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
    $form append [/strit -label "[strtrim $s -]:" -text [lcdui.settings cget $s]]
}

$form addcommand [/cmd -label Exit -longlabel Exit -type exit]
$form setcurrent
}

AddSample "File Browser" {

proc FileSelect {infohash bselect bback binfo cmd menu} {
    set root [hget $infohash root]

    set index [$menu selection get]

    set f [lindex [hget $infohash paths] $index]

    if {eq $cmd $bselect} {
	lappend $root $f
    } elseif {eq $cmd $bback} {
	lset $root -1
    }

    # If we are back at the root level, we need to do file.devs
    # instead of file.list
    if { = 0 [llen $root] } {
	set lst [file.devs]
    } else {
	set cpath "file:///[join $root {}]"
	set lst [file.list "$cpath"]
    }

    # Show some information on the file in question.
    if {eq $cmd $binfo} {
	set dismiss [lcdui.command -label Ok -longlabel Ok -type ok]
	set bform [lcdui.form -title "Info: $f" -commandaction [: {cmd form} [list $menu setcurrent]]]
	$bform addcommand $dismiss
	set directory [file.isdirectory $cpath]

	$bform append [lcdui.stringitem -label "Readable?" -text [file.readable $cpath]]
	$bform append [lcdui.stringitem -label "Writable?" -text [file.writable $cpath]]
	$bform append [lcdui.stringitem -label "Hidden?" -text [file.hidden $cpath]]
	if { not $directory } {
	    $bform append [lcdui.stringitem -label "Size" -text [file.size $cpath]]
	}
	$bform append [lcdui.stringitem -label "Basename" -text [file.name $cpath]]
	$bform append [lcdui.stringitem -label "Last Modified" -text [file.mtime $cpath]]
	$bform append [lcdui.stringitem -label "Directory?" -text $directory]
	$bform append [lcdui.stringitem -label "Open?" -text [file.isopen $cpath]]
	$bform setcurrent
    }

    hset $infohash paths $lst
    $menu deleteall
    foreach x $lst {
	$menu append $x
    }

    #Re-add the infohash into the -commandaction.
    $menu configure -commandaction [list FileSelect $infohash $bselect $bback $binfo]
}

set h [hash {}]
set devs [file.devs]
hset $h paths $devs
hset $h root {}

set bselect [lcdui.command -label Select -longlabel Select -type item]
set bback [lcdui.command -label Back -longlabel Back -type item]
set binfo [lcdui.command -label Info -longlabel "File Info" -type item]
set browser [lcdui.list -selectcommand $bselect -title "File Browser" \
		 -commandaction [list FileSelect $h $bselect $bback $binfo]]
$browser addcommand $bback
$browser addcommand $binfo
$browser setcurrent
foreach d $devs {
    $browser append $d
}
}

AddSample Canvas {
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
set c [/canvas -title "Test Canvas" -commandaction [: {cmd canvas} {done}] \
	   -eventhandler [: {canvas event} {
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
$c addcommand [/cmd -label Exit -longlabel Exit -type exit]
$c setcurrent
set WIDTH [$c cget -width]
set HEIGHT [$c cget -height]
DrawH [$c graphics] $X $Y
$c repaint
}

AddSample "Canvas Events" {
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
set c [/canvas -title "Canvas Events" -fullscreen 1 \
  -eventhandler [: {c e} {
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
	  done
      }
  }]]
$c setcurrent
set g [$c graphics]
set WIDTH [$g cget -clipwidth]
set HEIGHT [$g cget -clipheight]
set LINEH [/font [$g cget -font] cget -height]
set MAXLINE [/ $HEIGHT $LINEH]
PrintLn $g "Canvas ${WIDTH}x$HEIGHT, $MAXLINE lines $LINEH pixel each"
PrintLn $g "Press keys to test, Press 0 to quit"
$c repaint
}

# END OF EXAMPLES

set :src ""
set :err ""

proc showmain {} {
    global SourceCode
    $SourceCode setcurrent
}

set :adc showmain
set :ok [/cmd -label OK -longlabel OK -type ok]
set :de [/cmd -label Edit -longlabel Edit -type cancel]
set :def [/txtbox -commandaction [: {cmd form} {
    global :df
    $:df setcurrent
}]]
set :df [/alert -title Hecl -type info -commandaction [: {cmd alert} {
    global :ok :adc :de :def
    if {eq $cmd $:ok} {
	eval $:adc
    } elseif {eq $cmd $:de} {
	$:def conf -text [$alert cget -text]
	$:def conf -title [$alert cget -title]
	$:def setcurrent
    }
}]]
$:def addcommand $:ok
$:df addcommand $:ok
$:df addcommand $:de

proc showmsg {title text args} {
    global :adc :df
    set argc [llen $args]
    if {= $argc 0} {
	set :adc showmain
    } elseif {= $argc 1} {
	set :adc [lindex $args 0]
    } else {
	throw "showmsg title text ?code?"
    }
    $:df conf -title $title
    $:df conf -text $text
    $:df setcurrent
}

set :s:ac 0
set :s:fm 0
proc Setup {} {
    global :out :ac :s:ac :s:fm
    set :out ""
    set :s:ac [copy $:ac]
    system.gc
    set :s:fm [runtime.freememory]
}

# Exit to IDE
proc done {} {
    global :ac :s:ac :out SourceCode :outc
    set :ac [copy $:s:ac]

    if {strlen $:out} {
	$SourceCode addcommand $:outc
	showmsg Output $:out
    } else {
	showmain
    }
}

proc ShowMem {} {
    global :s:fm
    set total [runtime.totalmemory]
    set mb [long [- $total $:s:fm]]
    system.gc
    set ma [long [- $total [runtime.freememory]]]
    set mc [long [- $ma $mb]]
    showmsg Memory "Total: $total\nBefore: $mb\nAfter: $ma\nChanged: $mc"
}

proc LoadExample {name} {
    global :src :ex SourceCode
    set :src [hget $:ex $name]
    $SourceCode conf -text $:src
}

# Example List
set :sel [/cmd -label Select -longlabel Select -type item]
set :sf [/list -title "Examples:" -selectcommand $:sel -commandaction [: {cmd lst} {
    global :sel :exkeys SourceCode
    if {eq $cmd $:sel} {
	LoadExample [lindex $:exkeys [$lst selection get]]
    }
    $SourceCode setcurrent
}]]
foreach name $:exkeys {
    $:sf append $name
}
$:sf addcommand $:sel
$:sf addcommand [/cmd -label Back -longlabel Back -type back]

# Main
set :run  [/cmd -label Run -longlabel "Run the code" -type ok]
set :undo [/cmd -label Undo -longlabel "Undo edit" -type cancel]
set :errc  [/cmd -label Errors -longlabel "Show errors" -type item]
set :outc  [/cmd -label Output -longlabel "Show output" -type item -priority 2]
set :sample [/cmd -label Examples -longlabel "Examples" -type item -priority 3]
set :exit [/cmd -label Exit -longlabel "Exit Hecl" -type item -priority 4]

$SourceCode conf -commandaction [: {cmd main} {
    global :err :out :run :undo :errc :outc :sample :exit
    if {eq $cmd $:run} {
	global :src
	set :src [$main cget -text]
	Setup
	$main removecommand $:errc
	$main removecommand $:outc
	if {
	    catch {
		upeval 0 $:src
		after 100
		if {$main cget -isshown} {done}
	    } :err
	} {
	    $main addcommand $:errc
	    showmsg Errors $:err {done}
	}
    } elseif {eq $cmd $:undo} {
	global :src
	$main conf -text $:src
    } elseif {eq $cmd $:errc} {
	showmsg Errors $:err
    } elseif {eq $cmd $:outc} {
	showmsg Output $:out
    } elseif {eq $cmd $:sample} {
	global :sf
	$:sf setcurrent
    } elseif {eq $cmd $:exit} {
	midlet.exit
    }
}]
$SourceCode addcommand $:run
$SourceCode addcommand $:undo
$SourceCode addcommand $:sample
$SourceCode addcommand $:exit
$SourceCode conf -uneditable 0

$:sf setcurrent

#DEBUG [join [sort [intro commands]] "\n"]

proc bgerror {e} {
    [lcdui.alert -title "BgError" -text $e] setcurrent
}