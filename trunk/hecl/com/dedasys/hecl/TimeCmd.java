package com.dedasys.hecl;

import java.util.*;

/**
 * <code>TimeCmd</code> implements the "time" command, which times the
 * execution of a script.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

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
