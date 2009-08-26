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

package org.hecl;

import java.util.*;

/**
 * The <code>PrintThing</code> class is a utility class used to print out
 * Things. It is useful for debugging purposes.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */
public class PrintThing {

    /**
     * <code>ws</code> creats new bytes with nulls, and then replaces them
     * with whitespace. Kind of ugly, but it works to print out a given amount
     * of whitespace.
     *
     * @param n an <code>int</code> value
     * @return a <code>String</code> value
     */
    private static String ws(int n) {
        return new String(new byte[n]).replace('\0', ' ');
    }

    /**
     * <code>printThing</code> prints out a Thing.
     *
     * @param t a <code>Thing</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public static void printThing(Thing t) throws HeclException {
        printThing(t, 0);
    }

    /**
     * The main <code>printThing</code> function.
     *
     * @param t a <code>Thing</code> value
     * @param depth an <code>int</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public static void printThing(Thing t, int depth) throws HeclException {
        RealThing rt = t.getVal();
        if (rt instanceof IntThing) {
            System.out.println(ws(depth * 4) + "INT: " + IntThing.get(t) + " (copy: " + t.copy +") (literal: " + t.literal +")");
        } else if (rt instanceof StringThing) {
            System.out.println(ws(depth * 4) + "STR: " + StringThing.get(t) + " (copy: " + t.copy +")  (literal: " + t.literal +")");
        } else if (rt instanceof SubstThing) {
            System.out.println(ws(depth * 4) + "SUBST: "
                    + ((SubstThing) rt).getStringRep() + " (copy: " + t.copy +") (literal: " + t.literal +")");
        } else if (rt instanceof ListThing) {
            Vector v = ListThing.get(t);
            System.out.println(ws(depth * 4) + "LIST START" + " (copy: " + t.copy +") (literal: " + t.literal +")");
            for (Enumeration e = v.elements(); e.hasMoreElements();) {
                PrintThing.printThing((Thing) e.nextElement(), depth + 1);
            }
            System.out.println(ws(depth * 4) + "LIST END");
        } else if (rt instanceof GroupThing) {
            Vector v = GroupThing.get(t);
            System.out.println(ws(depth * 4) + "GROUP START " + v.size() + " (copy: " + t.copy +") (literal: " + t.literal +")");
            for (Enumeration e = v.elements(); e.hasMoreElements();) {
                PrintThing.printThing((Thing) e.nextElement(), depth + 1);
            }
            System.out.println(ws(depth * 4) + "GROUP END");
        } else if (rt instanceof HashThing) {
            Hashtable h = HashThing.get(t);
            System.out.println(ws(depth * 4) + "HASH START" + " (copy: " + t.copy +") (literal: " + t.literal +")");
            for (Enumeration e = h.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                System.out.println(ws(depth * 4) + " KEY: " + key);
                PrintThing.printThing((Thing) h.get(key), depth + 1);
            }
            System.out.println(ws(depth * 4) + "HASH END");
        } else if (rt instanceof CodeThing) {
            System.out.println("CODE:" + t + " (copy: " + t.copy +") (literal: " + t.literal +")");
        } else {
            System.out.println("OTHER:" + t + " (copy: " + t.copy +") (literal: " + t.literal +")");
        }
    }
}
