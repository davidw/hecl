/* Copyright 2006 David N. Welton
   Wolfgang S. Kechel

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

import org.hecl.IntThing;
import org.hecl.StringThing;


/**
 * The <code>StringCmds</code> class groups together a number of
 * commands that operate on Hecl strings.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
class StringCmds extends Operator {
    public static final int APPEND = 1;

    public static final int EQ = 4;
    public static final int NEQ = 5;

    public static final int STRBYTELEN = 7;
    public static final int STRCMP = 8;
    public static final int STRFIND = 9;
    public static final int STRINDEX = 10;
    public static final int STRLAST = 11;

    public static final int STRLEN = 12;
    public static final int STRRANGE = 13;
    public static final int STRREP = 14;

    public static final int STRLOWER = 15;
    public static final int STRUPPER = 16;

    public static final int STRTRIM = 17;
    public static final int STRTRIML = 18;
    public static final int STRTRIMR = 19;

    public static final int STRREPLACE = 20;


    public Thing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	String str = argv[1].toString();
	StringBuffer sb = null;
	String s = null;
	String where = null;

	switch (cmd) {
	    case APPEND:
		/* The 'append' command. */
		Thing result = argv[1];

		sb = new StringBuffer(StringThing.get(result));
		for (int i = 2; i < argv.length; i++) {
		    sb.append(argv[i].toString());
		}
		StringThing newval = new StringThing(sb);
		result.setCopyVal(newval);
		return new Thing(newval);

	    case EQ:
	      /* 'eq' */
	      return new Thing(Compare.same(argv[1],argv[2]) ?
			       IntThing.ONE : IntThing.ZERO);
	    case NEQ:
	      /* 'ne' */
	      return new Thing(!Compare.same(argv[1],argv[2]) ?
			       IntThing.ONE : IntThing.ZERO);

	    case STRBYTELEN:
		/* strbytelen "string" */
		return IntThing.create(str.getBytes().length);

	    case STRCMP:
		return IntThing.create(Compare.compareString(argv[1], argv[2]));

	    case STRFIND:
		// strfind str1 str2 ?startidx?
		where = argv[2].toString();
		return IntThing.create(where.indexOf(argv[1].toString(),
						     argv.length == 4 ?
						     position(where, argv[3]) : 0));

	    case STRINDEX:
		if (str.length() <= IntThing.get(argv[2])) {
		    return new Thing("");
		} else {
		    sb = new StringBuffer();
		    sb.append(str.charAt(position(str, argv[2])));
		    return new Thing(sb);
		}

	    case STRLAST:
		// strlast what where ?startidx?
		where = argv[2].toString();
//#ifdef j2se
		return IntThing.create(where.lastIndexOf(str,
							 argv.length == 4 ?
							 position(where, argv[3]) : where.length()-1));
//#else
		int len = where.length()-str.length();
		int pos = where.indexOf(str, argv.length == 4 ?
					position(where, argv[3]) : 0);
		while (pos >= 0 && pos + 1 < len) {
		    int pos2 = where.indexOf(str, pos+1);
		    if (pos2 < 0) {
			break;
		    }
		    pos = pos2;
		}
		return IntThing.create(pos);
//#endif
	    case STRLEN:
		return IntThing.create(str.length());

	    case STRRANGE:
		//System.out.println("from="+position(s,argv[3]) +", to="+position(s,argv[4]));
		return new Thing(str.substring(position(str, argv[2]),
					       position(str, argv[3]) + 1));
	    case STRREP:
		sb = new StringBuffer();
		for(int cnt = IntThing.get(argv[2]); cnt > 0; --cnt) {
		    sb.append(str);
		}
		return new Thing(sb.toString());

