/* Copyright 2004-2005 David N. Welton

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
 * The <code>Compare</code> class exists to compare things in different ways.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */
public class Compare {

    /**
     * <code>compareInt</code> exists to compare two Things as ints. Since it
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
    public static int compareInt(Thing a, Thing b) throws HeclException {
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

    /**
     * <code>compareString</code> compares two Things as strings. This can't
     * fail, because all Things may be represented as strings.
     * 
     * @param a
     *            a <code>Thing</code> value
     * @param b
     *            a <code>Thing</code> value
     * @return an <code>int</code> value
     * @throws HeclException
     */
    public static int compareString(Thing a, Thing b) {
        return a.toString().compareTo(b.toString());
    }

    /**
     * <code>compare</code> is a generic compare method that first tries to
     * compare two things as integers, and then tries again as strings.
     * 
     * @param a
     *            a <code>Thing</code> value
     * @param b
     *            a <code>Thing</code> value
     * @return an <code>int</code> value
     * @throws HeclException
     */

    public static int compare(Thing a, Thing b) {
        try {
            return compareInt(a, b);
        } catch (Exception e) {
            return compareString(a, b);
        }
    }

}
