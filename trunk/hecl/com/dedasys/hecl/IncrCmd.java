package com.dedasys.hecl;

/**
 * <code>IncrCmd</code> implements the "incr" command.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class IncrCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {

	int m = argv[1].toInt();
	int n = 1;
	if (argv.length > 2) {
	    n = argv[2].toInt();
	}
	int r = m + n;
	argv[1].setInt(r);
	interp.setResult(argv[1]);
    }
}
