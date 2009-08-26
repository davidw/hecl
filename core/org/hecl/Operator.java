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

    /**
     * <code>cmdcode</code> - the int that corresponds to the command
     * to be executed.
     *
     */
    protected int cmdcode;

    /**
     * <code>minargs</code> - the minimum number of arguments this
     * command accepts.
     *
     */
    protected int minargs;

    /**
     * <code>maxargs</code> - the maximum number of arguments this
     * command accepts.  A value of 0 means unlimited arguments.
     *
     */
    protected int maxargs;

    /**
     * Creates a new <code>Operator</code> instance.
     *
     * @param cmdcode an <code>int</code> value corresponding to the
     * command code number found in the class implementing the
     * command.
     * @param minargs an <code>int</code> value - the minimum number
     * of arguments to the command.
     * @param maxargs an <code>int</code> value - the maximum number
     * of arguments to the command, or -1 if unlimited.
     */
    protected Operator(int cmdcode, int minargs, int maxargs) {
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
     * @return The computed <code>Thing</code>, or null when no value has been
     * computed.
     * @exception HeclException if an error occurs
     */
    public Thing cmdCode(Interp interp, Thing[] argv) throws HeclException {
	Interp.checkArgCount(argv,this.minargs,this.maxargs);
	return operate(cmdcode, interp, argv);
    }

    /**
     * The <code>operate</code> method dispatches to the actual code.
     *
     * @param cmdcode an <code>int</code> value
     * @param interp an <code>Interp</code> value
     * @param argv a <code>Thing[]</code> value
     * @return a <code>Thing</code> value, or null when no value has been
     * @exception HeclException if an error occurs
     */
    public abstract Thing operate(int cmdcode,Interp interp,Thing[] argv)
	throws HeclException;


    /**
     * The <code>load</code> method loads the commands in a class that
     * extends Operator.
     *
     * @param ip an <code>Interp</code> value
     * @exception HeclException if an error occurs
     */
    protected static void load(Interp ip,Hashtable cmdtable) throws HeclException {
	//	System.err.println("-->Operator.load, ip="+ip+", cmdtable="+cmdtable);
	Enumeration e = cmdtable.keys();
	while(e.hasMoreElements()) {
	    String k = (String)e.nextElement();
	    //	    System.err.println("cmd="+k);
	    ip.addCommand(k, (Command)cmdtable.get(k));
	}
	//	System.err.println("<<--Operator.load");
    }


    /**
     * The <code>unload</code> method unloads the commands in a class
     * that extends Operator.
     *
     * @param ip an <code>Interp</code> value
     * @exception HeclException if an error occurs
     */
    protected static void unload(Interp ip,Hashtable cmdtable) throws HeclException {
	Enumeration e = cmdtable.keys();
	while(e.hasMoreElements()) {
	    ip.removeCommand((String)e.nextElement());
	}
    }
}
