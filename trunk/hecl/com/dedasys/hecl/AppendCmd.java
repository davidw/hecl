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
 * <code>AppendCmd</code> implements the "append" command, appending
 * some text to a string.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class AppendCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	StringBuffer strb;
	Thing result = argv[1];
	StringThing.get(result);
	StringThing st = (StringThing)result.val;

	for (int i = 2; i < argv.length; i++) {
	    st.append(argv[i].toString());
	}
	interp.setResult(result);
    }
}
