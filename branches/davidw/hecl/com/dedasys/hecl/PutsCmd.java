package com.dedasys.hecl;

class PutsCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	System.out.println(argv[1].toString());
    }
}
