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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.Hashtable;
import java.util.Vector;

import android.util.Log;

import org.hecl.HeclException;
import org.hecl.ListThing;
import org.hecl.Thing;

class Reflector {
    private Class forclass;
    private Method[] methods;
    private Hashtable methodnames;

    public Reflector(String classname) throws HeclException {
	try {
	    methodnames = new Hashtable();

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
	} catch (Exception e) {
	    throw new HeclException(e.toString());
	}
    }

    public Thing evaluate(Object o, String cmd, Thing[] argv)
        throws HeclException {

	Method m = selectMethod(cmd, argv);
	if (m == null) {
	    throw new HeclException("Method " + cmd + " not found for class" + forclass.toString());
	}
	Object []args = new Object[0];
	try {
	    args = HeclTypeMap.mapArgs(m, argv);
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
	    throw new HeclException(e.toString());
	}
    }

    private Method selectMethod(String cmd, Thing[] argv)
        throws HeclException {

	Vector<Method> v = ((Vector)methodnames.get(cmd.toLowerCase()));

	if (v == null) {
	    throw new HeclException("Method " + cmd + " not found for class" + forclass.toString());
	}

	Method[] methods = v.toArray(new Method[v.size()]);
	/* Match the signatures with the correct number first. */
	StringBuffer msg = new StringBuffer("");
	for (Method m : methods) {
	    Class[] javaparams = m.getParameterTypes();
	    if(javaparams.length != argv.length - 2) {
		continue;
	    }
	    int i = 0;
	    boolean match = true;
	    for (Class c : javaparams) {
		match = false;
		String javaparmt = c.getSimpleName();
		String heclparmt = argv[i + 2].getVal().thingclass();
		msg.append(javaparmt + "/" + heclparmt);
		msg.append(" ");

		if (heclparmt.equals("int") && (javaparmt.equals("int") ||
		    javaparmt.equals("boolean"))) {
		    match = true;
		} else if (heclparmt.equals("long") && javaparmt.equals("long")) {
		    match = true;
		} else if (heclparmt.equals("string") && (javaparmt.equals("CharSequence") ||
							  javaparmt.equals("String"))) {
		    match = true;
		} else if (heclparmt.equals("object")) {
		    match = true;
		}
		i ++;
		if (match == false)
		    break;
	    }
	    if (match) {
		return m;
	    }
	}
	throw new HeclException("no method matched " + cmd );
    }

}
