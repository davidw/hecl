/* Copyright 2004-2005 David N. Welton

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

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;


public class ListThing implements RealThing {

    protected Vector val = null;

    public ListThing() {
	val = new Vector ();
    }

    public ListThing(Vector v) {
	val = v;
    }

    public ListThing(String s) throws HeclException {
	ParseList parseList = new ParseList(s);
	/* FIXME - this probably doesn't handle newlines. */
	val = parseList.parse();
	if (val == null) {
	    val = new Vector();
	}
    }

    public static Thing create (Vector v) {
 	return new Thing(new ListThing(v));
    }

    private static void setListFromAny(Thing thing)
	    throws HeclException {
	RealThing realthing = thing.val;
	RealThing newthing = null;

	if (realthing instanceof ListThing) {
	    /* Nothing to be done. */
	    return;
	}

	Vector newval = new Vector();
	if (realthing instanceof IntThing) {
	    newval.addElement(new Thing(realthing));
	    newthing = new ListThing(newval);
	} else if (realthing instanceof HashThing) {
	    Hashtable h = HashThing.get(thing);

	    for (Enumeration e = h.keys() ; e.hasMoreElements(); ) {
		String key = (String)e.nextElement();
		newval.addElement(new Thing(key));
		newval.addElement(h.get(key));
	    }
	    newthing = new ListThing(newval);
	} else {
	    newthing = new ListThing(realthing.toString());
	}
	thing.setVal(newthing);
    }


    public static Vector get(Thing thing) throws HeclException {
	setListFromAny(thing);
	ListThing getlist = (ListThing)thing.val;
	return getlist.val;
    }

    public RealThing deepcopy() {
	Vector newv = new Vector();
	for (Enumeration e = val.elements(); e.hasMoreElements();) {
	    newv.addElement(e.nextElement());
	}

	return new ListThing(newv);
    }

    private String toListString(Thing thing) {
	String elementstring = thing.toString();
	StringBuffer resbuf = new StringBuffer();

	if (elementstring.indexOf(' ') > 0) {
	    resbuf.append("{" + elementstring + "}");
	} else {
	    resbuf.append(elementstring);
	}
	return resbuf.toString();
    }

    public String toString() {
	String result = null;
	StringBuffer resbuf = new StringBuffer("");
	int sz = val.size();
	int i = 0;

	if (sz > 0) {
	    for (i = 0; i < sz-1; i++) {
		resbuf.append(toListString((Thing)val.elementAt(i)) + " ");
	    }
	    resbuf.append(toListString((Thing)val.elementAt(i)));
	}

	return resbuf.toString();
    }

}
