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

/* $Id$ */

/* Integer things. */

package org.hecl;

/**
 * The <code>IntThing</code> class represents an integer Thing.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */
public class IntThing implements RealThing {
    private int val;

    /**
     * Creates a new <code>IntThing</code> instance equal to 0.
     *  
     */
    public IntThing() {
        val = 0;
    }

    /**
     * Creates a new <code>IntThing</code> instance with value i.
     * 
     * @param i
     *            an <code>int</code> value
     */
    public IntThing(int i) {
        val = i;
    }

    /**
     * Creates a new <code>IntThing</code> instance from boolean b where true
     * is 1 and false is 0.
     * 
     * @param b
     *            a <code>boolean</code> value
     */
    public IntThing(boolean b) {
        val = (b == true ? 1 : 0);
    }

    /**
     * Creates a new <code>IntThing</code> instance from string s.
     * 
     * @param s
     *            a <code>String</code> value
     */
    public IntThing(String s) {
        val = Integer.parseInt(s);
    }

    /**
     * The <code>create</code> method creates and returns a newly allocated
     * Thing with an IntThing internal representation.
     * 
     * @param i
     *            an <code>int</code> value
     * @return a <code>Thing</code> value
     */
    public static Thing create(int i) {
        return new Thing(new IntThing(i));
    }

    /**
     * The <code>create</code> method creates and returns a newly allocated
     * Thing with an IntThing internal representation.
     * 
     * @param b
     *            an <code>boolean</code> value
     * @return a <code>Thing</code> value
     */
    public static Thing create(boolean b) {
        return new Thing(new IntThing(b));
    }

    /**
     * <code>setIntFromAny</code> transforms the given Thing into an IntThing,
     * internally.
     * 
     * @param thing
     *            a <code>Thing</code> value
     * @exception HeclException
     *                if an error occurs
     */
    private static void setIntFromAny(Thing thing) throws HeclException {
        RealThing realthing = thing.val;

        if (realthing instanceof IntThing) {
	    return;
	} else if (realthing instanceof DoubleThing) {
	    double d = DoubleThing.get(thing);
	    thing.setVal(new IntThing((int)d));
	} else {
	/* Ok, just try with the string representation. */
            thing.setVal(new IntThing(thing.getStringRep()));
        }
    }

    /**
     * <code>get</code> attempts to fetch an integer value from a Thing.
     * 
     * @param thing
     *            a <code>Thing</code> value
     * @return an <code>int</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public static int get(Thing thing) throws HeclException {
        setIntFromAny(thing);
        IntThing getint = (IntThing) thing.val;
        return getint.val;
    }

    /**
     * <code>set</code> sets the internal value of an IntThing to i.
     * 
     * @param i
     *            an <code>int</code> value
     */
    public void set(int i) {
        val = i;
    }

    /**
     * <code>deepcopy</code> makes a copy.
     * 
     * @return a <code>RealThing</code> value
     */
    public RealThing deepcopy() {
        return new IntThing(val);
    }

    /**
     * <code>getStringRep</code> creates a string representation of the
     * IntThing.
     * 
     * @return a <code>String</code> value
     */
    public String getStringRep() {
        return Integer.toString(val);
    }

    /**
     * <code>compare</code> exists to compare two Things as ints. Since it
     * is possible that one or both are not integers, we may throw a
     * HeclException.
     * 
     * @param a
     *            a <code>Thing</code> value
     * @param b
     *            a <code>Thing</code> value
     * @return an <code>int</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public static int compare(Thing a, Thing b) throws HeclException {
        int ia = IntThing.get(a);
        int ib = IntThing.get(b);
        if (ia == ib) {
            return 0;
        } else if (ia < ib) {
            return -1;
        } else {
            return 1;
        }
    }
}
