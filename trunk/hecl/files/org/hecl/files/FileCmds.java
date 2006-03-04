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

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.ListThing;
import org.hecl.Thing;

class FileCmds {
    public static final int CD = 1;
    public static final int CURRENTFILE = 2;
    public static final int READALL = 3;
    public static final int WRITE = 4;
    public static final int SOURCE = 5;

    public static final int FILESIZE = 6;

    public static final int FILETOLIST = 7;
    public static final int LISTTOFILE = 8;

    static void dispatch(int cmd, Interp interp, Thing[] argv) throws HeclException {

	switch (cmd) {
	    case CD:
		HeclFile.changeDir(argv[1].toString());
		return;

	    case CURRENTFILE:
		interp.setResult(HeclFile.currentFile);
		return;

	    case FILESIZE:
		File fl = new File(argv[1].toString()).getAbsoluteFile();
		interp.setResult((int)fl.length());
		return;

	    case FILETOLIST:
		interp.setResult(ListThing.create(
				     HeclFile.fileToList(argv[1].toString())));
		return;

	    case LISTTOFILE:
		interp.setResult(new Thing(HeclFile.listToFile(ListThing.get(argv[1]))));
		return;

	    case READALL:
		interp.setResult(new Thing(HeclFile.readFile(argv[1].toString())));
		return;

	    case WRITE:
		String fn = argv[1].toString();
		String data = argv[2].toString();
		HeclFile.writeFile(fn, data);
		interp.setResult(data.length());
		return;

	    case SOURCE:
		HeclFile.sourceFile(interp, argv[1].toString());
		return;
	}
    }
}
