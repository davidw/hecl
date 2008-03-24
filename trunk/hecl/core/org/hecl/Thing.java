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
    private RealThing val;

    /* This flag is used by Stanza to indicate whether a Thing should
     * be copied if something tries to write to it.  */
    protected boolean copy = false;

    /**
     * <code>literal</code> is used to indicate Things which come
     * directly from the parser and thus should not be changed.
     *
     */
    protected boolean literal = false;

    /* Refers to a global variable? */
    public boolean global = false;

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
    }

    /**
     * Creates a new <code>Thing</code> instance from a string buffer.
     *
     * @param s
     *            a <code>StringBuffer</code> value
     */
    public Thing(StringBuffer s) {
        val = new StringThing(s);
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
    }

    /**
     * <code>setVal</code> sets the internal representation of the Thing.
     *
     * @param realthing a <code>RealThing</code> value
     */
    public void setVal(RealThing realthing) {
        val = realthing;
    }

    public void setCopyVal(RealThing realthing) {
	if (!copy && !literal) {
	    setVal(realthing);
	}
    }

    /**
     * <code>getVal</code> fetches the internal value of the Thing.
     *
     * @return a <code>RealThing</code> value
     */
    public RealThing getVal() {
        return val;
    }


    public static final Thing emptyThing() {
	return new Thing((String)null);
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
    }

    /**
     * <code>toString</code> returns the String value of a Thing.
     *
     * @return a <code>String</code> value
     */
    public String toString() {
	return val.getStringRep();
    }

//#if javaversion >= 1.5
    /**
     * The <code>hashCode</code> method overrides the object hashCode
     * method, using the hash of the string representation.
     *
     * @return an <code>int</code> value
     */
    public int hashCode() {
	if (val.thingclass().equals("object")) {
	    return ((ObjectThing)val).get().hashCode();
	} else {
	    return val.getStringRep().hashCode();
	}
    }

    /**
     * The <code>equals</code> method overrides the object equals
     * method, using either the two objects, in the case of two
     * ObjectThings, or the two string representations.
     *
     * @param obj an <code>Object</code> value
     * @return a <code>boolean</code> value
     */
    public boolean equals(Object obj) {
	Thing that = (Thing)obj;
	if (val.thingclass().equals("object")) {
	    if (that.val.thingclass().equals("object")) {
		return ((ObjectThing)val).get().equals(((ObjectThing)that.val).get());
	    }
	    return false;
	}
	/* FIXME - This isn't actually used by much - so far, only by
	 * the JavaCmd code.  However, we should probably investigate
	 * other possibilities. */
	return val.getStringRep().equals(that.toString());
    }
//#endif


    /**
     * The <code>setLiteral</code> method sets the thing in question
     * to be a literal Thing, and returns it.
     *
     * @return a <code>Thing</code> value
     */
    public Thing setLiteral() {
	literal = true;
	return this;
    }

    public boolean isLiteral() {
	return literal;
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
  	//Thing retval = new Thing(realthing);
	//retval.copy = this.copy;
	//retval.global = global;
	//return retval;
	return new Thing(realthing);
    }
}
