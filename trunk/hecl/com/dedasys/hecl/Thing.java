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
 * @version 1.1
 */

public class Thing extends Object {
    public RealThing val;

    protected String stringval;

    public Thing(String s) {
	val = new StringThing(s);
	stringval = s;
    }

    public Thing(StringBuffer s) {
	val = new StringThing(s);
	stringval = s.toString();
    }

    public Thing(RealThing realthing) {
	val = realthing;
	stringval = null;
    }

    public void setVal(RealThing realthing) {
	val = realthing;
	stringval = null;
    }

    public RealThing getVal() {
	return val;
    }

    public void appendToGroup(Thing thing) {
	Vector v = GroupThing.get(this);
	v.addElement(thing);
	stringval = null;
    }

    public void appendToGroup(char ch) {
	Vector v = GroupThing.get(this);
	//System.out.println("Group is :" + v + " char is :" + ch);
	Thing le = (Thing)v.lastElement();
	StringThing.get(le);
	RealThing rt = le.getVal();
	StringThing str = (StringThing)rt;
	str.append(ch);
	le.setVal(str);
	stringval = null;
	//System.out.println("Group is :" + v);
	//System.out.println("LastElement is :" + le);

    }

    /* FIXME - this one is kind of dubious in that string comparisons
     * between certain objects aren't the right approach... */

/*     public boolean equals (Object obj) {
	Thing thing = (Thing)obj;
	return this.toString().equals(thing.toString());
    }  */

    /**
     * <code>isTrue</code> is a convenience function that lets us know
     * if the result of a calculation is true or false.
     *
     * @param newval a <code>Thing</code> value.
     */

    public static boolean isTrue(Thing thing) throws HeclException {
	return (IntThing.get(thing) != 0);
    }


    /**
     * <code>makeref</code> sets the 'this' Thing to be a reference to
     * the newval that was passed to it.
     *
     * @param newval a <code>Thing</code> value.
     */

    public void makeref(Thing newval) {
	this.setVal(newval.getVal());
    }

    public String toString() {
/* 	if (stringval == null) {
	    stringval = val.toString();
	}  */
	stringval = val.toString();

/* 	if (stringval.compareTo("100000") == 0) {
	    (new Throwable()).printStackTrace();
	}

	System.out.println("stringval: " + stringval);  */
	return stringval;
    }

    public Thing deepcopy() {
	RealThing realthing = this.getVal().deepcopy();
	return new Thing(realthing);
    }

    public int compare(Thing x) {
	String xs = x.toString();
	String ts = this.toString();
	return ts.compareTo(xs);
    }

    public static String ws(int n) {
	return new String(new byte[n]).replace('\0', ' ');
    }

    public static void printThing(Thing t) throws HeclException {
	printThing(t, 0);
    }

    public static void printThing(Thing t, int depth) throws HeclException {
	RealThing rt = t.val;
	if (rt instanceof IntThing) {
	    System.out.println(ws(depth * 4) + "INT: " + ((IntThing) rt).get(t));
	} else if (rt instanceof StringThing) {
	    System.out.println(ws(depth * 4) + "STR: " + ((StringThing) rt).get(t));
	} else if (rt instanceof ListThing) {
	    Vector v = ((ListThing) rt).get(t);
	    System.out.println(ws(depth * 4) + "LIST START");
	    for (Enumeration e = v.elements(); e.hasMoreElements();) {
		Thing.printThing((Thing)e.nextElement(), depth + 1);
	    }
	    System.out.println(ws(depth * 4) + "LIST END");
	} else if (rt instanceof HashThing) {
	    Hashtable h = ((HashThing) rt).get(t);
	    System.out.println(ws(depth * 4) + "HASH START");
	    for (Enumeration e = h.keys(); e.hasMoreElements();) {
		String key = (String)e.nextElement();
		System.out.println(ws(depth * 4) + " KEY: " + key);
		Thing.printThing((Thing)h.get(key), depth + 1);
	    }
	    System.out.println(ws(depth * 4) + "HASH END");
	} else {
	    System.out.println("OTHER:" + t);
	}
    }
}
