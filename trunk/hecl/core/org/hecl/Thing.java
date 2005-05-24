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

package org.hecl;

import java.util.*;

/**
 * The <code>Thing</code> class is what Hecl revolves around. "Things" can be
 * of several types, include strings, integers, lists, hash tables.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.1
 */

public class Thing extends Object {
    public RealThing val;

    protected String stringval;

    /* Used to keep track of nesting depth. */
    private int depth = 0;

    /* Depth that things like lists are allowed to nest. */
    static final int NESTDEPTH = 10;

    /**
     * Creates a new <code>Thing</code> instance from a string.
     * 
     * @param s
     *            a <code>String</code> value
     */
    public Thing(String s) {
        val = new StringThing(s);
        stringval = s;
    }

    /**
     * Creates a new <code>Thing</code> instance from a string buffer.
     * 
     * @param s
     *            a <code>StringBuffer</code> value
     */
    public Thing(StringBuffer s) {
        val = new StringThing(s);
        stringval = s.toString();
    }

    /**
     * Creates a new <code>Thing</code> instance from an internal
     * representation.
     * 
     * @param realthing
     *            a <code>RealThing</code> value
     */
    public Thing(RealThing realthing) {
        val = realthing;
        stringval = null;
    }

    /**
     * <code>setVal</code> sets the internal representation of the Thing, and
     * cancels the string representation.
     * 
     * @param realthing
     *            a <code>RealThing</code> value
     */
    public void setVal(RealThing realthing) {
        val = realthing;
        stringval = null;
    }

    /**
     * <code>getVal</code> fetches the internal value of the Thing.
     * 
     * @return a <code>RealThing</code> value
     */
    public RealThing getVal() {
        return val;
    }

    /* FIXME - this shouldn't be here, really. */
    public void appendToGroup(Thing thing) throws HeclException {
        Vector v = GroupThing.get(this);
        v.addElement(thing);
        stringval = null;
    }

    /* FIXME - and neither should this. */
    public void appendToGroup(char ch) throws HeclException {
        Vector v = GroupThing.get(this);
        Thing le = (Thing) v.lastElement();
        StringThing.get(le);
        RealThing rt = le.getVal();
        StringThing str = (StringThing) rt;
        str.append(ch);
        le.setVal(str);
        stringval = null;
    }

    /**
     * <code>isTrue</code> is a convenience function that lets us know if the
     * result of a calculation is true or false.
     * 
     * @param newval
     *            a <code>Thing</code> value.
     */

    public static boolean isTrue(Thing thing) throws HeclException {
        return (IntThing.get(thing) != 0);
    }

    /**
     * <code>makeref</code> sets the 'this' Thing to be a reference to the
     * newval that was passed to it.
     * 
     * @param newval
     *            a <code>Thing</code> value.
     */

    public void makeref(Thing newval) {
        this.val = newval.val;
        this.stringval = newval.stringval;
    }

    /**
     * <code>toString</code> returns the String value of a Thing. FIXME this
     * could probably be improved in terms of efficiency. FIXME this could also
     * handle an exception better
     * 
     * @return a <code>String</code> value
     */
    public String toString() {
        return val.getStringRep();
    }

    public String getStringRep() {
        if (this instanceof RealThing) {
            return ((RealThing) this).getStringRep();
        } else {
            return this.toString();
        }
    }

    /**
     * <code>deepcopy</code> copies the thing, its value, and any elements the
     * value might contain.
     * 
     * @return a <code>Thing</code> value
     * @throws HeclException
     */
    public Thing deepcopy() throws HeclException {
	depth ++;
        /* If we have too deep a nesting, kill it. */
	if (depth > NESTDEPTH ) {
	    throw new
		HeclException("reference hard limit - circular reference?");
	}
	RealThing realthing = val.deepcopy();
	/* We've done the deepcopy, we can lower the depth again. */
	depth --;
	return new Thing(realthing);
    }
}
