package com.dedasys.hecl;

class SetCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {

	if (argv.length == 3) {
	    interp.setVar(argv[1], argv[2]);
	}
	interp.setResult(interp.getVar(argv[1]));
    }
}
