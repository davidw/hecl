/* Copyright 2006 David N. Welton

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.hecl;

import java.util.Enumeration;
import java.util.Vector;

/**
 * The <code>ListCmds</code> class implements the Hecl commands that
 * operate on lists, which are implemented by the ListThing class.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
class ListCmds extends Operator {
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


    public RealThing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
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
	    return new ListThing(result);

	  case LLEN:
	    list = ListThing.get(argv[1]);
	    return new IntThing(list.size());

	  case LINDEX:
	    list = ListThing.get(argv[1]);
	    idx = NumberThing.asNumber(argv[2]).intValue();
	    if (idx >= list.size()) {
		interp.setResult("");
	    } else {
		/* Count backwards from the end of the list. */
		if (idx < 0) {
		    idx += list.size();
		}
		interp.setResult((Thing)list.elementAt(idx));
	    }
	    break;

	  case LINSERT:
	    list = ListThing.get(argv[1]);
	    idx = NumberThing.asNumber(argv[2]).intValue();
	    if (idx < 0) {
		idx += list.size();
	    }
	    list.insertElementAt(argv[3], idx);
	    interp.setResult(argv[1]);
	    break;

	  case LSET:
	    list = ListThing.get(argv[1]);
	    idx = NumberThing.asNumber(argv[2]).intValue();
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
	    int first = NumberThing.asNumber(argv[2]).intValue();
	    last = NumberThing.asNumber(argv[3]).intValue();
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
	    return new ListThing(resultv);

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
	    return new ListThing(results);

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
	    return new StringThing(strres);

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
	    break;
	  default:
	    throw new HeclException("Unknown list command '"
				    + argv[0].toString() + "' with code '"
				    + cmd + "'.");
	}
	return null;
    }


    public static void load(Interp ip) throws HeclException {
	Operator.load(ip);
    }


    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip);
    }


    protected ListCmds(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }


    static {
	cmdtable.put("list", new ListCmds(LIST,-1,-1));
        cmdtable.put("llen", new ListCmds(LLEN,1,1));
        cmdtable.put("lappend", new ListCmds(LAPPEND,1,-1));
        cmdtable.put("lindex", new ListCmds(LINDEX,2,2));
        cmdtable.put("lset", new ListCmds(LSET,2,3));
        cmdtable.put("lrange", new ListCmds(LRANGE,3,3));
        cmdtable.put("filter", new ListCmds(FILTER,2,3));
        cmdtable.put("search", new ListCmds(SEARCH,2,3));
        cmdtable.put("join", new ListCmds(JOIN,1,2));
        cmdtable.put("split", new ListCmds(SPLIT,1,2));
    }
}
