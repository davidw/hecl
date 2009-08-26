/* Copyright 2004-2006 David N. Welton

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

import java.util.*;

/**
 * <code>Proc</code> is the class behind the "proc" command.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class Proc implements Command {
    private Thing vars;
    private Thing code;

    /**
     * Creates a new <code>Proc</code> instance, with the variable names in
     * cmdvars, and the actual code in cmdcode.
     *
     * @param cmdvars a <code>Thing</code> value
     * @param cmdcode a <code>Thing</code> value
     */
    public Proc(Thing cmdvars, Thing cmdcode) {
        vars = cmdvars;
        code = cmdcode;
    }

    private static final String varargvarname = "args";

    public Thing cmdCode(Interp interp, Thing[] argv) throws HeclException {
        Vector varnames = ListThing.get(vars);
        int i = 0;
	Vector vargvals = null;

        /* Push a new frame onto the stack. */
        interp.stackIncr();

        /* Create the argument variables. */
	int argc = varnames.size();

	/* If the last element of the variable names is called 'args',
	 * then it is a list that can accumulate any 'extra' args
	 * passed in to the proc. */
	if (argc > 0 && ((Thing) varnames.elementAt(argc - 1)).toString().equals(varargvarname)) {
	    vargvals = new Vector();
	    argc --;
	}
	HeclException he = null;
	if (argv.length - 1 < argc) {
	    he = new HeclException("proc " + argv[0]
				    + " doesn't have enough arguments");
	} else if (argv.length - 1 > argc && vargvals == null) {
            he = new HeclException("proc " + argv[0]
                    + " has too many arguments");
	}
	if (he != null) {
	    interp.stackDecr();
	    throw he;
	}

	/* Set the variables from argv.  Add one to argv, because
	 * argv0 is the name of the proc itself. */
        for (i = 0; i < argc; i++) {
            interp.setVar(((Thing) varnames.elementAt(i)).toString(),
			  argv[i + 1]);
        }

	/* Hoover up anything left over as varargs. */
	for (; i < argv.length - 1; i++ ) {
	    vargvals.addElement(argv[i + 1]);
	}
	if(vargvals != null)
	    interp.setVar(varargvarname, ListThing.create(vargvals));

	/* We actually run the code here. */
	Thing res = null;
        try {
            res = interp.eval(code);
        } catch (HeclException e) {
            if (e.code != HeclException.RETURN) {
		interp.stackDecr();
                throw e;
            } else {
		res = e.value;
	    }
        }

        /* We're done, pop the stack. */
        interp.stackDecr();
        //Hashtable ht = interp.stackDecr();
	//ht = null;
	return res;
    }

    /**
     * <code>getCode</code> returns the proc's code.
     *
     * @return a <code>Thing</code> value
     */
    public Thing getCode() {
	return code;
    }
}
