/* Copyright 2004 David N. Welton

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


class Compare {

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

    public static int compareString(Thing a, Thing b) {
	return StringThing.get(a).compareTo(StringThing.get(b));
    }

    public static int compare(Thing a, Thing b) {
	try {
	    return compareInt(a, b);
	} catch (Exception e) {
	    return compareString(a, b);
	}
    }

}
