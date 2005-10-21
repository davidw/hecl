set script {set num 0;
    set bckbtn {cmd label Back code back type back};
    proc evaltxt {tf} {
	global results
	set r [eval [getprop &tf text]]
	setprop &results text &r
    }
    proc back {} {global newform; setcurrent &newform };
    proc maketextbox {} {
	global bckbtn;
	setcurrent [textbox label {New TextBox} text defaulttext len 100 code $bckbtn];
    } ;
    proc makeform {} { global bckbtn; setcurrent [form code $bckbtn] } ;
    proc makelistbox {} {
	global bckbtn;
	setcurrent [listbox code {
	    string "Rock"
	    string "Paper"
	    string "Scissors"
	    eval $bckbtn
	} type exclusive]
    }
    set newform [form label hello code {
	stringitem label {Hecl Demo} text {};
	set tf [textfield label "Eval code:"];
	set results [stringitem label "Results:"];
	cmd label {Eval} code [list evaltxt &tf];
	cmd label {Make Form} code makeform;
	cmd label {Make Textbox} code maketextbox;
	cmd label {Make Listbox} code makelistbox;
	cmd label {Exit} type exit code exit;
    }];
    setcurrent &newform;
}

proc err {txt} {global errf; setcurrent &errf; string $txt};
proc run {} {global main; if { catch {upeval [getprop &main text]} problem } {err $problem} };
set errf [form label Error code {cmd label Back type back code {setcurrent &main}}];
set main [textbox label Hecl code {
    cmd label Switch code [list setcurrent &errf] ;
    cmd label Run code run ;
} text $script len 2000];
setcurrent &main;
