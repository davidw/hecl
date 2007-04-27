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

package org.hecl.files;

import java.io.File;
import java.util.Hashtable;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.LongThing;
import org.hecl.Operator;
import org.hecl.RealThing;
import org.hecl.StringThing;
import org.hecl.Thing;

public class FileCmds extends Operator {
    public static final int CD = 1;
    public static final int CURRENTFILE = 2;
    public static final int READALL = 3;
    public static final int WRITE = 4;
    public static final int SOURCE = 5;
    public static final int FILESIZE = 6;
    public static final int FILETOLIST = 7;
    public static final int LISTTOFILE = 8;

    private FileCmds(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }

    public Thing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	switch (cmd) {
	  case CD:
	    HeclFile.changeDir(argv[1].toString());
	    return null;
	    
	  case CURRENTFILE:
	    return new Thing(HeclFile.currentFile);
	    
	  case FILESIZE:
	    File fl = new File(argv[1].toString()).getAbsoluteFile();
	    return LongThing.create(fl.length());

	  case FILETOLIST:
	    return ListThing.create(HeclFile.fileToList(argv[1].toString()));

	  case LISTTOFILE:
	    return new Thing(HeclFile.listToFile(ListThing.get(argv[1])));

	  case READALL:
	    return new Thing(HeclFile.readFile(argv[1].toString()));

	  case WRITE:
	    String fn = argv[1].toString();
	    String data = argv[2].toString();
	    HeclFile.writeFile(fn, data);
	    return IntThing.create(data.length());

	  case SOURCE:
	    HeclFile.sourceFile(interp, argv[1].toString());
	    return null;

	  default:
	    throw new HeclException("Unknown file command '"
				    + argv[0].toString() + "' with code '"
				    + cmd + "'.");
	}
    }

    public static void load(Interp ip) throws HeclException {
	Operator.load(ip,cmdtable);
    }

    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip,cmdtable);
    }

    private static Hashtable cmdtable = new Hashtable();

    static {
        cmdtable.put("cd", new FileCmds(CD,1,1));
        cmdtable.put("currentfile", new FileCmds(CURRENTFILE,0,0));
        cmdtable.put("filesize", new FileCmds(FILESIZE,1,1));
        cmdtable.put("filetolist", new FileCmds(FILETOLIST,1,1));
        cmdtable.put("listtofile", new FileCmds(LISTTOFILE,1,1));
        cmdtable.put("readall", new FileCmds(READALL,1,1));
        cmdtable.put("write", new FileCmds(WRITE,2,2));
        cmdtable.put("source", new FileCmds(SOURCE,1,1));
    }
}
