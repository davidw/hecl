package com.dedasys.hecl;

class ReturnCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {

	interp.setResult(argv[1]);
    }
}
