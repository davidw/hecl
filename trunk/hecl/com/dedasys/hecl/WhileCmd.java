package com.dedasys.hecl;

/**
 * <code>WhileCmd</code> implements the "while" command, which
 * executes its second argument until the first argument returns
 * false.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class WhileCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	Eval eval = new Eval();

	Thing result = eval.eval(interp, argv[1]);

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
