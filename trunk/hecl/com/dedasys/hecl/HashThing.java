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

package com.dedasys.hecl;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

public class HashThing implements RealThing {
    private Hashtable val = null;

    public HashThing() {
	val = new Hashtable ();
    }

    public HashThing(Hashtable h) {
	val = h;
    }

    public HashThing(Vector v) throws HeclException {
	if ((v.size() % 2) != 0) {
	    throw new HeclException(
		"list must have even number of elements");
	}
	/* FIXME: I pulled this '3' (initial size of the hash table is
	 * list size + 3), out of the air... better suggestions based
	 * on experimentation are welcome. */
	val = new Hashtable(v.size() + 3);

	for (Enumeration e = v.elements(); e.hasMoreElements(); ) {
	    String key = ((Thing)e.nextElement()).toString();
	    Thing value = (Thing)e.nextElement();
	    val.put(key, value);
	}
    }

    private static void setHashFromAny(Thing thing)
	    throws HeclException {
	RealThing realthing = thing.val;
	Vector list = null;
	HashThing newthing = null;

	if (realthing instanceof HashThing) {
	    /* Nothing to be done. */
	    return;
	}

	list = ListThing.get(thing);

	newthing = new HashThing(list);
	thing.setVal(newthing);
    }


    public static Hashtable get(Thing thing) throws HeclException {
	setHashFromAny(thing);
	HashThing gethash = (HashThing)thing.val;
	return gethash.val;
    }

    public RealThing deepcopy() {
	Hashtable h = new Hashtable();

	for (Enumeration e = val.keys() ;
	     e.hasMoreElements(); ) {
	    String key = (String)e.nextElement();
	    /* FIXME - do a deepcopy below? */
	    h.put(key, val.get(key));
	}

	return new HashThing(h);
    }

    public String toString() throws HeclException {
	Vector v = ListThing.get(new Thing(new HashThing(val)));
	ListThing newthing = new ListThing(v);
	return newthing.toString();
    }

}
