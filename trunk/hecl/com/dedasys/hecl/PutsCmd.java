package com.dedasys.hecl;

/**
 * <code>PutsCmd</code> implements the "puts" command, which for the
 * moment just prints some text to stdout.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class PutsCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	System.out.println(argv[1].toString());
    }
}
