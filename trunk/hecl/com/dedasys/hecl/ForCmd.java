package com.dedasys.hecl;

/**
 * <code>ForCmd</code> implements the "for" command, which takes 4
 * arguments, a start block of code to evaluate once, a test to
 * determine whether the loop should end, a next block of code to
 * evaluate after each iteration, and a body to execute each time
 * through.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class ForCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	Eval eval = new Eval();

	/* start */
	eval.eval(interp, argv[1]);

	/* test */
	Thing result = eval.eval(interp, argv[2]);

	while (result.isTrue()) {
	    try {
		/* body */
		eval.eval(interp, argv[4]);
	    } catch (HeclException e) {
		if (e.code == HeclException.BREAK) {
		    break;
		} else if (e.code == HeclException.CONTINUE) {
		} else {
		    throw e;
		}
	    }
	    /* next */
	    eval.eval(interp, argv[3]);
	    /* test */
	    result = eval.eval(interp, argv[2]);
	}
    }
}
