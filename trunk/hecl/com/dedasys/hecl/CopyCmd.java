package com.dedasys.hecl;

/**
 * <code>CopyCmd</code> implements the "copy" command, which copies a
 * variable.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class CopyCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {

	interp.setResult(interp.getVar(argv[1]).copy());
    }
}
