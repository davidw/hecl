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

/**
 * The <code>Thing</code> class is what Hecl revolves around. "Things"
 * can be of several types, include strings, integers, lists, hash
 * tables.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.1
 */

public class Thing extends Object {
    public static final Thing EMPTYTHING = new Thing("");
    
    private RealThing val;

    /* This flag is used by Stanza to indicate whether a Thing should
     * be copied if something tries to write to it.  */
    protected boolean copy = false;

    /* Refers to a global variable? */
    public boolean global = false;

    protected String stringval = null;

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


    /**
     * <code>isTrue</code> is a convenience function that lets us know if the
     * result of a calculation is true or false.
     *
     * @param thing a <code>Thing</code> value
     * @return a <code>boolean</code> value
     * @exception HeclException if an error occurs
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
     * could probably be improved in terms of efficiency.
     *
     * @return a <code>String</code> value
     */
    public String toString() {
	if(stringval == null)
	    stringval = val.getStringRep();
	return stringval;
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
/*  	Thing retval = new Thing(realthing);
	retval.copy = this.copy;
	return retval;  */
	return new Thing(realthing);
    }
}
