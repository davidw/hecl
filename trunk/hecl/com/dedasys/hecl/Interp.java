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
 * <code>Interp</code> is the Hecl interpreter, the class responsible
 * for knowing what variables and commands are available.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class Interp {
    Hashtable commands;
    Stack stack;
    int stacklevel;
    Thing result;
    Stack error;

    Load load = null;
    String currentfile = null;

    /**
     * Creates a new <code>Interp</code> instance, initializing
     * command and variable hashtables, a stack, and an error stack.
     *
     * @param varname a <code>Load</code> instance of some type.
     * @exception HeclException if an error occurs
     */
    public Interp (Load newloader)
	throws HeclException {
	commands = new Hashtable();
	stack = new Stack();
	error = new Stack();

	load = newloader;
	// Set up stack frame for globals.
	stack.push(new Hashtable());

	initCommands();
    }

    /**
     * The <code>initCommands</code> method initializes all the built
     * in commands.
     *
     * @exception HeclException if an error occurs
     */
    public void initCommands()
	throws HeclException {
	addCommand("set", new SetCmd());

	addCommand("ref", new RefCmd());

	addCommand("puts", new PutsCmd());

	addCommand("=", new EqualsCmd());
	addCommand(">", new EqualsCmd());
	addCommand("<", new EqualsCmd());

	addCommand("if", new IfCmd());

	addCommand("while", new WhileCmd());

	addCommand("+", new BasicMathCmd());
	addCommand("-", new BasicMathCmd());
	addCommand("*", new BasicMathCmd());
	addCommand("/", new BasicMathCmd());

	addCommand("/", new BasicMathCmd());

	addCommand("list", new ListCmd());
	addCommand("llen", new ListCmd());
	addCommand("lappend", new ListCmd());
	addCommand("lindex", new ListCmd());
	addCommand("lset", new ListCmd());

	addCommand("split", new JoinSplitCmd());
	addCommand("join", new JoinSplitCmd());

	addCommand("proc", new ProcCmd());

	addCommand("foreach", new ForeachCmd());

	addCommand("break", new BreakCmd());
	addCommand("continue", new BreakCmd());

	addCommand("true", new TrueCmd());

	addCommand("catch", new CatchCmd());

	addCommand("intro", new IntrospectCmd());

	addCommand("source", new SourceCmd());

	addCommand("sourcehere", new SourceHereCmd());

	addCommand("upeval", new UpCmd());

	addCommand("sort", new SortCmd());

	addCommand("append", new AppendCmd());

	addCommand("slen", new StringCmd());
	addCommand("sindex", new StringCmd());

	addCommand("hash", new HashCmd());
	addCommand("hget", new HashCmd());
	addCommand("hset", new HashCmd());

	addCommand("eval", new EvalCmd());

	addCommand("global", new GlobalCmd());

	addCommand("return", new ReturnCmd());

	addCommand("time", new TimeCmd());

	addCommand("incr", new IncrCmd());

	addCommand("for", new ForCmd());

	addCommand("copy", new CopyCmd());
    }

    /**
     * The <code>stackIncr</code> method creates a new stack frame.
     * Used in the Proc class.
     *
     */
    public void stackIncr() {
	stackPush(new Hashtable());
    }

    /**
     * <code>stackDecr</code> pops the stack frame, returning it so
     * that commands like upeval can save it.  If it's not saved, it's
     * gone.
     *
     */
    public Hashtable stackDecr() {
	return (Hashtable)stack.pop();
    }

    /**
     * <code>stackDecr</code> pushes a new variable hashtable
     * (probably saved via upeval) onto the stack frame.
     *
     */
    public void stackPush(Hashtable vars) {
	stack.push(vars);
    }

    /**
     * <code>getVarhash</code> fetches the variable Hashtable at the
     * given level, where -1 means to just get the hashtable on top of
     * the stack.
     *
     * @param level an <code>int</code> value
     * @return a <code>Hashtable</code> value
     */
    private Hashtable getVarhash(int level) {
	if (level < 0) {
	    return (Hashtable)stack.peek();
	} else {
	    return (Hashtable)stack.elementAt(level);
	}
    }

    /**
     * <code>getVar</code> returns the value of a variable given its
     * name.
     *
     * @param varname a <code>Thing</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    public Thing getVar(Thing varname) throws HeclException {
	return getVar(varname.toString());
    }

    /**
     * <code>getVar</code> returns the value of a variable given its
     * name.
     *
     * @param varname a <code>String</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    public Thing getVar(String varname) throws HeclException {
	return getVar(varname, -1);
    }

    /**
     * <code>getVar</code> returns the value of a variable given its
     * name and level.
     *
     * @param varname a <code>String</code> value
     * @param level an <code>int</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    public Thing getVar(String varname, int level)
	throws HeclException {
	Hashtable lookup = getVarhash(level);

	Thing result = (Thing)lookup.get(varname);
	if (result == null) {
	    throw new HeclException("Variable " + varname + " does not exist");
	}
	return result;
    }

    /**
     * <code>setVar</code> sets a variable in the innermost variable
     * stack frame to a value.
     *
     * @param varname a <code>Thing</code> value
     * @param value a <code>Thing</code> value
     */
    public void setVar(Thing varname, Thing value) {
	setVar(varname.toString(), value);
    }


    /**
     * <code>setVar</code> sets a variable in the innermost variable
     * stack frame to a value.
     *
     * @param varname a <code>String</code> value
     * @param value a <code>Thing</code> value
     */
    public void setVar(String varname, Thing value) {
	setVar(varname, value, -1);
    }

    /**
     * <code>setVar</code> sets a variable to a value in the variable
     * stack frame specified by <code>level</code>.
     *
     * @param varname a <code>String</code> value
     * @param value a <code>Thing</code> value
     * @param level an <code>int</code> value
     */
    public void setVar(String varname, Thing value, int level) {
	Hashtable lookup = getVarhash(level);

	if (lookup.containsKey(varname)) {
	    Thing newval = (Thing)lookup.get(varname);
	    value.makeref(newval);
	} else {
	    lookup.put(varname, value);
	}
    }


    /**
     * <code>existsVar</code> returns <code>true</code> if the given
     * variable exists in the current variable stack frame,
     * <code>false</code> if it does not.
     *
     * @param varname a <code>Thing</code> value
     * @return a <code>boolean</code> value
     */
    public boolean existsVar(Thing varname) {
	return existsVar(varname.toString());
    }

    /**
     * <code>existsVar</code> returns <code>true</code> if the given
     * variable exists in the current variable stack frame,
     * <code>false</code> if it does not.
     *
     * @param varname a <code>String</code> value
     * @return a <code>boolean</code> value
     */
    public boolean existsVar(String varname) {
	return existsVar(varname, -1);
    }

    /**
     * <code>existsVar</code> returns <code>true</code> if the given
     * variable exists in the variable stack frame given by
     * <code>level</code>, <code>false</code> if it does not.
     *
     * @param varname a <code>String</code> value
     * @param level an <code>int</code> value
     * @return a <code>boolean</code> value
     */
    public boolean existsVar(String varname, int level) {
	Hashtable lookup = getVarhash(level);
	return lookup.containsKey(varname);
    }

    /**
     * <code>setResult</code> sets the interpreter result of the most
     * recent command.
     *
     * @param newresult a <code>Thing</code> value
     */
    public void setResult(Thing newresult) {
	result = newresult;
    }

    /**
     * <code>getResult</code> fetches the result saved by setResult.
     *
     * @return a <code>Thing</code> value
     */
    public Thing getResult() {
	return result;
    }

    /**
     * <code>addCommand</code> adds a command to the command hash
     * table.
     *
     * @param name a <code>String</code> value
     * @param cmd a <code>Command</code> value
     * @exception HeclException if an error occurs
     */
    public void addCommand(String name, Command cmd)
	throws HeclException {
	commands.put(name, cmd);
    }

    /**
     * <code>getCmd</code> fetches a command for excecution from the
     * command hash table.
     *
     * @param name a <code>String</code> value
     * @return a <code>Command</code> value
     */
    public Command getCmd(String name) {
	return (Command)commands.get(name);
    }

    /**
     * <code>cmdNames</code> returns a list of all commands registered.
     *
     * @return an <code>Enumeration</code> value
     * @exception HeclException if an error occurs
     */
    public Enumeration cmdNames() throws HeclException {
	return commands.keys();
    }

    /**
     * <code>addError</code> adds a Thing as an error message.
     *
     * @param err a <code>Thing</code> value
     */
    public void addError(Thing err) {
	error.push(err);
    }

    /**
     * <code>clearError</code> clears the error stack.
     *
     */
    public void clearError() {
	error = new Stack();
    }

    /**
     * The <code>getscript</code> method returns the text of a script
     * resource (file, url, whatever) as a Thing.
     *
     * @param resourcename a <code>String</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */

    /* FIXME - this ought not to use LoadFile, so that we can change
     * it. */

    public Thing getscript(String resourcename)
	throws HeclException {
	currentfile = resourcename;
	return load.getscript(resourcename);
    }

    /**
     * The <code>getScriptName</code> method returns the name of the
     * file being run, if it exists.
     *
     * @return a <code>String</code> value
     */
    public String getScriptName() {
	return currentfile;
    }
}
