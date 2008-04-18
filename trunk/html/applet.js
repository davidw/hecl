function HW () {
    applet = document.getElementById("microemulator");
    s = document.sf2.script2.value;
    applet.callMethod("runScript", s);
}

function reloadEmulator() {
    applet = document.getElementById("microemulator");
    applet.restartApp();
}

function runHecl() {
    s = document.sf1.script1.value;
    applet = document.getElementById("heclet");
    applet.runScript(s);
}
