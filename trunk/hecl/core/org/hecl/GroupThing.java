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

import java.util.Enumeration;
import java.util.Vector;

/**
 * The <code>GroupThing</code> class is for Hecl "groups". For instance, "foo
 * $foo [foo]" must have its components kept together, and is not a proper list.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

public class GroupThing implements RealThing {

    protected Vector val = null;

    /**
     * Creates a new <code>GroupThing</code> instance from a vector.
     *
     * @param v a <code>Vector</code> value
     */
    public GroupThing(Vector v) {
        val = v;
    }

    /**
     * Creates a new <code>GroupThing</code> instance from a string.
     *
     * @param s a <code>String</code> value
     */
    public GroupThing(String s) {
        val.addElement(new Thing(new StringThing(s)));
    }

    /**
     * The <code>create</code> method takes a Vector of Things and
     * creates a Thing containing a GroupThing.
     *
     * @param v a <code>Vector</code> value
     * @return a <code>Thing</code> value
     */
    public static Thing create(Vector v) {
        return new Thing(new GroupThing(v));
    }

    public String thingclass() {
	return "group";
    }


    /**
     * <code>setGroupFromAny</code> creates a group from another type of
     * Thing.
     *
     * @param thing a <code>Thing</code> value
     */
    private static void setGroupFromAny(Thing thing) throws HeclException {
        RealThing realthing = thing.getVal();

        if (!(realthing instanceof GroupThing)) {
            Vector group = new Vector();
            if (realthing instanceof CodeThing) {
                group.addElement(thing);
            } else {
                group.addElement(new Thing(thing.toString()));
            }
            thing.setVal(new GroupThing(group));
        }
    }

    /**
     * <code>get</code> returns a Vector containing other Things, representing
     * a group, from a Thing.
     *
     * @param thing a <code>Thing</code> value
     * @return a <code>Vector</code> value
     */
    public static Vector get(Thing thing) throws HeclException {
        setGroupFromAny(thing);
        return ((GroupThing)thing.getVal()).val;
    }

    /**
     * <code>deepcopy</code> makes a copy of a GroupThing and all its
     * elements.
     *
     * @return a <code>RealThing</code> value
     * @throws HeclException
     */
    public RealThing deepcopy() throws HeclException {
        Vector newv = new Vector();
        for (Enumeration e = val.elements(); e.hasMoreElements();) {
            newv.addElement(((Thing) e.nextElement()).deepcopy());
        }

        return new GroupThing(newv);
    }

    /**
     * <code>getStringRep</code> returns a string representation of the group.
     *
     * @return a <code>String</code> value
     */
    public String getStringRep() {
        StringBuffer resbuf = new StringBuffer("");
        int sz = val.size();
        Thing element = null;

        if (sz > 0) {
            for (int i = 0; i < sz; i++) {
                element = (Thing) val.elementAt(i);
                resbuf.append(element.toString());
            }
        }
        return resbuf.toString();
    }
}