/* 	      if(str.equals("split")) {
	      if(argv.length != 3 && argv.length != 4)
	      throw HeclException.createWrongNumArgsException(
	      argv,2,"string ?plitstringlist?");
	      String lookin = argv[2].toString();
	      Vector splitstrings = defsplitstrings;
	      if(argv.length == 4) {
	      splitstrings = ListThing.get(argv[3]);
	      }
	      Vector r = new Vector();
	      int n = splitstrings.size();
	      int startat = 0;
	      int pos = -1;
	      int match = -1;

	      do {
	      for(i=0; i<n; ++i) {
	      int newpos = lookin.indexOf((String)r.elementAt(i),startat);
	      if((pos < 0 && newpos >= 0)
	      || (pos >0 && newpos < pos)) {
	      pos = newpos;
	      match = i;
	      }
	      }
	      if(match >= 0) {
	      int len = ((String)r.elementAt(i)).length();
	      r.addElement(new Thing(lookin.substring(pos,pos+len)));
	      startat += len;
	      }
	      } while(match >= 0);
	      return new ListThing(r);
	      }
*/
	    case STRLOWER:
		return new Thing(str.toLowerCase());

	    case STRUPPER:
		return new Thing(str.toUpperCase());

	    case STRTRIM: {
		String resstr;
		Vector trimstrings = null;

		if (argv.length == 3 ) {
		    trimstrings = ListThing.get(argv[2]);
		} else {
		    /* We just use the native method. */
		    return new Thing(str.trim());
		}

		resstr = stripr(str, trimstrings);
		resstr = stripl(resstr, trimstrings);
		return new Thing(resstr);
	    }

	    case STRTRIML: {
		String resstr;
		Vector trimstrings = null;

		if (argv.length == 3 ) {
		    trimstrings = ListThing.get(argv[2]);
		} else {
		    trimstrings = defsplitstrings;
		}

		resstr = stripl(str, trimstrings);
		return new Thing(resstr);
	    }

	    case STRTRIMR: {
		String resstr;
		Vector trimstrings = null;

		if (argv.length == 3 ) {
		    trimstrings = ListThing.get(argv[2]);
		} else {
		    trimstrings = defsplitstrings;
		}

		resstr = stripr(str, trimstrings);
		return new Thing(resstr);
	    }

	    case STRREPLACE: {
		// strreplace {from to} stringwithfromtoreplace
		String resstr = null;
		Vector v = ListThing.get(argv[1]);
		String original = argv[2].toString();
		String from = ((Thing)v.elementAt(0)).toString();
		int start = original.indexOf(from);
		if (start < 0) {
		    resstr = original;
		} else {
		    resstr = original.substring(0, start) +
			((Thing)v.elementAt(1)).toString() +
			original.substring(start + from.length(), original.length());
		}
		return new Thing(resstr);
	    }

	    default:
		throw new HeclException("Unknown string command '"
					+ argv[1].toString() + "' with code '"
					+ cmd + "'.");
	}
    }


    public static void load(Interp ip) throws HeclException {
	Operator.load(ip,cmdtable);
    }


    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip,cmdtable);
    }

    protected StringCmds(int cmdcode, int minargs, int maxargs) {
	super(cmdcode, minargs, maxargs);
    }

    /**
     * The <code>stripr</code> method takes a string, and a Vector of
     * Hecl Things, and strips them off the left side of the string.
     *
     * @param str a <code>String</code> value
     * @param trimstrings a <code>Vector</code> value
     * @return a <code>String</code> value
     */
    protected static String stripl(String str, Vector trimstrings) {
	String ts;
	boolean modified = true;
	while (modified) {
	    modified = false;
	    for (Enumeration e = trimstrings.elements(); e.hasMoreElements();) {
		ts = ((Thing)e.nextElement()).toString();
		if (str.startsWith(ts)) {
		    str = str.substring(ts.length());
		    modified = true;
		}
	    }
	}
	return str;
    }

    /**
     * The <code>stripr</code> method takes a string, and a Vector of
     * Hecl Things, and strips them off the right side of the string.
     *
     * @param str a <code>String</code> value
     * @param trimstrings a <code>Vector</code> value
     * @return a <code>String</code> value
     */
    protected static String stripr(String str, Vector trimstrings) {
	String ts;
	boolean modified = true;
	while (modified) {
	    modified = false;
	    for (Enumeration e = trimstrings.elements(); e.hasMoreElements();) {
		ts = ((Thing)e.nextElement()).toString();
		if (str.endsWith(ts)) {
		    str = str.substring(0, str.length() - ts.length());
		    modified = true;
		}
	    }
	}
	return str;
    }

    /**
     * The <code>position</code> method 
     *
     * @param s an <code>String</code> value
     * @param what a <code>Thing</code> value
     * @return an <code>int</code> value
     * @exception HeclException if an error occurs
     */
    protected static int position(String s, Thing what) throws HeclException {
	int len = s.length();
	int pos = IntThing.get(what);

	if (pos > len) {
	    pos = len - 1;
	} else if (pos < 0) {
	    pos += len;
	    if (pos < 0) {
		pos = 0;
	    }
	}
	return pos;
    }

    private static Vector defsplitstrings;
    private static String deftrimchars = "\t\n\r ";

    private static Hashtable cmdtable = new Hashtable();

    static {
	defsplitstrings = new Vector();
	defsplitstrings.addElement(new Thing(" "));
	defsplitstrings.addElement(new Thing("\t"));
	defsplitstrings.addElement(new Thing("\n"));
	defsplitstrings.addElement(new Thing("\r"));

        cmdtable.put("append", new StringCmds(APPEND,1,-1));
        cmdtable.put("eq", new StringCmds(EQ,2,2));
	cmdtable.put("ne", new StringCmds(NEQ,2,2));

	cmdtable.put("strbytelen", new StringCmds(STRBYTELEN,1,1));
	cmdtable.put("strcmp", new StringCmds(STRCMP,2,2));
	cmdtable.put("strfind", new StringCmds(STRFIND,2,3));
	cmdtable.put("strindex", new StringCmds(STRINDEX,2,2));
	cmdtable.put("strlast", new StringCmds(STRLAST,2,3));
	cmdtable.put("strlen", new StringCmds(STRLEN,1,1));
	cmdtable.put("strrange", new StringCmds(STRRANGE,3,3));
	cmdtable.put("strrep", new StringCmds(STRREP,2,2));
	cmdtable.put("strlower", new StringCmds(STRLOWER,1,1));
	cmdtable.put("strupper", new StringCmds(STRUPPER,1,1));
	cmdtable.put("strtrim", new StringCmds(STRTRIM,1,2));
	cmdtable.put("strtriml", new StringCmds(STRTRIML,1,2));
	cmdtable.put("strtrimr", new StringCmds(STRTRIMR,1,2));
	cmdtable.put("strreplace", new StringCmds(STRREPLACE,2,2));
    }
}
