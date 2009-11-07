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

/**
 * The <code>LocationRequest</code> class is a thread that sets in
 * motion the location information request, and sends and async
 * request back to the interpreter when the information is available.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class LocationRequest extends Thread {
    Interp interp = null;
    Thing callbackProc = null;
    int timeout = 100;

    /**
     * Creates a new <code>LocationRequest</code> instance.
     *
     * @param i an <code>Interp</code> value
     * @param c a <code>Thing</code> value
     * @param t an <code>int</code> value
     */
    public LocationRequest(Interp i, Thing c, int t) {
	interp = i;
	callbackProc = c;
	timeout = t;
    }

    /**
     * The <code>run</code> method is where the real work is done.
     *
     */
    public synchronized void run() {
	try {
	    Vector cmd = ListThing.get(callbackProc.deepcopy());
	    cmd.addElement(LocationCmd.getLocation(timeout));
	    interp.evalAsync(ListThing.create(cmd));
	} catch (HeclException he) {
	    interp.backgroundError(he.toString());
	}
    }

}