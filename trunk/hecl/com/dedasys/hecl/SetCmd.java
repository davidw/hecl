/* Copyright 2004 David N. Welton

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

package com.dedasys.hecl;

/**
 * <code>SetCmd</code> implements the "set" command, setting a
 * variable to a particular value, or if no value is provided,
 * returning the value of the variable provided.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class SetCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {

	if (argv.length == 3) {
	    interp.setVar(argv[1], argv[2]);
	}
	interp.setResult(interp.getVar(argv[1]));
    }
}
