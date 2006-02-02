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
 * <code>WriteCmd</code> implements the "write" command.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class WriteCmd implements Command {

    /**
     * The standard <code>cmdCode</code> method that implements the
     * "write" command.
     *
     * @param interp an <code>Interp</code> value
     * @param argv a <code>Thing[]</code> value
     * @exception HeclException if an error occurs
     */
    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
        String cmdname = argv[0].getStringRep();

	if (cmdname.equals("write")) {
	    String fn = argv[1].toString();
	    String data = argv[2].toString();
	    HeclFile.writeFile(fn, data);
	    interp.setResult(data.length());
	}
    }
}
