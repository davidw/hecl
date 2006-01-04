package com.dedasys.hecl;

import java.util.*;

/**
 * <code>ForeachCmd</code> implements the "foreach" command.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class ForeachCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	Eval eval = new Eval();

	Vector varlist = argv[1].toList();
	Vector list = argv[2].toList();
	int i = 0;
	boolean end = false;
	while (true) {
	    /* This is for foreach loops where we have more than one
	     * variable to set: foreach {m n} $somelist { code ... } */
	    for (Enumeration e = varlist.elements(); e.hasMoreElements(); ) {
		if (end == true) {
		    throw new HeclException("Foreach argument list does not match list length");
		}

		Thing element = (Thing)list.elementAt(i);
		String varname = ((Thing)e.nextElement()).toString();
		interp.setVar(varname, element);
		i ++;
		if (i == list.size()) {
		    end = true;
		}
	    }

	    eval.eval(interp, argv[3]);
	    if (end == true)
		break;
	}
    }
}
