package com.dedasys.hecl;

/**
 * <code>ReturnCmd</code> sets the result of the operation being
 * evaluated.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class ReturnCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {

	interp.setResult(argv[1]);
    }
}
