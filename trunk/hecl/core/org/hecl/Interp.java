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

import java.util.Hashtable;
import java.util.Stack;

/**
 * <code>Interp</code> is the Hecl interpreter, the class responsible for
 * knowing what variables and commands are available.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */
public class Interp {
    /**
     * Package name prefix of the module classes.
     */
    public static final String MODULE_CLASS_PACKAGE = "org.hecl";

    public long cacheversion = 0;

    /* Save some space by making this public and removing the
     * accessors. */
    public Hashtable commands;

    Stack stack;

    int stacklevel;

    public Thing result;

    Stack error;

    /**
     * Creates a new <code>Interp</code> instance, initializing command and
     * variable hashtables, a stack, and an error stack.
     * 
     * @exception HeclException
     *                if an error occurs
     */
    public Interp() throws HeclException {
        commands = new Hashtable();
        stack = new Stack();
        error = new Stack();

        // Set up stack frame for globals.
        stack.push(new Hashtable());

        initInterp();
    }
    /**
     * The <code>eval</code> method evaluates some Hecl code passed to it.
     * 
     * @param interp
     *            an <code>Interp</code>.
     * @param in
     *            a <code>Thing</code> value representing the text to
     *            evaluate.
     * @return a <code>Thing</code> value - the result of the evaluation.
     * @exception HeclException
     *                if an error occurs.
     */
    public Thing eval(Thing in) throws HeclException {
	CodeThing.get(this, in).run(this);
	return result;
    }

    /**
     * The <code>initCommands</code> method initializes all the built in
     * commands. These are commands available in all versions of Hecl. J2SE
     * commands are initialized in Standard.java, and J2ME commands in
     * Micro.java.
     * 
     * @exception HeclException
     *                if an error occurs
     */
    private void initInterp() throws HeclException {
        commands.put("set", new SetCmd());
        commands.put("unset", new SetCmd());

        commands.put("puts", new PutsCmd());

        commands.put("=", new EqualsCmd());
        commands.put("eq", new EqualsCmd());
        commands.put(">", new EqualsCmd());
        commands.put("<", new EqualsCmd());
	commands.put("!=", new EqualsCmd());
	commands.put("ne", new EqualsCmd());

        commands.put("if", new IfCmd());

        commands.put("while", new WhileCmd());

        commands.put("+", new BasicMathCmd());
        commands.put("-", new BasicMathCmd());
        commands.put("*", new BasicMathCmd());
        commands.put("/", new BasicMathCmd());

        commands.put("%", new BasicMathCmd());

        commands.put("list", new ListCmd());
        commands.put("llen", new ListCmd());
        commands.put("lappend", new ListCmd());
        commands.put("lindex", new ListCmd());
        commands.put("lset", new ListCmd());
        commands.put("lrange", new ListCmd());

        commands.put("and", new LogicCmd());
        commands.put("not", new LogicCmd());
        commands.put("or", new LogicCmd());

        commands.put("split", new JoinSplitCmd());
        commands.put("join", new JoinSplitCmd());

        commands.put("proc", new ProcCmd());
        commands.put("rename", new ProcCmd());

        commands.put("foreach", new ForeachCmd());
        commands.put("filter", new FilterCmd());
        commands.put("search", new FilterCmd());

        commands.put("break", new BreakCmd());
        commands.put("continue", new BreakCmd());

        commands.put("true", new TrueCmd());

        commands.put("catch", new CatchCmd());

        commands.put("intro", new IntrospectCmd());

        commands.put("upeval", new UpCmd());

        commands.put("sort", new SortCmd());

        commands.put("append", new AppendCmd());

        commands.put("slen", new StringCmd());
        commands.put("sindex", new StringCmd());

        commands.put("hash", new HashCmd());
        commands.put("hget", new HashCmd());
        commands.put("hset", new HashCmd());

        commands.put("eval", new EvalCmd());

        commands.put("global", new GlobalCmd());

        commands.put("return", new ReturnCmd());

        commands.put("incr", new IncrCmd());

        commands.put("for", new ForCmd());

        commands.put("time", new TimeCmd());

        commands.put("exit", new ExitCmd());
    }

    /**
     * The <code>cmdRename</code> method renames a command, or throws
     * an error if the original command didn't exist.
     *
     * @param oldname a <code>String</code> value
     * @param newname a <code>String</code> value
     * @exception HeclException if an error occurs
     */
    public void cmdRename(String oldname, String newname) throws HeclException {
	Command tmp = (Command)commands.get(oldname);
	if (tmp == null) {
            throw new HeclException("Command " + oldname + " does not exist");
	}
	commands.put(newname, tmp);
    }

    /**
     * The <code>stackIncr</code> method creates a new stack frame. Used in
     * the Proc class.
     *
     */
    public void stackIncr() {
        stackPush(new Hashtable());
    }

    /**
     * <code>stackDecr</code> pops the stack frame, returning it so that
     * commands like upeval can save it. If it's not saved, it's gone.
     *  
     */
    public Hashtable stackDecr() {
        return (Hashtable) stack.pop();
    }

