package com.dedasys.hecl;

import java.util.*;

class TimeCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {

	Eval eval = new Eval();
	long now = new Date().getTime();
	eval.eval(interp, argv[1]);
	long done = new Date().getTime();
	interp.setResult(new Thing((int)(done - now)));
    }
}
