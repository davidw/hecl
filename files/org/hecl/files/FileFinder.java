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

import java.io.IOException;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.midlet.MIDlet;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

/**
 * The <code>FileFinder</code> class displays a very simple file
 * selection dialog that lets you descend through a file hierarchy and
 * select a file that matches a condition specified by the
 * FileFinderCallback.match method, and then perform an action
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class FileFinder extends List implements CommandListener, Runnable {
    Command selectFileCmd = null;
    Command cancelBrowseCmd = null;
    Command upCmd = null;
    String currentFile = "";
    String lastFile = "";
    FileFinderCallback ffcallback = null;

    /**
     * Creates a new <code>FileFinder</code> instance.
     *
     * @param title a <code>String</code> value
     * @param startDir a <code>String</code> value that specifies
     * where to start looking.  If this is null, start from the
     * 'roots'.
     * @param callback a <code>FileFinderCallback</code> value
     */
    public FileFinder(String title, String startDir, FileFinderCallback callback) {
	super(title, List.IMPLICIT);
	ffcallback = callback;

	currentFile = startDir;

	this.setCommandListener(this);
	selectFileCmd = new Command("Select", Command.ITEM, 1);
	cancelBrowseCmd = new Command("Cancel", Command.CANCEL, 3);
	upCmd = new Command("Up", Command.ITEM, 2);
	this.setSelectCommand(selectFileCmd);
	this.addCommand(upCmd);
	this.addCommand(cancelBrowseCmd);
	run();
    }

    /**
     * The <code>commandAction</code> method is called when the end
     * user presses either the 'select' or 'cancel' commands.
     *
     * @param c a <code>Command</code> value
     * @param d a <code>Displayable</code> value
     */
    public void commandAction(Command c, Displayable d) {
	if (c == cancelBrowseCmd) {
	    ffcallback.cancel(this);
	    return;
	} else if (c == upCmd) {
	    up();
	    this.deleteAll();
	} else if (c == selectFileCmd) {
	    int idx = this.getSelectedIndex();
	    if (idx < 0) {
		/* Nothing has been selected. */
		return;
	    }
	    String newFile = this.getString(idx);

	    if (newFile == null) {
		ffcallback.error(this, "No file selected");
		return;
	    }

	    if(newFile.startsWith("file:///")) {
		currentFile = newFile;
	    } else {
		if (currentFile == null || currentFile == "") {
		    currentFile = "file:///";
		}
		currentFile = currentFile + newFile;
	    }
	    this.deleteAll();
	}
	/* Kick off the thread to finish processing and displaying
	 * information.  */
	new Thread(this).start();
    }

    /**
     * The <code>up</code> method is called when the user presses the
     * 'up' command, in order to attempt to go up a directory.
     *
     */
    private void up() {
	if (currentFile == null) {
	    return;
	}
	int last = currentFile.lastIndexOf('/');
	int secondlast = currentFile.lastIndexOf('/', last-1);

	/* It's only a root. */
	if (currentFile.startsWith("file:///") && secondlast == 7) {
	    currentFile = null;
	    return;
	}
	currentFile = currentFile.substring(0, secondlast) + "/";
    }

    /**
     * The <code>run</code> method (in other words, a new thread) is
     * where we offload the processing so that it is not run in the
     * commandAction.
     *
     */
    public synchronized void run() {
	FileConnection fconn = null;

	/* If there is no currentFile, show the roots and return. */
 	if (currentFile == null) {
	    for (Enumeration e = FileSystemRegistry.listRoots(); e.hasMoreElements();) {
		String root = (String)e.nextElement();
		this.append(root, null);
	    }
	    return;
	}

	try {
	    fconn = (FileConnection)Connector.open(currentFile);
	} catch (Exception e) {
	    ffcallback.error(this, "Cannot open FileConnection \"" + currentFile + "\" :" + e.toString());
	    return;
	}

	/* If the selected file is a match, we indicate that we have
	 * selected it, and return. */
	if (ffcallback.match(this, fconn)) {
	    ffcallback.selected(this, currentFile);
	    return;
	}

	/* Otherwise, try and do a listing. */
	try {
	    for (Enumeration e = fconn.list(); e.hasMoreElements();) {
		String fname = (String)e.nextElement();
		this.append(fname, null);
	    }
	} catch (Exception e) {
	    ffcallback.error(this, "Cannot list files: " + e.toString());
	}
    }

}