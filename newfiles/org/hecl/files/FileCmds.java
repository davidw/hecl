/*
 * Copyright 2009
 * DedaSys LLC - http://www.dedasys.com
 *
 * Author: David N. Welton <davidw@dedasys.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hecl.files;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//#if javaversion >= 1.5
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
//#else
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
//#endif

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.hecl.DoubleThing;
import org.hecl.HashThing;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.LongThing;
import org.hecl.ObjectThing;
import org.hecl.Operator;
import org.hecl.StringThing;
import org.hecl.Thing;

/**
 * The <code>FileCmds</code> class implements various file handling
 * commands.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class FileCmds extends Operator {
    public static final int OPEN = 1;
    public static final int READABLE = 10;
    public static final int WRITABLE = 20;
    public static final int HIDDEN = 30;

    public static final int EXISTS = 40;
    public static final int DELETE = 50;

    public static final int SIZE = 60;
    public static final int BASENAME = 70;
    public static final int MTIME = 80;

    public static final int ISDIRECTORY = 90;
    public static final int ISOPEN = 100;

    public static final int LIST = 110;

    public static final int MKDIR = 120;

    public static final int RENAME = 130;

    public static final int TRUNCATE = 140;

    public static final int LISTROOTS = 150;

    public static final int DU = 160;

    public static final int FILESPLIT = 170;
    public static final int FILEJOIN = 180;

    public static final int SOURCE = 190;
    public static final int CURRENTFILE = 200;
    public static final int CD = 210;


    public Thing operate(int cmd, Interp interp, Thing[] argv)
	throws HeclException {
	String fname = null;

	/* These commands are platform agnostic - we'll handle them first. */
	switch(cmd) {
	    /* Note that FILESPLIT is much more platform dependent, so
	       it is below, in the two sections of ifdef'ed code.  */
	    case FILEJOIN: {
		Vector filenamelist = ListThing.get(argv[1]);
		/* Takes a list like {a b c} and converts it to a
		   filename such as a/b/c. */
		StringBuffer res = new StringBuffer("");
		boolean first = true;
		for (int i = 0; i < filenamelist.size(); i++) {
		    if (first == false) {
			res.append(Interp.fileseparator);
		    } else {
			/* FIXME - broken on windows */
			if (!filenamelist.elementAt(i).toString().equals("/")) {
			    first = false;
			}
		    }
		    res.append(filenamelist.elementAt(i).toString());
		}
		return new Thing(res.toString());
	    }

	    case SOURCE: {
		HeclFileUtils.sourceFile(interp, argv[1].toString());
		return null;
	    }
	    case CURRENTFILE: {
		return interp.currentFile;
	    }
	    case CD: {
//#if javaversion >= 1.5
		return new Thing(System.setProperty("user.dir", argv[1].toString()));
//#endif
	    }
	}


	/* 'Regular' Java uses File, J2ME uses FileConnection. */
//#if javaversion >= 1.5
	File tfile = null;
	if (cmd != LISTROOTS) {
	    fname = StringThing.get(argv[1]);
	    tfile = new File(fname);
	}
//#else
	FileConnection fconn = null;

	if (cmd != LISTROOTS) {
	    fname = StringThing.get(argv[1]);
	    try {
 		fconn = (FileConnection)Connector.open(fname);
	    } catch (IOException e) {
		throw new HeclException("IO Exception in " +
					argv[0].toString() + ": " + e.toString());
	    }
	}
//#endif


