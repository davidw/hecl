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
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Thing;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import java.util.Vector;

public class HeclServiceConnection implements ServiceConnection {
    public Thing onserviceconnected;
    public Thing onservicedisconnected;

    private Interp interp = null;

    public HeclServiceConnection(Interp i) {
	interp = i;
    }

    public void onServiceConnected(ComponentName name, IBinder service) {
	try {
	    Vector cmdline = ListThing.get(onserviceconnected.deepcopy());
	    cmdline.addElement(ObjectThing.create(name));
	    cmdline.addElement(ObjectThing.create(service));
	    interp.eval(ListThing.create(cmdline));
	} catch (Exception e) {
	    Log.d("HeclServiceConnection", "Exception in onserviceconnected: "
		  + e.toString());
	}
    }
    public void onServiceDisconnected(ComponentName name) {
	try {
	    Vector cmdline = ListThing.get(onservicedisconnected.deepcopy());
	    cmdline.addElement(ObjectThing.create(name));
	    interp.eval(ListThing.create(cmdline));
	} catch (Exception e) {
	    Log.d("HeclServiceConnection", "Exception in onservicedisconnected: "
		  + e.toString());
	}
    }
}