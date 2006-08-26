/* Copyright 2004-2006 David N. Welton, Wolfgang S. Kechel

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
 * The <code>IntThing</code> class represents an integer Thing.
 *
 * @author <a href="mailto:wolfgang.kechel@data2c.com">Wolfgang S. Kechel</a>
 * @version 1.0
 */
public class IntThing extends IntegralThing {
    public static IntThing ZERO = new IntThing(0);
    public static IntThing ONE = new IntThing(1);
    public static IntThing NEGONE = new IntThing(-1);

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
        val = b == true ? 1 : 0;
    }

    /**
     * Creates a new <code>IntThing</code> instance from string s.
     *
     * @param s
     *            a <code>String</code> value
     */
    public IntThing(String s) {
        set(Integer.parseInt(s));
    }

    public String thingclass() {
	return "int";
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
    private static void set(Thing thing) throws HeclException {
        RealThing realthing = thing.getVal();

        if (realthing instanceof IntThing)
	    return;

	if(NumberThing.isNumber(realthing)) {
	    // It's already a number
	    thing.setVal(new IntThing(((NumberThing)realthing).intValue()));
	} else {
            /* If it's not an intthing already, we make it from its
	     * string rep. */
            thing.setVal(new IntThing(thing.toString()));
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
        return NumberThing.asNumber(thing).intValue();
    }

    public byte byteValue() {
	return (byte)val;
    }

    public short shortValue() {
	return (short)val;
    }

    public int intValue() {
	return val;
    }

    public long longValue() {
	return (long)val;
    }

    public float floatValue() {
	return (float)val;
    }

    public double doubleValue() {
	return (double)val;
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

    private int val;
}
