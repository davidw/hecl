/* Copyright 2005-2006 David N. Welton

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

package org.hecl;

/**
 * The <code>ObjectThing</code> class provides a wrapper for objects
 * that are not directly representable as strings.  FIXME - I am
 * uncertain if this will actually work or not.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */
public class ObjectThing implements RealThing {
    private Object val;

    /**
     * Creates a new <code>ObjectThing</code> instance equal to 0.
     *
     */
    public ObjectThing() {
//        val = new Object();
    }

    /**
     * Creates a new <code>ObjectThing</code> instance with value i.
     *
     * @param o
     *            an <code>Object</code>.
     */
    public ObjectThing(Object o) {
        val = o;
    }

    public String thingclass() {
	return "object";
    }

    /**
     * Retrieve the object associated with this instance.
     */
    public Object get() {return this.val;}

    /**
     * The <code>create</code> method creates and returns a newly allocated
     * Thing with an ObjectThing internal representation.
     *
     * @param o
     *            an <code>Object</code>
     * @return a <code>Thing</code> value
     */
    public static Thing create(Object o) {
        return new Thing(new ObjectThing(o));
    }


    
    /**
     * <code>get</code> attempts to fetch an Object from the Thing.
     *
     * @param thing a <code>Thing</code> value
     * @return an <code>Object</code> value
     * @exception HeclException if an error occurs
     */
    public static Object get(Thing thing) throws HeclException {
        RealThing realthing = thing.getVal();

	if (!(realthing instanceof ObjectThing)) {
	    throw new HeclException("cannot transform " + thing.toString() +
				    " into an ObjectThing");
	}
	return ((ObjectThing)realthing).val;
    }

    /**
     * <code>deepcopy</code> makes a copy.
     *
     * @return a <code>RealThing</code> value
     */
    public RealThing deepcopy() {
        return new ObjectThing(val);
    }

    /**
     * <code>getStringRep</code> creates a string representation of the
     * ObjectThing.
     *
     * @return a <code>String</code> value
     */
    public String getStringRep() {
	/* FIXME! */
	return val != null ? val.toString() : "[null]";
        //return val.toString();
    }
}
