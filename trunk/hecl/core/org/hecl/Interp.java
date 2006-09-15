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
public class Interp extends Thread/*implements Runnable*/ {
    /**
     * Package name prefix of the module classes.
     */
    public static final String MODULE_CLASS_PACKAGE = "org.hecl";
    public static final int DONT_WAIT = 1;
    public static final int IDLE_EVENTS = 2;
    public static final int TIMER_EVENTS = 4;
    public static final int ALL_EVENTS = ~DONT_WAIT;

    public static final String ASYNCPREFIX = "async";
    public static final String IDLEPREFIX = "idle";
    public static final String TIMERPREFIX = "timer";

    public long cacheversion = 0;

    /**
     * The <code>commands</code> <code>Hashtable</code> provides the
     * mapping from the strings containing command names to the code
     * implementing the commands.
     *
     * We save some space by making this public and removing the
     * accessors.
     *
     */
    public Hashtable commands = new Hashtable();

    /**
     * The <code>auxdata</code> <code>Hashtable</code> is a place to
     * store extra information about the state of the program.
     *
     */
    protected Hashtable auxdata = new Hashtable();

    public Thing result = null;
    protected Stack stack = new Stack();
    protected Stack error = new Stack();

    protected Vector timers = new Vector();
    protected Vector asyncs = new Vector();
    protected Vector idle = new Vector();
    protected Hashtable waittokens = new Hashtable();
    protected long idlegeneration = 0;
    protected boolean running = true;
    protected long maxblocktime = 0;	    // block time in milliseconds
    
    /**
     * Creates a new <code>Interp</code> instance, initializing command and
     * variable hashtables, a stack, and an error stack.
     *
     * @exception HeclException
     *                if an error occurs
     */
    public Interp() throws HeclException {
        // Set up stack frame for globals.
        stack.push(new Hashtable());
        initInterp();
	start();
    }


    /**
     * Add a new command to an <code>Interp</code>.
     *
     * @param name
     *            the name of the command to add.
     * @param c
     *            the command to add.
     */
    public synchronized String addCommand(String name,Command c) {
	commands.put(name,c);
	return name;
    }

    /**
     * Remove a command from an <code>Interp</code>.
     *
     * @param name
     *            the name of the command to add.
     */
    public synchronized void removeCommand(String name) {
	commands.remove(name);
    }


    /**
     * Attach auxiliary data to an <code>Interp</code>.
     */
    public synchronized void setAuxData(String key,Object value) {
	auxdata.put(key, value);
    }


    /**
     * Retrieve auxiliary data from an <code>Interp</code>.
     *
     * @return a <code>Object</code> value or <code>null</code> when no
     * auxiliary data under the given key is attached to the interpreter.
     */
    public synchronized Object getAuxData(String key) {
	return auxdata.get(key);
    }


    /**
     * Remove auxiliary data from an <code>Interp</code>.
     */
    public synchronized void removeAuxData(String key) {
	auxdata.remove(key);
    }

    /**
     * The <code>eval</code> method evaluates some Hecl code passed to
     * it.
     *
     * @return a <code>Thing</code> value - the result of the
     * evaluation.
     * @exception HeclException
     *                if an error occurs.
     */
    public Thing eval(Thing in) throws HeclException {
	result = null;
	CodeThing.get(this, in).run(this);
	return result;
    }

    public HeclTask evalIdle(Thing idleThing) {
	return addTask(idle,new HeclTask(idleThing,idlegeneration,IDLEPREFIX),-1);
    }


    public HeclTask evalAsync(Thing asyncThing) {
	return addTask(asyncs, new HeclTask(asyncThing,0,ASYNCPREFIX),-1);
    }

    public Thing evalAsyncAndWait(Thing in) throws HeclException {
	HeclTask t = evalAsync(in);
	t.setErrorPrint(false);
	boolean done = false;
	while(!t.isDone()) {
	    try {
		synchronized(t) {
		    t.wait();
		}
	    }
	    catch(Exception e) {
		// ignore
		e.printStackTrace();
	    }
	}
	try {
	    Exception e = t.getError();
	    if(e != null)
		throw e;
	    return t.getResult();
	}
	catch (HeclException he) {
	    throw he;
	}
	catch(Exception e) {
	    throw new HeclException(e.getMessage());
	}
    }
    
    /**
     * This version of <code>eval</code> takes a 'level' argument that
     * tells Hecl what level to run the code at.  Level 0 means
     * global, negative numbers indicate relative levels down from the
     * current stackframe, and positive numbers mean absolute stack
     * frames counting up from 0.
     *
     * @param in a <code>Thing</code> value
     * @param level an <code>int</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    public Thing eval(Thing in, int level) throws HeclException {
	Thing result = null;
	Vector savedstack = new Vector();
	int stacklen = stack.size();
	int i = 0;
	int end = 0;

	if (level >= 0) {
	    end = level;
	} else {
	    end = (stacklen - 1 + level);
	}

	/* Save the old stack frames... */
	for (i = stacklen - 1; i > end; i--) {
	    savedstack.addElement(stackDecr());
	}

