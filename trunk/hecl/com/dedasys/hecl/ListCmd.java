package com.dedasys.hecl;

import java.util.*;

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
	    if (idx >= list.size() || idx < 0) {
		interp.setResult(new Thing(""));
	    } else {
		interp.setResult((Thing)list.elementAt(idx));
	    }
	} else if (cmdname.equals("lappend")) {
	    Vector list = argv[1].toList();
	    for (int i = 2; i < argv.length; i ++) {
		list.addElement(argv[i]);
	    }
	    interp.setResult(argv[1]);
	} else if (cmdname.equals("join")) {
	    Vector list = argv[1].toList();
	    StringBuffer result = new StringBuffer("");
	    boolean first = true;
	    String joinstr = null;
	    if (argv.length > 2) {
		joinstr = argv[2].toString();
	    } else {
		joinstr = " ";
	    }

	    for (Enumeration e = list.elements();
		 e.hasMoreElements();) {
		if (first == false) {
		    result.append(joinstr);
		} else {
		    first = false;
		}
		result.append(e.nextElement().toString());
	    }
	    interp.setResult(new Thing(result));
	} else if (cmdname.equals("split")) {
	    Vector result = new Vector();
	    String str = argv[1].toString();
	    int idx = 0;
	    int last = 0;
	    String splitstr = null;
	    if (argv.length > 2) {
		splitstr = argv[2].toString();
	    } else {
		splitstr = " ";
	    }

	    idx = str.indexOf(splitstr);
	    while (idx >= 0) {
		result.addElement(new Thing(str.substring(last, idx)));
		last = idx + splitstr.length();
		idx = str.indexOf(splitstr, last);
	    }
	    result.addElement(new Thing(str.substring(last, str.length())));
	    interp.setResult(new Thing(result));
	}
	return;
    }
}
