/* Copyright 2006 Wolfgang S. Kechel

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

/**
 * The <code>Operator</code> class implements a number of features
 * that are used in the creation of groups of commands, such as those
 * found in InterpCmds, ListCmds, and so on.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public abstract class Operator implements Command {
    protected int cmdcode;
    protected int minargs;
    protected int maxargs;

    static protected Hashtable cmdtable = new Hashtable();

    /**
     * Creates a new <code>Operator</code> instance.
     *
     * @param cmdcode an <code>int</code> value corresponding to the
     * command code number found in the class implementing the
     * command.
     * @param minargs an <code>int</code> value - the minimum number of arguments to the command.
     * @param maxargs an <code>int</code> value - the maximum number
     * of arguments to the command, or -1 if unlimited.
     */
    protected Operator(int cmdcode,int minargs,int maxargs) {
	this.cmdcode = cmdcode;
	this.minargs = minargs;
	this.maxargs = maxargs;
    }

    /**
     * The <code>cmdCode</code> method dispatches to the actual code
     * via the operate method and cmdcode argument.
     *
     * @param interp an <code>Interp</code> value
     * @param argv a <code>Thing[]</code> value
     * @exception HeclException if an error occurs
     */
    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
	checkArgCount(argv);

	/* Allow operator to set result on its own an return null to
	   indicate result has already been set. Of course, this
	   disallows returning null as result of an operator, but this
	   is not really an issue. */

	RealThing rt = operate(cmdcode, interp, argv);
	if(rt != null) {
	    interp.setResult(new Thing(rt));
	}
    }

    /**
     * The <code>operate</code> method dispatches to the actual code.
     *
     * @param cmdcode an <code>int</code> value
     * @param interp an <code>Interp</code> value
     * @param argv a <code>Thing[]</code> value
     * @return a <code>RealThing</code> value
     * @exception HeclException if an error occurs
     */
    public abstract RealThing operate(int cmdcode,Interp interp,Thing[] argv)
	throws HeclException;


    /**
     * <code>checkArgCount</code> checks to see whether the command
     * actually has the required number of arguments.
     *
     * @param argv a <code>Thing[]</code> value
     * @exception HeclException if an error occurs
     */
    protected void checkArgCount(Thing[] argv) throws HeclException {
	int n = argv.length-1;		    // Ignore command name
	if(minargs >= 0 && n < minargs) {
	    throw new HeclException("Too few arguments, at least "
				    + minargs + " arguments required.");
	}

	if(maxargs >= 0 && n > maxargs) {
	    throw new HeclException("Bad argument count, max. "
				    + maxargs
				    +" arguments allowed.");
	}
    }

    /**
     * The <code>load</code> method loads the commands in a class that
     * extends Operator.
     *
     * @param ip an <code>Interp</code> value
     * @exception HeclException if an error occurs
     */
    protected static void load(Interp ip) throws HeclException {
	Enumeration e = cmdtable.keys();
	while(e.hasMoreElements()) {
	    String k = (String)e.nextElement();
	    ip.addCommand(k, (Command)cmdtable.get(k));
	}
    }


    /**
     * The <code>unload</code> method unloads the commands in a class
     * that extends Operator.
     *
     * @param ip an <code>Interp</code> value
     * @exception HeclException if an error occurs
     */
    protected static void unload(Interp ip) throws HeclException {
	Enumeration e = cmdtable.keys();
	while(e.hasMoreElements()) {
	    ip.removeCommand((String)e.nextElement());
	}
    }
}
