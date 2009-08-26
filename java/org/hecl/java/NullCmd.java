/* Copyright 2008 David N. Welton - DedaSys LLC - http://www.dedasys.com

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
 * The <code>NullCmd</code> class implements the "null" command,
 * returning an ObjectThing containing 'null'.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class NullCmd implements org.hecl.Command {

    public NullCmd() {
    }

    public Thing cmdCode(Interp interp, Thing[] argv) throws HeclException {
	return ObjectThing.create(null);
    }

    public static void load(Interp interp)
	throws HeclException {
	interp.addCommand("null", new NullCmd());
    }

    public static void unload(Interp interp) {
	interp.removeCommand("null");
    }
}
