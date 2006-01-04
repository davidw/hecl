package com.dedasys.hecl;

import java.util.*;

/**
 * <code>CatchCmd</code> implements the "catch" command.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class CatchCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	Eval eval = new Eval();
	Thing result;
	Thing retval;
	try {
	    eval.eval(interp, argv[1]);
	    result = interp.getResult();
	    retval = new Thing(0);
	} catch (HeclException e) {
	    result = e.getStack();
	    retval = new Thing(1);
	}

	if (argv.length == 3) {
	    interp.setVar(argv[2].toString(), result);
	}

	interp.setResult(retval);
    }
}
