package com.dedasys.hecl;

import java.util.*;
import java.io.*;

/**
 * <code>SourceHereCmd</code> implements the "sourcehere" command,
 * which executes the code in a given external resource.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class SourceHereCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	String filename = argv[1].toString();
	Eval eval = new Eval();
	/* FIXME - we need to not have File's in here for J2ME. */
	File fl = new File(interp.getScriptName());
	File newfl = new File(fl.getParent(), filename);
	eval.eval(interp, interp.getscript(newfl.toString()));
    }
}
