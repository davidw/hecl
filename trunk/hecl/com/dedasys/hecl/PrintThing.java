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

/* $Id$ */

package com.dedasys.hecl;

import java.util.*;

public class PrintThing {

    private static String ws(int n) {
	return new String(new byte[n]).replace('\0', ' ');
    }

    public static void printThing(Thing t) throws HeclException {
	printThing(t, 0);
    }

    /* This could probably be pulled out of the J2ME version. */
    public static void printThing(Thing t, int depth) throws HeclException {
	RealThing rt = t.val;
	if (rt instanceof IntThing) {
	    System.out.println(ws(depth * 4) + "INT: " + ((IntThing) rt).get(t));
	} else if (rt instanceof StringThing) {
	    System.out.println(ws(depth * 4) + "STR: " + ((StringThing) rt).get(t));
	} else if (rt instanceof SubstThing) {
	    System.out.println(ws(depth * 4) + "SUBST: " + ((SubstThing) rt).toString());
	} else if (rt instanceof ListThing) {
	    Vector v = ((ListThing) rt).get(t);
	    System.out.println(ws(depth * 4) + "LIST START");
	    for (Enumeration e = v.elements(); e.hasMoreElements();) {
		PrintThing.printThing((Thing)e.nextElement(), depth + 1);
	    }
	    System.out.println(ws(depth * 4) + "LIST END");
	} else if (rt instanceof GroupThing) {
	    Vector v = ((GroupThing) rt).get(t);
	    System.out.println(ws(depth * 4) + "GROUP START " + v.size());
	    for (Enumeration e = v.elements(); e.hasMoreElements();) {
		PrintThing.printThing((Thing)e.nextElement(), depth + 1);
	    }
	    System.out.println(ws(depth * 4) + "GROUP END");
	} else if (rt instanceof HashThing) {
	    Hashtable h = ((HashThing) rt).get(t);
	    System.out.println(ws(depth * 4) + "HASH START");
	    for (Enumeration e = h.keys(); e.hasMoreElements();) {
		String key = (String)e.nextElement();
		System.out.println(ws(depth * 4) + " KEY: " + key);
		PrintThing.printThing((Thing)h.get(key), depth + 1);
	    }
	    System.out.println(ws(depth * 4) + "HASH END");
	} else if (rt instanceof CodeThing) {
	    System.out.println("CODE:" + t);
	} else {
	    System.out.println("OTHER:" + t);
	}
    }
}
