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

/**
 * The <code>Stanza</code> class represents one command. A CodeThing
 * object may have several Stanzas.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class Stanza {
    /* The line this stanza begins on. */
    private int lineno = 0;

    private Command command = null;
    private Thing[] argv = null;

    /**
     * Creates a new <code>Stanza</code> instance, taking a Command and
     * its arguments as input.
     *
     * @param newcmd a <code>Command</code> value
     * @param newargv a <code>Thing[]</code> value
     */
    Stanza(Command newcmd, Thing[] newargv, int ln) {
	this.command = newcmd;
	this.argv = newargv;
	this.lineno = ln;
    }


    /**
     * <code>deepcopy</code> creates a new stanza and returns it.
     *
     * @return a <code>Stanza</code> value
     */

    public Stanza deepcopy () throws HeclException {
	Thing[] destargv = new Thing[this.argv.length];

	for (int i = 0; i < this.argv.length; i++) {
	    destargv[i] = this.argv[i].deepcopy();
	}
	return new Stanza(this.command, destargv, this.lineno);
    }

    private static Thing cloneThing(Interp interp,Thing t) throws HeclException {
	//System.err.println("-->cloneThing");
	RealThing rt = t.getVal();
	Thing res = null;
	if (rt instanceof GroupThing) {
	    res = CodeThing.doGroupSubst(interp, t);
	    res.copy = true;
	} else if (rt instanceof SubstThing) {
	    res = CodeThing.doSubstSubst(interp, t);
	} else if (rt instanceof CodeThing) {
	    res = CodeThing.doCodeSubst(interp, t);
	} else {
	    res = t;
	    res.copy = true;
	}
	//System.err.println("<--cloneThing, res="+res.toString());
	return res;
    }
    
    /**
     * The <code>run</code> method runs the Stanza. In order to avoid
     * creating a new newargv each time, the most common cases are
     * preallocated.
     *
     * @param interp <code>Interp</code> value
     * @return A <code>Thing</code> being the result of the evaluation, or
     * <code>null</code> of no result has been computed.
     *
     * @exception HeclException if an error occurs
     */
    public Thing run(Interp interp) throws HeclException {
	RealThing realthing = null;
	Command tmpcommand = null;
	ClassCommandInfo info = null;
	
	//System.err.println("-->Stanza.run, this="+this);
	
	Thing[] newargv = new Thing[this.argv.length];

	/* If we have a CodeThing, GroupThing or SubstThing as
	 * argv[0], we don't want to save 'command'. */
	/* FIXME - this could get all messed up by renaming commands. */
	boolean saveit = false;

	String cmdName = null;
	newargv[0] = cloneThing(interp,this.argv[0]);
	if (this.command == null) {
	    realthing = newargv[0].getVal();
	    if(realthing instanceof ObjectThing) {
		info = interp.findClassCmd(((ObjectThing)realthing).get().getClass());
		if(info != null && argv.length < 2) {
		    throw new HeclException("Class-command required methodname",this.lineno);
		}
	    }
	    if(info == null)
		cmdName = newargv[0].toString();
	    if(cmdName != null) {
		//System.out.println("cmdname = " + cmdName);
		tmpcommand = (Command)interp.commands.get(cmdName);
	    }
	} else {
	    /* FIXME - this could get all messed up by renaming commands. */
	    cmdName = newargv[0].toString();
	    tmpcommand = this.command;
	}

	if(tmpcommand == null && info == null) {
	    throw new HeclException("Command '" + cmdName + "' does not exist",
				    this.lineno);
	}
	
	/* DEBUG - before. */
	if (//true
	    false
	    ) {
	    System.out.println("BEFORE COMMAND v");
	    for (int i = 0; i < argv.length; i ++) {
		PrintThing.printThing(argv[i]);
	    }
	    System.out.println("BEFORE ENDCOMMAND ^ ");
	}

	/*
	 * Fill in the elements of the new argv - doing substitutions and
	 * running code where needs be.
	 */
	try {
	    //for (int i = 0; i < argv.length; i++) {
	    for (int i = 1; i < argv.length; i++) {
		realthing = argv[i].getVal();
		if (realthing instanceof GroupThing) {
		    newargv[i] = CodeThing.doGroupSubst(interp, argv[i]);
		    newargv[i].copy = true;
		} else if (realthing instanceof SubstThing) {
		    newargv[i] = CodeThing.doSubstSubst(interp, argv[i]);
		} else if (realthing instanceof CodeThing) {
		    newargv[i] = CodeThing.doCodeSubst(interp, argv[i]);
		} else {
		    newargv[i] = argv[i];
		}
	    }
	} catch (HeclException he) {
	    he.setLine(this.lineno);
	    throw he;
	}

	/* DEBUG - after. */
	if (//true
	    false
	    ) {
	    System.out.println("AFTER COMMAND v");
	    for (int i = 0; i < newargv.length; i ++) {
		PrintThing.printThing(newargv[i]);
	    }
	    System.out.println("AFTER ENDCOMMAND ^ ");
	}

	Thing res = null;
	try {
	    if(info != null) {
		res = info.getCommand().method(interp,info,newargv);
	    } else {
		res = tmpcommand.cmdCode(interp, newargv);
	    }
	} catch (HeclException e) {
	    /* Uh oh, an "issue"! */
	    if (newargv[0] != null) {
		/* Let the exception know where we are. */
		e.where(newargv[0].toString(), this.lineno);
	    }
	    throw e;
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
	    throw new HeclException(msg,this.lineno);
	}

	/* Go ahead and save the command. */
	if (saveit) {
	    command = tmpcommand;
	}
	return res != null ? res : Thing.emptyThing();
    }

    /**
     * The <code>toString</code> method turns a Stanza into a string.
     *
     * @return a <code>String</code> value
     */
    public String toString() {
	StringBuffer out = new StringBuffer("");
	for (int i = 0; i < argv.length; i++) {
	    if (i != 0) {
		out.append(' ');
	    }
	    RealThing rt = argv[i].getVal();

	    if (rt instanceof CodeThing && ((CodeThing)rt).marksubst) {
		String avs = argv[i].toString();
		out.append('[').append(avs).append(']');
	    } else if (rt instanceof GroupThing) {
		out.append('\"').append(argv[i].toString()).append('\"');
	    } else {
		ListThing.appendListItem(out,argv[i]);
	    }
	}
	return out.toString();
    }

    public Thing[] getArgv() {
	return argv;
    }
}
