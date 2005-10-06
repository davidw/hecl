set script {set num 0;
    set bckbtn {cmd label Back code back type back};
    proc puttext {tf} { string [getprop $tf text] };
    proc putnum {num} { incr &num; string $num };
    proc back {} {global newform; setcurrent $newform };
    proc maketb {} {
	global bckbtn; setcurrent [textbox label {New TextBox} text defaulttext len 100 code $bckbtn]
    } ;
    proc makeform {} { global bckbtn; setcurrent [form code $bckbtn] } ;
    set newform [form label hello code {
	stringitem label {Hecl Demo} text {};
	set tf [textfield label text:];
	set tfeval [textfield label {eval hecl code:}];
	cmd label {Print Text} code [list puttext $tf];
	cmd label {Eval} code {string [eval [getprop $tfeval text]]};
	cmd label {Print Number} code [list putnum &num] ;
	cmd label {Make Textbox} code maketb;
	cmd label {Make Form} code makeform;
	cmd label {Exit} type exit;
    }];
    setcurrent $newform;
}

proc err {txt} {global errf; setcurrent $errf; string $txt};
proc run {} {global main; if { catch {upeval [getprop $main text]} problem } {err $problem} };
set errf [form label Error code {cmd label Back type back code {setcurrent $main}}];
set main [textbox label Hecl code {
    cmd label Switch code [list setcurrent $errf] ;
    cmd label Run code run ;
} text $script len 1000];
setcurrent $main;
