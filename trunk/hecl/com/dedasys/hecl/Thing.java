package com.dedasys.hecl;

import java.util.*;

public class Thing extends Object {
    protected int type;
    protected Object data;

    static final int STRING = 1;
    static final int INT = 2;
    static final int LIST = 3;
    static final int HASH = 4;
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
	//System.out.println("TOSTRING: " + data);
	switch (type) {
	    case LIST:
		int i = 0;
		Vector list = (Vector)data;
		resbuf = new StringBuffer();
		int sz = list.size();
		for (i = 0; i < sz-1; i++) {
		    resbuf.append(((Thing)list.elementAt(i)).toListString() + " ");
		}
		/* Tack last one on without a space. */
		resbuf.append(((Thing)list.elementAt(i)).toListString());
		result = resbuf.toString();
		break;
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
	    default:
		result = data.toString();
		break;
	}
	type = STRING;
	data = new StringBuffer(result);
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
		ParseList parse = new ParseList(data.toString());
		/* FIXME - this probably doesn't handle newlines. */
		result = parse.parse();
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

    /* Makes a new copy. */
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
