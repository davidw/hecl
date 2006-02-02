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

import java.io.File;

/**
 * <code>InfoCmd</code> implements the "filesize" and (eventually)
 * other commands returning information about a file.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class InfoCmd implements Command {
    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
	String cmd = argv[0].toString();

	if (cmd.equals("filesize")) {
	    File fl = new File(argv[1].toString()).getAbsoluteFile();
	    int len = (int)fl.length();
	    interp.setResult(len);
	}
    }
}
