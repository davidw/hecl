package com.dedasys.hecl;

/**
 * <code>StringCmd</code> implements the "slen" and "sindex" commands.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class StringCmd implements Command {

    public void cmdCode(Interp interp, Thing []argv) {
	String cmd = argv[0].toString();
	String str = argv[1].toString();

	if (cmd.equals("slen")) {
	    interp.setResult(new Thing(str.length()));
	    return;
	} else if (cmd.equals("sindex")) {
	    int idx = argv[2].toInt();
	    try {
		char chars[] = new char[1];
		chars[0] = str.charAt(idx);
		interp.setResult(
		    new Thing(new String(chars)));
	    } catch (StringIndexOutOfBoundsException e) {
		interp.setResult(new Thing(""));
	    }
	    return;
	}
    }

}
