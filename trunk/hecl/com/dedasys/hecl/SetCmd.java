package com.dedasys.hecl;

/**
 * <code>SetCmd</code> implements the "set" command, setting a
 * variable to a particular value, or if no value is provided,
 * returning the value of the variable provided.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class SetCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {

	if (argv.length == 3) {
	    interp.setVar(argv[1], argv[2]);
	}
	interp.setResult(interp.getVar(argv[1]));
    }
}
