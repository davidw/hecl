package com.dedasys.hecl;

/**
 * <code>IfCmd</code> implements the "if" command.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
class IfCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	Eval eval = new Eval();

	Thing result = eval.eval(interp, argv[1]);
	int argnum = 0;

	if (result.isTrue()) {
	    eval.eval(interp, argv[2]);
	    return;
	}

	if (argv.length > 3) {
	    for (int i = 3; i <= argv.length; i += 3) {
		if (argv[i].toString().equals("else")) {
		    /* It's an else block, evaluate it and return. */
		    eval.eval(interp, argv[i + 1]);
		    return;
		} else if (argv[i].toString().equals("elseif")) {
		    /* elseif - check and see if the condition is
		     * true, if so evaluate it and return. */
		    result = eval.eval(interp, argv[i + 1]);
		    if (result.isTrue()) {
			eval.eval(interp, argv[i + 2]);
			return;
		    }
		}
	    }
	}
    }
}
