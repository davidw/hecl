package com.dedasys.hecl;

/**
 * <code>GlobalCmd</code> implements the "global" command.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class GlobalCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	String varname = argv[1].toString();

	if (!interp.existsVar(varname, 0)) {
	    Thing newThing = new Thing("");
	    interp.setVar(varname, newThing, 0);
	    interp.setVar(argv[1], newThing);
	} else {
	    interp.setVar(
		argv[1], interp.getVar(varname, 0));

	}
    }
}
