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

/**
 * The <code>ListThing</code> class implements lists, storing them
 * internally as a Vector.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class ListThing implements RealThing {
    protected Vector val = null;

    /**
     * Creates a new, empty <code>ListThing</code> instance.
     *
     */
    public ListThing() {
	val = new Vector ();
    }

    /**
     * Creates a new <code>ListThing</code> instance from a vector.
     *
     * @param v a <code>Vector</code> value
     */
    public ListThing(Vector v) {
	val = v;
    }

    /**
     * Attempts to create a new <code>ListThing</code> instance from a
     * string.  May fail if the string can't be parsed into a list.
     *
     * @param s a <code>String</code> value
     * @exception HeclException if an error occurs
     */
    public ListThing(String s) throws HeclException {
	ParseList parseList = new ParseList(s);
	/* FIXME - this probably doesn't handle newlines. */
	val = parseList.parse();
	if (val == null) {
	    val = new Vector();
	}
    }

    /**
     * <code>create</code> allocates and returns a new ListThing typed
     * Thing.
     *
     * @param v a <code>Vector</code> value
     * @return a <code>Thing</code> value
     */
    public static Thing create (Vector v) {
 	return new Thing(new ListThing(v));
    }

    /**
     * <code>setListFromAny</code> attempts to transform the given
     * Thing into a ListThing typed Thing.
     *
     * @param thing a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
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


    /**
     * <code>get</code> attempts to transform the given Thing into a
     * List, and return its Vector value.
     *
     * @param thing a <code>Thing</code> value
     * @return a <code>Vector</code> value
     * @exception HeclException if an error occurs
     */
    public static Vector get(Thing thing) throws HeclException {
	setListFromAny(thing);
	ListThing getlist = (ListThing)thing.val;
	return getlist.val;
    }

    /**
     * <code>deepcopy</code> copies a list and all of its elements.
     *
     * @return a <code>RealThing</code> value
     * @exception HeclException if an error occurs
     */
    public RealThing deepcopy() throws HeclException {
	Vector newv = new Vector();

	for (Enumeration e = val.elements(); e.hasMoreElements();) {
	    newv.addElement(((Thing)e.nextElement()).deepcopy());
	}

	return new ListThing(newv);
    }

    /**
     * <code>toListString</code> is an internal function that
     * transforms list elements into the string form {foo bar} if the
     * element contains a space.
     *
     * @param thing a <code>Thing</code> value
     * @return a <code>String</code> value
     */
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

    /**
     * <code>toString</code> returns a string representation of a
     * ListThing.
     *
     * @return a <code>String</code> value
     */
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
