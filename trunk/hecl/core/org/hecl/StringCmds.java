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

    public static final int STREQ = 4;
    public static final int STRNEQ = 5;

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


    public RealThing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
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
	    result.setVal(new StringThing(sb));
	    interp.setResult(result);
	    break;
	    
	  case STREQ:
	  case STRNEQ:
	    /* 'eq' and 'ne' commands. */
	    int i = Compare.compareString(argv[1],argv[2]);
	    if(cmd == STREQ) {
		return i != 0 ? IntThing.ZERO : IntThing.ONE;
	    }
	    return i != 0 ? IntThing.ONE : IntThing.ZERO;
	    
	  case STRBYTELEN:
	    /* strbytelen "string" */
	    return new IntThing(str.getBytes().length);
	    
	  case STRCMP:
	    return new IntThing(Compare.compareString(argv[1], argv[2]));
	    
	  case STRFIND:
	    // strfind str1 str2 ?startidx?
	    where = argv[2].toString();
	    return new IntThing(where.indexOf(argv[1].toString(),
					      argv.length == 4 ?
					      position(where, argv[3]) : 0));
		
	  case STRINDEX:
	    sb = new StringBuffer();
	    sb.append(str.charAt(position(str, argv[2])));
	    return new StringThing(sb);
	    
	  case STRLAST:
	    // strlast what where ?startidx?
	    where = argv[2].toString();
//#ifdef ant:j2se
	    return new IntThing(where.lastIndexOf(str,
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
	    return new IntThing(pos);
//#endif
	  case STRLEN:
	    return new IntThing(str.length());

	  case STRRANGE:
	    //System.out.println("from="+position(s,argv[3]) +", to="+position(s,argv[4]));
	    return new StringThing(str.substring(position(str, argv[2]),
						 position(str, argv[3]) + 1));
	  case STRREP:
	    sb = new StringBuffer();
	    for(int cnt = IntThing.get(argv[2]); cnt > 0; --cnt) {
		sb.append(str);
	    }
	    return new StringThing(sb.toString());

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
	    return new StringThing(str.toLowerCase());
	    
	  case STRUPPER:
	    return new StringThing(str.toUpperCase());
	    
	  case STRTRIM: {
	      String trimchars = argv.length >= 3 ? argv[2].toString() : deftrimchars;
	      int n = str.length();
	      int from = 0;
	      int to = n-1;
	      // Quite dumb algorithm, should use more elaborate technique...
	      while(from < n && 0 <= trimchars.indexOf(str.charAt(from)))
		  ++from;
	      while(to >= from && 0 <= trimchars.indexOf(str.charAt(to)))
		  --to;
	      return new StringThing(from <= to ? str.substring(from,to+1) : "");
	  }
	    
	  case STRTRIML: {
	      String trimchars = argv.length >= 3 ? argv[2].toString() : deftrimchars;
	      int n = str.length();
	      int from = 0;
	      while(from < n && 0 <= trimchars.indexOf(str.charAt(from)))
		  ++from;
	      return new StringThing(from < n ? str.substring(from,n) : "");
	  }
	    
	  case STRTRIMR: {
	      String trimchars = argv.length >= 3 ? argv[2].toString() : deftrimchars;
	      int to = str.length()-1;
	      while(to >= 0 && 0 <= trimchars.indexOf(str.charAt(to)))
		  --to;
	      return new StringThing(to >= 0 ? str.substring(0,to) : "");
	  }
	    
	  default:
	    throw new HeclException("Unknown string command '"
				    + argv[1].toString() + "' with code '"
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

    protected StringCmds(int cmdcode, int minargs, int maxargs) {
	super(cmdcode, minargs, maxargs);
    }

    /**
     * The <code>position</code> method 
     *
     * @param s a <code>String</code> value
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

    //private static Vector defsplitstrings;
    private static String deftrimchars = "\t\n\r ";
    
    static {
	/*
	defsplitstrings = new Vector();
	defsplitstrings.addElement(" ");
	defsplitstrings.addElement("\t");
	defsplitstrings.addElement("\n");
	defsplitstrings.addElement("\r");
	*/
        cmdtable.put("append", new StringCmds(APPEND,1,-1));
        cmdtable.put("eq", new StringCmds(STREQ,2,2));
	cmdtable.put("ne", new StringCmds(STRNEQ,2,2));

	cmdtable.put("strbytelen", new StringCmds(STRBYTELEN,1,1));
	cmdtable.put("strcmp", new StringCmds(STRCMP,2,2));
	cmdtable.put("strfind", new StringCmds(STRFIND,2,3));
	cmdtable.put("strindex", new StringCmds(STRINDEX,2,2));
	cmdtable.put("strlast", new StringCmds(STRLAST,2,2));
	cmdtable.put("strlen", new StringCmds(STRLEN,1,1));
	cmdtable.put("strrange", new StringCmds(STRRANGE,3,3));
	cmdtable.put("strrep", new StringCmds(STRREP,2,2));
	cmdtable.put("strlower", new StringCmds(STRLOWER,1,1));
	cmdtable.put("strupper", new StringCmds(STRUPPER,1,1));
	cmdtable.put("strtrim", new StringCmds(STRTRIM,1,2));
	cmdtable.put("strtriml", new StringCmds(STRTRIML,1,2));
	cmdtable.put("strtrimr", new StringCmds(STRTRIMR,1,2));
    }
}
