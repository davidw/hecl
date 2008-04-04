/* Copyright 2006 David N. Welton

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

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * The <code>InterpCmds</code> implements various Hecl commands that
 * deal with the state of the interpreter.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
class InterpCmds extends Operator {
    public static final int SET = 1;
    public static final int UNSET = 2;
    public static final int PROC = 3;
    public static final int RENAME = 4;
    public static final int EVAL = 5;
    public static final int GLOBAL = 6;
    public static final int INTROSPECT = 7;
    public static final int RETURN = 8;
    public static final int CATCH = 9;
    public static final int EXIT = 10;
    public static final int UPCMD = 11;
    public static final int TIMECMD = 12;

    public static final int COPY = 13;
    public static final int THROW = 14;
    public static final int AFTER = 15;
    public static final int BGERROR = 16;
    public static final int TOKENWAIT = 17;
    public static final int TOKENNOTIFY = 18;

    protected static final int GC = 19;
    protected static final int GETPROP = 20;
    protected static final int HASPROP = 21;
    protected static final int CLOCKCMD = 22;

    protected static final int FREEMEM = 23;
    protected static final int TOTALMEM = 24;


    public static final int HASCLASS = 70;//Class.forName()

    public static final int CLASSINFO = 80; // hecl internal!!!

//#if android || j2se
    public static final int GETINTERP = 100;
//#endif

    public Thing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	Thing result = null;
	int retval = 0;
	String subcmd  = null;
	
	switch (cmd) {
	  case SET:
	    if (argv.length == 3) {
		interp.setVar(argv[1], argv[2]);
		return argv[2];
	    }
	    return interp.getVar(argv[1]);

	  case COPY:
	    return argv[1].deepcopy();

	  case UNSET:
	    interp.unSetVar(argv[1]);
	    break;

	  case PROC:
	    interp.commands.put(argv[1].toString(), new Proc(argv[2], argv[3]));
	    break;

	  case RENAME:
	    interp.cmdRename(argv[1].toString(), argv[2].toString());
	    break;

	  case EVAL:
	    return interp.eval(argv[1]);

	  case GLOBAL:
	    ;
//#ifdef notdef
	    {
		String varname = null;
		Thing newThing = null;
		for (int i = 1; i < argv.length; i ++) {
		    varname = argv[i].toString();
		    newThing = null;
		    
		    if (!interp.existsVar(varname, 0)) {
			/* Create a new value for it. */
			newThing = new Thing("");
		    } else {
			/* If it already exists, make a copy of it
			 * that is no longer marked to be copied. */
			newThing = interp.getVar(varname, 0);
		    }
		    newThing.global = true;
		    interp.setVar(varname, newThing);
		    interp.setVar(varname, newThing, 0);
		}
		break;
	    }
//#else
	    for (int i = 1; i < argv.length; i ++) {
		interp.setVar(argv[i].toString(),Interp.GLOBALREFTHING,-1);
	    }
