/*
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

import android.app.Activity;

import android.widget.Button;
import android.view.View;

import android.util.Log;

import org.hecl.ClassCommand;
import org.hecl.ClassCommandInfo;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.StringThing;
import org.hecl.Thing;

public class ActivityCmd implements org.hecl.Command {
    private static Activity activity;
    private final static String CMDNAME = "activity";
    private static Reflector reflector = null;

    public Thing cmdCode(Interp interp, Thing[] argv) throws HeclException {
	String subcmd = argv[1].toString();
	if (subcmd.equals("getcontext")) {
	    return ObjectThing.create(activity);
	} else {
	    return reflector.evaluate(activity, subcmd, argv);
	}
    }

    public ActivityCmd(Activity a)
	throws HeclException {
	activity = a;
    }

    static {
	try {
	    reflector = new Reflector("android.app.Activity");
	} catch (HeclException he) {
	    Log.v("hecl", "Problem with ActivityCmd.java init: " + he.toString());
	}
    }
}
