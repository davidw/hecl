######
# A tiny application showing the usage if some MIDP2.0 ui elements

#################################################
# Create some lcdui.commands
#################################################
set backcmd [lcdui.command -label Back -longlabel Back -type back -priority 1];
set exitcmd [lcdui.command -label Exit -longlabel Exit -type exit -priority 1];
set nocmd [lcdui.command -type screen -label "No" -longlabel "No" -priority 2];
set okcmd [lcdui.command -label Ok -longlabel Ok -type ok -priority 1];
set selectcmd [lcdui.command -label Select -longlabel Select -type item -priority 1];
set smscmd [lcdui.command -label SMS -longlabel SMS -type ok];
set yescmd [lcdui.command -type screen -label "Ja" -longlabel "Ja" -priority 1];

#################################################
# lcdui elements
#################################################

# Read the Hecl logo from the resources...
set logo [lcdui.image -resource /hecl_logo.png];

# A form with various elements...
set datefield [lcdui.date -label "Date/Time"];
set choicegroup [lcdui.choicegroup -label Choice -type popup];
foreach x {c1 c2 c3} {
    $choicegroup append $x;
}
set ticker [lcdui.ticker -text "I am a Ticker!"];

set defform [lcdui.form -title "Demo Form" -commandaction "backToMainMenu"];
$defform configure -ticker $ticker;
$defform append [lcdui.textfield -label "TextField" -text "TextField" -uneditable 1];
$defform append [lcdui.textfield -label "Editable TextField" -text "ediatble text"];
$defform append [lcdui.imageitem -image $logo -anchor center];
$defform append [lcdui.spacer -label spacer1 -minwidth 200 -minheight 2];
$defform append [lcdui.stringitem -text "Stringitem"];
$defform append [lcdui.spacer -label spacer2 -minwidth 200 -minheight 4];
$defform append $datefield;
$defform append $choicegroup;
$defform append [lcdui.imageitem -image $logo];
$defform addcommand $backcmd;

set deflist [lcdui.list];
$deflist addcommand $selectcmd;
$deflist addcommand $backcmd;


# The main menu
set mainmenu [lcdui.list -title "Hecl MIDP2.0 Demo" -commandaction "mainsel"];
$mainmenu addcommand $exitcmd;
$mainmenu addcommand $selectcmd;
$mainmenu append "List Demo";
$mainmenu append "Form Demo";
$mainmenu append "Call...";
$mainmenu append "Send SMS...";
$mainmenu append "TextBox...";
$mainmenu append "Alert";
$mainmenu append "Information";

set menu1 [lcdui.list -commandaction "menu1sel"];
$menu1 addcommand $backcmd;
$menu1 addcommand $selectcmd;
$menu1 deleteall;   
foreach x {1 2 3 4 5 6 7 8 9 10} {$menu1 append "E$x"};

set infoform [lcdui.form -title Information -commandaction "backToMainMenu"];
$infoform append [lcdui.textfield -label "Midlet Version" \
		      -text [midlet.getappproperty "MIDlet-Version"] -uneditable 1];
foreach {l p} {
    "Java Profil" microedition.profiles
    "Java Configuration" microedition.configuration
    "Java Locale" microedition.locale
    "Java Plattform" microedition.platform
    "Java Encoding" microedition.encoding
    "Java Version" java.fullversion
    "MMAPI Snapshot möglich" supports.video.capture
    "MMAPI Snapshot Formate" video.snapshot.encodings} {
    if {= [catch {set p [system.getproperty $p]}] 0} {
	if {> [strlen $p] 0} {
	    $infoform append [lcdui.textfield -label $l -text $p -uneditable 1];
	}
    }
}
$infoform append \
    [lcdui.textfield -label "Snapshot" \
	 -text [midlet.checkpermissions \
		    "javax.microedition.media.control.VideoControl.getSnapshot"] \
	 -uneditable 1];
$infoform append \
    [lcdui.textfield -label "File Access" \
	 -text [midlet.checkpermissions \
		    "javax.microedition.io.Connector.file.read"] \
	 -uneditable 1];
$infoform addcommand $backcmd;

# a textbox
set textbox [lcdui.textbox -text "Hello world" -commandaction backToMainMenu];
$textbox addcommand $backcmd;

proc main {} {
    global mainmenu;
    $mainmenu setcurrent;
}

proc mainsel {cmd d} {
    global exitcmd;
    if {eq $cmd $exitcmd} {
	midlet.exit;
    }
    set idx [$d selection get];
    if {< $idx 0} {
	return;
    }
    if {= $idx 0} {
	global menu1;
	$menu1 setcurrent;
    } elseif {= $idx 1} {
	global defform;
	$defform setcurrent;
    } elseif {= $idx 2} {
	# Call...
	midlet.platformrequest "tel:+4369911259952";
    } elseif {= $idx 3} {
	# Send SMS...
	midlet.platformrequest "sms:+4369911259952";
    } elseif {= $idx 4} {
	global textbox;
	$textbox setcurrent;
    } elseif {= $idx 5} {
	[lcdui.alert -type confirmation -title Alert\
	     -commandaction backToMainMenu \
	     -text "This is the alert message!" -timeout forever] setcurrent;
    } elseif {= $idx 6} {
	global infoform;
	$infoform setcurrent;
    }
}

proc menu1sel {cmd d} {
    global backcmd selectcmd mainmenu {lcdui.select_command};
    if {eq $cmd $backcmd} {
	$mainmenu setcurrent;
    } elseif {or [eq $cmd $selectcmd] [eq $cmd ${lcdui.select_command}]} {
	puts "selected index=[$d selection get]";
    }
}

proc backToMainMenu {cmd d} {
    global mainmenu
    $mainmenu setcurrent; 
}

#proc showsmsform {back} {
#    global textbox backcmd okcmd;
#    setBackmenu $back;
#    $textbox configure -title "Schreibe SMS" -maxlength 120;
#    $textbox configure -commandaction "handlesmscmd";
#    $textbox addcommand $backcmd;
#    $textbox addcommand $okcmd;
#    $textbox setcurrent;
#}

### Local Variables:
### mode:tcl
### coding:utf-8
### End:

main
