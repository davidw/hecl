package com.dedasys.hecl;

class WhileCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	Eval eval = new Eval();

	Thing result = eval.eval(interp, argv[1]);
	int argnum = 0;

	while (result.isTrue()) {
	    try {
		eval.eval(interp, argv[2]);
	    } catch (HeclException e) {
		if (e.code == HeclException.BREAK) {
		    break;
		} else if (e.code == HeclException.CONTINUE) {
		} else {
		    throw e;
		}
	    }
	    result = eval.eval(interp, argv[1]);
	}
    }
}
