package org.hecl;

import java.util.Enumeration;
import java.util.Vector;

class ControlCmds {

    public static final int IF = 1;
    public static final int FOR = 2;
    public static final int FOREACH = 3;
    public static final int WHILE = 4;

    public static final int BREAK = 5;
    public static final int CONTINUE = 6;

    static void dispatch(int cmd, Interp interp, Thing[] argv) throws HeclException {
	switch (cmd) {
	    case IF:
		interp.eval(argv[1]);
		Thing result = interp.result;

		if (Thing.isTrue(result)) {
		    interp.eval(argv[2]);
		    return;
		}

		/*
		 * We loop through to capture all else if...else if...else
		 * possibilities.
		 */
		if (argv.length > 3) {
		    for (int i = 3; i <= argv.length; i += 3) {
			if (argv[i].getStringRep().equals("else")) {
			    /* It's an else block, evaluate it and return. */
			    interp.eval(argv[i + 1]);
			    return;
			} else if (argv[i].getStringRep().equals("elseif")) {
			    /*
			     * elseif - check and see if the condition is true, if so
			     * evaluate it and return.
			     */
			    interp.eval(argv[i + 1]);
			    result = interp.result;
			    if (Thing.isTrue(result)) {
				interp.eval(argv[i + 2]);
				return;
			    }
			}
		    }
		}
		return;

	    case FOR:
		/* start */
		interp.eval(argv[1]);

		/* test */
		while (Thing.isTrue(interp.eval(argv[2]))) {
		    try {
			/* body */
			interp.eval(argv[4]);
		    } catch (HeclException e) {
			if (e.code == HeclException.BREAK) {
			    break;
			} else if (e.code == HeclException.CONTINUE) {
			} else {
			    throw e;
			}
		    }
		    /* next */
		    interp.eval(argv[3]);
		}
		return;

	    case FOREACH:
		Vector list = ListThing.get(argv[2]);
		if (list.size() == 0) {
		    return;
		}
		Vector varlist = ListThing.get(argv[1]);
		int i = 0;
		boolean cont = true;

		//System.out.println("argv2 is " + argv[2] + " copy is " + argv[2].copy);

		while (cont) {
		    /*
		     * This is for foreach loops where we have more than one variable to
		     * set: foreach {m n} $somelist { code ... }
		     */
		    for (Enumeration e = varlist.elements(); e.hasMoreElements();) {
			if (cont == false) {
			    throw new HeclException(
				"Foreach argument list does not match list length");
			}

			Thing element = (Thing) list.elementAt(i);
			element.copy = true; /* Make sure that we don't fiddle
					      * with the original value. */
			String varname = ((Thing) e.nextElement()).getStringRep();

			//System.out.println("set " +varname+ " to " +element+ " copy: " + element.copy);

			interp.setVar(varname, element);
			i++;
			if (i == list.size()) {
			    cont = false;
			}
		    }

		    try {
			interp.eval(argv[3]);
		    } catch (HeclException e) {
			if (e.code == HeclException.BREAK) {
			    break;
			} else if (e.code == HeclException.CONTINUE) {
			} else {
			    throw e;
			}
		    }
		}
		return;

	    case WHILE:
		while (Thing.isTrue(interp.eval(argv[1]))) {
		    try {
			interp.eval(argv[2]);
		    } catch (HeclException e) {
			if (e.code == HeclException.BREAK) {
			    break;
			} else if (e.code == HeclException.CONTINUE) {
			} else {
			    throw e;
			}
		    }
		}
		return;

	    case BREAK:
		throw new HeclException(HeclException.BREAK);

	    case CONTINUE:
		throw new HeclException(HeclException.CONTINUE);
	}
    }
}
