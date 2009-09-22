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

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

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
 * commands, but not actual opening/closing of files.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class FileCmds extends Operator {
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

    public Thing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	String fname = null;
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

	try {
	    switch(cmd) {
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

		default:
		    throw new HeclException("Unknown file command '"
					    + argv[0].toString() + "' with code '"
					    + cmd + "'.");
	    }
	} catch (IOException e) {
	    throw new HeclException("IO Exception in " +
				    argv[0].toString() + ": " + e.toString());
	}
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
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Can't create file commands.");
	}

    }
}