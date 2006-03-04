/* Copyright 2005 David N. Welton

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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Vector;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Thing;

/**
 * <code>HeclFile</code> implements all the filesystem interaction
 * methods.  It's not used in J2ME code, so we can add all we like
 * here.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class HeclFile implements org.hecl.modules.HeclModule {
    /* Keep track of the file currently being run. */
    public static String currentFile = new String("");

    /**
     * <code>changeDir</code> changes where Java thinks the current
     * directory is, but DOES NOT CHANGE the process' actual working
     * directory, so this may cause problems if you exec something.
     *
     * @param dirname a <code>String</code> value
     */
    public static void changeDir(String dirname) {
	System.setProperty("user.dir", dirname);
    }

    /**
     * <code>readFile</code> reads in a text file, given a filename,
     * and returns its contents as a StringBuffer.  FIXME - we
     * probably ought to do this better, by making it more configurable.
     *
     * @param filename a <code>String</code> value
     * @return a <code>StringBuffer</code> value
     * @exception HeclException if an error occurs
     */
    public static StringBuffer readFile(String filename) throws HeclException {
	File realfn = new File(filename).getAbsoluteFile();
	StringBuffer data = new StringBuffer();
	BufferedInputStream fis = null;

	try {
	    fis = new BufferedInputStream(new FileInputStream(realfn));
 	    int total;
	    int ch;
	    for (total = 0; (ch = fis.read()) != -1; total ++) {
		data.append((char)ch);
	    }
	} catch (IOException e) {
	    throw new HeclException("error reading " + realfn +
				    " : " + e.toString());
	} finally {
	    try {
		if (fis != null) {
		    fis.close();
		}
	    } catch (IOException e) {
		throw new HeclException("error closing " + realfn +
					" stream : " + e.toString());
	    }
	}
	return data;
    }

    /**
     * The <code>writeFile</code> method writes the 'data' String to
     * the file given by 'filename'.
     *
     * @param filename a <code>String</code> value
     * @param data a <code>String</code> value
     * @exception HeclException if an error occurs
     */
    public static void writeFile(String filename, String data) throws HeclException {
	File realfn = new File(filename).getAbsoluteFile();
	BufferedOutputStream fos = null;

	try {
	    char[] chars = new char[data.length()];
	    data.getChars(0, data.length(), chars, 0);

	    fos = new BufferedOutputStream(new FileOutputStream(realfn));
	    for (int i = 0; i < chars.length; i++) {
		fos.write(chars[i]);
	    }
	} catch (IOException e) {
	    throw new HeclException("error writing to " + realfn +
				    " : " + e.toString());
	} finally {
	    try {
		if (fos != null) {
		    fos.close();
		}
	    } catch (IOException e) {
		throw new HeclException("error closing " + realfn +
					" stream" + " : " + e.toString());
	    }
	}
    }


    /**
     * <code>listToFile</code> takes a list like {a b c} and converts
     * it to a filename such as a/b/c.
     *
     * @param filenamelist a <code>Vector</code> value
     * @return a <code>String</code> value
     */
    public static String listToFile(Vector filenamelist) {
	StringBuffer res = new StringBuffer("");
	boolean first = true;
	for (int i = 0; i < filenamelist.size(); i++) {
	    if (first == false) {
		res.append(File.separator);
	    } else {
		/* FIXME - broken on windows */
		if (!filenamelist.elementAt(i).toString().equals("/")) {
		    first = false;
		}
	    }
	    res.append(filenamelist.elementAt(i).toString());
	}
	return res.toString();
    }


    /**
     * <code>fileToList</code> splits a path like /a/b/c into the list
     * {/ a b c} so that it's easy to manipulate programatically.
     *
     * @param filename a <code>String</code> value
     * @return a <code>Vector</code> value
     */
    public static Vector fileToList(String filename) {
	Vector resultv = new Vector();
	Vector reversed = new Vector();
	File fn = new File(filename);
	File pf = fn.getParentFile();

	String fns;
	String pfs;

	/* Walk through all elements, compare the element with its
	 * parent, and tack the difference onto the Vector.  */
	while (pf != null) {
	    fns = fn.toString();
	    pfs = pf.toString();

	    reversed.addElement(
		new Thing(new String(fns.substring(
					 pfs.length(), fns.length()))));
	    fn = pf;
	    pf = pf.getParentFile();
	}
	reversed.addElement(new Thing(fn.toString()));

	/* Ok, now we correct the order of the list by reversing it.
	 * We also trim the path seperators off of those entries that
	 * need it. If the path starts with a /, then we don't chop
	 * the second element, otherwise we do. */
	int j = 0;
	for (int i = reversed.size() - 1; i >= 0 ; i --) {
	    Thing t = (Thing)reversed.elementAt(i);
	    if (j == 0) {
		resultv.addElement(t);
		/* FIXME - broken on windows */
		if (t.toString().equals("/")) {
		    j --;
		}
	    } else {
		String elstr = t.toString();
		resultv.addElement(new Thing(elstr.substring(1, elstr.length())));
	    }
	    j ++;
	}

	return resultv;
    }


    /**
     * The <code>sourceFile</code> method is the equivalent of the
     * "source" command.
     *
     * @param interp an <code>Interp</code> value
     * @param filename a <code>String</code> value
     * @exception HeclException if an error occurs
     */
    public static void sourceFile(Interp interp, String filename)
	throws HeclException {
	File realfn = new File(filename).getAbsoluteFile();
	currentFile = realfn.toString();

        interp.eval(new Thing(readFile(filename)));
    }

    public void loadModule(Interp interp) throws HeclException {
        interp.commands.put("cd", new FileCmdFacade(FileCmds.CD));
        interp.commands.put("currentfile", new FileCmdFacade(FileCmds.CURRENTFILE));
        interp.commands.put("filesize", new FileCmdFacade(FileCmds.FILESIZE));
        interp.commands.put("filetolist", new FileCmdFacade(FileCmds.FILETOLIST));
        interp.commands.put("listtofile", new FileCmdFacade(FileCmds.LISTTOFILE));
        interp.commands.put("readall", new FileCmdFacade(FileCmds.READALL));
        interp.commands.put("write", new FileCmdFacade(FileCmds.WRITE));
        interp.commands.put("source", new FileCmdFacade(FileCmds.SOURCE));
    }

    public void unloadModule(Interp interp) throws HeclException {
        interp.commands.remove("cd");
        interp.commands.remove("currentfile");
        interp.commands.remove("filesize");
        interp.commands.remove("filetolist");
        interp.commands.remove("listtofile");
        interp.commands.remove("readall");
        interp.commands.remove("write");
        interp.commands.remove("source");
    }
}
