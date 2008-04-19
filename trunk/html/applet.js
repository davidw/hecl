function sendToEmulator (scriptid, emulatorid) {
    applet = document.getElementById(emulatorid);
    script = document.getElementById(scriptid).value;
    applet.callMethod("runScript", script);
}

function reloadEmulator(id) {
    applet = document.getElementById(id);
    applet.restartApp();
}

function runHecl() {
    s = document.sf1.script1.value;
    applet = document.getElementById("heclet");
    applet.runScript(s);
}
