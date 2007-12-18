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
 * The <code>DoubleThing</code> class represents a Thing that contains
 * a double value.
 */
public
//#if cldc == 1.0
abstract
//#endif
class DoubleThing extends FractionalThing {
    public String thingclass() {
	return "double";
    }

    /**
     * Creates a new <code>DoubleThing</code> instance equal to 0.
     *
     */
//#if javaversion >= 1.5 || cldc > 1.0
    public DoubleThing() {
	set(0.0);
    }

    /**
     * Creates a new <code>DoubleThing</code> instance with value i.
     * 
     * @param d
     *            a <code>double</code> value
     */
    public DoubleThing(double d) {
	set(d);
    }

    /**
     * Creates a new <code>DoubleThing</code> instance from boolean b where true
     * is 1 and false is 0.
     *
     * @param b
     *            a <code>boolean</code> value
     */
    public DoubleThing(boolean b) {
        set(b == true ? 1.0 : 0.0);
    }

    /**
     * Creates a new <code>DoubleThing</code> instance from string s.
     * 
     * @param s
     *            a <code>String</code> value
     */
    public DoubleThing(String s) {
        set(Double.parseDouble(s));
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
     * <code>set</code> transforms the given Thing into a DoubleThing,
     * internally.
     * 
     * @param thing
     *            a <code>Thing</code> value
     * @exception HeclException
     *                if an error occurs
     */
    private static void set(Thing thing) throws HeclException {
        RealThing realthing = thing.getVal();

        if (realthing instanceof DoubleThing)
	    return;

	if(NumberThing.isNumber(realthing)) {
	    // It's already a number
	    thing.setVal(new DoubleThing(((NumberThing)realthing).doubleValue()));
	} else {
	    /* Otherwise, try and parse the string representation. */
            thing.setVal(new DoubleThing(thing.toString()));
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
        set(thing);
	return ((DoubleThing)thing.getVal()).doubleValue();
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
	return (long)val;
    }

    public float floatValue() {
	return (float)val;
    }

    public double doubleValue() {
	return (double)val;
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


    private double val;
//#endif
}
