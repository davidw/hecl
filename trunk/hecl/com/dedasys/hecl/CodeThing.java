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

    /**
     * The <code>setCodeFromAny</code> method makes the Thing passed
     * to it into a CodeThing representation.
     *
     * @param interp an <code>Interp</code> value
     * @param thing a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    private static void setCodeFromAny(Interp interp, Thing thing)
	throws HeclException {
	RealThing realthing = thing.val;

	/* FIXME - SubstThing?  */

	if (!(realthing instanceof CodeThing)) {
	    CodeThing newthing = null;
	    Parse hp = new Parse(interp, thing.toString());
	    newthing = hp.parseToCode();
	    thing.setVal(newthing);
	}
    }

    /**
     * <code>get</code> returns a CodeThing object from any kind of
     * Thing - or returns an error.
     *
     * @param interp an <code>Interp</code> value
     * @param thing a <code>Thing</code> value
     * @return a <code>CodeThing</code> value
     * @exception HeclException if an error occurs
     */
    public static CodeThing get(Interp interp, Thing thing)
	throws HeclException {
	setCodeFromAny(interp, thing);
	return (CodeThing)thing.val;
    }

    public RealThing deepcopy() {
	/* FIXME - not ok.  */
	return new CodeThing();
    }

    /**
     * <code>doCodeSubst</code> takes a code Thing and runs it,
     * returning the result.  This is used for substitution in
     * situations like this: "foo [bar] baz", where the substitution
     * needs to be run every time, but the block can't be broken up.
     * doCodeSubst operates on the [bar] word in the above case.
     *
     * @param interp an <code>Interp</code> value
     * @param thing a <code>Thing</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    protected static Thing doCodeSubst(Interp interp, Thing thing)
	throws HeclException {
	RealThing realthing = thing.val;
	Thing newthing = null;

	if (((CodeThing) realthing).marksubst) {
	    Eval.eval(interp, thing);
	    newthing = interp.result;
	} else {
	    newthing = thing;
	}
	return newthing;
    }

    /**
     * <code>doSubstSubst</code> runs substitutions on things of the
     * SubstThing type, which means $foo or &foo in Hecl.
     *
     * @param interp an <code>Interp</code> value
     * @param thing a <code>Thing</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    protected static Thing doSubstSubst(Interp interp, Thing thing)
	throws HeclException {
	return SubstThing.get(interp, thing);
    }

    /**
     * <code>doGroupSubst</code> runs substitutions on 'groups' of
     * things, such as "foo $foo [foo]".  The group can't be broken
     * up, so it needs to be substituted together by subst'ing the
     * individual components.
     *
     * @param interp an <code>Interp</code> value
     * @param thing a <code>Thing</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
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
		} else if (realthing instanceof SubstThing) {
		    result.append(doSubstSubst(interp, t).toString());
		} else if (realthing instanceof CodeThing) {
		    result.append(doCodeSubst(interp, t).toString());
		} else {
		    result.append(t.toString());
		}
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
     * The <code>Stanza</code> class represents one command.  A
     * CodeThing object may have several Stanzas.
     *
     * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
     * @version 1.0
     */

    class Stanza {
	private Command command = null;
	private Thing[] argv = null;

 	private Thing[] nav1 = new Thing[1];
	private Thing[] nav2 = new Thing[2];
	private Thing[] nav3 = new Thing[3];
	private Thing[] newargv;

	/**
	 * Creates a new <code>Stanza</code> instance, taking a
	 * Command and its arguments as input.
	 *
	 * @param newcmd a <code>Command</code> value
	 * @param newargv a <code>Thing[]</code> value
	 */
	Stanza(Command newcmd, Thing[] newargv) {
	    command = newcmd;
	    argv = newargv;
	}

	/**
	 * The <code>run</code> method runs the Stanza.  In order to
	 * avoid creating a new newargv each time, the most common
	 * cases are preallocated.
	 *
	 * @param interp <code>Interp</code> value
	 * @exception HeclException if an error occurs
	 */
	public void run(Interp interp) throws HeclException {
	    RealThing realthing = null;

	    /* These are the three most common argv lengths. */
 	    switch (argv.length) {
		case 1:
		    newargv = nav1;
		    break;
		case 2:
		    newargv = nav2;
		    break;
		case 3:
		    newargv = nav3;
		    break;
		default:
		    newargv = new Thing[argv.length];
	    }

	    realthing = argv[0].val;
	    if (command == null) {
		String cmdName = null;
		/* If the argv[0] is a substitution waiting to
		 * happen, substitute it to get the name. */
		if (realthing instanceof CodeThing) {
		    cmdName = doCodeSubst(interp, argv[0]).toString();
		} else if (realthing instanceof GroupThing) {
		    cmdName = doGroupSubst(interp, argv[0]).toString();
		} else if (realthing instanceof SubstThing) {
		    cmdName = doSubstSubst(interp, argv[0]).toString();
		} else {
		    cmdName = argv[0].toString();
		}
		command = interp.getCmd(cmdName);
		if (command == null) {
		    throw new HeclException("Command " + cmdName + " does not exist");
		}
	    }

	    /* Fill in the elements of the new argv - doing
	     * substitutions and running code where needs be. */
	    for (int i = 0; i < argv.length; i ++) {
		realthing = argv[i].val;
		if (realthing instanceof GroupThing) {
		    newargv[i] = doGroupSubst(interp, argv[i]);
		} else if (realthing instanceof SubstThing) {
		    newargv[i] = doSubstSubst(interp, argv[i]);
		} else if (realthing instanceof CodeThing) {
		    newargv[i] = doCodeSubst(interp, argv[i]);
		} else {
		    newargv[i] = argv[i];
		}
	    }

	 /* System.out.println("COMMAND v");
	    for (int i = 0; i < newargv.length; i ++) {
		Thing.printThing(newargv[i]);
	    }
      	    System.out.println("ENDCOMMAND ^ ");  */

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
