/* Copyright 2004 David N. Welton

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

/**
 * <code>Proc</code> is the class behind the "proc" command.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class Proc implements Command {
    private Thing vars;
    private Thing code;

    public Proc(Thing cmdvars, Thing cmdcode) {
	vars = cmdvars;
	code = cmdcode;
    }

    public void cmdCode (Interp interp, Thing [] argv)
	throws HeclException {
	Vector varnames = vars.toList();
	Eval eval = new Eval();
	int i = 0;

	interp.stackIncr();
	for (i = 0; i < varnames.size(); i++) {
	    if (i == argv.length - 1) {
		interp.stackDecr();
		throw new HeclException(
		    "proc " + argv[0] + " doesn't have enough arguments");
	    }
	    interp.setVar(varnames.elementAt(i).toString(), argv[i + 1]);
	}

	if (i != argv.length - 1) {
	    interp.stackDecr();
	    throw new HeclException(
		"proc " + argv[0] + " has too many arguments");
	}

	eval.eval(interp, code);
	interp.stackDecr();
    }
}
