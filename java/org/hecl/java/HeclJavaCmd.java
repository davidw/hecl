/* Copyright 2007-2009 David N. Welton - DedaSys LLC - http://www.dedasys.com

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

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.ObjectThing;
import org.hecl.StringThing;
import org.hecl.Thing;

/**
 * The <code>HeclJavaCmd</code> class implements the "java" command,
 * which can be used to access, instantiate and call methods of Java
 * classes.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class HeclJavaCmd implements org.hecl.Command {

    public HeclJavaCmd() {
    }

    public Thing cmdCode(Interp interp, Thing[] argv) throws HeclException {
	if (argv.length == 3) {
	    JavaCmd.load(interp, argv[1].toString(), argv[2].toString());
	    return argv[2];
	} else if (argv.length == 2) {
	    JavaCmd.load(interp, argv[1].toString(), null);
	    return new Thing("");
	} else {
	    throw HeclException.createWrongNumArgsException(argv, 1,
							    "JavaClass ?heclcommand?");
	}
    }

    public static void load(Interp interp)
	throws HeclException {
	interp.addCommand("java", new HeclJavaCmd());
    }

    public static void unload(Interp interp) {
	interp.removeCommand("java");
    }
}
