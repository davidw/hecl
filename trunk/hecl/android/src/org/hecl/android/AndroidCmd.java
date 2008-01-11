/* Copyright 2007-2008 David N. Welton - DedaSys LLC - http://www.dedasys.com

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

import android.content.Intent;

import android.database.Cursor;

import android.net.ContentURI;

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

import org.hecl.java.HeclJavaCmd;
import org.hecl.java.JavaCmd;

/**
 * The <code>AndroidCmd</code> class is where all the Android specific
 * commands go.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class AndroidCmd extends Operator {
    protected static Hecl hecl = null;

    public static final int ACTIVITY = 0;
    public static final int EXIT = 1;
    public static final int RESLOOKUP = 2;
    public static final int ALERT = 3;
    public static final int FINDVIEW = 4;
    public static final int LOG = 4;
    public static final int NULLCMD = 5;
    public static final int MENUSETUP = 6;
    public static final int MENUCALLBACK = 7;

    public static final int PROVIDERQUERY = 8;

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
	NotificationManager nm = (NotificationManager)hecl.getSystemService(Activity.NOTIFICATION_SERVICE);
	nm.notifyWithText(1, msg, NotificationManager.LENGTH_LONG, null);
	hecl.showAlert("Hecl Alert", msg, "dismiss", false);
    }

    /**
     * The <code>setCurrentHecl</code> method makes AndroidCmd contain
     * the correct Hecl class to point at.  This is used for SubHecls.
     *
     * @param h a <code>Hecl</code> value
     */
    public static void setCurrentHecl(Hecl h) {
	hecl = h;
    }

    public Thing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	switch (cmd) {
	    case ACTIVITY:
		return ObjectThing.create(hecl);

	    case ALERT:
		makeAlert(argv[1].toString());
		return new Thing(argv[1].toString());
	    case EXIT:
		hecl.finish();
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


	    case PROVIDERQUERY:
		Cursor cur = null;
		try {
		    ContentURI curi = new ContentURI(argv[1].toString());
		    cur = hecl.managedQuery(curi, null, null, null);
		} catch (Exception e) {
		    Hecl.logStacktrace(e);
		    throw new HeclException(argv[0].toString() + " error: " + e.toString());
		}
		return ObjectThing.create(cur);

	    case NULLCMD:
		return ObjectThing.create(null);

	    case MENUSETUP:
		hecl.menuvar = argv[1];
		hecl.menucode = argv[2];
		return null;
	    case MENUCALLBACK:
		hecl.menucallbackvar = argv[1];
		hecl.menucallbackcode = argv[2];
		return null;

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
	    cmdtable.put("activity", new AndroidCmd(ACTIVITY,0,0));

	    cmdtable.put("exit", new AndroidCmd(EXIT,0,0));
	    cmdtable.put("reslookup", new AndroidCmd(RESLOOKUP,1,1));
	    cmdtable.put("alert", new AndroidCmd(ALERT,1,1));
	    cmdtable.put("findview", new AndroidCmd(FINDVIEW,1,1));
	    cmdtable.put("androidlog", new AndroidCmd(LOG,1,1));

	    cmdtable.put("query", new AndroidCmd(PROVIDERQUERY,1,1));

	    cmdtable.put("null", new AndroidCmd(NULLCMD,0,0));

	    cmdtable.put("menusetup", new AndroidCmd(MENUSETUP,2,2));
	    cmdtable.put("menucallback", new AndroidCmd(MENUCALLBACK,2,2));

	    cmdtable.put("i", new AndroidCmd(TOINT, 1, 1));
	    cmdtable.put("l", new AndroidCmd(TOLONG, 1, 1));
	    cmdtable.put("d", new AndroidCmd(TODOUBLE, 1, 1));
	    cmdtable.put("s", new AndroidCmd(TOSTR, 1, 1));
	} catch (Exception e) {
	    e.printStackTrace();
	    Log.v("hecl", "Can't create android commands.");
	}
    }

    public static void load(Interp ip, Hecl a) throws HeclException {
	hecl = a;
	Operator.load(ip, cmdtable);
	HeclJavaCmd.load(ip);

	JavaCmd.load(ip, "android.app.Activity", "androidactivity");

	JavaCmd.load(ip, "android.content.Intent", "intent");

	JavaCmd.load(ip, "android.database.Cursor", "cursor");

	JavaCmd.load(ip, "android.view.Menu", "menu");
	JavaCmd.load(ip, "android.view.Menu$Item", "menuitem");
	JavaCmd.load(ip, "android.view.View", "view");

	JavaCmd.load(ip, "android.widget.AdapterView", "adapterview");

	JavaCmd.load(ip, "android.widget.ArrayAdapter", "arrayadapter");
	JavaCmd.load(ip, "android.widget.Button", "button");
	JavaCmd.load(ip, "android.widget.CheckBox", "checkbox");
	JavaCmd.load(ip, "android.widget.EditText", "edittext");
	JavaCmd.load(ip, "android.widget.LinearLayout", "linearlayout");
	JavaCmd.load(ip, "android.widget.LinearLayout$LayoutParams", "linearlayoutparams");
	JavaCmd.load(ip, "android.widget.ListView", "listview");
	JavaCmd.load(ip, "android.widget.RadioGroup$LayoutParams", "radiogrouplayoutparams");
	JavaCmd.load(ip, "android.widget.ProgressBar", "progressbar");
	JavaCmd.load(ip, "android.widget.RadioButton", "radiobutton");
	JavaCmd.load(ip, "android.widget.RadioGroup", "radiogroup");
	JavaCmd.load(ip, "android.widget.ScrollView", "scrollview");
	JavaCmd.load(ip, "android.widget.Spinner", "spinner");
	JavaCmd.load(ip, "android.widget.TextView", "textview");
	JavaCmd.load(ip, "android.widget.TimePicker", "timepicker");

	JavaCmd.load(ip, "org.hecl.android.HeclCallback", "callback");
	JavaCmd.load(ip, "org.hecl.android.Hecl", "hecl");
	JavaCmd.load(ip, "org.hecl.android.SubHecl", "subhecl");

	HeclCallback.interp = ip;
    }

    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip, cmdtable);
	JavaCmd.unload(ip);
    }

}
