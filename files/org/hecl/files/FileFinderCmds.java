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
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.Thing;

import org.hecl.midp20.lcdui.ScreenCmd;

import javax.microedition.io.file.FileConnection;

public class FileFinderCmds extends ScreenCmd implements FileFinderCallback {

    public Thing method(Interp interp, ClassCommandInfo context, Thing[] argv)
	throws HeclException {
	return new Thing("");
    }

    public Thing cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	Properties props = new Properties();
	props.setProps(argv, 1);
	String startDir = null;
	if (props.existsProp("-startdir")) {
	    startDir = props.getProp("-startdir").toString();
	}
	return ObjectThing.create(
	    new FileFinder(
		props.getProp("-title", new Thing("File Finder")).toString(),
		startDir, this));
    }


    public void error(FileFinder ff, String errmsg) {
	System.err.println(errmsg);
    }

    public boolean match(FileFinder ff, FileConnection fconn) {
	return !fconn.isDirectory();
    }

    public void selected(FileFinder ff, String currentFile) {
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
