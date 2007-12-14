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

import java.util.Enumeration;
import java.util.Vector;

import org.hecl.HeclException;
import org.hecl.IntThing;
import org.hecl.Interp;
import org.hecl.Properties;
import org.hecl.Thing;

/**
 * The <code>MethodProps</code> class is utilized during the
 * instantiation of classes, where it's possible to do things like:
 * foo -new $bar -text "blah" -color red, where the keys are
 * transformed into methods like setText and setColor, and run with
 * the provided values.  This is sort of hackey, but it works, given
 * that setters with that naming convention are ubiquitous in the Java
 * world.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class MethodProps extends Properties {

    public MethodProps() {
	super();
    }

    public void evalProps(Interp interp, Object target, Reflector ref)
	throws HeclException {

	Enumeration names = props.keys();
	while(names.hasMoreElements()) {
	    String name = (String)names.nextElement();
	    String methodname = "set" + name.substring(1);
	    /* First two need to be null to replace cmdname and subcmdname. */
	    Thing[] argv = { null, null, getProp(name) };
	    ref.evaluate(target, methodname, argv);
	}
    }
}
