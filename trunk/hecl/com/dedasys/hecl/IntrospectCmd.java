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

import java.util.*;

/**
 * <code>IntrospectCmd</code> implements the "intro" command,
 * providing different ways of examining the state of the Hecl
 * interpreter.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class IntrospectCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	String subcmd = argv[1].toString();
	Vector results = new Vector();
	if (subcmd.equals("commands")) {
	    for (Enumeration e = interp.cmdNames() ; e.hasMoreElements(); ) {
		Thing t = new Thing((String)e.nextElement());
		results.addElement(t);
	    }
	    interp.setResult(ListThing.create(results));
	    return;
	}
    }
}
