package com.dedasys.hecl;

/**
 * <code>AppendCmd</code> implements the "append" command, appending
 * some text to a string.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class AppendCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	Thing result;

	if (interp.existsVar(argv[1])) {
	    result = interp.getVar(argv[1]);
	} else {
	    result = new Thing("");
	}
	for (int i = 2; i < argv.length; i++) {
	    result.appendString(argv[i].toString());
	}
	interp.setVar(argv[1], result);
	interp.setResult(result);
    }
}
