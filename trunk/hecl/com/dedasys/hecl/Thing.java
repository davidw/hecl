/* Copyright 2004 David N. Welton

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

package com.dedasys.hecl;

import java.util.*;

/**
 * The <code>Thing</code> class is what Hecl revolves around.
 * "Things" can be of several types, include strings, integers, lists,
 * hash tables.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

public class Thing extends Object {
    protected int type;
    protected Object data;

    /* Regular old strings. Represented internally by StringBuffer's*/
    static final int STRING = 1;

    /* Integers, represented by Integer objects. */
    static final int INT = 2;

    /* Lists of objects, represented by Vector objects. */
    static final int LIST = 3;

    /* Hash tables of objects, represented by Hashtable objects. */
    static final int HASH = 4;

    /* "Compiled" code, ready to execute.  CodeThing object is
     * internal representation. */
    static final int CODE = 5;

    /* Used when there is a group of things scrunched together, such
     * as "[something] blah blah" and they must be kept together. */
    static final int GROUP = 6;

    /* Like CODE, but to be substituted before it is passed to the
     * command that uses it. */
    static final int SUBST = 7;

    /* No float so far because we are targeting J2ME... */

    /* static final int FLOAT = 3; */

    public Thing(String thing) {
	type = STRING;
	data = new StringBuffer(thing);
    }

    public Thing(StringBuffer thing) {
	type = STRING;
	data = thing;
    }

    public Thing(Integer thing) {
	type = INT;
	data = thing;
    }

    public Thing(int thing) {
	type = INT;
	data = new Integer(thing);
    }

    public Thing(Vector thing) {
	type = LIST;
	data = thing;
    }

    public Thing(Hashtable thing) {
	type = HASH;
	data = thing;
    }

    public Thing(boolean thing) {
	type = INT;
	data = new Integer(thing == true ? 1 : 0);
    }

    public String toString() {
	String result;
	StringBuffer resbuf;
	int i = 0;
	Vector list = null;
	int sz = 0;
	//System.out.println("TOSTRING: " + data);
	switch (type) {
	    case LIST:
		list = (Vector)data;
		resbuf = new StringBuffer();
		sz = list.size();
		for (i = 0; i < sz-1; i++) {
		    resbuf.append(((Thing)list.elementAt(i)).toListString() + " ");
		}
		/* Tack last one on without a space. */
		resbuf.append(((Thing)list.elementAt(i)).toListString());
		result = resbuf.toString();
		break;
	    case GROUP:
		/* This one also must not transform data into a string
		 * type. */
		i = 0;
		list = (Vector)data;
		resbuf = new StringBuffer();
		sz = list.size();
		resbuf.append("GROUP: ");
		for (i = 0; i < sz-1; i++) {
		    resbuf.append(((Thing)list.elementAt(i)).toString() + "|");
		}
		return resbuf.toString();
/* 	    case FLOAT:
		return data.toString();  */
	    case HASH:
		resbuf = new StringBuffer();
		int j = 0;
		for (Enumeration e = ((Hashtable)data).keys() ; e.hasMoreElements(); ) {
		    String key = (String)e.nextElement();
		    /* FIXME. */
		    resbuf.append((j != 0 ? " " : "") +
				  key + " " + ((Hashtable)data).get(key));
		    if (j == 0)
			j ++;
		}
		result = resbuf.toString();
		break;
	    case SUBST:
 	    case CODE:
		/* As a special case, we don't transform this back
		 * into a string. */
		CodeThing code = (CodeThing)data;
		return "CODE/SUBST: " + code.toString();
	    default:
		result = data.toString();
		break;
	}
	type = STRING;
	data = new StringBuffer(result);
/* 	(new Throwable()).printStackTrace();
	System.out.println("STRING is: " + result);  */
	return result;
    }

    public StringBuffer toStringBuffer() {
	if (type != STRING) {
	    this.toString();
	}
	return (StringBuffer)data;
    }

    private String toListString() {
	switch (type) {
	    case LIST:
		if (((Vector)data).size() > 1) {
		    return "{" + this.toString() + "}";
		} else {
		    return this.toString();
		}
	    case STRING:
		/* FIXME: This should also quote {} characters. */
		String strval = (String)data.toString();
		if (strval.indexOf(' ') > 0) {
		    return "{" + strval + "}";
		} else {
		    return strval;
		}
	    default:
		return this.toString();
	}
    }

    public int toInt() {
	Integer result;
	switch (type) {
	    case INT:
		result = ((Integer)data);
		break;
	    default:
 		result = new Integer(Integer.parseInt(data.toString(), 10));
		break;
	}
	type = INT;
	data = result;
	return ((Integer)data).intValue();
    }

    public void setInt(int i) {
	type = INT;
	data = new Integer(i);
    }

    public Vector toList() throws HeclException {
	Vector result = null;
	switch (type) {
	    case STRING:
		ParseList parseLst = new ParseList(data.toString());
		/* FIXME - this probably doesn't handle newlines. */
		result = parseLst.parse();
		if (result == null) {
		    result = new Vector();
		}
		break;
	    case INT:
		result = new Vector();
		result.addElement(data);
		break;
	    case HASH:
		/* FIXME - iterate through hash, create list. */
		result = new Vector();
		for (Enumeration e = ((Hashtable)data).keys() ; e.hasMoreElements(); ) {
		    String key = (String)e.nextElement();
		    result.add(new Thing(key));
		    result.add(((Hashtable)data).get(key));
		}
		break;
	    case LIST:
		return (Vector)data;
	}
	type = LIST;
	data = result;
	return result;
    }

    public Hashtable toHash() throws HeclException {
	Hashtable result = null;
	Vector lst;
	switch (type) {
	    /* Note that these two share code. */
	    case STRING:
		this.toList();
	    case LIST:
		lst = (Vector)data;

		if ((lst.size() % 2) != 0) {
		    throw new HeclException(
			"list must have even number of elements");
		}
		/* FIXME: I pulled this '3' (initial size of the hash
		 * table is list size + 3), out of the air... better
		 * suggestions based on experimentation are
		 * welcome. */
		result = new Hashtable(lst.size() + 3);

		for (Enumeration e = lst.elements(); e.hasMoreElements(); ) {
		    String key = ((Thing)e.nextElement()).toString();
		    Thing val = (Thing)e.nextElement();
		    result.put(key, val);
		}
		break;
	    case INT:
		throw new HeclException("hash must be set from a list");
	    case HASH:
		return (Hashtable)data;
	}
	type = HASH;
	data = result;
	return result;
    }

    /* FIXME - I'm not entirely  happy with how these two types (CODE,
     * GROUP) fit in with the 'real' types. */

    /* Set code from CodeThing object. */

    public void setCode(CodeThing thing) {
	type = CODE;
	data = thing;
    }

    /* Fetch the code. */

    public CodeThing getCode() {
	if (type == CODE || type == SUBST) {
	    return (CodeThing)data;
	}
	return null;
    }

    public void setSubst(CodeThing thing) {
	type = SUBST;
	data = thing;
    }


    public void setGroup() {
	if (type == GROUP) {
	    return;
	}
	Thing newthing = null;
	Vector group = new Vector();
	if (type == CODE || type == SUBST) {
	    newthing = new Thing("");
	    newthing.type = type;
	    newthing.data = data;
	} else {
	    newthing = new Thing(this.toString());
	}
/* 	newthing.type = type;
	newthing.data = data;
  */
	group.addElement(newthing);

	type = GROUP;
	data = group;
    }

    public void appendToGroup(Thing thing) {
	this.setGroup();
//	System.out.println("appendToGroup: " + thing);
	((Vector)data).addElement(thing);
    }

    public Vector getGroup() {
	return (Vector)data;
    }

    public void appendToGroup(char ch) {
	StringBuffer sb = null;
	if (type == GROUP) {
	    Thing le = (Thing)((Vector)data).lastElement();
	    if (le.type != SUBST) {
		sb = le.toStringBuffer();
	    } else {
		/* It's a SUBST type, so we make a new thing and tack it on. */
		sb = new StringBuffer("");
		sb.append(ch);
		this.appendToGroup(new Thing(sb));
		return;
	    }
	} else {
	    sb = this.toStringBuffer();
	}
	sb.append(ch);
    }

    /* FIXME - this one is kind of dubious. */

    public boolean equals (Object obj) {
	Thing thing = (Thing)obj;
	return this.toString().equals(thing.toString());
    }

    public boolean isTrue() {
	switch (type) {
	    case INT:
		return (((Integer)data).intValue() != 0);
	    default:
		return (Integer.parseInt(data.toString()) != 0);
	}
    }

    public int compare(Thing x) {
	String xs;
	String ts;
	switch (type) {
	    case INT:
		int ti = ((Integer)data).intValue();
		int xi = x.toInt();
		if (xi == ti)
		    return 0;
		else if (ti < xi)
		    return -1;
		else
		    return 1;
/* 	    case STRING:
		xs = x.toString();
		ts = ((StringBuffer)data).toString();
		return ts.compareTo(xs);  */
	    default:
		xs = x.toString();
		ts = ((StringBuffer)data).toString();
		return ts.compareTo(xs);
	}
    }

    /* Makes a deep copy. */
    public Thing copy() {
	Thing newthing = null;
	switch (type) {
	    case STRING:
		StringBuffer sb = new StringBuffer();
		sb.append((StringBuffer)data);
		newthing = new Thing(sb);
		break;
	    case INT:
		newthing = new Thing(new Integer(((Integer)data).intValue()));
		break;
	    case LIST:
		Vector v = new Vector();
		for (Enumeration e = ((Vector)data).elements();
		     e.hasMoreElements();) {
		    v.addElement(e.nextElement());
		}

		newthing = new Thing(v);
		break;
	    case HASH:
		Hashtable h = new Hashtable();

		for (Enumeration e = ((Hashtable)data).keys() ;
		     e.hasMoreElements(); ) {
		    String key = (String)e.nextElement();
		    h.put(key, ((Hashtable)data).get(key));
		}

		newthing = new Thing(h);
		break;
	}
	return newthing;
    }

    /* Doesn't make a new copy. */
    public void makeref(Thing newval) {
	newval.type = type;
	newval.data = data;
    }

}
