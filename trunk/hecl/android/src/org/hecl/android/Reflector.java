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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

import java.util.Hashtable;
import java.util.Vector;

import android.util.Log;

import org.hecl.DoubleThing;
import org.hecl.HeclException;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.LongThing;
import org.hecl.ObjectThing;
import org.hecl.Thing;

class Reflector {
    private Class forclass;
    private Method[] methods;
    private Hashtable methodnames;
    private Hashtable constnames;

    public Reflector(String classname) throws HeclException {
	try {
	    methodnames = new Hashtable();
	    constnames = new Hashtable();

	    forclass = Class.forName(classname);
	    /* We could also do getDeclaredMethods and do the
	     * "subclassing" some other way.  */
	    methods = forclass.getMethods();
	    for (Method m : methods) {
		Vector<Method> v = null;
		String name = m.getName().toLowerCase();
		if (methodnames.containsKey(name)) {
		    v = (Vector)methodnames.get(name);
		} else {
		    v = new Vector<Method>();
		}
		v.add(m);
		methodnames.put(name, v);
	    }

 	    for (Field f : forclass.getFields()) {
		int mod = f.getModifiers();
		StringBuffer msg = new StringBuffer("");
		if (Modifier.isPublic(mod) && Modifier.isFinal(mod) && Modifier.isStatic(mod)) {
		    String name = f.getName();
		    Class type = f.getType();
		    if (type == boolean.class) {
			constnames.put(name, IntThing.create(f.getBoolean(forclass)));
		    } else if (type == double.class) {
			constnames.put(name, DoubleThing.create(f.getDouble(forclass)));
		    } else if (type == float.class) {
			constnames.put(name, DoubleThing.create(f.getFloat(forclass)));
		    } else if (type == int.class) {
			constnames.put(name, IntThing.create(f.getInt(forclass)));
		    } else if (type == long.class) {
			constnames.put(name, LongThing.create(f.getLong(forclass)));
		    } else if (type == String.class) {
			constnames.put(name, new Thing((String)f.get(forclass)));
		    } else {
			throw new HeclException("Type " + type.getName() +
						", a field in " + forclass.getName() +
						" was not handled in the Reflector instantiation");
		    }
		}
	    }
	} catch (Exception e) {
	    throw new HeclException(e.toString());
	}
    }

    public Thing instantiate(Thing[] argv)
        throws HeclException {


	Constructor c = selectConstructor(argv);
	if (c == null) {
	    throw new HeclException("Constructor not found for class: " + forclass.getName());
	}
	Object[] args = new Object[0];
	try {
	    args = HeclTypeMap.mapArgs(c.getParameterTypes(), argv, 0);
	    return ObjectThing.create(c.newInstance(args));
	} catch (InvocationTargetException e) {
	    String msg = "Problem invoking " + forclass.getName() + " constructor " + c.getName() + " with arguments: ";
	    for (Thing t : argv) {
		msg += t.toString() + " ";
	    }
	    msg += " (Translated to:) ";
	    for (Object eo : args) {
		msg += eo.toString() + " ";
	    }
	    msg += " " + e.getTargetException().toString();
	    throw new HeclException(msg);
	} catch (Exception e) {
	    throw new HeclException("Reflector.evaluate error :" + e.toString());
	}
    }

    public Thing evaluate(Object o, String cmd, Thing[] argv)
        throws HeclException {
	Method m = selectMethod(cmd, argv);

	if (m == null) {
	    throw new HeclException("Method " + cmd + " not found for class: " + forclass.getName());
	}
	Object[] args = new Object[0];
	try {
	    args = HeclTypeMap.mapArgs(m.getParameterTypes(), argv, 2);

	    Object retval = m.invoke(o, args);
	    return HeclTypeMap.mapRetval(m, retval);
	} catch (InvocationTargetException e) {
	    String msg = "Problem invoking " + o.getClass().getName() + " " + cmd + "/" + m.getName() + " with arguments: ";
	    for (Thing t : argv) {
		msg += t.toString() + " ";
	    }
	    msg += " (Translated to:) ";
	    for (Object eo : args) {
		msg += eo.toString() + " ";
	    }
	    msg += " " + e.getTargetException().toString();
	    throw new HeclException(msg);
	} catch (Exception e) {
	    throw new HeclException("Reflector.evaluate error :" + e.toString());
	}
    }

    private Constructor selectConstructor(Thing[] argv)
	throws HeclException {

	Constructor[] constructors = forclass.getConstructors();

	if (constructors == null) {
	    throw new HeclException(forclass.toString() + " has no constructors!");
	}

	for (Constructor c : constructors) {
	    Class[] javaparams = c.getParameterTypes();
	    if(javaparams.length != argv.length) {
		continue;
	    }
	    if (matchParams(javaparams, argv, 0)) {
		return c;
	    }
	}
	return null;
    }

    private Method selectMethod(String cmd, Thing[] argv)
        throws HeclException {

	Vector<Method> v = ((Vector)methodnames.get(cmd.toLowerCase()));

	if (v == null) {
	    throw new HeclException("Method " + cmd + " not found for class" + forclass.toString());
	}

	Method[] methods = v.toArray(new Method[v.size()]);
	/* Match the signatures with the correct number first. */
	for (Method m : methods) {
	    Class[] javaparams = m.getParameterTypes();
	    if(javaparams.length != argv.length - 2) {
		continue;
	    }

	    if (matchParams(javaparams, argv, 2)) {
		return m;
	    }
	}
	throw new HeclException("no method matched " + cmd );
    }

    protected boolean matchParams(Class[] javaparams, Thing[] argv, int offset)
        throws HeclException {

	int i = 0;
	boolean match = true;
	for (Class c : javaparams) {
	    match = false;

	    /* Tweak inparam according to the constant table we've
	     * been passed. */
	    if (constnames.containsKey(argv[i + offset].toString())) {
		argv[i + offset] = (Thing)constnames.get(argv[i + offset].toString());
	    }

	    String javaparmt = c.getSimpleName();
	    String heclparmt = argv[i + offset].getVal().thingclass();

	    if (javaparmt.equals("int") || javaparmt.equals("boolean")) {
		if (heclparmt.equals("int")) {
		    match = true;
		}
	    } else if (javaparmt.equals("long")) {
		if (heclparmt.equals("long")) {
		    match = true;
		}
	    } else if ((javaparmt.equals("CharSequence") ||
			javaparmt.equals("String"))) {
		if (heclparmt.equals("string")) {
		    match = true;
		}
	    } else if (heclparmt.equals("object")) {
		match = true;
	    }
	    i ++;
	    if (match == false)
		break;
	}
	return match;
    }

}
