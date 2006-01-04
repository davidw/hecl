package com.dedasys.hecl;

import java.util.*;

/**
 * <code>Proc</code> is the class behind the "proc" command.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class Proc implements Command {
    private Thing vars;
    private Thing code;

    public Proc(Thing cmdvars, Thing cmdcode) {
	vars = cmdvars;
	code = cmdcode;
    }

    public void cmdCode (Interp interp, Thing [] argv)
	throws HeclException {
	Vector varnames = vars.toList();
	Eval eval = new Eval();
	int i = 0;

	interp.stackIncr();
	for (i = 0; i < varnames.size(); i++) {
	    if (i == argv.length - 1) {
		interp.stackDecr();
		throw new HeclException(
		    "proc " + argv[0] + " doesn't have enough arguments");
	    }
	    interp.setVar(varnames.elementAt(i).toString(), argv[i + 1]);
	}
	// System.out.println("command is " + argv[0] + " i is " + i + " argv.length is " + argv.length);

	if (i != argv.length - 1) {
	    interp.stackDecr();
	    throw new HeclException(
		"proc " + argv[0] + " has too many arguments");
	}

	interp.setResult(eval.eval(interp, code));
	interp.stackDecr();
    }
}