//#endif
	    break;
	    
	  case INTROSPECT:
	    subcmd = argv[1].toString();
	    Vector results = new Vector();

	    if (subcmd.equals("commands")) {
		for (Enumeration e = interp.commands.keys(); e.hasMoreElements();) {
		    Thing t = new Thing((String) e.nextElement());
		    results.addElement(t);
		}
		return ListThing.create(results);
	    }
	    if (subcmd.equals("proccode")) {
		Proc p = (Proc)interp.commands.get(argv[2].toString());
		return new Thing(p.getCode().getVal());
	    }
	    break;

	  case RETURN:
	    throw new HeclException("", HeclException.RETURN,
				    argv.length > 1 ? argv[1] : Thing.emptyThing());

	  case CATCH:
	    try {
		result = interp.eval(argv[1]);
		retval = 0;
	    } catch (HeclException e) {
		result = e.getStack();
		retval = 1;
	    }

	    if (argv.length == 3) {
		interp.setVar(argv[2].toString(),
			      result!= null ? result : Thing.emptyThing());
	    }
	    return new Thing(retval != 0 ? IntThing.ONE : IntThing.ZERO);

	  case THROW:
	    String errmsg = argv[1].toString();
	    if (argv.length == 2) {
		throw new HeclException(errmsg);
	    }
	    throw new HeclException(errmsg, argv[2].toString());

	  case AFTER:
	    subcmd = argv[1].toString();
	    if(subcmd.equals("info")) {
		if(argv.length == 2) {
		    Vector v = interp.getAllEvents();
		    int n = v.size();
		    for(int i=0; i<n; ++i) {
			HeclTask t = (HeclTask)v.elementAt(i);
			v.setElementAt(new Thing(t.getName()),i);
		    }
		    return ListThing.create(v);
		}
		if(argv.length == 3) {
		    String evname = argv[2].toString();
		    HeclTask t = interp.getEvent(evname);
		    // event specified, must exist
		    if(t != null) {
			Vector v = new Vector();
			v.addElement(new Thing(t.getScript().toString()));
			v.addElement(new Thing(t.getType()));
			return ListThing.create(v);
		    }
		    throw new HeclException("Event '"+evname+"' doesn't exist.");
		}
		throw HeclException.createWrongNumArgsException(argv,2,"?id?");
	    }
	    if(subcmd.equals("cancel")) {
		for(int i=2; i<argv.length; ++i) {
		    String s = argv[2].toString();
		    if(s.startsWith(Interp.IDLEPREFIX))
			interp.cancelIdle(s);
		    else if(s.startsWith(Interp.TIMERPREFIX))
			interp.cancelTimer(s);
		    else if(s.startsWith(Interp.ASYNCPREFIX))
			interp.cancelAsync(s);
		}
		break;
	    }
	    if(subcmd.equals("idle")) {
		if(argv.length != 3)
		    throw HeclException.createWrongNumArgsException(
			argv,2,"script");
		interp.evalIdle(argv[2]);
		break;
	    }
	    int milli = IntThing.get(argv[1]);
	    if(milli >= 0) {
		switch(argv.length) {
		  case 3:
		      return new Thing((interp.addTimer(argv[2],milli)).getName());
		  case 2:
		    ;			    // fool emacs indentation...
		    {
			HeclTask t = interp.addTimer(Thing.emptyThing(),milli);
			while(! t.isDone()) {
			    interp.doOneEvent(Interp.ALL_EVENTS);
			}
		    }
		    break;
		  default:
		    throw HeclException.createWrongNumArgsException(
			argv,2,"script");
		}
		break;
	    }
	    throw new HeclException("Unknown after option '"+subcmd+"'.");
	  
	  case TOKENWAIT:
	    subcmd = argv[1].toString();    // varname
	    interp.waitForToken(argv[1].toString());
	    break;

	  case TOKENNOTIFY:
	    interp.notifyToken(argv[1].toString());
	    break;
	    
	  case BGERROR:
	    System.err.println("bgerror - "+argv[1].toString());
	    break;
	    
	  case EXIT:
	    retval = 0;
	    if (argv.length > 1) {
		retval = IntThing.get(argv[1]);
	    }
	    System.exit(retval);
	    break;

	  case UPCMD:
	    Hashtable save = null;
	    Thing code = null;
	    int level = -1;

	    if (argv.length == 2) {
		code = argv[1];
	    } else if (argv.length == 3) {
		code = argv[2];
		level = IntThing.get(argv[1]);
	    }
	    return interp.eval(code, level);
		
	  case TIMECMD:
	    int times = 1;
	    
	    if (argv.length > 2) {
		times = NumberThing.asNumber(argv[2]).intValue();
	    }
	    long then = new Date().getTime();
	    while (times > 0) {
		interp.eval(argv[1]);
		times--;
	    }
	    return LongThing.create(new Date().getTime() - then);
	    
	  case HASCLASS:
	    // beware: you may be get fooled in j2me when you use an
	    // obfuscator: custom class names may get changed. Use only for
	    // system-defined classes!
	    retval = 0;
	    try {
		retval = null != Class.forName(argv[1].toString()) ? 1 : 0;
	    }
	    catch (Exception e) {}
	    return IntThing.create(retval);

	  case CLASSINFO:
	    return new Thing("<"+argv[1].getVal().thingclass()+">");

//#if android || j2se
	    case GETINTERP:
		return ObjectThing.create(interp);
//#endif

	  case GC:
	    System.gc();
	    break;
	    
	  case GETPROP:
	    String s = System.getProperty(argv[1].toString());
	    return new Thing(s != null ? s : "");
	    
	  case HASPROP:
	    return IntThing.create(System.getProperty(argv[1].toString())!=null ? 1 : 0);

	  case CLOCKCMD:
	    subcmd = argv[1].toString();
	    {
		long l = System.currentTimeMillis();
		if(subcmd.equals("seconds"))
		    return LongThing.create(l/1000);
		if(subcmd.equals("time") || subcmd.equals("milli"))
		    return LongThing.create(l);
		if(subcmd.equals("format")) {
		    // to bad, j2me does not support DataFormat,
		    if(argv.length == 3)
			return new Thing(
			    new ListThing((new Date(LongThing.get(argv[2]))).toString()));
		    throw HeclException.createWrongNumArgsException(argv,2,"?milli?");
		}
		throw HeclException.createWrongNumArgsException(argv,1,"option ?time?");
	    }

