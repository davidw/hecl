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
 * The <code>CodeThing</code> class implements a chunk of "compiled"
 * code including multiple "Stanzas", or individual commands.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

public class CodeThing implements RealThing {
    /* The number of lines of commands. */
    private Vector stanzas;
    /* Mark this for substitution or not. */
    public boolean marksubst = false;

    CodeThing() {
	stanzas = new Vector();
    }

    private static void setCodeFromAny(Interp interp, Thing thing)
	throws HeclException {
	RealThing realthing = thing.val;



	/* FIXME - SubstThing?  */
	if (realthing instanceof CodeThing) {
	    return;
	}
	CodeThing newthing = null;
	Parse hp = new Parse(interp, thing.toString());
	newthing = hp.parseToCode();
	thing.setVal(newthing);
    }


    public static CodeThing get(Interp interp, Thing thing)
	throws HeclException {
	setCodeFromAny(interp, thing);
	return (CodeThing)thing.val;
    }

    public RealThing deepcopy() {
	/* FIXME - not ok.  */
	return new CodeThing();
    }

    protected static Thing doCodeSubst(Interp interp, Thing thing)
	throws HeclException {
	RealThing realthing = thing.val;
	Thing newthing = null;

	//System.out.println("CODE");
	if (((CodeThing) realthing).marksubst) {
	    Eval.eval(interp, thing);
	    newthing = interp.getResult();
	} else {
	    newthing = thing;
	}
	return newthing;
    }

    protected static Thing doGroupSubst(Interp interp, Thing thing)
	throws HeclException {
	RealThing realthing = thing.val;
	Thing newthing = null;

	StringBuffer result = new StringBuffer("");
	Vector v = GroupThing.get(thing);

	/* As a special case, one element groups get turned into
	 * regular things. */
	if (v.size() == 1) {
	    StringThing.get(thing);
	    return thing;
	} else {
	    for (Enumeration e = v.elements();
		 e.hasMoreElements(); ) {
		Thing t = (Thing)e.nextElement();

		realthing = t.val;
		if (realthing instanceof GroupThing) {
		    result.append(doGroupSubst(interp, t).toString());
		} else if (realthing instanceof CodeThing) {
		    result.append(doCodeSubst(interp, t).toString());
		} else {
		    result.append(t.toString());
		    //System.out.println("OTHER");
		    //	newargv[i] = argv[i];
		}
//	    result.append(doGroupSubst(interp, t));
	    }
	}
	return new Thing(new StringThing(result));
    }


    /**
     * The <code>addStanza</code> method adds a new command and its
     * arguments.
     *
     * @param command <code>Command</code> value
     * @param argv <code>Thing[]</code> value
     */

    public void addStanza(Command command, Thing[] argv) {
	Stanza sz = new Stanza(command, argv);
	stanzas.addElement(sz);
	//System.out.println("ADDING : " + sz.toString() + "</ADDING>");
	// (new Throwable()).printStackTrace();
    }

    /**
     * The <code>run</code> method runs the CodeThing.
     *
     * @param interp <code>Interp</code> value
     * @exception HeclException if an error occurs
     */
    public void run(Interp interp) throws HeclException {
	//System.out.println("RUNNING: " + this.toString() + "</RUNNING>");
	for (Enumeration e = stanzas.elements(); e.hasMoreElements(); ) {
	    Stanza s = (Stanza)e.nextElement();
	    s.run(interp);
	}
    }

    /**
     * The <code>toString</code> method returns a String
     * representation of the commands it represents.
     *
     * @return a <code>String</code> value.
     */
    public String toString() {
	StringBuffer out = new StringBuffer();
	for (Enumeration e = stanzas.elements(); e.hasMoreElements(); ) {
	    Stanza s = (Stanza)e.nextElement();
//	    out.append("[");
	    out.append(s.toString());
//	    out.append("]\n");
	}
	return out.toString();
    }

    /**
     * The <code>Stanza</code> class represents one command.
     *
     * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
     * @version 1.0
     */

    class Stanza {
	private Command command = null;
	private Thing[] argv = null;

	Stanza(Command newcmd, Thing[] newargv) {
	    command = newcmd;
	    argv = newargv;
	}

	/**
	 * The <code>run</code> method runs the Stanza.
	 *
	 * @param interp <code>Interp</code> value
	 * @exception HeclException if an error occurs
	 */
	public void run(Interp interp) throws HeclException {
	    RealThing realthing = null;
	    Thing[] newargv = new Thing[argv.length];

	    if (command == null) {
		String cmdName = null;
		realthing = argv[0].val;
		/* If the argv[0] is a substitution waiting to
		 * happen, substitute it to get the name. */
		if (realthing instanceof CodeThing) {
		    cmdName = doCodeSubst(interp, argv[0]).toString();
		} else {
		    cmdName = argv[0].toString();
		}
		command = interp.getCmd(cmdName);
		if (command == null) {
		    //(new Throwable()).printStackTrace();
		    throw new HeclException("Command " + cmdName + " does not exist");
		}
	    }

	    /* Create new array.  Run args that are SUBST or GROUP
	     * types, use references to others. */
	    for (int i = 0; i < argv.length; i ++) {
		realthing = argv[i].val;
		if (realthing instanceof GroupThing) {
		    newargv[i] = doGroupSubst(interp, argv[i]);
		} else if (realthing instanceof CodeThing) {
		    newargv[i] = doCodeSubst(interp, argv[i]);
		} else {
		    //System.out.println("OTHER");
		    newargv[i] = argv[i];
		}

		//newargv[i] = doSubst(interp, argv[i]);
	    }

/*      	    System.out.println("COMMAND ");
	    for (int i = 0; i < newargv.length; i ++) {
		Thing.printThing(newargv[i]);
		//System.out.println(i + ": " + newargv[i]);
	    }   */

	    try {
		command.cmdCode(interp, newargv);
	    } catch (HeclException e) {
		/* Uh oh, an "issue"! */
		if (newargv[0] != null) {
		    /* Let the exception know where we are. */
		    e.where(newargv[0].toString());
		}
		switch (e.code) {
		    case HeclException.BREAK:
			throw e;
		    case HeclException.CONTINUE:
			throw e;
		    case HeclException.RETURN:
			throw e;
		    case HeclException.ERROR:
			throw e;
		}
	    } catch(Exception e) {
		/* Transform a Java exception into a form more palatable
		 * to Hecl. */
		Vector errors = new Vector();
		StackTraceElement stack[] = e.getStackTrace();
		for (int j = 0; j < stack.length; j ++) {
		    errors.addElement(new Thing(stack[j].toString()));
		}
		Thing newexception = new Thing(new ListThing(errors));
		throw new HeclException(newexception.toString());
	    }

	}

	/**
	 * The <code>toString</code> method turns a Stanza into a
	 * string.
	 *
	 * @return a <code>String</code> value
	 */
	public String toString() {
	    StringBuffer out = new StringBuffer(argv[0].toString());
	    for (int i = 1; i < argv.length; i ++) {
		out.append(" " + argv[i].toString());
	    }
	    return out.toString();
	}
    }
}
