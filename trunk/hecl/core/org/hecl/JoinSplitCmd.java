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
 * <code>JoinSplitCmd</code> implements the "join" and "split" commands.
 * Defaults to " " as a split/join character.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class JoinSplitCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
        String cmdname = argv[0].getStringRep();

        if (cmdname.equals("join")) {
            Vector list = ListThing.get(argv[1]);
            StringBuffer result = new StringBuffer("");
            boolean first = true;
            String joinstr = null;
            if (argv.length > 2) {
                joinstr = argv[2].getStringRep();
            } else {
                joinstr = " ";
            }

            for (Enumeration e = list.elements(); e.hasMoreElements();) {
                if (first == false) {
                    result.append(joinstr);
                } else {
                    first = false;
                }
                result.append(((Thing) e.nextElement()).getStringRep());
            }
            interp.setResult(new Thing(new StringThing(result)));
        } else if (cmdname.equals("split")) {
            Vector result = new Vector();
            String str = argv[1].getStringRep();
            int idx = 0;
            int last = 0;
            String splitstr = null;
            if (argv.length > 2) {
                splitstr = argv[2].getStringRep();
            } else {
                /* By default, we split on spaces. */
                splitstr = " ";
            }

            idx = str.indexOf(splitstr);
            while (idx >= 0) {
                result.addElement(new Thing(str.substring(last, idx)));
                last = idx + splitstr.length();
                idx = str.indexOf(splitstr, last);
            }
            result.addElement(new Thing(str.substring(last, str.length())));
            interp.setResult(ListThing.create(result));
        }

    }
}