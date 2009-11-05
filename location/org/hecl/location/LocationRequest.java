/*
 * Copyright 2009
 * DedaSys LLC - http://www.dedasys.com
 *
 * Author: David N. Welton <davidw@dedasys.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hecl.location;

import java.util.Vector;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.ListThing;
import org.hecl.Thing;

public class LocationRequest extends Thread {
    Interp interp = null;
    Thing callbackProc = null;
    int timeout = 100;

    public LocationRequest(Interp i, Thing c, int t) {
	interp = i;
	callbackProc = c;
	timeout = t;
    }

    public synchronized void run() {
	try {
	    Vector cmd = ListThing.get(callbackProc.deepcopy());
	    cmd.addElement(LocationCmd.getLocation(timeout));
	    interp.evalAsync(ListThing.create(cmd));
	} catch (HeclException he) {
	    /* FIXME - bgerror  */
	}
    }

}