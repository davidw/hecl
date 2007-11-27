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

import java.lang.reflect.InvocationTargetException;

import java.util.Enumeration;
import java.util.Vector;

import android.view.ViewGroup.LayoutParams;

import android.util.AttributeSet;
import android.util.Log;
import android.util.XmlPullAttributes;

import android.view.View;

import org.hecl.ClassCommand;
import org.hecl.ClassCommandInfo;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.StringThing;
import org.hecl.Thing;
import java.util.Map;


public class JavaCmd implements ClassCommand, org.hecl.Command {
    private static Vector commands = null;

    private String cmdname = null;
    private Class thisclass = null;
    private Reflector classreflector = null;

    public JavaCmd(String clsname, String cmd)
	throws HeclException {

	classreflector = new Reflector(clsname);
	try {
	    thisclass = Class.forName(clsname);
	} catch (Exception e) {
	    Hecl.logStacktrace(e);
	    throw new HeclException("Error trying to create " + clsname + " : " + e.toString());
	}
    }

    public Thing cmdCode(Interp interp, Thing[] argv) throws HeclException {
	Object view = null;
	try {

	    /* These are for all the attributes like -text -height, etc... */
	    MethodProps mp = new MethodProps();
	    mp.setProps(argv, 2);

	    /* Create a new instance. */
	    Thing newthing = classreflector.instantiate(ListThing.getArray(argv[1]));

	    mp.evalProps(interp, ObjectThing.get(newthing), classreflector);

	    return newthing;
/* 	} catch (InvocationTargetException te) {
	throw new HeclException("Constructor error: " + te.getTargetException().toString());  */
	} catch (Exception e) {
	    Hecl.logStacktrace(e);
	    throw new HeclException(argv[0].toString() + " " +
				    argv[1].toString() + " error " + e.toString());
	}
    }

    public Thing method(Interp interp, ClassCommandInfo context, Thing[] argv)
	throws HeclException {
	if(argv.length > 1) {
	    String subcmd = argv[1].toString().toLowerCase();
	    Object target = ObjectThing.get(argv[0]);
	    return classreflector.evaluate(target, subcmd, argv);
	}
	throw HeclException.createWrongNumArgsException(argv, 2, "Object method [arg...]");
    }

    public Class getCmdClass() {
	return thisclass;
    }

    public String getCmdName() {
	return cmdname;
    }

    public static void load(Interp ip, String cname, String cmd) 
	throws HeclException {

	if (commands == null) {
	    commands = new Vector();
	}
	JavaCmd newjavacmd = new JavaCmd(cname, cmd);
	ip.addCommand(cmd, newjavacmd);
	ip.addClassCmd(newjavacmd.getCmdClass(), newjavacmd);
	commands.add(newjavacmd);
    }

    public static void unload(Interp ip) {
	Enumeration e = commands.elements();
	while(e.hasMoreElements()) {
	    JavaCmd c = (JavaCmd)e.nextElement();
	    ip.removeCommand(c.getCmdName());
	    ip.removeClassCmd(c.getCmdClass());
	}
    }
}