//#if javaversion >= 1.5
	switch(cmd) {
	    case OPEN: {
		boolean write = false;
		if (argv.length == 3) {
		    String perms = argv[2].toString();
		    if (perms.indexOf('w') > -1) {
			write = true;
		    }
		}
		HeclChannel retval;
		try {
		    if (write) {
			retval = new HeclChannel(new DataOutputStream(new FileOutputStream(new File(fname))));
		    } else {
			retval = new HeclChannel(new DataInputStream(new FileInputStream(new File(fname))));
		    }
		} catch (IOException ioe) {
		    throw new HeclException("Error opening '" + fname + "' :" + ioe.toString());
		}
		return ObjectThing.create(retval);
	    }

	    case READABLE:
	    {
/* 		    if (argv.length == 3) {
		    boolean readable = IntThing.get(argv[2]) == 1;
		    fconn.setReadable(readable);
		    }  */
		return IntThing.create(tfile.canRead());
	    }

	    case WRITABLE:
	    {
/* 		    if (argv.length == 3) {
		    boolean writable = IntThing.get(argv[2]) == 1;
		    fconn.setWritable(writable);
		    }  */
		return IntThing.create(tfile.canWrite());
	    }

	    case HIDDEN:
	    {
/* 		    if (argv.length == 3) {
		    boolean hidden = IntThing.get(argv[2]) == 1;
		    fconn.setHidden(hidden);
		    }  */
		return IntThing.create(tfile.isHidden());
	    }

	    case EXISTS:
	    {
		return IntThing.create(tfile.exists());
	    }

	    case SIZE:
	    {
		return LongThing.create(tfile.length());
	    }
	    case BASENAME:
	    {
		return new Thing(tfile.getName());
	    }
	    case MTIME:
	    {
		return LongThing.create(tfile.lastModified());
	    }
	    case ISDIRECTORY:
	    {
		return IntThing.create(tfile.isDirectory());
	    }

	    case ISOPEN:
	    {
		throw new HeclException("not implemented");
	    }

	    case LIST: {
		Vector v = new Vector();
		String[] filenames = tfile.list();
		for (int i = 0; i < filenames.length; i++) {
		    v.addElement(new Thing(filenames[i]));
		}
		return ListThing.create(v);
	    }

	    case LISTROOTS: {
		Vector v = new Vector();
		File[] roots = File.listRoots();
		for (int i = 0; i < roots.length; i++) {
		    v.addElement(new Thing(roots[i].getName()));
		}
		return ListThing.create(v);
	    }

	    case MKDIR: {
		tfile.mkdir();
		return new Thing(fname);
	    }

	    case RENAME: {
		tfile.renameTo(new File(argv[2].toString()));
		return argv[2];
	    }

	    case TRUNCATE: {
		throw new HeclException("not implemented");
	    }

	    case DU: {
//#if javaversion >= 1.6
		Hashtable du = new Hashtable();
		du.put("total", LongThing.create(tfile.getTotalSpace()));
		du.put("used", LongThing.create(tfile.getUsableSpace()));
		return HashThing.create(du);
//#else
		throw new HeclException("not implemented");
//#endif
	    }

	    case FILESPLIT: {
		Vector resultv = new Vector();
		Vector reversed = new Vector();
		File fn = new File(fname);
		File pf = fn.getParentFile();

		String fns;
		String pfs;

		/* Walk through all elements, compare the element with
		 * its parent, and tack the difference onto the
		 * Vector.  */
		String ss = null;
		while (pf != null) {
		    fns = fn.toString();
		    pfs = pf.toString();

		    ss = fns.substring(pfs.length(), fns.length());
		    /* The 'diff' operation leaves path components
		     * with a leading slash.  Remove it. */
		    if (ss.charAt(0) == File.separatorChar) {
			ss = ss.substring(1, ss.length());
		    }
		    reversed.addElement(new Thing(ss));
		    fn = pf;
		    pf = pf.getParentFile();
		}
		reversed.addElement(new Thing(fn.toString()));

		/* Ok, now we correct the order of the list by
		 * reversing it. */
		int j = 0;
		for (int i = reversed.size() - 1; i >= 0 ; i --) {
		    Thing t = (Thing)reversed.elementAt(i);
		    resultv.addElement(t);
		    j ++;
		}

		return ListThing.create(resultv);
	    }

	    default:
		throw new HeclException("Unknown file command '"
					+ argv[0].toString() + "' with code '"
					+ cmd + "'.");
	}
