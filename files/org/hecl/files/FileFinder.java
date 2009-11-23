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

import java.io.IOException;

import java.util.Enumeration;

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
public class FileFinder extends List implements CommandListener {
    Command selectFile = null;
    Command cancelBrowse = null;
    String currentFile = "";
    FileFinderCallback ffcallback = null;

    /**
     * Creates a new <code>FileFinder</code> instance.
     *
     * @param title a <code>String</code> value
     * @param startDir a <code>String</code> value that specifies
     * where to start looking.
     * @param callback a <code>FileFinderCallback</code> value
     */
    public FileFinder(String title, String startDir, FileFinderCallback callback) {
	super(title, List.IMPLICIT);
	ffcallback = callback;

	if (startDir == null) {
	    for (Enumeration e = FileSystemRegistry.listRoots(); e.hasMoreElements();) {
		this.append((String)e.nextElement(), null);
	    }
	} else {
	    /* Start things off in the right place in order to avoid
	     * annoying file selection dialogs as much as possible. */
	    this.append(startDir, null);
	}
	this.setCommandListener(this);
	selectFile = new Command("Select", Command.ITEM, 1);
	cancelBrowse = new Command("Cancel", Command.CANCEL, 2);
	this.addCommand(cancelBrowse);
	this.setSelectCommand(selectFile);
    }

    /**
     * The <code>commandAction</code> method is called when the end
     * user presses either the 'select' or 'cancel' commands.
     *
     * @param c a <code>Command</code> value
     * @param d a <code>Displayable</code> value
     */
    public void commandAction(Command c, Displayable d) {
	if (c == cancelBrowse) {
	    ffcallback.cancel(this);
	    return;
	}

	if (c == selectFile) {
	    String newFile = this.getString(this.getSelectedIndex());

	    if (newFile == null) {
		ffcallback.error(this, "No file selected");
		return;
	    }

	    if (currentFile == "") {
		currentFile = "file://" + newFile;
	    } else {
		currentFile = currentFile + newFile;
	    }
	    this.deleteAll();

	    FileConnection fconn = null;
	    try {
 		fconn = (FileConnection)Connector.open(currentFile);
	    } catch (Exception e) {
		ffcallback.error(this, "Cannot open FileConnection \"" + currentFile + "\" :" + e.toString());
		return;
	    }

	    if (ffcallback.match(this, fconn)) {
		ffcallback.selected(this, currentFile);
		return;
	    }

	    try {
		for (Enumeration e = fconn.list(); e.hasMoreElements();) {
		    this.append((String)e.nextElement(), null);
		}
	    } catch (Exception e) {
		ffcallback.error(this, "Cannot list files: " + e.toString());
	    }
	}
    }
}