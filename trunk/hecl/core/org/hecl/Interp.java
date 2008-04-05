/* Copyright 2004-2007 David N. Welton, DedaSys LLC

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

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

//#if j2se
import java.util.LinkedList;
import java.util.List;
import jline.ArgumentCompletor;
import jline.ConsoleReader;
import jline.NullCompletor;
import jline.SimpleCompletor;
//#endif


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

    /**
     * Flags for the event loop.
     */
    public static final int DONT_WAIT = 1;
    public static final int IDLE_EVENTS = 2;
    public static final int TIMER_EVENTS = 4;
    public static final int ALL_EVENTS = ~DONT_WAIT;

    /**
     * Some string constants used to generate names for internal events.
     */
    public static final String ASYNCPREFIX = "async";
    public static final String IDLEPREFIX = "idle";
    public static final String TIMERPREFIX = "timer";

    /**
     * The prompt for the <code>readEvalPrint</code> loop.
     */
    public final String PROMPT = "hecl> ";

    /**
     * The prompt for continued lines in the <code>readEvalPrint</code> loop.
     */
    public final String PROMPT2 = "hecl+ ";

    /**
     * A <code>Thing</code> to indicate  global reference.
     */
    static final Thing GLOBALREFTHING = new Thing("");

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
    protected Hashtable commands = new Hashtable();

    /**
     * The <code>auxdata</code> <code>Hashtable</code> is a place to
     * store extra information about the state of the program.
     *
     */
    protected Hashtable auxdata = new Hashtable();

    protected Stack stack = new Stack();
    protected Stack error = new Stack();

    protected Vector timers = new Vector();
    protected Vector asyncs = new Vector();
    protected Vector idle = new Vector();
    protected Hashtable waittokens = new Hashtable();
    protected long idlegeneration = 0;
    protected boolean running = true;
    protected long maxblocktime = 0;	    // block time in milliseconds
    protected Vector ci = new Vector();
    protected Hashtable classcmdcache = new Hashtable();
    
    /**
     * Creates a new <code>Interp</code> instance, initializing command and
     * variable hashtables, a stack, and an error stack.
     *
     * @exception HeclException if an error occurs
     */
    public Interp() throws HeclException {
        // Set up stack frame for globals.
        stack.push(new Hashtable());
        initInterp();
	start();
    }

//#ifdef j2se
    protected String[] hashKeysToArray(Hashtable h) {
	return hashKeysToArray(h, "");
    }

    protected String[] hashKeysToArray(Hashtable h, String prefix) {
	Vector<String> cmds = new Vector<String>();
	for (Enumeration e = h.keys(); e.hasMoreElements();) {
	    cmds.add(prefix + (String)e.nextElement());
	}
	String[] scmds = new String[cmds.size()];
	cmds.copyInto(scmds);
	return scmds;
    }
