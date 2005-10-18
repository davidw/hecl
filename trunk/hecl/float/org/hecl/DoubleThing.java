/* Copyright 2005 David N. Welton

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

/* $Id$ */

/* Integer things. */

package org.hecl;

/**
 * The <code>DoubleThing</code> class represents a Thing that contains
 * either a floating point or large number.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */
public class DoubleThing implements RealThing {
    private double val;

    /**
     * Creates a new <code>DoubleThing</code> instance equal to 0.
     *
     */
    public DoubleThing() {
        val = 0;
    }

    /**
     * Creates a new <code>DoubleThing</code> instance with value i.
     * 
     * @param d
     *            a <code>double</code> value
     */
    public DoubleThing(double d) {
        val = d;
    }

    /**
     * Creates a new <code>DoubleThing</code> instance from boolean b where true
     * is 1 and false is 0.
     *
     * @param b
     *            a <code>boolean</code> value
     */
    public DoubleThing(boolean b) {
        val = (b == true ? 1 : 0);
    }

    /**
     * Creates a new <code>DoubleThing</code> instance from string s.
     * 
     * @param s
     *            a <code>String</code> value
     */
    public DoubleThing(String s) {
        val = Double.parseDouble(s);
    }

    /**
     * The <code>create</code> method creates and returns a newly allocated
     * Thing with a DoubleThing internal representation.
     * 
     * @param d
     *            a <code>double</code> value
     * @return a <code>Thing</code> value
     */
    public static Thing create(double d) {
        return new Thing(new DoubleThing(d));
    }

    /**
     * The <code>create</code> method creates and returns a newly allocated
     * Thing with a DoubleThing internal representation.
     * 
     * @param b
     *            an <code>boolean</code> value
     * @return a <code>Thing</code> value
     */
    public static Thing create(boolean b) {
        return new Thing(new DoubleThing(b));
    }

    /**
     * <code>setDoubleFromAny</code> transforms the given Thing into a DoubleThing,
     * internally.
     * 
     * @param thing
     *            a <code>Thing</code> value
     * @exception HeclException
     *                if an error occurs
     */
    private static void setDoubleFromAny(Thing thing) throws HeclException {
        RealThing realthing = thing.val;

        if (realthing instanceof DoubleThing) {
	    return;
	}  else if (realthing instanceof IntThing) {
	    int i = IntThing.get(thing);
	    thing.setVal(new DoubleThing((double)i));
        }
	else {
	    /* Otherwise, try and parse the string representation. */
            thing.setVal(new DoubleThing(thing.getStringRep()));
	}
    }

    /**
     * <code>get</code> attempts to fetch a double value from a Thing.
     * 
     * @param thing
     *            a <code>Thing</code> value
     * @return a <code>double</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public static double get(Thing thing) throws HeclException {
        setDoubleFromAny(thing);
        DoubleThing getdouble = (DoubleThing) thing.val;
        return getdouble.val;
    }

    /**
     * <code>set</code> sets the internal value of a DoubleThing to i.
     * 
     * @param d
     *            a <code>double</code> value
     */
    public void set(double d) {
        val = d;
    }

    /**
     * <code>deepcopy</code> makes a copy.
     * 
     * @return a <code>RealThing</code> value
     */
    public RealThing deepcopy() {
        return new DoubleThing(val);
    }

    /**
     * <code>getStringRep</code> creates a string representation of the
     * DoubleThing.
     * 
     * @return a <code>String</code> value
     */
    public String getStringRep() {
        return Double.toString(val);
    }

    /**
     * The <code>promote</code> method takes a thing and promotes it
     * to a double in some way or another.  If it's a double or an
     * int, we don't do anything.  If it's something else, we attempt
     * to transform it into either an a double, or if it is an
     * integer, into an integer.
     *
     * @param t a <code>Thing</code> value
     * @return a <code>double</code> value
     * @exception HeclException if an error occurs
     */
    public static double promote(Thing t) throws HeclException {
	RealThing val = t.val;
	if (val instanceof DoubleThing) {
	    return DoubleThing.get(t);
	} else if (val instanceof IntThing) {
	    return (double)IntThing.get(t);
	} else {
	    double dval = DoubleThing.get(t);
	    if (dval == Math.rint(dval) &&
		dval < Integer.MAX_VALUE && dval > Integer.MIN_VALUE) {
		/* It's an integer. */
		IntThing.get(t);
	    }
	    return dval;
	}
    }

    /**
     * The <code>argPromotion</code> method fills in an array of
     * doubles with the values of their arguments.
     *
     * @param argv a <code>Thing[]</code> value
     * @param dargv a <code>double[]</code> value
     * @return a <code>boolean</code> value
     * @exception HeclException if an error occurs
     */
    public static void argPromotion(Thing[] argv, double [] dargv)
	throws HeclException {

	boolean promote = false;
	for (int i = 1; i < argv.length; i++ ) {
	    dargv[i-1] = promote(argv[i]);
	}
    }

}