	result = eval(in);

	/* ... and then restore them after evaluating the code. */
	for (i = savedstack.size() - 1; i >= 0; i--) {
	    stackPush((Hashtable)savedstack.elementAt(i));
	}

	return result;
    }

    
    public synchronized HeclTask getEvent(String name) {
	int n = timers.size();
	Vector v = new Vector();
	HeclTask t = null;
	for(int i=0; i<n; ++i) {
	    t = (HeclTask)timers.elementAt(i);
	    if(name.equals(t.getName()))
		return t;
	}
	n = idle.size();
	for(int i=0; i<n; ++i) {
	    t = (HeclTask)idle.elementAt(i);
	    if(name.equals(t.getName()))
		return t;
	}
	return null;
    }
    
    public synchronized Vector getAllEvents() {
	int n = timers.size();
	Vector v = new Vector();
	for(int i=0; i<n; ++i)
	    v.addElement(timers.elementAt(i));
	n = idle.size();
	for(int i=0; i<n; ++i)
	    v.addElement(timers.elementAt(i));
	return v;
    }
    
    public synchronized HeclTask addTimer(Thing timerThing,int millisecs) {
	int n = timers.size();
	long ts = System.currentTimeMillis()+millisecs;
	HeclTask t = new HeclTask(timerThing, ts,TIMERPREFIX);
	
	int i;
	for(i=0; i<n; ++i) {
	    HeclTask other = (HeclTask)timers.elementAt(i);
	    if(other.getGeneration() > ts)
		break;
	}
	//System.err.println("Adding timer, time="+ts);
	return addTask(timers,t,i);
    }
    

    public void cancelTimer(String name) {
	cancelTask(timers,name);
    }
    
    public void cancelIdle(String name) {
	cancelTask(idle,name);
    }
    
    public void cancelAsync(String name) {
	cancelTask(asyncs,name);
    }
    
    public synchronized void cancelIdle(HeclTask idletask) {
	idle.removeElement(idletask);
    }
    
    
    public boolean doOneEvent(int flags) {
	if((flags & ALL_EVENTS) == 0)
	    flags = ALL_EVENTS;

	// The core of this procedure is an infinite loop, even though
	// we only service one event.  The reason for this is that we
	// may be processing events that don't do anything inside of Hecl.
	int count = 0;
	while(true) {
	    // First check for async events...
	    HeclTask t = nextTask(asyncs,-1);
	    if(t != null) {
		return executeTask(t);
	    }
	    
	    long now = System.currentTimeMillis();

	    if((flags & TIMER_EVENTS) != 0) {
		t = nextTask(timers,now);
		if(t != null) {
		    return executeTask(t);
		}
	    }

	    // Determine maxblocktime
	    maxblocktime = (flags & DONT_WAIT) != 0 ? 0 : 1000;
	    synchronized(this) {
		if(timers.size() > 0) {
		    t = (HeclTask)timers.elementAt(0);
		    maxblocktime = t.getGeneration() - now;
		}
	    }
	    // this may reduce maxblocktime!
	    if((flags & IDLE_EVENTS) != 0) {
		serviceIdleTask();
	    }

	    if(count > 0 || maxblocktime <= 0)
		break;
	    //System.err.println("interp wait for "+maxblocktime);
	    yield();			    // give other thread a chance
	    synchronized(this) {
		try {
		    this.wait(maxblocktime);
		}
		catch (InterruptedException e) {
		    // it doesn't matter
		}
	    }
	    //System.err.println("interp wait done, next loop iteration");
	    ++count;
	}
	//System.err.println("<--doOneEvent:false");
	return false;
    }

    public void waitForToken(String tokenname) throws HeclException {
	boolean exists = false;
	WaitToken token = null;
	synchronized(this) {
	    exists = waittokens.containsKey(tokenname);
	    if(exists)
		throw new HeclException("Wait token '"+tokenname+"' already exists.");
	    token = new WaitToken();
	    waittokens.put(tokenname,token);
	}

	// Endless loop, some event in the future should kick us off this loop
	boolean b = true;
	while(b) {
	    // Carefully read/modify status information
	    synchronized(this) {
		b = token.waiting;
	    }
	    if(b) {
		// Service one event
		doOneEvent(Interp.ALL_EVENTS);
	    }
	}
    }

    public void notifyToken(String tokenname) throws HeclException {
	synchronized(this) {
	    WaitToken token = (WaitToken)waittokens.get(tokenname);
	    if(token == null)
		throw new HeclException("No wait token '"+tokenname+"'.");
	    token.waiting = false;
	    waittokens.remove(tokenname);
	}
    }

    /**
     * The <code>terminate</code> method terminates the Hecl
     * interpreter thread in a graceful manner. The thread will
     * eventually finish its run-method.
     *
     */
    public void terminate() {
	running = false;
	synchronized(this) {
	    notify();
	}
    }

    public void run() {
	//System.err.println("interp running...");
	long now = System.currentTimeMillis();
	while(running) {
	    doOneEvent(ALL_EVENTS);
	}
	//System.err.println("interp stopped!");
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
	/* Do not use the 'Facade' style commands as an example if you
	 * just have to add a simple command or two.  The pattern
	 * works best when you need to add several commands with
	 * related functionality. */

	/* Commands that manipulate interp data structures -
	 * variables, procs, commands, and so forth.  */
	InterpCmds.load(this);

	/* Math and logic commands. */
	MathCmds.load(this);

	/* List related commands. */
	ListCmds.load(this);

	/* Control commands. */
	ControlCmds.load(this);

	/* String commands. */
	StringCmds.load(this);

	/* Hash table commands. */
	HashCmds.load(this);

        commands.put("puts", new PutsCmd());
        commands.put("sort", new SortCmd());
    }

    /**
     * The <code>cmdRename</code> method renames a command, or throws
     * an error if the original command didn't exist.
     *
     * @param oldname a <code>String</code> value
     * @param newname a <code>String</code> value
     * @exception HeclException if an error occurs
     */
    public synchronized void cmdRename(String oldname, String newname)
	throws HeclException {
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
    public synchronized void stackIncr() {
        stackPush(new Hashtable());
    }

    /**
     * <code>stackDecr</code> pops the stack frame, returning it so that
     * commands like upeval can save it. If it's not saved, it's gone.
     *
     */
    public synchronized Hashtable stackDecr() {
        return (Hashtable) stack.pop();
    }

    /**
     * <code>stackDecr</code> pushes a new variable hashtable
     * (probably saved via upeval) onto the stack frame.
     *
     */
    public synchronized void stackPush(Hashtable vars) {
        cacheversion++;
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
        return getVar(varname.toString(), -1);
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
    public synchronized Thing getVar(String varname, int level) throws HeclException {
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
     * @param varname a <code>String</code> value
     * @param value a <code>Thing</code> value
     */
    public void setVar(String varname, Thing value) {
        setVar(varname, value, -1);
    }

    /**
     * <code>setVar</code> sets a variable to a value in the variable stack
     * frame specified by <code>level</code>.
     *
     * @param varname a <code>String</code> value
     * @param value a <code>Thing</code> value
     * @param level an <code>int</code> value
     */
    public synchronized void setVar(String varname, Thing value, int level) {
        Hashtable lookup = getVarhash(level);

	/* Bump the cache number so that SubstThing.get refetches the
	 * variable. */
        cacheversion++;
	if (lookup.containsKey(varname)) {
	     Thing oldval = (Thing) lookup.get(varname);

	     /* In order to make the 'global' command work, we check
	      * and see if the previous 'inhabitant' of the hashtable
	      * had its global flag set.  If that's the case, then we
	      * set the variable both at the local level, and at the
	      * global level.  */
	     if (oldval.global && level != 0) {
		 value.global = true;
		 Hashtable globalhash = getVarhash(0);
		 globalhash.put(varname, value);
	     }
	}
	lookup.put(varname, value);
    }


    /**
     * <code>unSetVar</code> unsets a variable in the current stack
     * frame.
     *
     * @param varname a <code>Thing</code> value
     */
    public void unSetVar(Thing varname) throws HeclException {
	unSetVar(varname.toString(),-1);
    }

    public synchronized void unSetVar(String varname) throws HeclException {
	unSetVar(varname,-1);
    }
    
    public synchronized void unSetVar(String varname,int level) throws HeclException {
        Hashtable lookup = getVarhash(level);
	/* Bump the cache number so that SubstThing.get refetches the
	 * variable. */
	Thing value = (Thing)lookup.get(varname);
	if (value != null) {
	    cacheversion++;
	    lookup.remove(varname);
	    if (value.global) {
		Hashtable globalhash = getVarhash(0);
		value = (Thing)globalhash.get(varname);
		if (value != null) {
		    globalhash.remove(varname);
		}
	    }
	} else {
            throw new HeclException("Variable " + varname + " does not exist");
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
        return existsVar(varname.toString());
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
    public synchronized boolean existsVar(String varname, int level) {
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
    public synchronized void setResult(Thing newresult) {
        result = newresult;
    }

    /**
     * <code>setResult</code> sets the interpreter result of the most recent
     * command.
     *
     * @param newresult a <code>String</code> value
     */
    public synchronized void setResult(String newresult) {
	if(newresult == null)
	    newresult = "";
	result = new Thing(newresult);
    }

    /**
     * <code>setResult</code> sets the interpreter result to the specified value.
     *
     * @param newresult a <code>long</code> value
     */
    public synchronized void setResult(long newresult) {
	result = LongThing.create(newresult);
    }

    /**
     * <code>setResult</code> sets the interpreter result of the most recent
     * command.
     *
     * @param newresult a <code>boolean</code> value
     */
    public synchronized void setResult(boolean newresult) {
	result = new Thing(newresult ? IntThing.ONE : IntThing.ZERO);
    }

    /**
     * <code>getResult</code> fetches the result saved by setResult.
     *
     * @return a <code>Thing</code> value
     */
    public synchronized Thing getResult() {
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


    /**
     * <code>nextTask</code> extracts first element from given vector.
     * This function operates in a synchronized manner on the argument
     * <code>v</code>.
     *
     * @param v
     * A <code>Vector</code> of tasks.
     * @param until
     * A value to compare the result of <code>getGeneration</code> of the
     * <code>HeclTask</code>. If <code>until</code> is less than 0 or
     * <code>getGeneration</code> is less than <code>until</code> for the
     * first  element of <code>v</code>, the first task in the vector is
     * returned, null otherwise.
     */
    protected synchronized HeclTask nextTask(Vector v,long until) {
	HeclTask t = null;
	
	if(v.size() > 0) {
	    t = (HeclTask)v.elementAt(0);
	    //System.err.println("now="+ts+", fire="+t.getGeneration());
	    if(until < 0 || t.getGeneration() <= until) {
		v.removeElementAt(0);
	    } else {
		t = null;
	    }
	}
	return t;
    }
    

    /**
     * Service at most one idle task of the idle task queue.
     *
     * @return a <code>boolean</code> indicatign that an idle task has been
     * serviced (=true) or not (=false).
     */
    protected boolean serviceIdleTask() {
	// The code below is trickier than it may look, for the following
	// reasons:
	//
	// 1. New handlers can get added to the list while the current
	//    one is being processed.  If new ones get added, we don't
	//    want to process them during this pass through the list (want
	//    to check for other work to do first).  This is implemented
	//    using the generation number in the handler:  new handlers
	//    will have a different generation than any of the ones currently
	//    on the list.
	// 2. The handler can call doOneEvent, so we have to remove
	//    the handler from the list before calling it. Otherwise an
	//    infinite loop could result.
	// 3. cancelIdleCall can be called to remove an element from
	//    the list while a handler is executing, so the list could
	//    change structure during the call.
	long oldgeneration;
	synchronized(this) {
	    if(idle.size()== 0) {
		idlegeneration = 0;
		return false;
	    }
	    oldgeneration = idlegeneration;
	    ++idlegeneration;
	}
	HeclTask t = nextTask(idle,oldgeneration);
	if(t != null)
	    t.execute(this);
	if(idle.size() > 0)
	    maxblocktime = 0;
	return true;
    }
    

    /**
     * Service at most one idle task of the idle task queue.
     *
     * @param v
     * A <code>Vector</code> of tasks to add the task to.
     * @param task
     * The <code>HeclTask</code> to add.
     * @param pos
     * The </code>int</code> position of <code>v</code> where to add
     * <code>task</code> being in the range from 0 to <code>v.size()</code>.
     * -1 indicates to add <code>task</code> to the end of <code>v</code>.
     *
     * @return A <code>String</code> being the name of the inserted task.
     */
    private synchronized HeclTask addTask(Vector v,HeclTask task,int pos) {
	if(pos < 0)
	    v.addElement(task);
	else
	    v.insertElementAt(task,pos);
	notify();
	setResult(task.getName());
	return task;
    }
    

    /**
     * Cancel a task of the specified name in the specified vector.
     * The functions performs nothing when no task of the specified name is
     * an element of the vector.
     *
     * @param v
     * A vector of <code>HeclTask</code>s.
     * @param name
     * A <code>String</code> specifying the name of the task to be removed
     * from <code>v</code>.
     */
    private synchronized void cancelTask(Vector v,String name) {
	int n = v.size();
	for(int i = 0; i<n; ++i) {
	    HeclTask t = (HeclTask)v.elementAt(i);
	    if(name.equals(t.getName())) {
		v.removeElementAt(i);
		return;
	    }
	}
    }


    /**
     * Execute a <code>task</code>.
     *
     * @return Always the boolean value <code>true</code> to indicate that a
     * task has been serviced.
     */
    private boolean executeTask(HeclTask task) {
	try {
	    task.execute(this);
	}
	catch(Exception e) {
	    // Nothing to do. It is expected that each task handles
	    // its own locally exception but this block is to ensure
	    // that the queue continues to operate
	}
	return true;
    }

    protected static class WaitToken {
	public volatile boolean waiting = true;
    }
    
}
