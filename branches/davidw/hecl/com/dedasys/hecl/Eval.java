package com.dedasys.hecl;

import java.util.*;
import java.lang.*;

/**
 * <code>Eval</code> takes care of evaluating code.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

public class Eval {

    /**
     * The <code>eval</code> method evaluates some Hecl code passed to
     * it.
     *
     * @param interp an <code>Interp</code>.
     * @param in a <code>Thing</code> value representing the text to
     * evaluate.
     * @return a <code>Thing</code> value - the result of the evaluation.
     * @exception HeclException if an error occurs.
     */

    public Thing eval(Interp interp, Thing in)
	throws HeclException
    {
	String cmdName = null;
	Parse hp = new Parse(interp, in.toString());
	Command command;
	int i = 0;

	//System.out.println("IN is : " + in + "<<<");
	try {
	    while (hp.more()) {
		cmdName = null;
		Vector cmd = new Vector();
		cmd = hp.parse();

		//System.out.println("CMD is " + cmd);

		if (cmd == null || cmd.size() == 0) {
		    continue;
		}

		Thing[] argv =  new Thing[cmd.size()];
		for (i = 0; i < cmd.size(); i ++) {
		    argv[i] = (Thing)cmd.elementAt(i);
		}
		cmdName = cmd.elementAt(0).toString();

		//System.out.println("CMD is " + cmdName);
		//System.out.println("ARGS ARE " + Arrays.asList(argv));

		command = interp.getCmd(cmdName);
		command.cmdCode(interp, argv);
	    }

	} catch (HeclException e) {

	    /* Uh oh, an "issue"! */
	    if (cmdName != null) {
		e.where(cmdName);
	    }
//	    System.out.println(e);
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
	    Thing newexception = new Thing(errors);
	    throw new HeclException(newexception.toString());
	}

	return interp.getResult();
    }

}
