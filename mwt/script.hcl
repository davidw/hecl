# vim: syntax=tcl
#-- set toolkit manager
set htm [mwt.manager]
set width [$htm width]
set height [$htm height]
set bw [- $width [* $width 0.1]]
set bh [- $height [* $height 0.1]] 

#-- classic font
set f1 [mwt.font -size "size_medium" -color D9C01C]
#-- classic skin
set skin [mwt.skin {FFE220 000000 5999FF 000000 D9C01C}]
set skin1 [mwt.skin {FFCCCF A34184 FF66CF FF66CF}]
set skin2 [mwt.skin {FFFFFF 000000 A1C632 CFFF40}]	


set main [mwt.window -x 5 -y 5 -width [- $width 5] -height [- $height 5] -skin [list $skin "style_default"]]
$main add [mwt.button -x [- [/ [$main cget -width] 2] 40] -y 20 -width 80 -height 35 -text "buttons" -actiontype 1 -font [list "style_default" $f1]]
$main add [mwt.button -x [- [/ [$main cget -width] 2] 40] -y 60 -width 80 -height 35 -text "labels" -actiontype 2 -font [list "style_default" $f1]]
$main add [mwt.button -x [- [/ [$main cget -width] 2] 40] -y 100 -width 80 -height 35 -text "window" -actiontype 3 -font [list "style_default" $f1]]
$main add [mwt.button -x [- [/ [$main cget -width] 2] 40] -y 140 -width 80 -height 35 -text "font" -actiontype 4 -font [list "style_default" $f1]]
$main add [mwt.button -x [- [/ [$main cget -width] 2] 40] -y 180 -width 80 -height 35 -text "skins" -actiontype 5 -font [list "style_default" $f1]]

$main cset -focusfirst dummy
$htm main $main
$htm run

proc processevent {actiontype comp} {
    global main htm
    if {eq 1 $actiontype} {
        buttons
    } elseif {eq 2 $actiontype} {
        labels
    } elseif {eq 3 $actiontype} {
        window
	  } elseif {eq 4 $actiontype} {
				fonts
		} elseif {eq 5 $actiontype} {
			skins
    } elseif {eq 10 $actiontype} {
        $main dialogclose
    } elseif {eq 20 $actiontype} {  
        $htm main $main
    }
}

set align [list "bottom_center" "bottom_left" "bottom_right" "middle_center" "middle_left" "middle_right" "top_center" "top_left" "top_right"]
set fontsize [list "size_large" "size_medium" "size_small"]
set fontnames [list "style_plain" "style_bold" "style_italic" "style_underlined"]
set fontface [list "face_system" "face_monospace" "face_proportional"]
set fotnmap [list "type_system" "type_mapped" "type_strip"]



proc buttons {} {
    global width height bw bh f1 skin main skin1 align
		
    set win [mwt.window -x [- [/ $width 2] [/ $bw 2]] -y [- [/ $height 2] [/ $bh 2]] -width $bw -height $bh -skin [list $skin1 "style_default"]]
		set y 15
		set dx 10
		set count 0
		#-- lopp over all align props
		foreach p $align {
    	$win add [mwt.button -x [- [/ [$win cget -width] 2] $dx] -y $y -width 80 -height 35 -text $p -actiontype 10 -align $p]
			if {eq [% $count 2] 0} {
				set dx 100
				set y [+ $y 50]
			} else {	
				set dx 10
			}
			incr $count
		}
    $win cset -focusfirst dummy
    $main dialogopen $win
}
proc labels {} {
    global width height bw bh f1 skin main skin1 align
		
    set win [mwt.window -x [- [/ $width 2] [/ $bw 2]] -y [- [/ $height 2] [/ $bh 2]] -width $bw -height $bh -skin [list $skin1 "style_default"]]
		set y 15
		set dx 10
		set count 0
		foreach p $align {
    	$win add [mwt.label -x [- [/ [$win cget -width] 2] $dx] -y $y -width 80 -height 35 -text $p -align $p]
			if {eq [% $count 2] 0} {
				set dx 100
				set y [+ $y 50]
			} else {	
				set dx 10
			}
			incr $count
		}

    $win add [mwt.button -x 10 -y 20 -width 80 -height 35 -text "BACK" -actiontype 10]
    $win cset -focusfirst dummy
    $main dialogopen $win
}

