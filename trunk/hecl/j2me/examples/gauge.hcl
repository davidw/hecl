# gauge.hcl --
# gauge example

proc cycle {} {
    global g
    global si
    for {set i 0} {< &i 11} {incr &i} {
	setprop &g val $i
	setprop &si text $i
    }
}

set f [form label "Gauge Example" code {
    set si [stringitem label "Value:" text 0]
    set g [gauge label Foo maxval 10 val 0]
    cmd label "move" code cycle
    cmd label "reset" code {
	setprop &g val 0
	setprop &si text 0
    }
}]
setcurrent $f;
