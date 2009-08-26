/* Copyright 2006 Wolfgang S. Kechel

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
 * The <code>LongThing</code> class represents a 'long' Thing.
 *
 * @version 1.0
 */
public class LongThing extends FractionalThing {
    /* The internal value. */
    private long val;

    /**
     * Creates a new <code>LongThing</code> instance equal to 0.
     *
     */
    public LongThing() {
	this(0);
    }

    /**
     * Creates a new <code>LongThing</code> instance with value l.
     *
     * @param l a <code>long</code> value
     */
    public LongThing(long l) {
	set(l);
    }

    /**
     * Creates a new <code>LongThing</code> instance from boolean b
     * where true is 1 and false is 0.
     *
     * @param b a <code>boolean</code> value
     */
    public LongThing(boolean b) {
        this(b == true ? 1 : 0);
    }

    /**
     * Creates a new <code>LongThing</code> instance from string s.
     *
     * @param s a <code>String</code> value
     */
    public LongThing(String s) {
        this(Long.parseLong(s));
    }

    public String thingclass() {
	return "long";
    }

    /**
     * The <code>create</code> method creates and returns a newly allocated
     * Thing with an LongThing internal representation.
     *
     * @param i an <code>int</code> value
     * @return a <code>Thing</code> value
     */
    public static Thing create(int i) {
	return create((long)i);
    }

    /**
     * The <code>create</code> method creates and returns a newly allocated
     * Thing with an LongThing internal representation.
     *
     * @param l an <code>long</code> value
     * @return a <code>Thing</code> value
     */
    public static Thing create(long l) {
        return new Thing(new LongThing(l));
    }

    /**
     * The <code>create</code> method creates and returns a newly
     * allocated Thing with an LongThing internal representation.
     *
     * @param b
     *            an <code>boolean</code> value
     * @return a <code>Thing</code> value
     */
    public static Thing create(boolean b) {
	return create(b == true ? 1 : 0);
    }

    /**
     * <code>set</code> transforms the given Thing into an LongThing,
     * internally.
     *
     * @param thing a <code>Thing</code> value
     * @exception HeclException
     *                if an error occurs
     */
    private static void set(Thing thing) throws HeclException {
        RealThing realthing = thing.getVal();

        if (realthing instanceof LongThing)
	    return;

	if(NumberThing.isNumber(thing)) {
	    // It's already a number
	    thing.setVal(new LongThing(((NumberThing)realthing).longValue()));
	} else {
	    // If it's not a longthing already, we make it from its string rep.
	    thing.setVal(new LongThing(thing.toString()));
        }
    }

    /**
     * <code>get</code> attempts to fetch an integer value from a
     * Thing.
     *
     * @param thing a <code>Thing</code> value
     * @return an <code>int</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public static long get(Thing thing) throws HeclException {
        set(thing);
        return ((LongThing)thing.getVal()).longValue();
    }


    public byte byteValue() {
	return (byte)val;
    }

    public short shortValue() {
	return (short)val;
    }

    public int intValue() {
	return (int)val;
    }

    public long longValue() {
	return val;
    }

    public float floatValue() {
	return (float)val;
    }

    public double doubleValue() {
	return (double)val;
    }

    public boolean isLong() {
	return true;
    }


    /**
     * <code>set</code> sets the internal value of an LongThing to l.
     *
     * @param l
     *            a <code>long</code> value
     */
    public void set(long l) {
        val = l;
    }

    /**
     * <code>deepcopy</code> makes a copy.
     *
     * @return a <code>RealThing</code> value
     */
    public RealThing deepcopy() {
        return new LongThing(val);
    }

    /**
     * <code>getStringRep</code> creates a string representation of the
     * LongThing.
     *
     * @return a <code>String</code> value
     */
    public String getStringRep() {
        return Long.toString(val);
    }
}
