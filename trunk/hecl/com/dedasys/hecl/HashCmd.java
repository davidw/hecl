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
 * <code>HashCmd</code> implements the "hash", "hget" and "hset"
 * commands.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class HashCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	String cmdname = argv[0].toString();

	if (cmdname.equals("hash")) {
	    Thing result = argv[1];
	    HashThing.get(result);
	    interp.setResult(result);
	} else if (cmdname.equals("hget")) {
	    Hashtable hash = HashThing.get(argv[1]);
	    String key = argv[2].toString();
	    interp.setResult((Thing)hash.get(key));
	} else if (cmdname.equals("hset")) {
	    Hashtable hash = HashThing.get(argv[1]);
	    String key = argv[2].toString();
	    Thing val = argv[3];
	    hash.put(key, val);
	    interp.setResult(val);
	}
	return;
    }
}
