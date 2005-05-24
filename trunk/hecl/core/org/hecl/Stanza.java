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

package org.hecl;

/**
 * The <code>Stanza</code> class represents one command. A CodeThing
 * object may have several Stanzas.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
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
     * Creates a new <code>Stanza</code> instance, taking a Command and
     * its arguments as input.
     * 
     * @param newcmd
     *            a <code>Command</code> value
     * @param newargv
     *            a <code>Thing[]</code> value
     */
    Stanza(Command newcmd, Thing[] newargv) {
	command = newcmd;
	argv = newargv;
    }

    /**
     * The <code>run</code> method runs the Stanza. In order to avoid
     * creating a new newargv each time, the most common cases are
     * preallocated.
     * 
     * @param interp
     *            <code>Interp</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public void run(Interp interp) throws HeclException {
	RealThing realthing = null;

	/* These are the three most common argv lengths. */
	switch (argv.length) {
	case 1 :
	    newargv = nav1;
	    break;
	case 2 :
	    newargv = nav2;
	    break;
	case 3 :
	    newargv = nav3;
	    break;
	default :
	    newargv = new Thing[argv.length];
	}

	realthing = argv[0].val;
	if (command == null) {
	    String cmdName = null;
	    /*
	     * If the argv[0] is a substitution waiting to happen,
	     * substitute it to get the name.
	     */
	    if (realthing instanceof CodeThing) {
		cmdName = CodeThing.doCodeSubst(interp, argv[0]).getStringRep();
	    } else if (realthing instanceof GroupThing) {
		cmdName = CodeThing.doGroupSubst(interp, argv[0]).getStringRep();
	    } else if (realthing instanceof SubstThing) {
		cmdName = CodeThing.doSubstSubst(interp, argv[0]).getStringRep();
	    } else {
		cmdName = argv[0].getStringRep();
	    }
	    command = (Command)interp.commands.get(cmdName);
	    if (command == null) {
		throw new HeclException("Command " + cmdName
					+ " does not exist");
	    }
	}

	/*
	 * Fill in the elements of the new argv - doing substitutions and
	 * running code where needs be.
	 */
	for (int i = 0; i < argv.length; i++) {
	    realthing = argv[i].val;
	    if (realthing instanceof GroupThing) {
		newargv[i] = CodeThing.doGroupSubst(interp, argv[i]);
	    } else if (realthing instanceof SubstThing) {
		newargv[i] = CodeThing.doSubstSubst(interp, argv[i]);
	    } else if (realthing instanceof CodeThing) {
		newargv[i] = CodeThing.doCodeSubst(interp, argv[i]);
	    } else {
		newargv[i] = argv[i];
	    }
	}

	/*
	 * System.out.println("COMMAND v"); for (int i = 0; i <
	 * newargv.length; i ++) { Thing.printThing(newargv[i]); }
	 * System.out.println("ENDCOMMAND ^ ");
	 */

	try {
	    command.cmdCode(interp, newargv);
	} catch (HeclException e) {
	    /* Uh oh, an "issue"! */
	    if (newargv[0] != null) {
		/* Let the exception know where we are. */
		e.where(newargv[0].getStringRep());
	    }
	    switch (e.code) {
	    case HeclException.BREAK :
	    case HeclException.CONTINUE :
	    case HeclException.RETURN :
	    case HeclException.ERROR :
		/* Automatically fall through. */
		throw e;
	    }
	} catch (Exception e) {
	    String msg;
	    /*
	     * Transform a Java exception into a form more palatable to
	     * Hecl.
	     */
	    msg = e.getMessage();
	    if (msg == null) {
		msg = "(null exception of type " + e.getClass() + ")";
	    } else {
		msg = "Exception of type " + e.getClass() + ": " + msg;
	    }
	    throw new HeclException(msg);
	}

    }

    /**
     * The <code>toString</code> method turns a Stanza into a string.
     * 
     * @return a <code>String</code> value
     */
    public String toString() {
	StringBuffer out = new StringBuffer(argv[0].toString());
	for (int i = 1; i < argv.length; i++) {
	    out.append(" ");
	    out.append(argv[i].toString());
	}
	return out.toString();
    }
}
