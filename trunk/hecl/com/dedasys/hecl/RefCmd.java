package com.dedasys.hecl;

/**
 * <code>RefCmd</code> implements the "ref" command, similiar in
 * practice to the &foo variable syntax.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class RefCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {

	if (argv.length == 3) {
	    interp.setVar(argv[1], argv[2]);
	}
	interp.setResult(interp.getVar(argv[1]));
    }
}
