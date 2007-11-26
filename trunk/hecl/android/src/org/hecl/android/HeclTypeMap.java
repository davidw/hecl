/* Copyright 2007 David N. Welton - DedaSys LLC - http://www.dedasys.com

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

package org.hecl.android;

import java.lang.reflect.Method;

import java.util.Vector;

import android.util.Log;

import org.hecl.HeclException;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.LongThing;
import org.hecl.ObjectThing;
import org.hecl.StringThing;
import org.hecl.Thing;

class HeclTypeMap {
    public static Object[] mapArgs(Class[] outparams, Thing[] argv, int offset)
        throws HeclException {

	Object[] outobjs = new Object[outparams.length];
	Class c = null;
	for (int i = 0; i < outparams.length; i++) {
	    Thing inparam = argv[i + offset];
	    Class outparam = outparams[i];
	    String javaclassname = outparam.getSimpleName();

	    if (javaclassname.equals("boolean") || javaclassname.equals("int")) {
		outobjs[i] = IntThing.get(inparam);
	    } else if (javaclassname.equals("long")) {
		outobjs[i] = LongThing.get(inparam);
	    } else if (javaclassname.equals("CharSequence") ||
		       javaclassname.equals("String")) {
		outobjs[i] = StringThing.get(inparam);
	    } else if (javaclassname.equals("int[]")) {
		Vector v = ListThing.get(inparam);
		int[] ints = new int[v.size()];
		for (int j = 0; j < v.size(); j++) {
		    ints[j] = IntThing.get((Thing)v.elementAt(j));
		}
		outobjs[i] = ints;
	    } else {
		outobjs[i] = ObjectThing.get(inparam);
	    }
	}
	return outobjs;
    }

    public static Thing mapRetval(Method m, Object o) {
	String rtype = m.getReturnType().getSimpleName();
	if (rtype.equals("void")) {
	    return null;
	} else if (rtype.equals("int")) {
	    return IntThing.create(((Integer)o).intValue());
	} else if (rtype.equals("long")) {
	    return LongThing.create(((Long)o).longValue());
	} else if (rtype.equals("String") || rtype.equals("CharSequence")) {
	    return new Thing((String)o);
	} else if (rtype.equals("int[]")) {
	    return new Thing("FIXME");
	}
	return ObjectThing.create(o);
    }
}
