package com.dedasys.hecl;

/**
 * <code>EvalCmd</code> implements the "eval" command.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class EvalCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	Eval eval = new Eval();

	Thing result = eval.eval(interp, argv[1]);
	interp.setResult(result);
    }
}
