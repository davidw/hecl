package com.dedasys.hecl;

import java.util.*;

/**
 * <code>SourceCmd</code> implements the "source" command, loading and
 * executing a script resource (be it a file or something else).
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
class SourceCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	String name = argv[1].toString();
	Eval eval = new Eval();
	eval.eval(interp, interp.getscript(name));
    }
}
