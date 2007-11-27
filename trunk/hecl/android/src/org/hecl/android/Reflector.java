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

	Object[] args = new Object[0];
	Constructor selected = null;
	try {
	    Constructor[] constructors = forclass.getConstructors();

	    if (constructors == null) {
		throw new HeclException(forclass.toString() + " has no constructors!");
	    }

	    for (Constructor c : constructors) {
		Class[] javaparams = c.getParameterTypes();

		if(javaparams.length != argv.length) {
		    continue;
		}

		args = mapParams(javaparams, argv, 0);
		if (args != null) {
		    selected = c;
		    break;
		}
	    }
	    if (selected == null) {
		throw new HeclException("Couldn't find a constructor for class:" + forclass.getName());
	    }
	    return ObjectThing.create(selected.newInstance(args));
	} catch (InvocationTargetException e) {
	    String msg = "Problem invoking " + forclass.getName() + " constructor " + selected.getName() + " with arguments: ";
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

	Object[] args = new Object[0];
	Method selected = null;
	try {
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

		args = mapParams(javaparams, argv, 2);
		if (args != null) {
		    selected = m;
		    break;
		}
	    }
	    if (selected == null) {
		throw new HeclException("No method matched " + cmd + " for class " + forclass.getName());
	    }
	    Object retval = selected.invoke(o, args);
	    return mapRetval(selected, retval);
	} catch (InvocationTargetException e) {
	    String msg = "Problem invoking " + o.getClass().getName() + " " + cmd + "/" + selected.getName() + " with arguments: ";
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

    protected Object[] mapParams(Class[] outparams, Thing[] argv, int offset)
	throws HeclException {

	if(outparams.length != argv.length - offset) {
	    /* No match */
	    return null;
	}

	Object[] outobjs = new Object[outparams.length];
	Class c = null;
	for (int i = 0; i < outparams.length; i++) {
	    Thing inparam = argv[i + offset];
	    Class outparam = outparams[i];
	    String javaclassname = outparam.getSimpleName();

	    /* Tweak inparam according to the constant table we've
	     * been passed. */
	    String val = inparam.toString();
	    if (constnames.containsKey(val)) {
		inparam = (Thing)constnames.get(val);
	    }
	    String heclparmt = inparam.getVal().thingclass();

/* 	    Log.v("mapParams", "javatype: " + outparam.getName() + " hecltype: " +
		  heclparmt + " heclval: " + inparam.toString());  */

	    if (outparam == boolean.class || outparam == int.class) {
		if (heclparmt.equals("int")) {
		    outobjs[i] = IntThing.get(inparam);
		} else {
		    outobjs = null;
		}
	    } else if (outparam == long.class) {
		if (heclparmt.equals("long")) {
		    outobjs[i] = LongThing.get(inparam);
		} else {
		    outobjs = null;
		}
	    } else if (outparam == CharSequence.class ||
		       outparam == String.class) {
		if (heclparmt.equals("string")) {
		    outobjs[i] = inparam.toString();
		} else {
		    outobjs = null;
		}
	    } else if (javaclassname.equals("int[]")) {
		Vector v = ListThing.get(inparam);
		int[] ints = new int[v.size()];
		for (int j = 0; j < v.size(); j++) {
		    ints[j] = IntThing.get((Thing)v.elementAt(j));
		}
		outobjs[i] = ints;
	    } else if (javaclassname.equals("Object[]")) {
		Thing[] things = ListThing.getArray(inparam);
		Object[] objects = new Object[things.length];
		int j = 0;
		for (Thing t : things) {
		    /* This is a bit of a hack.  If they're objects,
		     * move them on through as objects, otherwise, as
		     * strings. */
		    if (t.getVal().thingclass().equals("object")) {
			objects[j] = ObjectThing.get(t);
		    } else {
			objects[j] = t.toString();
		    }
		    j++;
		}
		outobjs[i] = objects;
	    } else if (heclparmt.equals("object")) {
		outobjs[i] = ObjectThing.get(inparam);
	    } else {
		/* No match, return null. */
		outobjs = null;
	    }
	}
	return outobjs;
    }

    private  Thing mapRetval(Method m, Object o) {
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