//#else

	try {
	    switch(cmd) {
		case OPEN: {
		    boolean write = false;
		    if (argv.length == 3) {
			String perms = argv[2].toString();
			if (perms.indexOf('w') > -1) {
			    write = true;
			}
		    }
		    Object retval;
		    if (write) {
			retval = fconn.openDataOutputStream();
		    } else {
			retval = fconn.openDataInputStream();
		    }
		    return ObjectThing.create(retval);
		}

		case READABLE:
		{
		    if (argv.length == 3) {
			boolean readable = IntThing.get(argv[2]) == 1;
			fconn.setReadable(readable);
		    }
		    return IntThing.create(fconn.canRead());
		}

		case WRITABLE:
		{
		    if (argv.length == 3) {
			boolean writable = IntThing.get(argv[2]) == 1;
			fconn.setWritable(writable);
		    }
		    return IntThing.create(fconn.canWrite());
		}

		case HIDDEN:
		{
		    if (argv.length == 3) {
			boolean hidden = IntThing.get(argv[2]) == 1;
			fconn.setHidden(hidden);
		    }
		    return IntThing.create(fconn.isHidden());
		}

		case EXISTS:
 		{
		    return IntThing.create(fconn.exists());
		}

		case SIZE:
		{
		    return LongThing.create(fconn.fileSize());
		}
		case BASENAME:
		{
		    return new Thing(fconn.getName());
		}
		case MTIME:
		{
		    return LongThing.create(fconn.lastModified());
		}
		case ISDIRECTORY:
 		{
		    return IntThing.create(fconn.isDirectory());
		}

		case ISOPEN:
 		{
		    return IntThing.create(fconn.isOpen());
		}

		case LIST: {
		    Vector v = new Vector();
		    for (Enumeration e = fconn.list(); e.hasMoreElements();) {
			v.addElement(new Thing((String)e.nextElement()));
		    }
		    return ListThing.create(v);
		}

		case LISTROOTS: {
		    Vector v = new Vector();
		    for (Enumeration e = FileSystemRegistry.listRoots(); e.hasMoreElements();) {
			v.addElement(new Thing((String)e.nextElement()));
		    }
		    return ListThing.create(v);
		}

		case MKDIR: {
		    fconn.mkdir();
		    return new Thing(fname);
		}

		case RENAME: {
		    fconn.rename(argv[2].toString());
		    return argv[2];
		}

		case TRUNCATE: {
		    fconn.truncate(LongThing.get(argv[2]));
		}

		case DU: {
		    Hashtable du = new Hashtable();
		    du.put("total", LongThing.create(fconn.totalSize()));
		    du.put("used", LongThing.create(fconn.usedSize()));
		    return HashThing.create(du);
		}

		case FILESPLIT: {
		    /* This isn't very good, but it's basically the *
		       best we can do with what's available on mobile
		       devices. */
		    return ListThing.stringSplit(fname, Interp.fileseparator);
		}

		default:
		    throw new HeclException("Unknown file command '"
					    + argv[0].toString() + "' with code '"
					    + cmd + "'.");
	    }
	} catch (IOException e) {
	    throw new HeclException("IO Exception in " +
				    argv[0].toString() + ": " + e.toString());
	}
//#endif
    }

    public static void load(Interp ip) throws HeclException {
	Operator.load(ip,cmdtable);
    }


    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip,cmdtable);
    }

    protected FileCmds(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }

    private static Hashtable cmdtable = new Hashtable();
    static {
	try {
	    cmdtable.put("open", new FileCmds(OPEN,1,2));
	    cmdtable.put("file.readable", new FileCmds(READABLE,1,2));
	    cmdtable.put("file.writable", new FileCmds(WRITABLE,1,2));
	    cmdtable.put("file.hidden", new FileCmds(HIDDEN,1,2));
	    cmdtable.put("file.exists", new FileCmds(EXISTS,1,1));

	    cmdtable.put("file.exists", new FileCmds(EXISTS,1,1));
	    cmdtable.put("file.size", new FileCmds(SIZE,1,1));
	    cmdtable.put("file.basename", new FileCmds(BASENAME,1,1));
	    cmdtable.put("file.mtime", new FileCmds(MTIME,1,1));
	    cmdtable.put("file.isdirectory", new FileCmds(ISDIRECTORY,1,1));
	    cmdtable.put("file.isopen", new FileCmds(ISOPEN,1,1));

	    cmdtable.put("file.mkdir", new FileCmds(MKDIR,1,1));
	    cmdtable.put("file.truncate", new FileCmds(TRUNCATE,1,1));
	    cmdtable.put("file.rename", new FileCmds(RENAME,2,2));

	    cmdtable.put("file.list", new FileCmds(LIST,1,1));
	    cmdtable.put("file.devs", new FileCmds(LISTROOTS,0,0));
	    cmdtable.put("file.du", new FileCmds(DU,1,1));

	    cmdtable.put("file.split", new FileCmds(FILESPLIT,1,1));
	    cmdtable.put("file.join", new FileCmds(FILEJOIN,1,1));

	    cmdtable.put("source", new FileCmds(SOURCE,1,1));
	    cmdtable.put("file.current", new FileCmds(CURRENTFILE,0,0));
	    cmdtable.put("file.cd", new FileCmds(CD,1,1));

	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Can't create file commands.");
	}
    }
}