/* Copyright 2004-2005 David N. Welton

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
 * <code>ForCmd</code> implements the "for" command, which takes 4
 * arguments, a start block of code to evaluate once, a test to
 * determine whether the loop should end, a next block of code to
 * evaluate after each iteration, and a body to execute each time
 * through.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class ForCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {

	/* start */
	Eval.eval(interp, argv[1]);

	/* test */
	while (Thing.isTrue(Eval.eval(interp, argv[2]))) {
	    try {
		/* body */
		Eval.eval(interp, argv[4]);
	    } catch (HeclException e) {
		if (e.code == HeclException.BREAK) {
		    break;
		} else if (e.code == HeclException.CONTINUE) {
		} else {
		    throw e;
		}
	    }
	    /* next */
	    Eval.eval(interp, argv[3]);
	}
    }
}
