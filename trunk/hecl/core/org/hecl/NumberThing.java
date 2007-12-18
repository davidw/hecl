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

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The <code>NumberThing</code> class is what all numeric Thing types
 * are derived from.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public abstract class NumberThing implements RealThing {
    // Satisfy RealThing...
    public abstract RealThing deepcopy() throws HeclException;
    public abstract String getStringRep();

    /**
     * <code>isNumber</code> returns true if Thing t is represented as
     * a number internally.
     *
     * @param t a <code>Thing</code> value
     * @return a <code>boolean</code> value
     */
    public static boolean isNumber(Thing t) {
	return t.getVal() instanceof NumberThing;
    }

    public static boolean isNumber(RealThing rt) {
	return rt instanceof NumberThing;
    }
    
    /**
     * <code>isIntegral</code> returns true if Thing t is a whole
     * number (an int or long), or false if it isn't.
     *
     * @param t a <code>Thing</code> value
     * @return a <code>boolean</code> value
     */
    public static boolean isIntegral(Thing t) {
	RealThing rt = t.getVal();
	return isNumber(rt) && ((NumberThing)rt).isIntegral();
    }


    /**
     * <code>isFractional</code> returns true if Thing t is a
     * fractional (floating point) number, false if it isn't.
     *
     * @param t a <code>Thing</code> value
     * @return a <code>boolean</code> value
     */
    public static boolean isFractional(Thing t) {
	RealThing rt = t.getVal();
	return isNumber(rt) && ((NumberThing)rt).isFractional();
    }

    public abstract boolean isIntegral();
    public abstract boolean isFractional();


    /**
     * <code>byteValue</code> returns the number as a byte.
     *
     * @return a <code>byte</code> value
     */
    public abstract byte byteValue();


    /**
     * <code>shortValue</code> returns the number as a short.
     *
     * @return a <code>short</code> value
     */
    public abstract short shortValue();

    /**
     * <code>intValue</code> returns the number as an int.
     *
     * @return an <code>int</code> value
     */
    public abstract int intValue();


    /**
     * <code>longValue</code> returns the value as a long.
     *
     * @return a <code>long</code> value
     */
    public abstract long longValue();
//#if javaversion >= 1.5 || cldc > 1.0

    /**
     * <code>floatValue</code> returns the value as a float.  Not
     * available in CLDC 1.0
     *
     * @return a <code>float</code> value
     */
    public abstract float floatValue();


    /**
     * <code>doubleValue</code> returns the value as a double.  Not
     * available in CLDC 1.0
     *
     * @return a <code>double</code> value
     */
    public abstract double doubleValue();
//#endif

    /**
     * <code>asNumber</code> attempts to transform Thing t into a
     * number of some sort.  It first tries to get an int from t, then
     * a long, and finally a double.
     *
     * @param t a <code>Thing</code> value
     * @return a <code>NumberThing</code> value
     */
    public static NumberThing asNumber(Thing t) {
	if(isNumber(t))
	    return (NumberThing)(t.getVal());
	String s = t.toString();
	try {
	    return new IntThing(s);
	}
	catch(NumberFormatException ne) {
//#if cldc == 1.0
	    return new LongThing(s);
//#else
	    try {
		return new LongThing(s);
	    }
	    catch(NumberFormatException ne2) {
		return new DoubleThing(s);
	    }
//#endif
	}
    }

    /**
     * The <code>create</code> method creates a new Thing from
     * NumberThing n.
     *
     * @param n a <code>NumberThing</code> value
     * @return a <code>Thing</code> value
     */
    public static Thing create(NumberThing n) {
	return new Thing(n);
    }
}
