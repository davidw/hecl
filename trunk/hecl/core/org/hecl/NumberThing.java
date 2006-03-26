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

public abstract class NumberThing implements RealThing {
    // Satisfy RealThing...
    public abstract RealThing deepcopy() throws HeclException;
    public abstract String getStringRep();

    // Some predicates
    public static boolean isNumber(Thing t) {
	return t.val instanceof NumberThing;
    }

    public static boolean isIntegral(Thing t) {
	return isNumber(t) && ((NumberThing)t.val).isIntegral();
    }

    public static boolean isFractional(Thing t) {
	return isNumber(t) && ((NumberThing)t.val).isFractional();
    }

    public abstract boolean isIntegral();

    public abstract boolean isFractional();

    // Accessors
    public abstract byte byteValue();
    public abstract short shortValue();
    public abstract int intValue();
    public abstract long longValue();
//#ifndef ant:cldc1.0
    public abstract float floatValue();
    public abstract double doubleValue();
//#endif

    // Conversion
    public static NumberThing asNumber(Thing t) {
	if(isNumber(t))
	    return (NumberThing)(t.val);
	String s = t.toString();
	try {
	    return new IntThing(s);
	}
	catch(NumberFormatException ne) {
//#ifdef ant:cldc1.0
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

    public static Thing create(NumberThing n) {
	return new Thing(n);
    }
}
