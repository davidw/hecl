package com.dedasys.hecl;

/**
 * <code>TrueCmd</code> implements the "true" command, which always
 * returns a 'true' value.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class TrueCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {

	interp.setResult(new Thing(1));
    }
}
