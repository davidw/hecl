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

import java.util.*;

/**
 * <code>TimeCmd</code> implements the "time" command, which times the
 * execution of a script.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class TimeCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {

	Eval eval = new Eval();
	long now = new Date().getTime();
	eval.eval(interp, argv[1]);
	long done = new Date().getTime();
	interp.setResult(new Thing((int)(done - now)));
    }
}
