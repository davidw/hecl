/* Copyright 2006 David N. Welton

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

import java.util.Enumeration;
import java.util.Vector;

class InterpCmds {

    public static final int SET = 1;
    public static final int UNSET = 2;
    public static final int PROC = 3;
    public static final int RENAME = 4;
    public static final int EVAL = 5;
    public static final int GLOBAL = 6;

    public static final int INTROSPECT = 7;

    public static final int RETURN = 8;

    public static final int CLASSNAME = 9;

    static void dispatch(int cmd, Interp interp, Thing[] argv) throws HeclException {
	switch (cmd) {
	    case SET:
		if (argv.length == 3) {
		    interp.setVar(argv[1], argv[2]);
		}
		interp.setResult(interp.getVar(argv[1]));
		return;

	    case UNSET:
		interp.unSetVar(argv[1]);
		return;

	    case PROC:
		if (argv.length != 4) {
		    throw HeclException.createWrongNumArgsException(
			argv, 1, "procname argumentnames code");
		}

		interp.commands.put(argv[1].toString(), new Proc(argv[2], argv[3]));
		return;

	    case RENAME:
		interp.cmdRename(argv[1].toString(), argv[2].toString());
		return;

	    case EVAL:
		interp.eval(argv[1]);
		return;

	    case GLOBAL:
		for (int i = 1; i < argv.length; i ++) {
		    String varname = argv[i].toString();
		    Thing newThing = null;

		    if (!interp.existsVar(varname, 0)) {
			newThing = new Thing("");
		    } else {
			/* If it already exists, make a copy of it that is no
			 * longer marked to be copied. */
			Thing globalthing = interp.getVar(varname, 0);
			newThing = globalthing.deepcopy();
		    }
		    interp.setVar(varname, newThing, 0);
		    interp.setVar(argv[i], newThing);
		}
		return;

	    case INTROSPECT:
		String subcmd = argv[1].toString();
		Vector results = new Vector();
		if (subcmd.equals("commands")) {
		    for (Enumeration e = interp.commands.keys(); e.hasMoreElements();) {
			Thing t = new Thing((String) e.nextElement());
			results.addElement(t);
		    }
		    interp.setResult(ListThing.create(results));
		    return;
		}

	    case RETURN:
		if (argv.length > 1) {
		    interp.setResult(argv[1]);
		}
		throw new HeclException(HeclException.RETURN);

		/* Gets the class name of the RealThing behind the
		 * Thing in question. */
	    case CLASSNAME:
		Class c = argv[1].val.getClass();
		interp.setResult(new Thing(c.getName()));
		return;
	}
    }
}
