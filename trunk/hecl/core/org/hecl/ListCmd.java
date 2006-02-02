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
 * <code>ListCmd</code> implements the "list", "llen", "lindex",
 * "lrange" and "lappend" commands.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class ListCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
        String cmdname = argv[0].getStringRep();

        if (cmdname.equals("list")) {
            Vector result = new Vector();
            for (int i = 1; i < argv.length; i++) {
                result.addElement(argv[i]);
            }
            interp.setResult(ListThing.create(result));
        } else if (cmdname.equals("llen")) {
            Vector list = ListThing.get(argv[1]);
            interp.setResult(list.size());
        } else if (cmdname.equals("lindex")) {
            Vector list = ListThing.get(argv[1]);
            int idx = IntThing.get(argv[2]);
            if (idx >= list.size()) {
                interp.setResult("");
            } else {
                /* Count backwards from the end of the list. */
                if (idx < 0) {
                    idx += list.size();
                }
                interp.setResult((Thing) list.elementAt(idx));
            }
        } else if (cmdname.equals("lappend")) {
            Vector list = ListThing.get(argv[1]);
            for (int i = 2; i < argv.length; i++) {
                list.addElement(argv[i]);
            }
            interp.setResult(argv[1]);
        } else if (cmdname.equals("linsert")) {
            Vector list = ListThing.get(argv[1]);
            int idx = IntThing.get(argv[2]);
            if (idx < 0) {
                idx += list.size();
            }
            list.insertElementAt(argv[3], idx);
            interp.setResult(argv[1]);
        } else if (cmdname.equals("lset")) {
            Vector list = ListThing.get(argv[1]);
            int idx = IntThing.get(argv[2]);
            if (idx < 0) {
                idx += list.size();
            }
            if (argv.length < 4) {
                list.removeElementAt(idx);
            } else {
                list.setElementAt(argv[3], idx);
            }
            interp.setResult(argv[1]);
        } else if (cmdname.equals("lrange")) {
	    Vector list = ListThing.get(argv[1]);
	    int first = IntThing.get(argv[2]);
	    int last = IntThing.get(argv[3]);
	    int ls = list.size();

            if (first < 0) {
                first += ls;
            }

	    if (last < 0) {
		last += ls;
	    }

	    if (last <= first || last >= ls || first >= ls) {
                interp.setResult("");
	    }
	    Vector resultv = new Vector();
            for (int i = first; i <= last; i++) {
		resultv.addElement(list.elementAt(i));
            }
            interp.setResult(ListThing.create(resultv));
	}
        return;
    }
}
