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
import java.util.Hashtable;
import java.util.Vector;

/**
 * <code>ControlCmds</code> implements 'control' constructs like if,
 * while, for, foreach, and so on.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
class ControlCmds extends Operator {

    public static final int IF = 1;
    public static final int FOR = 2;
    public static final int FOREACH = 3;
    public static final int WHILE = 4;
    public static final int BREAK = 5;
    public static final int CONTINUE = 6;

    public Thing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	Thing res = null;
	
	switch (cmd) {
	  case IF:
	    /* The 'if' command. */
	    res = interp.eval(argv[1]);
	    if (res != null && Thing.isTrue(res)) {
		return interp.eval(argv[2]);
	    }

	    /*
	     * We loop through to capture all else if...else if...else
	     * possibilities.
	     */
	    if (argv.length > 3) {
		for (int i = 3; i < argv.length; i += 3) {
		    if (argv[i].toString().equals("else")) {
			/* It's an else block, evaluate it and return. */
			if(argv.length != i+2)
			    throw new HeclException("malformed \"else\"");
			return interp.eval(argv[i + 1]);
		    } else if (argv[i].toString().equals("elseif")) {
			/*
			 * elseif - check and see if the condition is true, if so
			 * evaluate it and return.
			 */
			if(i+3 > argv.length)
			    throw new HeclException("malformed \"elseif\"");
			res = interp.eval(argv[i + 1]);
			if (res != null && Thing.isTrue(res)) {
			    return interp.eval(argv[i + 2]);
			}
		    } else
			throw new HeclException("missing \"else/elseif\" in \"if\"");
		}
	    }
	    break;

	  case FOR:
	    /* The 'for' command. */
	    /* start */
	    interp.eval(argv[1]);
	    /* test */
	    while (Thing.isTrue(interp.eval(argv[2]))) {
		try {
		    /* body */
		    interp.eval(argv[4]);
		} catch (HeclException e) {
		    if (e.code.equals(HeclException.BREAK)) {
			break;
		    } else if (e.code.equals(HeclException.CONTINUE)) {
		    } else {
			throw e;
		    }
		}
		/* next */
		interp.eval(argv[3]);
	    }
	    break;

	  case FOREACH:
	    /* The 'foreach' command. */
	    Vector list = ListThing.get(argv[2]);
	    if (list.size() == 0) {
		break;
	    }
	    Vector varlist = ListThing.get(argv[1]);
	    int i = 0;
	    boolean cont = true;

	    //System.out.println("argv2 is " + argv[2] + " copy is " + argv[2].copy);

	    while (cont) {
		/*
		 * This is for foreach loops where we have more than one variable to
		 * set: foreach {m n} $somelist { code ... }
		 */
		for (Enumeration e = varlist.elements(); e.hasMoreElements();) {
		    if (cont == false) {
			throw new HeclException(
			    "Foreach argument list does not match list length");
		    }

		    Thing element = (Thing) list.elementAt(i);
		    element.copy = true; /* Make sure that we don't fiddle
					  * with the original value. */
		    String varname = ((Thing) e.nextElement()).toString();

		    // System.out.println("set " +varname+ " to " +element+ " copy: " + element.copy);

		    interp.setVar(varname, element);
		    i++;
		    if (i == list.size()) {
			cont = false;
		    }
		}

		try {
		    res = interp.eval(argv[3]);
		} catch (HeclException e) {
		    if (e.code.equals(HeclException.BREAK)) {
			break;
		    } else if (e.code.equals(HeclException.CONTINUE)) {
		    } else {
			throw e;
		    }
		}
	    }
	    break;

	  case WHILE:
	    /* The 'while' command. */
	    while (Thing.isTrue(interp.eval(argv[1]))) {
		try {
		    interp.eval(argv[2]);
		} catch (HeclException e) {
		    if (e.code.equals(HeclException.BREAK)) {
			break;
		    } else if (e.code.equals(HeclException.CONTINUE)) {
		    } else {
			throw e;
		    }
		}
	    }
	    break;

	  case BREAK:
	    /* The 'break' command. */
	    throw new HeclException("", HeclException.BREAK);

	  case CONTINUE:
	    /* The 'continue' command. */
	    throw new HeclException("", HeclException.CONTINUE);
	  default:
	    throw new HeclException("Unknown list command '"
				    + argv[0].toString() + "' with code '"
				    + cmd + "'.");
	}
	return null;
    }


    public static void load(Interp ip) throws HeclException {
	Operator.load(ip,cmdtable);
    }


    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip,cmdtable);
    }


    protected ControlCmds(int cmdcode,int minargs,int maxargs) {
	super(cmdcode, minargs, maxargs);
    }

    private static Hashtable cmdtable = new Hashtable();

    /* Creates these commands when this class is loaded. */
    static {
        cmdtable.put("if", new ControlCmds(IF,2,-1));
        cmdtable.put("for", new ControlCmds(FOR,4,4));
        cmdtable.put("foreach", new ControlCmds(FOREACH,3,3));
        cmdtable.put("while", new ControlCmds(WHILE,2,2));
        cmdtable.put("break", new ControlCmds(BREAK,0,0));
        cmdtable.put("continue", new ControlCmds(CONTINUE,0,0));
    }
}
