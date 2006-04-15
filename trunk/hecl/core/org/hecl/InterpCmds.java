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

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * The <code>InterpCmds</code> implements various Hecl commands that
 * deal with the state of the interpreter.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
class InterpCmds extends Operator {
    public static final int SET = 1;
    public static final int UNSET = 2;
    public static final int PROC = 3;
    public static final int RENAME = 4;
    public static final int EVAL = 5;
    public static final int GLOBAL = 6;
    public static final int INTROSPECT = 7;
    public static final int RETURN = 8;
    public static final int CATCH = 9;
    public static final int EXIT = 10;
    public static final int UPCMD = 11;
    public static final int TIMECMD = 12;

    public static final int CLASSINFO = 20;


    public RealThing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	Thing result = null;
	int retval = 0;

	switch (cmd) {
	    case SET:
		if (argv.length == 3) {
		    interp.setVar(argv[1], argv[2]);
		}
		interp.setResult(interp.getVar(argv[1]));
		break;

	    case UNSET:
		interp.unSetVar(argv[1]);
		break;

	    case PROC:
		interp.commands.put(argv[1].toString(), new Proc(argv[2], argv[3]));
		break;

	    case RENAME:
		interp.cmdRename(argv[1].toString(), argv[2].toString());
		break;

	    case EVAL:
		interp.eval(argv[1]);
		break;

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
		break;

	    case INTROSPECT:
		String subcmd = argv[1].toString();
		Vector results = new Vector();

		if (subcmd.equals("commands")) {
		    for (Enumeration e = interp.commands.keys(); e.hasMoreElements();) {
			Thing t = new Thing((String) e.nextElement());
			results.addElement(t);
		    }
		    return new ListThing(results);
		}
		break;

	    case RETURN:
		if (argv.length > 1) {
		    interp.setResult(argv[1]);
		}
		throw new HeclException("", HeclException.RETURN);

	    case CATCH:
		try {
		    interp.eval(argv[1]);
		    result = interp.result;
		    retval = 0;
		} catch (HeclException e) {
		    result = e.getStack();
		    retval = 1;
		}

		if (argv.length == 3) {
		    interp.setVar(argv[2].toString(), result);
		}
		return retval != 0 ? IntThing.ONE : IntThing.ZERO;

	    case EXIT:
		retval = 0;
		if (argv.length > 1) {
		    retval = NumberThing.asNumber(argv[1]).intValue();
		}
		System.exit(retval);
		break;

	    case UPCMD:
		Hashtable save = interp.stackDecr();
		interp.eval(argv[1]);
		interp.stackPush(save);
		break;

	    case TIMECMD:
		int times = 1;

		if (argv.length > 2) {
		    times = NumberThing.asNumber(argv[2]).intValue();
		}
		long then = new Date().getTime();
		while (times > 0) {
		    interp.eval(argv[1]);
		    times--;
		}
		return new LongThing(new Date().getTime() - then);

	    case CLASSINFO:
		return new StringThing("<"+argv[1].val.thingclass()+">");

	    default:
		throw new HeclException("Unknown interp command '"
					+ argv[0].toString() + "' with code '"
					+ cmd + "'.");
	}
	return null;
    }


    public static void load(Interp ip) throws HeclException {
	Operator.load(ip);
    }


    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip);
    }


    private InterpCmds(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }


    static {
	cmdtable.put("set", new InterpCmds(SET, 1 ,2));
        cmdtable.put("unset", new InterpCmds(UNSET, 1, 1));
        cmdtable.put("proc", new InterpCmds(PROC, 3, 3));
        cmdtable.put("rename", new InterpCmds(RENAME, 2, 2));
        cmdtable.put("eval", new InterpCmds(EVAL, 1, 1));
        cmdtable.put("global", new InterpCmds(GLOBAL, 0, -1));
        cmdtable.put("intro", new InterpCmds(INTROSPECT, 1, -1));
        cmdtable.put("return", new InterpCmds(RETURN, 0, 1));
        cmdtable.put("catch", new InterpCmds(CATCH, 1, 2));
        cmdtable.put("exit", new InterpCmds(EXIT, 0, 1));
        cmdtable.put("upeval", new InterpCmds(UPCMD, 1, 1));
        cmdtable.put("time", new InterpCmds(TIMECMD, 1, 2));

        cmdtable.put("classof", new InterpCmds(CLASSINFO, 1, 1));
    }

}