/*
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(new Date(l));
  
		    String fmtstr;
		    StringBuffer sb = new StringBuffer();
		    boolean percseen = false;
		    for(int i = 0; i<fmtstr.length(); ++i) {
			char ch = fmtstr.charAt(i);
			if(ch == '%') {
			    if(percseen) {
				sb.append('%');
				percseen = false;
			    } else
				percseen = true;
			    continue;
			}

// %a Abbreviated weekday name (Mon, Tue, etc.).
// %A Full weekday name (Monday, Tuesday, etc.).
// %b Abbreviated month name (Jan, Feb, etc.).
// %B Full month name.
// %C First two digits of the four-digit year (19 or 20).
// %d Day of month (01 - 31).
// %D Date as %m/%d/%y.
// %e Day of month (1 - 31), no leading zeros.
// %h Abbreviated month name.
// %H Hour in 24-hour format (00 - 23).
// %I Hour in 12-hour format (01 - 12).
// %j Day of year (001 - 366).
// %k Hour in 24-hour format, without leading zeros (0 - 23).
// %l Hour in 12-hour format, without leading zeros (1 - 12).
// %m Month number (01 - 12).
// %M Minute (00 - 59). 
// %n Insert a newline. 
// %p AM/PM indicator. 
// %R Time as %H:%M. 
// %s Count of seconds since the epoch, expressed as a decimal integer. 
// %S Seconds (00 - 59). 
// %t Insert a tab. 
// %T Time as %H:%M:%S. 
// %u Weekday number (Monday = 1, Sunday = 7). 
// %U Week of year (00 - 52), Sunday is the first day of the week. 
// %V Week of year according to ISO-8601 rules. Week 1 of a given year is the
//    week containing 4 January.  
// %w Weekday number (Sunday = 0, Saturday = 6). 
// %W Week of year (00 - 52), Monday is the first day of the week. 
// %y Year without century (00 - 99).
// %Y Year with century (e.g. 1990)
// %Z Time zone name.
			switch(ch) {
			  case 'a':
			    sb.append(cal.get(Calendar.
			  default:
			    sb.append(ch);
			}
		    }
		    return new StringThing(sb);
*/
	  case FREEMEM:
	    return LongThing.create(Runtime.getRuntime().freeMemory());
	    
	  case TOTALMEM:
	    return LongThing.create(Runtime.getRuntime().totalMemory());

	  default:
	    throw new HeclException("Unknown interp command '"
				    + argv[0].toString() + "' with code '"
				    + cmd + "'.");
	}
	return null;
    }


    public static void load(Interp ip) throws HeclException {
	Operator.load(ip,cmdtable);
    }


    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip,cmdtable);
    }


    private InterpCmds(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }

    private static Hashtable cmdtable = new Hashtable();

    static {
	cmdtable.put("set", new InterpCmds(SET, 1 ,2));
        cmdtable.put("unset", new InterpCmds(UNSET, 1, 1));
        cmdtable.put("proc", new InterpCmds(PROC, 3, 3));
        cmdtable.put("rename", new InterpCmds(RENAME, 2, 2));
        cmdtable.put("eval", new InterpCmds(EVAL, 1, 1));
        cmdtable.put("global", new InterpCmds(GLOBAL, 0, -1));
        cmdtable.put("intro", new InterpCmds(INTROSPECT, 1, -1));
        cmdtable.put("return", new InterpCmds(RETURN, 0, 1));
        cmdtable.put("catch", new InterpCmds(CATCH, 1, 2));
        cmdtable.put("throw", new InterpCmds(THROW, 1, 2));
        cmdtable.put("exit", new InterpCmds(EXIT, 0, 1));
        cmdtable.put("upeval", new InterpCmds(UPCMD, 1, 2));
        cmdtable.put("time", new InterpCmds(TIMECMD, 1, 2));
        cmdtable.put("after", new InterpCmds(AFTER, 1, -1));
	cmdtable.put("bgerror", new InterpCmds(BGERROR, 1, 1));
        cmdtable.put("twait", new InterpCmds(TOKENWAIT, 1, 1));
        cmdtable.put("tnotify", new InterpCmds(TOKENNOTIFY, 1, 1));

        cmdtable.put("copy", new InterpCmds(COPY, 1, 1));

	cmdtable.put("system.gc", new InterpCmds(GC,0,0));
	cmdtable.put("system.getproperty", new InterpCmds(GETPROP,1,1));
	cmdtable.put("system.hasproperty", new InterpCmds(HASPROP,1,1));
	cmdtable.put("clock", new InterpCmds(CLOCKCMD,1,2));

	cmdtable.put("runtime.freememory", new InterpCmds(FREEMEM,0,0));
	cmdtable.put("runtime.totalmemory", new InterpCmds(TOTALMEM,0,0));

        cmdtable.put("hasclass", new InterpCmds(HASCLASS, 1, 1));

        cmdtable.put("classof", new InterpCmds(CLASSINFO, 1, 1));

//#if android || j2se
        cmdtable.put("thisinterp", new InterpCmds(GETINTERP, 0, 0));
//#endif

    }
}
