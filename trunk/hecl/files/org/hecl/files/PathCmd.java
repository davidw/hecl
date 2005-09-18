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

import org.hecl.*;

/**
 * <code>PathCmd</code> implements path manipulation commands.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class PathCmd implements Command {
    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
        String cmdname = argv[0].getStringRep();

	if (cmdname.equals("filetolist")) {
	    interp.setResult(ListThing.create(
				 HeclFile.fileToList(argv[1].toString())));
	} else if (cmdname.equals("listtofile")) {
	    interp.setResult(new Thing(HeclFile.listToFile(ListThing.get(argv[1]))));
	}
    }
}
