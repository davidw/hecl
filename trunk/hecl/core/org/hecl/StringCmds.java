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
    public static final int SLEN = 2;
    public static final int SINDEX = 3;
    public static final int STREQ = 4;
    public static final int STRNEQ = 5;
    public static final int STRCMD = 6;

    public RealThing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	String str = argv[1].toString();
	StringBuffer sb = null;

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
	    case SLEN:
		/* 'slen' command. */
		return new IntThing(str.length());

	    case SINDEX:
		/* 'sindex' command. */
		int idx = IntThing.get(argv[2]);
		try {
		    char chars[] = new char[1];
		    chars[0] = str.charAt(idx);
		    interp.setResult(new String(chars));
		} catch (StringIndexOutOfBoundsException e) {
		    interp.setResult("");
		}
		break;
	    case STREQ:
	    case STRNEQ:
		/* 'eq' and 'ne' commands. */
		int i = Compare.compareString(argv[1],argv[2]);
		if(cmd == STREQ) {
		    return i != 0 ? IntThing.ZERO : IntThing.ONE;
		}
		return i != 0 ? IntThing.ONE : IntThing.ZERO;

	    case STRCMD:
	      /* string repeat "string" count */
	      if(str.equals("bytelength")) {
		  if(argv.length != 3)
		      throw HeclException.createWrongNumArgsException(argv,2,"string");
		  return new IntThing(argv[2].toString().getBytes().length);
	      }
	      
	      if(str.equals("compare")) {
		  if(argv.length != 4)
		      throw HeclException.createWrongNumArgsException(
			  argv,2,"string1 string2");
		  return new IntThing(Compare.compareString(argv[2],argv[3]));
	      }

	      
	      if(str.equals("equal")) {
		  if(argv.length != 4)
		      throw HeclException.createWrongNumArgsException(
			  argv,2,"string1 string2");
		  return Compare.compareString(argv[2],argv[3])!=0 ?
		      IntThing.ZERO : IntThing.ONE;
	      }

	      if(str.equals("first")) {
		  /* string first str1 str2 ?startidx? */
		  if(argv.length < 4 || argv.length > 5) {
		      throw HeclException.createWrongNumArgsException(
			  argv,2,"string1 string2 ?startindex?");
		  }
		  String where = argv[3].toString();
		  return new IntThing(where.indexOf(argv[2].toString(),
						    argv.length == 5 ?
						    position(where,argv[4]) : 0));
	      }

	      if(str.equals("index")) {
		  /* string index string charindex */
		  if(argv.length != 4) {
		      throw HeclException.createWrongNumArgsException(
			  argv,2, "string charindex");
		  }
		  String s = argv[2].toString();
		  sb = new StringBuffer();
		  sb.append(s.charAt(position(s,argv[3])));
		  return new StringThing(sb);
	      }
	      
	      if(str.equals("last")) {
		  if(argv.length < 4 || argv.length > 5) {
		      throw HeclException.createWrongNumArgsException(
			  argv,2,"string1 string2 ?lastindex?");
		  }
		  String where = argv[3].toString();

//#ifdef ant:j2se
		  return new IntThing(where.lastIndexOf(argv[2].toString(),
							argv.length == 5 ?
							position(where,argv[4]) : 0));
//#else
		  String s = argv[2].toString();
		  int pos = where.indexOf(s, argv.length == 5 ?
					  position(where,argv[4]) : 0);
		  while(pos >= 0 && pos+1 < where.length() - s.length()) {
		      int pos2 = where.indexOf(s,pos+1);
		      if(pos2 < 0)
			  break;
		  }
		  return new IntThing(pos);
//#endif
	      }
	      
	      if(str.equals("length")) {
		  /* string length str */
		  if(argv.length != 3) {
		      throw HeclException.createWrongNumArgsException(argv,2,"string");
		  }
		  return new IntThing(argv[2].toString().length());
	      }
	      
	      if(str.equals("range")) {
		  /* string range string first last */
		  if(argv.length != 5) {
		      throw HeclException.createWrongNumArgsException(
			  argv,2,"string first last");
		  }
		  String s = argv[2].toString();
		  //System.out.println("from="+position(s,argv[3]) +", to="+position(s,argv[4]));
		  return new StringThing(s.substring(position(s,argv[3]),
						     position(s,argv[4])));
	      }
	      
	      if(str.equals("repeat")) {
		  if(argv.length != 4) {
		      throw HeclException.createWrongNumArgsException(
			  argv,2,"string count");
		  }
		  String s = argv[2].toString();
		  sb = new StringBuffer();
		  for(int cnt = IntThing.get(argv[3]); cnt > 0; --cnt) {
		      sb.append(s);
		  }
		  return new StringThing(sb.toString());
	      }

	      if(str.equals("split")) {
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

	      if(str.equals("tolower")) {
		  if(argv.length != 3)
		      throw HeclException.createWrongNumArgsException(
			  argv,2,"string");
		  return new StringThing(argv[2].toString().toLowerCase());
	      }

	      if(str.equals("toupper")) {
		  if(argv.length != 3)
		      throw HeclException.createWrongNumArgsException(
			  argv,2,"string");
		  return new StringThing(argv[2].toString().toUpperCase());
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


    protected StringCmds(int cmdcode,int minargs,int maxargs) {
	super(cmdcode, minargs, maxargs);
    }

    protected static int position(String s,Thing what) throws HeclException {
	int len = s.length();
	int pos = len;
	String w = what.toString();
	
	if(!w.equals("end")) {
	    pos = w.startsWith("end-") ? 
		pos - IntThing.get(new Thing(w.substring(4,w.length())))
		: 1+IntThing.get(what);
	    if(pos < 0)
		pos = 0;
	    if(pos > len) {
		pos = len;
	    }
	}
	return pos;
    }

    private static Vector defsplitstrings;
    
    static {
	defsplitstrings = new Vector();
	defsplitstrings.addElement(" ");
	defsplitstrings.addElement("\t");
	defsplitstrings.addElement("\n");
	defsplitstrings.addElement("\r");

        cmdtable.put("append", new StringCmds(APPEND,1,-1));
        cmdtable.put("slen", new StringCmds(SLEN,1,1));
        cmdtable.put("sindex", new StringCmds(SINDEX,2,2));
        cmdtable.put("eq", new StringCmds(STREQ,2,2));
	cmdtable.put("ne", new StringCmds(STRNEQ,2,2));
	cmdtable.put("string", new StringCmds(STRCMD,2,-1));
    }
}
