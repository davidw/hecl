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


public class GroupThing implements RealThing {

    protected Vector val = null;

    public GroupThing(Vector v) {
	val = v;
    }

    public GroupThing(String s) {
	val.addElement(new Thing(new StringThing(s)));
    }


    private static void setGroupFromAny(Thing thing) {
	RealThing realthing = thing.val;

	if (realthing instanceof GroupThing) {
	    /* Nothing to be done. */
	    return;
	}

	Vector group = new Vector();
	if (realthing instanceof CodeThing) {
	    group.addElement(thing);
	} else {
	    group.addElement(new Thing(new StringThing(thing.toString())));
	}
	thing.setVal(new GroupThing(group));
    }


    public static Vector get(Thing thing) {
	setGroupFromAny(thing);
	GroupThing group = (GroupThing)thing.val;
	return group.val;
    }

    public RealThing deepcopy() {
	Vector newv = new Vector();
	for (Enumeration e = val.elements(); e.hasMoreElements();) {
	    newv.addElement(e.nextElement());
	}

	return new ListThing(newv);
    }

    public String toString() {
	String result = null;
	StringBuffer resbuf = new StringBuffer("");
	int sz = val.size();
	Thing element = null;

	if (sz > 0) {
	    for (int i = 0; i < sz; i++) {
		element = (Thing)val.elementAt(i);
		resbuf.append(element.toString());
	    }
	}
	return resbuf.toString();
    }
}
