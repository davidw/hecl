/* Copyright 2004-2005 David N. Welton

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

package com.dedasys.hecl;

import java.util.*;
import java.io.*;

/**
 * <code>SourceHereCmd</code> implements the "sourcehere" command,
 * which executes the code in a given external resource.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class SourceHereCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	String filename = argv[1].toString();
	/* FIXME - we need to not have File's in here for J2ME. */
	File fl = new File(interp.getScriptName());
	File newfl = new File(fl.getParent(), filename);

	Eval.eval(interp, interp.getscript(newfl.toString()));
    }
}
