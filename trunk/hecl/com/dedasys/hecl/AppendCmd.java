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
	StringBuffer strb;
	Thing result = argv[1];
	strb = result.toStringBuffer();
	for (int i = 2; i < argv.length; i++) {
	    strb.append(argv[i].toString());
	}
	interp.setResult(result);
    }
}
