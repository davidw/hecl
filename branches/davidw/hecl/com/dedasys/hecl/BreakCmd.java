package com.dedasys.hecl;

/**
 * <code>BreakCmd</code> implements the "break" and "continue"
 * commands.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class BreakCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {

	String cmd = argv[0].toString();
	if (cmd.equals("break")) {
	    throw new HeclException(HeclException.BREAK);
	} else if (cmd.equals("continue")) {
	    throw new HeclException(HeclException.CONTINUE);
	}
    }
}
