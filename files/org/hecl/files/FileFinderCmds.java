//#condition midp >= 2.0
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

import java.util.Hashtable;

import org.hecl.ClassCommand;
import org.hecl.ClassCommandInfo;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.Thing;

import org.hecl.midp20.lcdui.ScreenCmd;

import javax.microedition.io.file.FileConnection;


/**
 * The <code>FileFinderCmds</code> class implements the
 * FileFinderCallback methods as well as the 'filefinder' command
 * itself.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class FileFinderCmds extends ScreenCmd implements FileFinderCallback {
    private Thing errorCmd = null;
    private Thing matchCmd = null;
    private Thing selectedCmd = null;
    private Interp interp = null;

    public Thing method(Interp interp, ClassCommandInfo context, Thing[] argv)
	throws HeclException {
	return new Thing("");
    }

    public Thing cmdCode(Interp ip, Thing[] argv)
	throws HeclException {
	interp = ip;
	Properties props = new Properties();
	props.setProps(argv, 1);
	String startDir = null;
	if (props.existsProp("-startdir")) {
	    startDir = props.getProp("-startdir").toString();
	}

	errorCmd = props.getProp("-errorcmd");
	matchCmd = props.getProp("-matchcmd");
	selectedCmd = props.getProp("-selectedcmd");

	return ObjectThing.create(
	    new FileFinder(
		props.getProp("-title", new Thing("File Finder")).toString(),
		startDir, this));
    }


    /* These are all from FileFinderCallback.java  */

    public void error(FileFinder ff, String errmsg) {
	if (errorCmd == null) {
	    System.err.println(errmsg);
	} else {
	    try {
		Thing res = interp.eval(ListThing.buildCmd(errorCmd, new Object [] {
			    new Thing(errmsg)
			}));
	    } catch (Exception e) {
		System.err.println("Original error: " + errmsg + " error handler error: " + e.toString());
	    }
	}
    }

    public boolean match(FileFinder ff, FileConnection fconn) {
	if (matchCmd == null) {
	    /* We try and have a decent default: match any file. */
	    return !fconn.isDirectory();
	} else {
	    try {
		Thing res = interp.eval(ListThing.buildCmd(matchCmd, new Object [] {
			    new Thing(fconn.getURL())
			}));
		return IntThing.get(res) == 1;
	    } catch (Exception e) {
		error(ff, e.toString());
	    }
	}
	return false;
    }

    public void selected(FileFinder ff, String currentFile) {
	if (selectedCmd != null) {
	    Object [] arguments = {
		new Thing(currentFile)
	    };
	    try {
		Thing cmd = ListThing.buildCmd(selectedCmd, arguments);
		interp.evalAsync(cmd);
	    } catch (Exception e) {
		error(ff, e.toString());
	    }
	}
	/* Since a file has been selected, we can dispose of the
	 * FileFinder. */
	ff = null;
    }

    public void cancel(FileFinder ff) {
	ff = null;
    }

    public static void load(Interp interp) throws HeclException {
	interp.addCommand(CMDNAME, cmd);
	interp.addClassCmd(FileFinderCmds.class, cmd);
    }
    public static void unload(Interp interp) {
	interp.removeClassCmd(FileFinderCmds.class);
    }

    private FileFinderCmds() {
    }

    private static FileFinderCmds cmd = new FileFinderCmds();
    private static final String CMDNAME = "filefinder";
}
