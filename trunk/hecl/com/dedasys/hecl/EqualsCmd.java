package com.dedasys.hecl;

/**
 * <code>EqualsCmd</code> implements basic equivalence commands - "=",
 * ">" and "<".
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class EqualsCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	char cmd = (argv[0].toString()).charAt(0);

	switch (cmd) {
	    case '=':
		interp.setResult(new Thing(argv[1].equals(argv[2])));
		break;
	    case '>':
		interp.setResult(new Thing(argv[1].toInt() > argv[2].toInt()));
		break;
	    case '<':
		interp.setResult(new Thing(argv[1].toInt() < argv[2].toInt()));
		break;
	}
    }
}
