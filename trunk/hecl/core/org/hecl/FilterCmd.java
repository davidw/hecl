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

package org.hecl;

import java.util.*;

/**
 * <code>FilterCmd</code> implements the "filter" and "search" commands.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class FilterCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
        Vector results = new Vector();
        Vector list = ListThing.get(argv[1]);
        String varname = argv[2].getStringRep();
        int sz = list.size();
        Thing val;
        boolean brk = false;

        if (argv[0].getStringRep().equals("search")) {
            brk = true;
        }

        for (int i = 0; i < sz; i++) {
            val = (Thing) list.elementAt(i);
	    val.copy = true; /* Make sure that the original value
			      * doesn't get fiddled with. */
            interp.setVar(varname, val);
            interp.eval(argv[3]);

            if (IntThing.get(interp.getResult()) != 0) {
                results.addElement(val);
                if (brk == true) {
                    break;
                }
            }
        }
        interp.setResult(new Thing(new ListThing(results)));
        return;
    }
}
