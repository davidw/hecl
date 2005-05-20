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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

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
    /**
     * Class name for the module initialization class.
     */
    public static final String MODULE_CLASS_LOADERCLASS = "HeclModule";
    Hashtable modules = new Hashtable();

    public long cacheversion = 0;

    Hashtable commands;

    Stack stack;

    int stacklevel;

    public Thing result;

    Stack error;

    Vector getters = new Vector();

    String currentfile = null;

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
     * The <code>initCommands</code> method initializes all the built in
     * commands. These are commands available in all versions of Hecl. J2SE
     * commands are initialized in Standard.java, and J2ME commands in
     * Micro.java.
     * 
     * @exception HeclException
     *                if an error occurs
     */
    private void initInterp() throws HeclException {
        addCommand("set", new SetCmd());

        addCommand("puts", new PutsCmd());

        addCommand("=", new EqualsCmd());
        addCommand("eq", new EqualsCmd());
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
        addCommand("filter", new FilterCmd());
        addCommand("search", new FilterCmd());

        addCommand("break", new BreakCmd());
        addCommand("continue", new BreakCmd());

        addCommand("true", new TrueCmd());

        addCommand("catch", new CatchCmd());

        addCommand("intro", new IntrospectCmd());

        addCommand("source", new ResCmd());

        addCommand("sourcehere", new ResCmd());

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

        addCommand("incr", new IncrCmd());

        addCommand("for", new ForCmd());

        addCommand("time", new TimeCmd());

        addCommand("module", new HeclModuleCmd());

        /* Try to load standard modules, if they exist. */
        loadModule("pjava", false);
        loadModule("j2me", false);
        loadModule("http", false);
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
        setVar(varname.getStringRep(), value);
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

        if (lookup.containsKey(varname)) {
            Thing oldval = (Thing) lookup.get(varname);
            oldval.makeref(value);
        } else {
            lookup.put(varname, value);
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
     * <code>addCommand</code> adds a command to the command hash table.
     * 
     * @param name
     *            a <code>String</code> value
     * @param cmd
     *            a <code>Command</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public void addCommand(String name, Command cmd) throws HeclException {
        commands.put(name, cmd);
    }

    public void removeCommand(String name) throws HeclException {
        commands.remove(name);
    }

    /**
     * <code>getCommand</code> fetches a command for excecution from the command
     * hash table.
     * 
     * @param name
     *            a <code>String</code> value
     * @return a <code>Command</code> value
     */
    public Command getCommand(String name) {
        return (Command) commands.get(name);
    }

    /**
     * <code>cmdNames</code> returns a list of all commands registered.
     * 
     * @return an <code>Enumeration</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public Enumeration cmdNames() throws HeclException {
        return commands.keys();
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

    /**
     * Registers a new <code>ResourceGetter</code> in this interpreter.
     * 
     * @param getter
     *            new getter to use
     */
    public void addResourceGetter(ResHandle getter) {
        int pri, i, j = -1, size;
        /* if we have already registered this getter, do nothing */
        if (getters.contains(getter))
            return;
        size = getters.size();
        pri = getter.getPriority();
        for (i = 0; i < size; i++) {
            if (((ResHandle) getters.elementAt(i)).getPriority() < pri) {
                j = i;
                break;
            }
        }
        if (j < 0)
            getters.addElement(getter);
        else
            getters.insertElementAt(getter, j);
    }

    /**
     * Unregisters a <code>ResourceGetter</code> from this interpreter.
     * 
     * @param getter
     */
    public void removeResourceGetter(ResHandle getter) {
        getters.removeElement(getter);
    }

    /**
     * 
     * @param resourcename
     * @return
     */
    ResHandle findResourceGetter(String resourcename) {
        Enumeration elements;
        ResHandle rc = null;
        for (elements = getters.elements(); elements.hasMoreElements();) {
            rc = (ResHandle) elements.nextElement();
            if (rc.handleRes(resourcename)) {
                return rc;
            }
        }
        return null;
    }

    /**
     * The <code>getResAsThing</code> method returns the text of a script
     * resource (file, url, whatever) as a Thing.
     * 
     * @param resourcename
     *            a <code>String</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public Thing getResAsThing(String resourcename) throws HeclException {
        ResHandle loader;
        loader = findResourceGetter(resourcename);
        if (loader == null)
            throw new HeclException("Unable to load resource \"" + resourcename
                    + "\"");
        currentfile = resourcename;
        return loader.getRes(resourcename);
    }

    /**
     * The <code>getScriptName</code> method returns the name of the file
     * being run, if it exists.
     * 
     * @return a <code>String</code> value
     */
    public String getCurrentScriptName() {
        return currentfile;
    }
    public void loadModule(String name, boolean throwException)
            throws HeclException {
        String className;
        className = MODULE_CLASS_PACKAGE + "." + name.toLowerCase() + "."
                + MODULE_CLASS_LOADERCLASS;
        loadModule(name, className, throwException);
    }

    public void unloadModule(String name, boolean throwException)
            throws HeclException {
        String className;
        className = MODULE_CLASS_PACKAGE + "." + name.toLowerCase() + "."
                + MODULE_CLASS_LOADERCLASS;
        unloadModule(name, className, throwException);
    }

    void loadModule(String name, String className, boolean throwException)
            throws HeclException {
        Class cls;
        HeclModule clsmodule;
        if (modules.get(className) != null) {
            if (throwException)
                throw new HeclException("module \"" + name
                        + "\" already loaded.");
            else
                return;
        }

        try {
            cls = Class.forName(className);
            clsmodule = (HeclModule) cls.newInstance();
        } catch (Exception exception) {
            if (throwException)
                throw new HeclException("module \"" + name
                        + "\" does not exist.");
            else
                return;
        }

        modules.put(className, clsmodule);
        clsmodule.loadModule(this);
    }

    void unloadModule(String name, String className, boolean throwException)
            throws HeclException {
        HeclModule clsmodule;
        clsmodule = (HeclModule) modules.get(className);
        if (clsmodule == null) {
            if (throwException)
                throw new HeclException("module \"" + name
                        + "\" is not loaded.");
            else
                return;
        }
        modules.remove(className);
        clsmodule.unloadModule(this);
    }

    Thing modules() {
        Enumeration keys;
	Vector res = new Vector();
        for (keys = modules.keys(); keys.hasMoreElements();) {
	    res.addElement(new Thing((String)keys.nextElement()));
        }
	return ListThing.create(res);
    }
}
