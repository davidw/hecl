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

package org.hecl;

/**
 * <code>LogicCmd</code> implements the basic logic commands, 'and',
 * 'not' and 'or'.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class LogicCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
        String cmd = argv[0].toString();

	int res = 0;
	if (cmd.equals("and")) {
	    res = IntThing.get(argv[1]);
	    for (int i = 2; i < argv.length; i ++) {
		res &= IntThing.get(argv[i]);
	    }
	} else if (cmd.equals("not")) {
	    res = IntThing.get(argv[1]);
	    if (res == 0) {
		res = 1;
	    } else {
		res = 0;
	    }
	} else if (cmd.equals("or")) {
	    for (int i = 1; i < argv.length; i ++) {
		res |= IntThing.get(argv[i]);
	    }
	}
	interp.setResult(res);
    }
}