proc window {} {
   global width height bw bh f1 skin main skin2 htm
   set win [mwt.window -x [- [/ $width 2] [/ $bw 2]] -y [- [/ $height 2] [/ $bh 2]] -width $bw -height $bh -skin [list $skin2 "style_default"]] 
   set win1 [mwt.window -x 25 -y 25 -width 50 -height 50 -skin [list $skin "style_default"]] 
   $win1 add [mwt.label -x 12 -y 12 -width 25 -height 25 -text "win1"] 
   set win2 [mwt.window -x 25 -y 85 -width 50 -height 50 -skin [list $skin "style_default"]] 
   $win2 add [mwt.label -x 12 -y 12 -width 25 -height 25 -text "win2"]
   set win3 [mwt.window -x 85 -y 25 -width 50 -height 50 -skin [list $skin "style_default"]] 
   $win3 add [mwt.label -x 12 -y 12 -width 25 -height 25 -text "win3"]
   set win4 [mwt.window -x 85 -y 85 -width 50 -height 50 -skin [list $skin "style_default"]] 
   $win4 add [mwt.label -x 12 -y 12 -width 25 -height 25 -text "win4"]
   $win add [mwt.button -x [- [/ [$win cget -width] 2] 10] -y 220 -width 80 -height 35 -text "BACK" -actiontype 20]
   $win add $win1
   $win add $win2
   $win add $win3
   $win add $win4
   $win cset -focusfirst dummy
   $htm main $win
}

proc fonts {} {
  global width height bw bh f1 skin main skin1 fontsize fontface
		
    set win [mwt.window -x [- [/ $width 2] [/ $bw 2]] -y [- [/ $height 2] [/ $bh 2]] -width $bw -height $bh -skin [list $skin1 "style_default"]]
		set y 15
		set dx 10
		set count 0
		foreach fsize $fontsize {
			foreach fface $fontface {
				set f_ [mwt.font -size $fsize -face $fface]
    		$win add [mwt.label -x [- [/ [$win cget -width] 2] $dx] -y $y -width 80 -height 35 -text $fsize -font [list "style_default" $f_]]
				if {eq [% $count 2] 0} {
					set dx 100
					set y [+ $y 50]
				} else {	
					set dx 10
				}
			incr $count
			}
		}

    $win add [mwt.button -x 10 -y 20 -width 80 -height 35 -text "BACK" -actiontype 10]
    $win cset -focusfirst dummy
    $main dialogopen $win

}

proc skins {} {
		
		global width height bw bh main
			
		set skin [mwt.skin {FFE220 000000 5999FF 000000 D9C01C}]
		
		set skin_default [mwt.skin {FFE220 000000 5999FF 000000 D9C01C}]
		set skin_style_disable [mwt.skin {2ee220 000000 ff8159 000000 a6d91c}]
		set skin_style_focused [mwt.skin {cbd9cb 000000 929c92 000000 926c92}]
		set skin_style_pressed [mwt.skin {a12832 000000 d79ca2 000000 9c9fd7}]
    set win [mwt.window -x [- [/ $width 2] [/ $bw 2]] -y [- [/ $height 2] [/ $bh 2]] -width $bw -height $bh -skin [list $skin "style_default"]]
		set b [mwt.button -x 10 -y 10 -width 80 -height 35 -text "TEST"]
		$b cset -skin [list $skin_default "style_default"]
		$b cset -skin [list $skin_style_disable "style_disable"]
		$b cset -skin [list $skin_style_focused "style_focused"]
		$b cset -skin [list $skin_style_pressed "style_pressed"]
		$win add $b
		$win add [mwt.button -x 10 -y 50 -width 80 -height 35 -text "BACK" -actiontype 10]
    $win cset -focusfirst dummy
    $main dialogopen $win
}

proc keypressed {keycode} {
    global htm 
    [$htm getmain] setkeystate $keycode "keystate_pressed" "true"
}

proc keyreleased {keycode} {
    global htm 
    [$htm getmain] setkeystate $keycode "keystate_released" "true"
}

