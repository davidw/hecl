package com.dedasys.hecl;

import java.util.*;

class SourceCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	String filename = argv[1].toString();
	Eval eval = new Eval();
	eval.eval(interp, interp.getscript(filename));
    }
}
