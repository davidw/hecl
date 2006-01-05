# alert.hcl --

# alert example

proc DoAlert {} {
    global tf
    set txt [getprop &tf text]
    setcurrent [alert label "Alert Example" text &txt]
}

set f [form label "Alert Example" code {
    set tf [textfield label "Alert text:"]
    cmd label "Run alert" code DoAlert
}]

setcurrent &f