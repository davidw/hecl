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
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.ListThing;
import org.hecl.Operator;
import org.hecl.StringThing;
import org.hecl.Thing;


public class FileCmds extends Operator {
    public static final int READABLE = 10;
    public static final int WRITEABLE = 20;
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
	    cmdtable.put("file.list", new FileCmds(LIST,1,1));
	    cmdtable.put("file.devs", new FileCmds(LISTROOTS,0,0));
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Can't create file commands.");
	}

    }
}