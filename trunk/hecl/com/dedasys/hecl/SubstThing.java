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

/* Integer things. */

package com.dedasys.hecl;

public class SubstThing implements RealThing {
    public long cacheversion = -1;
    public boolean ref = false;
    private Thing val = null;
    private String varName = null;

    public SubstThing() {
    }

    public SubstThing(String s) {
	varName = s;
    }

    public SubstThing(String s, boolean isref) {
	ref = isref;
	varName = s;
    }

    public SubstThing(String s, Thing t) {
	varName = s;
	val = t;
    }

    public static Thing create(String s) {
	return new Thing(new SubstThing(s));
    }

    private static void setSubstFromAny(Interp interp, Thing thing)
	    throws HeclException {
	RealThing realthing = thing.val;

	if (realthing instanceof SubstThing) {
	    /* Don't need to modify it. */
	} else {
	    thing.setVal(new SubstThing(thing.toString()));
	}
    }

    public static Thing get(Interp interp, Thing thing) throws HeclException {
	setSubstFromAny(interp, thing);
	SubstThing getcopy = (SubstThing)thing.val;

	if (getcopy.cacheversion != interp.cacheversion) {
	    getcopy.cacheversion = interp.cacheversion;
	    getcopy.val = interp.getVar(getcopy.varName);
	}

	if (getcopy.ref) {
	    return getcopy.val;
	} else {
	    return getcopy.val.deepcopy();
	}
    }

    public void set(Thing t) {
	val = t;
    }

    public RealThing deepcopy() {
	return new SubstThing(varName);
    }

    public String toString() {
	if (ref) {
	    return "&{" + varName + "}";
	} else {
	    return "${" + varName + "}";
	}
    }
}
