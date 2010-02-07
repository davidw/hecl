/* Copyright 2010 David N. Welton, DedaSys LLC

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

package org.hecl;

/**
 * The <code>AnonProc</code> class implements anonymous procedures,
 * created like so: set aproc [proc {x} { puts $x }]
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class AnonProc implements ClassCommand {

    public Thing method(Interp interp, ClassCommandInfo context, Thing[] argv)
	throws HeclException {
	Thing[] newargv = new Thing[argv.length];
	newargv[0] = new Thing("anonproc");
	for (int i = 1; i < argv.length; i++) {
	    newargv[i] = argv[i];
	}

	RealThing rt = argv[0].getVal();
	Proc proc = (Proc)((ObjectThing)rt).get();
	return proc.cmdCode(interp, newargv);
    }
}
