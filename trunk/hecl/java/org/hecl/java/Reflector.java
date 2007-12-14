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

package org.hecl.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

import java.util.Hashtable;
import java.util.Vector;

import org.hecl.DoubleThing;
import org.hecl.HeclException;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.LongThing;
import org.hecl.ObjectThing;
import org.hecl.Thing;

/**
 * The <code>Reflector</code> class maps between Java types and Hecl
 * types in order to make it possible to call Java methods from Hecl.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class Reflector {
    private Class forclass;
    private Method[] methods;
    private Hashtable methodnames;
    private Hashtable constnames;

    /**
     * Creates a new <code>Reflector</code> instance.
     *
     * @param classname a <code>String</code> value describing the
     * full name (including package) of a Java class.
     * @exception HeclException if an error occurs
     */
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
			constnames.put(name, ObjectThing.create(f.get(forclass)));
		    }
		}
	    }
	} catch (Exception e) {
	    throw new HeclException(e.toString());
	}
    }

    /**
     * The <code>instantiate</code> method is called to create an
     * instance of a class (new, in other words).
     *
     * @param argv a <code>Thing</code> value that is mapped onto Java
     * parameters and passed to the appropriate constructor for the
     * class.
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
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

    /**
     * <code>getField</code> fetches a constant field value.
     *
     * @param name a <code>String</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    public Thing getField(String name)
	throws HeclException {

	Thing result =  (Thing)constnames.get(name);
	if (result == null) {
	    throw new HeclException("No field '" + name + "'");
	}
	return result;
    }


    /**
     * The <code>evaluate</code> method takes a target object to
     * operate on, a methodname, and some Hecl values, and attempts to
     * find and call a Java method with the supplied values.
     *
     * @param o an <code>Object</code> value
     * @param cmd a <code>String</code> value
     * @param argv a <code>Thing</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
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
	    Class[] javaparams = null;
	    for (Method m : methods) {
		javaparams = m.getParameterTypes();
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
		String msg = "No method matched " + cmd + " for class " + forclass.getName() + " last javaparams tried: ";
		if (javaparams != null) {
		    for (Class c : javaparams) {
			msg += c.getSimpleName() + " ";
		    }
		}
		throw new HeclException(msg);
	    }
	    Object retval = selected.invoke(o, args);
	    return mapRetval(selected, retval);
	} catch (InvocationTargetException e) {
	    String msg = "Problem invoking " + forclass.getName() + " " + cmd + "/" + selected.getName() + " with arguments: ";
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


    /**
     * The <code>mapParams</code> method is where the magic happens -
     * it maps Hecl types/values onto Java types/values.
     *
     * @param outparams a <code>Class</code> value
     * @param argv a <code>Thing</code> value
     * @param offset an <code>int</code> value - where to start
     * looking in argv.
     * @return an <code>Object[]</code> value
     * @exception HeclException if an error occurs
     */
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

	    if (outparam == boolean.class || outparam == Boolean.class) {
		if (heclparmt.equals("int")) {
		    outobjs[i] = IntThing.get(inparam) != 0;
		} else {
		    outobjs = null;
		}
	    } else if(outparam == int.class || outparam == Integer.class) {
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
	    } else if (outparam == Thing.class) {
		/* We can use this to pass Things around directly, to
		 * classes that support it. */
		outobjs[i] = inparam;
	    } else if (heclparmt.equals("object")) {
		/* We are getting an ObjectThing from Hecl... */
		outobjs[i] = ObjectThing.get(inparam);
	    } else if (outparam == Object.class) {
		/* We're not getting an ObjectThing from Hecl, but
		 * Java can take any Object. Give it Things directly.
		 * This is sort of a last resort as more specific is
		 * better. */
		outobjs[i] = inparam;
	    } else {
		/* No match, return null. */
		outobjs = null;
	    }
	}
	return outobjs;
    }

    /**
     * The <code>mapRetval</code> method is the "opposite" of the
     * mapParams method - it maps a returned Java value onto a Hecl
     * Thing, which it then returns.
     *
     * @param m a <code>Method</code> value
     * @param o an <code>Object</code> value
     * @return a <code>Thing</code> value
     */
    private Thing mapRetval(Method m, Object o) {
	Class rtype = m.getReturnType();
	String rtypename = rtype.getSimpleName();
	if (o == null) {
	    return null;
	} else if (rtype == void.class) {
	    return null;
	} else if (rtype == int.class) {
	    return IntThing.create(((Integer)o).intValue());
	} else if (rtype == boolean.class) {
	    boolean val = ((Boolean)o).equals(Boolean.TRUE);
	    return IntThing.create(val ? 1 : 0);
	} else if (rtype == long.class) {
	    return LongThing.create(((Long)o).longValue());
	} else if (rtype == String.class || rtype == CharSequence.class) {
	    return new Thing((String)o);
	} else if (rtypename.equals("int[]")) {
	    return new Thing("FIXME");
	} else if (rtype == Object.class) {
	    if (o.getClass() == Thing.class) {
		/* If we've managed to stash a thing somewhere. */
		return (Thing)o;
	    }
	}
	return ObjectThing.create(o);
    }
}