    /**
     * <code>stackDecr</code> pushes a new variable hashtable (probably saved
     * via upeval) onto the stack frame.
     *  
     */
    public void stackPush(Hashtable vars) {
        cacheversion++;
        stack.push(vars);
    }

    /**
     * <code>getVarhash</code> fetches the variable Hashtable at the given
     * level, where -1 means to just get the hashtable on top of the stack.
     * 
     * @param level
     *            an <code>int</code> value
     * @return a <code>Hashtable</code> value
     */
    private Hashtable getVarhash(int level) {
        if (level < 0) {
            return (Hashtable) stack.peek();
        } else {
            return (Hashtable) stack.elementAt(level);
        }
    }

    /**
     * <code>getVar</code> returns the value of a variable given its name.
     * 
     * @param varname
     *            a <code>Thing</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public Thing getVar(Thing varname) throws HeclException {
        return getVar(varname.getStringRep(), -1);
    }

    /**
     * <code>getVar</code> returns the value of a variable given its name.
     * 
     * @param varname
     *            a <code>String</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public Thing getVar(String varname) throws HeclException {
        return getVar(varname, -1);
    }

    /**
     * <code>getVar</code> returns the value of a variable given its name and
     * level.
     * 
     * @param varname
     *            a <code>String</code> value
     * @param level
     *            an <code>int</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public Thing getVar(String varname, int level) throws HeclException {
        Hashtable lookup = getVarhash(level);
	//System.out.println("getvar: " + varname + " " + level + " " + lookup);

        Thing res = (Thing) lookup.get(varname);
        if (res == null) {
            throw new HeclException("Variable " + varname + " does not exist");
        }
        return res;
    }

    /**
     * <code>setVar</code> sets a variable in the innermost variable stack
     * frame to a value.
     * 
     * @param varname
     *            a <code>Thing</code> value
     * @param value
     *            a <code>Thing</code> value
     */
    public void setVar(Thing varname, Thing value) throws HeclException {
        setVar(varname.toString(), value);
    }

    /**
     * <code>setVar</code> sets a variable in the innermost variable stack
     * frame to a value.
     * 
     * @param varname
     *            a <code>String</code> value
     * @param value
     *            a <code>Thing</code> value
     */
    public void setVar(String varname, Thing value) {
        setVar(varname, value, -1);
    }

    /**
     * <code>setVar</code> sets a variable to a value in the variable stack
     * frame specified by <code>level</code>.
     * 
     * @param varname
     *            a <code>String</code> value
     * @param value
     *            a <code>Thing</code> value
     * @param level
     *            an <code>int</code> value
     */
    public void setVar(String varname, Thing value, int level) {
        Hashtable lookup = getVarhash(level);

	/* Bump the cache number so that SubstThing.get refetches the
	 * variable. */
        cacheversion++;
	if (lookup.containsKey(varname)) {
	     Thing oldval = (Thing) lookup.get(varname);
	     /* If Stanza has indicated that this value should be
	      * copied if a write is attempted to it, don't use the
	      * existing reference. */
	     if (!oldval.copy) {
		 oldval.makeref(value);
		 return;
	     }
	}
	lookup.put(varname, value);
    }


    /**
     * <code>unSetVar</code> unsets a variable in the current stack
     * frame.
     *
     * @param varname
     *            a <code>Thing</code> value
     */
    public void unSetVar(Thing varname) throws HeclException {
        Hashtable lookup = getVarhash(-1);
	String vn = varname.toString();
	/* Bump the cache number so that SubstThing.get refetches the
	 * variable. */
        cacheversion++;
	if (lookup.containsKey(vn)) {
	    lookup.remove(vn);
	} else {
            throw new HeclException("Variable " + vn + " does not exist");
	}
    }


    /**
     * <code>existsVar</code> returns <code>true</code> if the given
     * variable exists in the current variable stack frame, <code>false</code>
     * if it does not.
     * 
     * @param varname
     *            a <code>Thing</code> value
     * @return a <code>boolean</code> value
     */
    public boolean existsVar(Thing varname) throws HeclException {
        return existsVar(varname.getStringRep());
    }

    /**
     * <code>existsVar</code> returns <code>true</code> if the given
     * variable exists in the current variable stack frame, <code>false</code>
     * if it does not.
     * 
     * @param varname
     *            a <code>String</code> value
     * @return a <code>boolean</code> value
     */
    public boolean existsVar(String varname) {
        return existsVar(varname, -1);
    }

    /**
     * <code>existsVar</code> returns <code>true</code> if the given
     * variable exists in the variable stack frame given by <code>level</code>,
     * <code>false</code> if it does not.
     * 
     * @param varname
     *            a <code>String</code> value
     * @param level
     *            an <code>int</code> value
     * @return a <code>boolean</code> value
     */
    public boolean existsVar(String varname, int level) {
        Hashtable lookup = getVarhash(level);
        return lookup.containsKey(varname);
    }

    /**
     * <code>setResult</code> sets the interpreter result of the most recent
     * command.
     * 
     * @param newresult
     *            a <code>Thing</code> value
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
     * <code>addError</code> adds a Thing as an error message.
     * 
     * @param err
     *            a <code>Thing</code> value
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
}
