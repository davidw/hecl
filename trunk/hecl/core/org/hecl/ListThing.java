/* Copyright 2004-2006 David N. Welton

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
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * The <code>ListThing</code> class implements lists, storing them internally
 * as a Vector.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */
public class ListThing implements RealThing {
    protected Vector val = null;

    private int depth = 0;

    /**
     * Creates a new, empty <code>ListThing</code> instance.
     *  
     */
    public ListThing() {
        val = new Vector();
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
     * Attempts to create a new <code>ListThing</code> instance from a string.
     * May fail if the string can't be parsed into a list.
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
     * <code>create</code> allocates and returns a new ListThing typed Thing.
     *
     * @param v a <code>Vector</code> value.  Note that the Vector
     * must contain Things, rather than, say, int's or Strings or
     * something else!
     * @return a <code>Thing</code> value
     */
    public static Thing create(Vector v) {
        return new Thing(new ListThing(v));
    }

    public String thingclass() {
	return "list";
    }

    /**
     * <code>setListFromAny</code> attempts to transform the given Thing into
     * a ListThing typed Thing.
     *
     * @param thing a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    private static void setListFromAny(Thing thing) throws HeclException {
        RealThing realthing = thing.getVal();

        if (realthing instanceof ListThing) {
            /* Nothing to be done. */
            return;
        }

        RealThing newthing = null;
        Vector newval = new Vector();
	if (realthing instanceof HashThing) {
            Hashtable h = HashThing.get(thing);

            for (Enumeration e = h.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                newval.addElement(new Thing(key));
                newval.addElement(h.get(key));
            }
            newthing = new ListThing(newval);
        } else {
            newthing = new ListThing(realthing.getStringRep());
        }
	thing.setVal(newthing);
    }

    /**
     * <code>get</code> attempts to transform the given Thing into a List, and
     * return its Vector value.
     * 
     * @param thing a <code>Thing</code> value
     * @return a <code>Vector</code> value
     * @exception HeclException if an error occurs
     */
    public static Vector get(Thing thing) throws HeclException {
        setListFromAny(thing);
        ListThing getlist = (ListThing) thing.getVal();

	/* If the thing is slated for copying, it's elements should be
	 * as well. */
 	if (thing.copy) {
	    for (Enumeration e = getlist.val.elements(); e.hasMoreElements();) {
		Thing te = (Thing) e.nextElement();
		te.copy = true;
	    }
	}

        return getlist.val;
    }

    /**
     * <code>getArray</code> attempts to transform the given Thing
     * into a List, and return it as an array of Things.
     *
     * @param thing a <code>Thing</code> value
     * @return a <code>Thing[]</code> value
     * @exception HeclException if an error occurs
     */
    public static Thing[] getArray(Thing thing) throws HeclException {
	Vector v = ListThing.get(thing);
	Thing[] res = new Thing[v.size()];
	int i = 0;
	for (Enumeration e = v.elements(); e.hasMoreElements();) {
	    res[i] = (Thing)e.nextElement();
	    i ++;
	}
	return res;
    }

    /**
     * <code>deepcopy</code> copies a list and all of its elements.
     * 
     * @return a <code>RealThing</code> value
     * @throws HeclException
     */
    public RealThing deepcopy() throws HeclException {
        Vector newv = new Vector();
        for (Enumeration e = val.elements(); e.hasMoreElements();) {
            newv.addElement(((Thing) e.nextElement()).deepcopy());
        }

        return new ListThing(newv);
    }

    /**
     * <code>toListString</code> transforms list elements into the
     * string form {foo bar} if the element contains a space.
     *
     * @param thing a <code>Thing</code> value
     * @return a <code>String</code> value
     */
    public static String toListString(Thing thing) {
        String elementstring = thing.toString();

        if (elementstring.indexOf(' ') >= 0
	    || elementstring.indexOf('\t') >= 0) {
	    StringBuffer resbuf = new StringBuffer();
            resbuf.append('{').append(elementstring).append('}');

	    // System.err.println("toListString: >"+elementstring+"< --> " + resbuf.toString());
	    return resbuf.toString();
        }
	return elementstring;
    }

    public static StringBuffer appendListItem(StringBuffer buf,Thing thing) {
        String elementstring = thing.toString();

        if (elementstring.indexOf(' ') >= 0
	    || elementstring.indexOf('\t') >= 0) {
	    buf.append('{').append(elementstring).append('}');
	} else {
	    buf.append(elementstring);
	}
	return buf;
    }
    
//#ifdef notdef
    static final int USE_BRACES = 1;
    static final int DONT_USE_BRACES = 2;
    static final int BRACES_UNMATCHED = 4;
    
    static int scanElement(Thing t) {
	String s = t.toString();
	int flags = 0;
	int n = s.length();
	char ch;
	
	if(n == 0 || (((ch = s.charAt(0)) == '{') || ch == '}'))
	    flags |= USE_BRACES;

	int nestinglevel = 0;
	for(int i=0; i<n; ++i) {
	    switch (s.charAt(i)) {
	      case '{':
		++nestinglevel;
		break;
	      case '}':
		--nestinglevel;
		if (nestinglevel < 0) {
		    flags |= DONT_USE_BRACES|BRACES_UNMATCHED;
		}
		break;
	      case '[':
	      case '$':
	      case ';':
	      case ' ':
	      case 0x0c:		    // formfeed
	      case 0x0a:		    // line feed
	      case 0x0d:		    // carriage return
	      case 0x09:		    // tab
	      case 0x0b:		    // vertical tab
		flags |= USE_BRACES;
		break;
	      case '\\':
		if ((i+1 == n) || (s.charAt(i+1) == '\n')) {
		    flags = DONT_USE_BRACES | BRACES_UNMATCHED;
		} else {
		    int size=5;
		    //Tcl_UtfBackslash(p, &size, NULL);
		    i += size-1;
		    flags |= USE_BRACES;
		}
		break;
	    }
	}
	if (nestinglevel != 0) {
	    flags = DONT_USE_BRACES | BRACES_UNMATCHED;
	}
	return flags;
    }
//#endif


    /**
     * <code>getStringRep</code> returns a string representation of a
     * ListThing.
     * 
     * @return a <code>String</code> value
     */
    public String getStringRep() {
        int sz = val.size();
	if(sz == 0) {
	    return "";
	    //return "{}";
	}

/* 	if(depth > 1) {
	    //throw new HeclException("Can't print circular references");
	    return "CIRCULAR REF! BANG!";
	} else {
	    depth ++;
	}  */


	int i = 0;
//#ifdef notdef
	int[] flags = new int[sz];
	for(i=0; i<sz; ++i) {
	    Thing elem = (Thing)val.elementAt(i);
	    flags[i] = scanElement(elem);
	}
//#endif
        StringBuffer resbuf = new StringBuffer();
	for (i=0; i < sz; ++i) {
	    if(i > 0)
		resbuf.append(' ');
	    //resbuf.append(toListString((Thing)val.elementAt(i)));
	    appendListItem(resbuf,(Thing)val.elementAt(i));
	}
        return resbuf.toString();
    }
}
