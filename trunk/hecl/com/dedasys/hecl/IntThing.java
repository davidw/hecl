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

public class IntThing implements RealThing {

    private int val;

    public IntThing() {
	val = 0;
    }

    public IntThing(int i) {
	val = i;
    }

    public IntThing(boolean b) {
	val = (b == true ? 1 : 0);
    }

    public IntThing(String s) {
	val = Integer.parseInt(s);
    }

    public static Thing create(int i) {
	return new Thing(new IntThing(i));
    }

    public static Thing create(boolean b) {
	return new Thing(new IntThing(b));
    }

    private static void setIntFromAny(Thing thing)
	    throws HeclException {
	RealThing realthing = thing.val;

	if (realthing instanceof IntThing) {
	    /* Don't need to modify it. */
	} else {
	    thing.setVal(new IntThing(thing.toString()));
	}
    }


    public static int get(Thing thing) throws HeclException {
	setIntFromAny(thing);
	IntThing getint = (IntThing)thing.val;
	return getint.val;
    }

    public void set(int i) {
	val = i;
    }

    public RealThing deepcopy() {
	return new IntThing(val);
    }

    public String toString() {
	return Integer.toString(val);
    }

}
