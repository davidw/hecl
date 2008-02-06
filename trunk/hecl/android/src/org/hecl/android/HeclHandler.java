/* Copyright 2008 David N. Welton - DedaSys LLC - http://www.dedasys.com

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

package org.hecl.android;

import org.hecl.Interp;
import org.hecl.Thing;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * The class <code>HeclHandler</code> is a handler that executes the
 * Hecl code passed to it.  This is useful for intra-thread
 * communication.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class HeclHandler extends Handler {
    private Interp interp  = null;

    public HeclHandler(Interp i) {
	interp = i;
    }

    public void handleMessage(Message msg) {
	Thing script = (Thing)msg.obj;
	try {
	    interp.eval(script);
	} catch (Exception e) {
	    Log.v("androidhandler", "interp eval exception: " + e.toString());
	}
    }
}