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

package org.hecl.fp;

import org.hecl.*;

/**
 * <code>FloatCmd</code> implements a variety of commands for the
 * floating point system.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

/* FIXME - needs support for other output channels. */

class FloatCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
        String cmd = argv[0].toString();
	double arg = DoubleThing.get(argv[1]);

	if (cmd.equals("round")) {
	    interp.setResult(IntThing.create((int)Math.rint(arg)));
	}
    }
}
