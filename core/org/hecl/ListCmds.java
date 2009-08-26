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
import java.util.Hashtable;
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


    public Thing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	int idx = 0;
	int last = 0;
	Vector list;
	Vector result;
	ListThing newval = null;

	switch (cmd) {
	    case LIST:
		result = new Vector();
		for (int i = 1; i < argv.length; i++) {
		    result.addElement(argv[i]);
		}
		return ListThing.create(result);

	    case LLEN:
		list = ListThing.get(argv[1]);
		return IntThing.create(list.size());

	    case LINDEX:
	      {
		  Thing res = argv[1];
		  for(int i = 2; i < argv.length; ++i) {
		      list = ListThing.get(res);
		      last = list.size();
		      idx = getIndex(argv[i],last);
		      if (idx >= last) {
			  list = new Vector();
			  res = ListThing.create(list);
		      } else {
			  res = (Thing)list.elementAt(idx);
		      }
		  }
		  return res;
	      }

	    case LINSERT:
		list = ListThing.get(argv[1]);
		list.insertElementAt(argv[3], getIndex(argv[2],list.size()));
		newval = new ListThing(list);
		argv[1].setCopyVal(newval);
		return new Thing(newval);

	    case LSET:
		list = ListThing.get(argv[1]);
		idx = getIndex(argv[2],list.size());
		if (argv.length < 4) {
		    list.removeElementAt(idx);
		} else {
		    list.setElementAt(argv[3], idx);
		}
		newval = new ListThing(list);
		argv[1].setCopyVal(newval);
		return new Thing(newval);

	    case LRANGE:
		list = ListThing.get(argv[1]);
		int ls = list.size();
		int first = getIndex(argv[2],ls);
		last = getIndex(argv[3],ls);
		if (last < first || first > ls)
		    return Thing.emptyThing();
		result = new Vector();
		for (int i = first; i <= last; i++) {
		    result.addElement(list.elementAt(i));
		}
		return ListThing.create(result);

	    case LAPPEND:
		list = ListThing.get(argv[1]);
		for (int i = 2; i < argv.length; i++) {
		    list.addElement(argv[i]);
		}
		newval = new ListThing(list);
		argv[1].setCopyVal(newval);
		return new Thing(newval);

	    case FILTER:
	    case SEARCH:
		list = ListThing.get(argv[1]);
		result = new Vector();
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
		    if (IntThing.get(interp.eval(argv[3])) != 0) {
			result.addElement(val);
			if (brk == true) {
			    break;
			}
		    }
		}
		return ListThing.create(result);

	    case JOIN:
		list = ListThing.get(argv[1]);
		StringBuffer strres = new StringBuffer("");
		boolean firstone = true;
		String joinstr = null;
		if (argv.length > 2) {
		    joinstr = argv[2].toString();
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
		return new Thing(strres);

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
		return ListThing.create(result);

	    default:
		throw new HeclException("Unknown list command '"
					+ argv[0].toString() + "' with code '"
					+ cmd + "'.");
	}
    }

    public static int getIndex(Thing t,int llen) throws HeclException {
	String s = t.toString();
	if(s.equals("end"))
	    return llen-1;
	if(s.equals("start"))
	    return 0;
	int idx = IntThing.get(t);
	if (idx < 0) {
	    idx += llen;
	    if (idx < 0) {
		idx = 0;
	    }
	}
	return idx;
    }

    public static void load(Interp ip) throws HeclException {
	Operator.load(ip,cmdtable);
    }


    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip,cmdtable);
    }


    protected ListCmds(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }

    private static Hashtable cmdtable = new Hashtable();

    static {
	cmdtable.put("list", new ListCmds(LIST,-1,-1));
        cmdtable.put("llen", new ListCmds(LLEN,1,1));
        cmdtable.put("lappend", new ListCmds(LAPPEND,1,-1));
        cmdtable.put("lindex", new ListCmds(LINDEX,2,-1));
        cmdtable.put("linsert", new ListCmds(LINSERT,3,3));
        cmdtable.put("lset", new ListCmds(LSET,2,3));
        cmdtable.put("lrange", new ListCmds(LRANGE,3,3));
        cmdtable.put("filter", new ListCmds(FILTER,2,3));
        cmdtable.put("search", new ListCmds(SEARCH,2,3));
        cmdtable.put("join", new ListCmds(JOIN,1,2));
        cmdtable.put("split", new ListCmds(SPLIT,1,2));
    }
}
