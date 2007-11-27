/* Copyright 2007 David N. Welton - DedaSys LLC - http://www.dedasys.com

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

import java.lang.reflect.Field;

import java.util.Hashtable;

import android.app.Activity;
import android.app.NotificationManager;

import android.view.View;

import android.util.Log;

import org.hecl.DoubleThing;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.LongThing;
import org.hecl.ObjectThing;
import org.hecl.Operator;
import org.hecl.RealThing;
import org.hecl.StringThing;
import org.hecl.Thing;


public class AndroidCmd extends Operator {
    static protected Activity activity = null;

    public static final int EXIT = 1;
    public static final int RESLOOKUP = 2;
    public static final int ALERT = 3;
    public static final int FINDVIEW = 4;
    public static final int LOG = 4;
    public static final int NULLCMD = 5;

    /* These will eventually be moved elsewhere - the core most
     * likely. */
    public static final int TOINT = 100;
    public static final int TOLONG = 101;
    public static final int TODOUBLE = 102;
    public static final int TOSTR = 103;

    protected AndroidCmd(int cmdcode,int minargs,int maxargs) {
	super(cmdcode, minargs, maxargs);
    }

    private void makeAlert(String msg) {
	NotificationManager nm = (NotificationManager)activity.getSystemService(Activity.NOTIFICATION_SERVICE);
	nm.notifyWithText(1, msg, NotificationManager.LENGTH_LONG, null);
	activity.showAlert("Hecl Alert", msg, "dismiss", false);
    }

    public Thing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	switch (cmd) {
	    case ALERT:
		makeAlert(argv[1].toString());
		return new Thing(argv[1].toString());
	    case EXIT:
		activity.finish();
		return null;
	    case RESLOOKUP:
		String pieces[] = argv[1].toString().split("\\.");
		String classname = null;
		Thing result = null;
		String fieldname = null;
		if (pieces.length != 3 || !pieces[0].equals("R")) {
		    /* It's probably one of the built in ones. */
		    classname = "android.R$";
		    classname += pieces[2];
		    fieldname = pieces[3];
		} else {
		    classname = "org.hecl.android." + "R$" + pieces[1];
		    fieldname = pieces[2];
		}
		try {
		    int id = 0;
		    Class c = Class.forName(classname);
		    Field f = c.getField(fieldname);
		    id = f.getInt(c);
		    return IntThing.create(id);
		} catch (Exception e) {
		    throw new HeclException("Couldn't find a match for classname: " +
					    classname + " : " + e.toString());
		}
	    case LOG:
		Log.v("hecl log", argv[1].toString());
		return null;

	    case NULLCMD:
		return ObjectThing.create(null);


	    case TOINT:
		return IntThing.create(IntThing.get(argv[1]));
	    case TOLONG:
		return LongThing.create(LongThing.get(argv[1]));
	    case TODOUBLE:
		return DoubleThing.create(DoubleThing.get(argv[1]));
	    case TOSTR:
		return StringThing.create(StringThing.get(argv[1]));

	    default:
		throw new HeclException("Unknown android command '"
					+ argv[0].toString() + "' with code '"
					+ cmd + "'.");

	}
    }

    private static Hashtable cmdtable = new Hashtable();

    static {
	try {
	    cmdtable.put("exit", new AndroidCmd(EXIT,0,0));
	    cmdtable.put("reslookup", new AndroidCmd(RESLOOKUP,1,1));
	    cmdtable.put("alert", new AndroidCmd(ALERT,1,1));
	    cmdtable.put("findview", new AndroidCmd(FINDVIEW,1,1));
	    cmdtable.put("log", new AndroidCmd(LOG,1,1));

	    cmdtable.put("null", new AndroidCmd(NULLCMD,0,0));

	    cmdtable.put("i", new AndroidCmd(TOINT, 1, 1));
	    cmdtable.put("l", new AndroidCmd(TOLONG, 1, 1));
	    cmdtable.put("d", new AndroidCmd(TODOUBLE, 1, 1));
	    cmdtable.put("s", new AndroidCmd(TOSTR, 1, 1));
	}
	catch (Exception e) {
	    e.printStackTrace();
	    Log.v("hecl", "Can't create android commands.");
	}
    }

    public static void load(Interp ip, Activity a) throws HeclException {
	activity = a;
	Operator.load(ip, cmdtable);
	ViewCmd.load(ip, a, "android.view.View", "view");
	ViewCmd.load(ip, a, "android.widget.Button", "button");
	ViewCmd.load(ip, a, "android.widget.TextView", "textview");
	ViewCmd.load(ip, a, "android.widget.RadioButton", "radiobutton");
	ViewCmd.load(ip, a, "android.widget.AnalogClock", "analogclock");
	ViewCmd.load(ip, a, "android.webkit.WebView", "webview");
	ViewCmd.load(ip, a, "com.google.android.maps.MapView", "mapview");
	ViewCmd.load(ip, a, "android.widget.DatePicker", "datepicker");
	ViewCmd.load(ip, a, "android.widget.RadioGroup", "radiogroup");
	ViewCmd.load(ip, a, "android.widget.Spinner", "spinner");
	ip.addCommand("activity", new ActivityCmd(a));

	JavaCmd.load(ip, "android.widget.ArrayAdapter", "arrayadapter");
	JavaCmd.load(ip, "android.widget.LinearLayout", "linearlayout");
	JavaCmd.load(ip, "android.widget.ProgressBar", "progressbar");
	JavaCmd.load(ip, "android.widget.LinearLayout$LayoutParams", "linearlayoutparams");
	JavaCmd.load(ip, "android.widget.Spinner", "spinner");
    }

    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip, cmdtable);
	ViewCmd.unload(ip);
    }

}
