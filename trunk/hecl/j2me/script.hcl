proc err {txt} {
    global errf
    setcurrent &errf
    string $txt
};

proc run {} {
    global main
    if { catch {upeval [getprop &main text]} problem } {
	err $problem
    }
}

set errf [form label Error code {
    cmd label Back type back code {setcurrent &main}
}];

# This is a mess because it's got to fit in 900 characters for my
# Nokia phone.
set script {set num 0
set bckbtn {cmd label Back code back type back}
proc back {} {global frm; setcurrent &frm }
proc evaltxt {tf} {
global results
set r [eval [getprop &tf text]]
setprop &results text &r
}
proc maketb {} {
global bckbtn
setcurrent [textbox label {New TextBox} text defaulttext len 100 code $bckbtn]
}
proc makeform {} { global bckbtn
setcurrent [form code $bckbtn] }
proc makelb {} {
global bckbtn
global bckbtn;
setcurrent [listbox code { string "Rock" ; string "Paper" ; string "Scissors" ; eval $bckbtn } type exclusive]
}
set frm [form label hello code {
stringitem label {Hecl Demo} text {}
set tf [textfield label "Code:"]
set results [stringitem label "Results:"]
cmd label {Eval} code {evaltxt &tf}
cmd label {Make Form} code makeform
cmd label {Make Textbox} code maketb
cmd label {Make Listbox} code makelb
cmd label {Exit} type exit code exit
}]
setcurrent &frm
}

set main [textbox label Hecl code {
    cmd label Switch code [list setcurrent &errf] ;
    cmd label Run code run ;
} len 900 text &script];

setcurrent &main;