//#endif


    /**
     * The <code>commandLine</code> method implements a
     * Read/Eval/Print Loop.
     *
     * @param in Input stream to read input from.
     * @param out Output stream to print results to.
     * @param err Output stream for error messages.
     *
     * This function never returns.
     */
    public void readEvalPrint(InputStream in, PrintStream out, PrintStream err) {
	String prompt = PROMPT;
	StringBuffer sb = new StringBuffer();
//#if j2se
        List completors = null;
	ConsoleReader reader = null;
	int oldsz = 0;
	int newsz = 0;

//#else
	InputStreamReader reader = new InputStreamReader(in);
//#endif
	while(true) {
	    byte outbytes[] = null;

	    String line = null;
//#if j2se

	    Hashtable vars = getVarhash(-1);
	    newsz = commands.size() + vars.size();

	    /* If the number of commands or variables has increased,
	     * reindex them in the command line completor.  Currently,
	     * it uses commands for the first completion, and variable
	     * names (with leading $) for subsequent completions. */
	    if (newsz > oldsz) {
		completors = new LinkedList();
		completors.add(
		    new SimpleCompletor(hashKeysToArray(commands)));
		completors.add(
		    new SimpleCompletor(hashKeysToArray(vars, "$")));

		completors.add(new NullCompletor());

		try {
		    reader = new ConsoleReader();
		    reader.addCompletor(new ArgumentCompletor(completors));
		} catch (IOException e) {
		    System.err.println(e);
		    return;
		}

		oldsz = newsz;
	    }

	    try {
		line = reader.readLine(prompt);
	    } catch (IOException e) {
		err.println(e);
		break;
	    }

//#else
	    out.print(prompt);
	    out.flush();
	    line = readLine(reader);
//#endif
	    if(line == null)
		break;
	    if(sb.length() > 0)
		sb.append('\n');
	    sb.append(line);
	    try {
		if(sb.length() <= 0)
		    continue;

		Thing res = evalAsyncAndWait(new Thing(sb.toString()));
		if (res != null) {
		    String s = res.toString();
		    if(s.length() > 0) {
			// It seems that DataOutputStream.println(String)
			// is broken and returns OutOfmemory when the
			// string is to long, so we convert the string
			// into bytes and write out the pure bytes
			// directly.
			outbytes = s.getBytes();
		    }
		}
		sb.delete(0,sb.length());
		prompt = PROMPT;
	    }
	    catch(HeclException he) {
		if (he.code.equals("PARSE_ERROR")) {
		    // Change prompt and get more input
		    prompt = PROMPT2;
		} else {
		    sb.delete(0,sb.length());
		    he.printStackTrace();
		    outbytes = he.getMessage().getBytes();
		    prompt = PROMPT;
		}
	    }
	    if(outbytes != null) {
		// result output
		try {
		    out.write(outbytes);
		    out.println();
		}
		catch(IOException ioex) {
		    err.println(ioex.getMessage());
		    break;
		}
		outbytes = null;
	    }
	}
    }

    /**
     * Add a new class command to an <code>Interp</code>.
     *
     * @param clazz The Java class the command should operate on.
     * @param cmd The command to add. When this paramter is <code>null</code>,
     * an existing command is removed.
     */
    public void addClassCmd(Class clazz,ClassCommand cmd) {
	// clear cache first, even when deleting a cmd
	this.classcmdcache.clear();
	
	int l = this.ci.size();
	for(int i=0; i<l; ++i) {
	    ClassCommandInfo info = (ClassCommandInfo)this.ci.elementAt(i);
	    if(info.forClass() == clazz) {
		//identical, replace
		if(cmd == null) {
		    ci.removeElementAt(i);
		} else {
		    info.setCommand(cmd);
		}
		return;
	    }
	}
	if(cmd != null)
	    this.ci.addElement(new ClassCommandInfo(clazz,cmd));
    }

    /**
     * Remove a command for a specific class from an <code>Interp</code>.
     *
     * @param clazz The class to remove the command for.
     */
    public void removeClassCmd(Class clazz) { addClassCmd(clazz,null);}
	    
    
     /**
     * Add a new class command to an <code>Interp</code>.
     *<br>
     * The current implementation does not support any subclassing and selects
     * the first class command <code>clazz</code> is assignable to.
     *
     * @param clazz The Java class to look up the class command for.
     * @return A <code>ClassCommandInfo</code> decsribing the class command,
     * or <code>null</null> if no command was found.
     */
    ClassCommandInfo findClassCmd(Class clazz) {
	ClassCommandInfo found = (ClassCommandInfo)this.classcmdcache.get(clazz);

	if(found == null) {
	    // No entry in cache, so we loop over all class commands and try
	    // to detect the most specific one.

	    int l = this.ci.size();
	    for(int i=0; i<l; ++i) {
		ClassCommandInfo info = (ClassCommandInfo)this.ci.elementAt(i);
		Class cl2 = info.forClass();
		if(cl2.isAssignableFrom(clazz)) {
		    //System.err.println("clazz="+clazz+" assignable to cl="+cl2);
		    if(found == null)
			found = info;
		    else {
			// check if this is more specialized than the one we
			// already have.
			if(found.forClass().isAssignableFrom(cl2)) {
			    //System.err.println("superclass="+found.forClass()+" for cl="+cl2);
			    found = info;
			}
			// else keep existing one
		    }
		}
	    }
	    // Add what we found to the cache, so we do not need to look it up
	    // next time.
	    if(found != null)
		this.classcmdcache.put(clazz,found);
	}
	return found;
    }
    
    
    /**
     * Add a new command to an <code>Interp</code>.
     *
     * @param name the name of the command to add.
     * @param c the command to add.
     */
    public synchronized String addCommand(String name,Command c) {
	commands.put(name,c);
	return name;
    }

    /**
     * Remove a command from an <code>Interp</code>.
     *
     * @param name the name of the command to add.
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
     * @exception HeclException if an error occurs.
     */
    public synchronized Thing eval(Thing in) throws HeclException {
	//System.err.println("-->eval: "+in.toString());
	return CodeThing.get(this, in).run(this);
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
	HeclException save_exception = null;

	if (level >= 0) {
	    end = level;
	} else {
	    end = (stacklen - 1 + level);
	}

	/* Save the old stack frames... */
	for (i = stacklen - 1; i > end; i--) {
	    savedstack.addElement(stackDecr());
	}
	try {
	    result = eval(in);
	} catch (HeclException he) {
	    /* If this is an upeval situation, we need to catch the
	     * exception and then throw it *after* the old stack frame
	     * has been restored.  */
	    save_exception = he;
	}
	/* ... and then restore them after evaluating the code. */
	for (i = savedstack.size() - 1; i >= 0; i--) {
	    stackPush((Hashtable)savedstack.elementAt(i));
	}
	if (save_exception != null) {
	    throw save_exception;
	}

	return result;
    }

    
    public boolean hasIdleTasks() {
	return this.idle.size() == 0;
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

    public HeclTask addTimer(Thing timerThing, int millisecs) {
	synchronized (timers) {
	    int n = timers.size();
	    long ts = System.currentTimeMillis()+millisecs;
	    HeclTask t = new HeclTask(timerThing, ts, TIMERPREFIX);

	    int i;
	    for(i=0; i<n; ++i) {
		HeclTask other = (HeclTask)timers.elementAt(i);
		if(other.getGeneration() > ts)
		    break;
	    }
	    //System.err.println("Adding timer, time="+ts);
	    return addTask(timers,t,i);
	}
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
	    if ((flags & DONT_WAIT) != 0) {
		maxblocktime = 0;
	    } else {
		maxblocktime = 1000;
		synchronized(this) {
		    if(timers.size() > 0) {
			t = (HeclTask)timers.elementAt(0);
			maxblocktime = t.getGeneration() - now;
		    }
		}
	    }
	    // this may reduce maxblocktime!
	    if((flags & IDLE_EVENTS) != 0) {
		serviceIdleTask();
	    }

	    if(count > 0 || maxblocktime <= 0)
		break;

	    yield();			    // give other thread a chance
	    synchronized(this) {
		try {
		    this.wait(maxblocktime);
		} catch (InterruptedException e) {
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
//	System.err.println("interp running...");
	long now = System.currentTimeMillis();
	while(running) {
	    doOneEvent(ALL_EVENTS);
	}
//	System.err.println("interp stopped!");
    }


    /**
     * The <code>initCommands</code> method initializes all the built in
     * commands. These are commands available in all versions of Hecl. J2SE
     * commands are initialized in Standard.java, and J2ME commands in
     * Micro.java.
     *
     * @exception HeclException if an error occurs
     */
    private void initInterp() throws HeclException {
	/* Do not use the 'Facade' style commands as an example if you
	 * just have to add a simple command or two.  The pattern
	 * works best when you need to add several commands with
	 * related functionality. */

	//	System.err.println("-->initinterp");
	//	System.err.println("loading interp cmds...");
	/* Commands that manipulate interp data structures -
	 * variables, procs, commands, and so forth.  */
	InterpCmds.load(this);

	//	System.err.println("loading math cmds...");
	/* Math and logic commands. */
	MathCmds.load(this);

	//	System.err.println("loading list cmds...");
	/* List related commands. */
	ListCmds.load(this);

	//	System.err.println("loading control cmds...");
	/* Control commands. */
	ControlCmds.load(this);

	//	System.err.println("loading string cmds...");
	/* String commands. */
	StringCmds.load(this);

	//	System.err.println("loading hash cmds...");
	/* Hash table commands. */
	HashCmds.load(this);

        commands.put("puts", new PutsCmd());
        commands.put("sort", new SortCmd());
	//	System.err.println("<--initinterp");
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
	commands.remove(oldname);
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
	return level < 0 ? (Hashtable)stack.peek()
	    : (Hashtable)stack.elementAt(level);
    }

    /**
     * <code>getVar</code> returns the value of a variable given its name.
     *
     * @param varname a <code>Thing</code> value
     *
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    public Thing getVar(Thing varname) throws HeclException {
        return getVar(varname.toString(), -1);
    }

    /**
     * <code>getVar</code> returns the value of a variable given its name.
     *
     * @param varname a <code>String</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    public Thing getVar(String varname) throws HeclException {
        return getVar(varname, -1);
    }

    /**
     * <code>getVar</code> returns the value of a variable given its name and
     * level.
     *
     * @param varname a <code>String</code> value
     * @param level an <code>int</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    public synchronized Thing getVar(String varname, int level) throws HeclException {
        Hashtable lookup = getVarhash(level);
	//System.out.println("getvar: " + varname + " " + level + " " + lookup);
        Thing res = (Thing) lookup.get(varname);
//#ifdef old
        if (res == null) {
            throw new HeclException("Variable " + varname + " does not exist");
        }
        return res;
//#else
	if(res == GLOBALREFTHING) {
	    // ref to a global var
	    Hashtable globalhash = getVarhash(0);
	    res = (Thing)globalhash.get(varname);
	    if(res == GLOBALREFTHING) {
		// should not happen, but just in case...
		System.err.println("Unexpected GLOBALREFTHING in globalhash");
		res = null;
	    }
//#ifdef emptyglobals
	    else if (res == null) {
		// Return a fake empty value for a non-set global variable for
		// the sake of modifying commands.
		// !!!! THIS IS STRANGE !!!
		System.err.println("FAKE EMPTY VALUE for global var");
		res = new Thing("");
		globalhash.put(varname,res);
	    }
//#endif
	}
        if (res == null) {
            throw new HeclException("Variable " + varname + " does not exist");
        }
	//System.err.println("<<getvar, res="+res);
        return res;
//#endif
    }

    /**
     * <code>setVar</code> sets a variable in the innermost variable stack
     * frame to a value.
     *
     * @param varname a <code>Thing</code> value
     * @param value a <code>Thing</code> value
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

	// Bump the cache number so that SubstThing.get refetches the
	// variable.
        cacheversion++;
	//if(value == GLOBALREFTHING) System.err.println("flag '"+varname+"' as global on level="+level);
	//System.err.println("set local("+level+") var="+varname + ", val="+value.toString());

//#ifdef old
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
//#else

	if (value.isLiteral()) {
	    try {
		Thing copy = value.deepcopy();
		value = copy;
	    } catch (HeclException he) {
		/* This isn't going to happen - we're dealing with a
		 * literal from the parser. */
		System.err.println("Interp.java: This can never happen!");
	    }
	}

	// first take care of GLOBALREFTHING used to flag ref to global var
	if(value == GLOBALREFTHING) {
	    // do not clutter global table with GLOBALREFTHING
	    Hashtable globalhash = getVarhash(0);
	    if(lookup != globalhash) {
		//System.err.println(" not on global level");
		lookup.put(varname, value);
//#ifdef emptyglobals
		if(null == globalhash.get(varname)) {
		    // Insert a new empty thing at top level for the sake of
		    // modifying commands.
		    //System.err.println(" inserting empty global value");
		    globalhash.put(varname, new Thing(""));
		}
//#endif
	    } else {
		//System.err.println(" ignored, already in global scope");
	    }
	    return;
	}
	
	if(lookup.containsKey(varname)) {
	    Thing oldval = (Thing)lookup.get(varname);
	    if(oldval == GLOBALREFTHING) {
		// level must be at != 0
		//System.err.println(" forwarded to global value");
		lookup = getVarhash(0);
	    }
	}
	lookup.put(varname, value);
//#endif
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
	// Bump the cache number so that SubstThing.get refetches the
	// variable.
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
     * @param varname a <code>Thing</code> value
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
     * @param varname a <code>String</code> value
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
     * @param varname a <code>String</code> value
     * @param level an <code>int</code> value
     * @return a <code>boolean</code> value
     */
    public synchronized boolean existsVar(String varname, int level) {
        Hashtable lookup = getVarhash(level);
        return lookup.containsKey(varname);
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
     * <code>checkArgCount</code> checks to see whether the command
     * actually has the required number of arguments. The first element of the
     * parameter array <code>argv</code> is not counted as argument!
     *
     * @param argv A <code>Thing[]</code> parameter array.
     * @param minargs The minimal number of arguments or -1 if no check is required.
     * @param maxargs The maximal number of arguments or -1 if no check is required.
     * @exception HeclException if an error occurs
     */
    public static void checkArgCount(Thing[] argv,int minargs,int maxargs) 
	throws HeclException {
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
     * <code>nextTask</code> extracts first element from given vector.
     * This function operates in a synchronized manner on the argument
     * <code>v</code>.
     *
     * @param v A <code>Vector</code> of tasks.
     * @param until A value to compare the result of
     * <code>getGeneration</code> of the <code>HeclTask</code>. If
     * <code>until</code> is less than 0 or <code>getGeneration</code>
     * is less than <code>until</code> for the first element of
     * <code>v</code>, the first task in the vector is returned, null
     * otherwise.
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
     * @param v A <code>Vector</code> of tasks to add the task to.
     * @param task The <code>HeclTask</code> to add.
     * @param pos The </code>int</code> position of <code>v</code> where to
     * add <code>task</code> being in the range from 0 to
     * <code>v.size()</code>.  -1 indicates to add <code>task</code> to the
     * end of <code>v</code>.
     *
     * @return A <code>String</code> being the name of the inserted task.
     */
    private HeclTask addTask(Vector v,HeclTask task,int pos) {
	synchronized (v) {
	if(pos < 0)
	    v.addElement(task);
	else
	    v.insertElementAt(task,pos);
//	notify();
	return task;
	}
    }
    

    /**
     * Cancel a task of the specified name in the specified vector.
     * The functions performs nothing when no task of the specified name is
     * an element of the vector.
     *
     * @param v A vector of <code>HeclTask</code>s.
     * @param name A <code>String</code> specifying the name of the
     * task to be removed from <code>v</code>.
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
     * @return Always the boolean value <code>true</code> to indicate
     * that a task has been serviced.
     */
    private boolean executeTask(HeclTask task) {
	try {
	    task.execute(this);
	}
	catch(Exception e) {
	    // Nothing to do. It is expected that each task handles
	    // its exceptions locally but we use this block is to ensure
	    // that the queue continues to operate.
	}
	return true;
    }

    protected static class WaitToken {
	public volatile boolean waiting = true;
    }

    static String readLine(InputStreamReader is) {
	StringBuffer b = new StringBuffer();
	int ch = -1;

	try {
	    while ((ch = is.read()) != -1) {
		if(ch == '\r')
		    continue;
		if(ch == '\n')
		    break;
		b.append((char)ch);
	    }
	}
	catch(IOException iox) {
	}
	if(b.length() > 0 || ch != -1)
	    return b.toString();
	return null;
    }
}
