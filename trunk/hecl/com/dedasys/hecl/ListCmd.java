package com.dedasys.hecl;

import java.util.*;

/**
 * <code>ListCmd</code> implements the "list", "llen", "lindex" and
 * "lappend" commands.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class ListCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	String cmdname = argv[0].toString();

	if (cmdname.equals("list")) {
	    Vector result = new Vector();
	    for (int i = 1; i < argv.length; i++) {
		result.addElement(argv[i]);
	    }
	    interp.setResult(new Thing(result));
	} else if (cmdname.equals("llen")) {
	    Vector list = argv[1].toList();
	    interp.setResult(new Thing(list.size()));
	} else if (cmdname.equals("lindex")) {
	    Vector list = argv[1].toList();
	    int idx = argv[2].toInt();
	    if (idx >= list.size()) {
		interp.setResult(new Thing(""));
	    } else {
		/* Count backwards from the end of the list. */
		if (idx < 0) {
		    idx += list.size();
		}
		interp.setResult((Thing)list.elementAt(idx));
	    }
	} else if (cmdname.equals("lappend")) {
	    Vector list = argv[1].toList();
	    for (int i = 2; i < argv.length; i ++) {
		list.addElement(argv[i]);
	    }
	    interp.setResult(argv[1]);
	} else if (cmdname.equals("linsert")) {
	    Vector list = argv[1].toList();
	    int idx = argv[2].toInt();
	    list.insertElementAt(argv[3], idx);
	    interp.setResult(argv[1]);
	} else if (cmdname.equals("lset")) {
	    Vector list = argv[1].toList();
	    int idx = argv[2].toInt();
	    if (idx < 0) {
		idx += list.size();
	    }
	    if (argv.length < 4) {
		list.removeElementAt(idx);
	    } else {
		list.setElementAt(argv[3], idx);
	    }
	    interp.setResult(argv[1]);
	}
	return;
    }
}
