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

    LoadFile loadfile = null;
    String currentfile = null;

    /**
     * Creates a new <code>Interp</code> instance.
     *
     * @exception HeclException if an error occurs
     */
    public Interp ()
	throws HeclException {
	commands = new Hashtable();
	stack = new Stack();
	error = new Stack();
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
	addCommand("split", new ListCmd());
	addCommand("join", new ListCmd());

	addCommand("proc", new ProcCmd());

	addCommand("foreach", new ForeachCmd());

	addCommand("break", new BreakCmd());
	addCommand("continue", new BreakCmd());

	addCommand("true", new TrueCmd());

	addCommand("catch", new CatchCmd());

	addCommand("intro", new IntrospectCmd());

	addCommand("source", new SourceCmd());

	addCommand("sourcehere", new SourceHereCmd());

	addCommand("upstack", new SourceCmd());

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
    }

    /**
     * The <code>stackIncr</code> method creates a new stack frame.
     * Used in the Proc class.
     *
     */
    public void stackIncr() {
	stack.push(new Hashtable());
    }

    /**
     * <code>stackDecr</code> pops the stack frame, destroying it.
     *
     */
    public void stackDecr() {
	stack.pop();
    }

    /**
     * Describe <code>getVarhash</code> method here.
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
     * Describe <code>getVar</code> method here.
     *
     * @param varname a <code>Thing</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    public Thing getVar(Thing varname) throws HeclException {
	return getVar(varname.toString());
    }

    /**
     * Describe <code>getVar</code> method here.
     *
     * @param varname a <code>String</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    public Thing getVar(String varname) throws HeclException {
	return getVar(varname, -1);
    }

    /**
     * Describe <code>getVar</code> method here.
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
     * Describe <code>setVar</code> method here.
     *
     * @param varname a <code>String</code> value
     * @param value a <code>Thing</code> value
     */
    public void setVar(String varname, Thing value) {
	setVar(varname, value, -1);
    }

    /**
     * Describe <code>setVar</code> method here.
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
     * Describe <code>setVar</code> method here.
     *
     * @param varname a <code>Thing</code> value
     * @param value a <code>Thing</code> value
     */
    public void setVar(Thing varname, Thing value) {
	setVar(varname.toString(), value);
    }

    /**
     * Describe <code>existsVar</code> method here.
     *
     * @param varname a <code>Thing</code> value
     * @return a <code>boolean</code> value
     */
    public boolean existsVar(Thing varname) {
	return existsVar(varname.toString());
    }

    /**
     * Describe <code>existsVar</code> method here.
     *
     * @param varname a <code>String</code> value
     * @return a <code>boolean</code> value
     */
    public boolean existsVar(String varname) {
	return existsVar(varname, -1);
    }

    /**
     * Describe <code>existsVar</code> method here.
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
     * Describe <code>setResult</code> method here.
     *
     * @param newresult a <code>Thing</code> value
     */
    public void setResult(Thing newresult) {
	result = newresult;
    }

    /**
     * Describe <code>getResult</code> method here.
     *
     * @return a <code>Thing</code> value
     */
    public Thing getResult() {
	return result;
    }

    /**
     * Describe <code>addCommand</code> method here.
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
     * Describe <code>getCmd</code> method here.
     *
     * @param name a <code>String</code> value
     * @return a <code>Command</code> value
     * @exception HeclException if an error occurs
     */
    public Command getCmd(String name) throws HeclException {
	Command result = (Command)commands.get(name);
	if (result == null) {
	    throw new HeclException("Command " + name + " does not exist");
	}
	return result;
    }

    /**
     * Describe <code>cmdNames</code> method here.
     *
     * @return an <code>Enumeration</code> value
     * @exception HeclException if an error occurs
     */
    public Enumeration cmdNames() throws HeclException {
	return commands.keys();
    }

    /**
     * Describe <code>addError</code> method here.
     *
     * @param err a <code>Thing</code> value
     */
    public void addError(Thing err) {
	error.push(err);
    }

    /**
     * Describe <code>clearError</code> method here.
     *
     */
    public void clearError() {
	error = new Stack();
    }

    /**
     * Describe <code>getscript</code> method here.
     *
     * @param filename a <code>String</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    public Thing getscript(String filename)
	throws HeclException {
	currentfile = filename;
	loadfile = new LoadFile();
	return loadfile.getscript(filename);
    }

    /**
     * Describe <code>getScriptName</code> method here.
     *
     * @return a <code>String</code> value
     */
    public String getScriptName() {
	return currentfile;
    }
}
