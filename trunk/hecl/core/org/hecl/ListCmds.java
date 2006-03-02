
package org.hecl;

import java.util.Enumeration;
import java.util.Vector;

class ListCmds {

    public static final int LIST = 1;
    public static final int LLEN = 2;
    public static final int LINDEX = 3;
    public static final int LINSERT = 4;
    public static final int LSET = 5;
    public static final int LRANGE = 6;
    public static final int LAPPEND = 7;

    public static final int FILTER = 8;
    public static final int SEARCH = 9;

    public static final int JOIN = 10;
    public static final int SPLIT = 11;

    static void dispatch(int cmd, Interp interp, Thing[] argv) throws HeclException {
	int idx = 0;
	int last = 0;
	Vector list;
	Vector result;

	switch (cmd) {
	    case LIST:
		result = new Vector();
		for (int i = 1; i < argv.length; i++) {
		    result.addElement(argv[i]);
		}
		interp.setResult(ListThing.create(result));
		break;

	    case LLEN:
		list = ListThing.get(argv[1]);
		interp.setResult(list.size());
		break;

	    case LINDEX:
		list = ListThing.get(argv[1]);
		idx = IntThing.get(argv[2]);
		if (idx >= list.size()) {
		    interp.setResult("");
		} else {
		    /* Count backwards from the end of the list. */
		    if (idx < 0) {
			idx += list.size();
		    }
		    interp.setResult((Thing) list.elementAt(idx));
		}
		break;

	    case LINSERT:
		list = ListThing.get(argv[1]);
		idx = IntThing.get(argv[2]);
		if (idx < 0) {
		    idx += list.size();
		}
		list.insertElementAt(argv[3], idx);
		interp.setResult(argv[1]);
		break;

	    case LSET:
		list = ListThing.get(argv[1]);
		idx = IntThing.get(argv[2]);
		if (idx < 0) {
		    idx += list.size();
		}
		if (argv.length < 4) {
		    list.removeElementAt(idx);
		} else {
		    list.setElementAt(argv[3], idx);
		}
		interp.setResult(argv[1]);
		break;

	    case LRANGE:
		list = ListThing.get(argv[1]);
		int first = IntThing.get(argv[2]);
		last = IntThing.get(argv[3]);
		int ls = list.size();

		if (first < 0) {
		    first += ls;
		}

		if (last < 0) {
		    last += ls;
		}

		if (last <= first || last >= ls || first >= ls) {
		    interp.setResult("");
		}
		Vector resultv = new Vector();
		for (int i = first; i <= last; i++) {
		    resultv.addElement(list.elementAt(i));
		}
		interp.setResult(ListThing.create(resultv));
		break;
	    case LAPPEND:
		list = ListThing.get(argv[1]);
		for (int i = 2; i < argv.length; i++) {
		    list.addElement(argv[i]);
		}
		interp.setResult(argv[1]);
		break;

	    case FILTER:
	    case SEARCH:
		list = ListThing.get(argv[1]);
		Vector results = new Vector();
		String varname = argv[2].toString();
		int sz = list.size();
		Thing val;
		boolean brk = false;

		if (cmd == SEARCH) {
		    brk = true;
		}

		for (int i = 0; i < sz; i++) {
		    val = (Thing) list.elementAt(i);
		    val.copy = true; /* Make sure that the original value
				      * doesn't get fiddled with. */
		    interp.setVar(varname, val);
		    interp.eval(argv[3]);

		    if (IntThing.get(interp.getResult()) != 0) {
			results.addElement(val);
			if (brk == true) {
			    break;
			}
		    }
		}
		interp.setResult(new Thing(new ListThing(results)));
		return;


	    case JOIN:
		list = ListThing.get(argv[1]);
		StringBuffer strres = new StringBuffer("");
		boolean firstone = true;
		String joinstr = null;
		if (argv.length > 2) {
		    joinstr = argv[2].getStringRep();
		} else {
		    joinstr = " ";
		}

		for (Enumeration e = list.elements(); e.hasMoreElements();) {
		    if (firstone == false) {
			strres.append(joinstr);
		    } else {
			firstone = false;
		    }
		    strres.append(((Thing) e.nextElement()).toString());
		}
		interp.setResult(strres.toString());
		return;

	    case SPLIT:
		result = new Vector();
		String str = argv[1].toString();
		String splitstr = null;
		if (argv.length > 2) {
		    splitstr = argv[2].toString();
		} else {
		    /* By default, we split on spaces. */
		    splitstr = " ";
		}

		idx = str.indexOf(splitstr);
		while (idx >= 0) {
		    result.addElement(new Thing(str.substring(last, idx)));
		    last = idx + splitstr.length();
		    idx = str.indexOf(splitstr, last);
		}
		result.addElement(new Thing(str.substring(last, str.length())));
		interp.setResult(ListThing.create(result));

		return;
	}
    }

}
