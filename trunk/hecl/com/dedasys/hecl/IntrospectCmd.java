package com.dedasys.hecl;

import java.util.*;

class IntrospectCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	String subcmd = argv[1].toString();
	Vector results = new Vector();
	if (subcmd.equals("commands")) {
	    for (Enumeration e = interp.cmdNames() ; e.hasMoreElements(); ) {
		Thing t = new Thing((String)e.nextElement());
		results.add(t);
	    }
	    interp.setResult(new Thing(results));
	    return;
	}
    }
}
