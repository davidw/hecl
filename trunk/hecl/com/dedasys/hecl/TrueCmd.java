package com.dedasys.hecl;

class TrueCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {

	interp.setResult(new Thing(1));
    }
}
