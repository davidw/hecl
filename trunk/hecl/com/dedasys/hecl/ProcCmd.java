package com.dedasys.hecl;

/**
 * <code>ProcCmd</code> implements the "proc" command, also
 * implemented in the Proc.java file.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class ProcCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	Proc proc = new Proc(argv[2], argv[3]);
	interp.addCommand(argv[1].toString(), proc);
    }
}
