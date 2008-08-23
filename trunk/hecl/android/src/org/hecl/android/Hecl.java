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

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Thing;
import org.hecl.java.JavaCmd;
import org.hecl.net.Base64Cmd;
import org.hecl.net.HttpCmd;

/**
 * The <code>Hecl</code> class is the main entry point into Hecl
 * applications on Android.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class Hecl extends Activity
{
    /**
     * The <code>interp</code> variable is static in order to be
     * available to SubHecl activities.
     *
     */
    protected static Interp interp;

    protected HeclHandler heclHandler;


    public Thing onCreateOptionsMenuCallBack = null;
    public Thing onOptionsItemSelectedCallBack = null;

    public Thing onPauseCallBack = null;
    public Thing onDestroyCallBack = null;

    /**
     *  <code>mailBox</code> is used as a place to stash scripts being
     *  passed to SubHecls.
     *
     */
    private static Thing mailBox = null;


    /**
     * <code>refCount</code> is a reference count for the interpreter
     * itself.  We want it to go away when the main Hecl instance is
     * destroyed, but not for SubHecl instances.
     *
     */
    protected static int refCount = 0;


    /**
     * The <code>onCreate</code> is the application's entry point.
     * Everything starts here.
     *
     * @param heclApp a <code>Bundle</code> value
     */

    @Override
    public void onCreate(Bundle heclApp)
    {
        super.onCreate(heclApp);
	if (refCount == 0) {
	    try {
		Log.v("hecl", "Starting new interp");
		String script;
		interp = new Interp();
		loadLibs(interp);
		createCommands(interp);
		script = getResourceAsString(this.getClass(), R.raw.script, "UTF-8");
		interp.eval(new Thing(script));
	    } catch (Exception e) {
		logStacktrace(e);
		errmsg("Hecl Error: " + e.toString());
	    }
	}
	heclHandler = new HeclHandler(interp);
	refCount ++;
    }

    protected void createCommands(Interp i) throws HeclException {
 	JavaCmd.load(interp, "org.hecl.android.Hecl", "hecl");
 	JavaCmd.load(interp, "org.hecl.android.SubHecl", "subhecl");
    }

    /**
     * The <code>errmsg</code> method displays and logs an error
     * message.
     *
     * @param msg a <code>String</code> value
     */
    protected void errmsg(String msg) {
	Log.v("hecl", msg);
	Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void setMailBox(Thing m) {
	mailBox = m;
    }

    public Thing getMailBox() {
	return mailBox;
    }

    /**
     * The <code>logStacktrace</code> method dumps an Exception's
     * stack trace to the Android log.
     *
     * @param e an <code>Exception</code> value
     */
    public static void logStacktrace(Exception e) {
	Log.v("stacktrace", e.toString());
	StackTraceElement elements[] = e.getStackTrace();
	for (int i = 0; i < elements.length ; i ++) {
	    Log.v("stacktrace", elements[i].toString());
	}
    }


    /**
     * The <code>loadLibs</code> method loads some classes, commands,
     * and the lib.hcl file.
     *
     * @param interp an <code>Interp</code> value
     * @exception Exception if an error occurs
     */
    public void loadLibs(Interp interp) throws Exception {
	AndroidCmd.load(interp, this);
	HttpCmd.load(interp);
	Base64Cmd.load(interp);
	String script = getResourceAsString(this.getClass(), R.raw.lib, "UTF-8");
	interp.eval(new Thing(script));
    }

    /**
     * <code>onResume</code> is called both the first time the
     * activity is run, as well as when the activity is reawakened.
     *
     */
    @Override
    protected void onResume() {
	super.onResume();

	/* Make sure everything's pointing at the right place. */
	AndroidCmd.setCurrentHecl(this);
	Log.v("hecl", "onResume");
     }

    @Override
    protected void onPause() {
	super.onPause();

	if (onPauseCallBack != null) {
	    try {
		Vector vec = ListThing.get(onPauseCallBack.deepcopy());
		interp.eval(ListThing.create(vec));
	    } catch (HeclException he) {
		Hecl.logStacktrace(he);
		Log.v("hecl onPause callback", he.toString());
	    }
	}

	Log.v("hecl", "onPause");
    }

    @Override
    protected void onStop() {
	super.onStop();
	Log.v("hecl", "onStop");
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();

	if (onDestroyCallBack != null) {
	    try {
		Vector vec = ListThing.get(onDestroyCallBack.deepcopy());
		interp.eval(ListThing.create(vec));
	    } catch (HeclException he) {
		Hecl.logStacktrace(he);
		Log.v("hecl onDestroy callback", he.toString());
	    }
	}

	refCount --;
	if (refCount == 0) {
	    Log.v("hecl", "destroying interpreter");
	    interp.terminate();
	    interp = null;
	}
	Log.v("hecl", "onDestroy");
    }

    /**
     * The <code>onCreateOptionsMenu</code> method is called when the
     * user hits the phones 'menu' key.  It utilizes variables set by
     * the menusetup command defined in AndroidCmd.
     *
     * @param menu a <code>Menu</code> value
     * @return a <code>boolean</code> value
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

	if (onCreateOptionsMenuCallBack != null) {
	    try {
		Vector vec = ListThing.get(onCreateOptionsMenuCallBack.deepcopy());
		vec.add(ObjectThing.create(menu));
		interp.eval(ListThing.create(vec));
	    } catch (HeclException he) {
		Hecl.logStacktrace(he);
		Log.v("hecl onCreateOptionsMenu callback", he.toString());
	    }
	}

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * <code>onOptionsItemSelected</code> is called when the user
     * selects a menu item.  The callbacks are set up via the
     * menucallback command, defined in AndroidCmd.
     *
     * @param item a <code>MenuItem</code> value
     * @return a <code>boolean</code> value
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

	if (onOptionsItemSelectedCallBack != null) {
	    try {
		Vector vec = ListThing.get(onOptionsItemSelectedCallBack.deepcopy());
		vec.add(ObjectThing.create(item));
		interp.eval(ListThing.create(vec));
	    } catch (HeclException he) {
		Hecl.logStacktrace(he);
		Log.v("hecl onOptionsItemSelected callback", he.toString());
	    }
	}

        return super.onOptionsItemSelected(item);
    }


    /**
     * The <code>getResourceAsString</code> method returns a String,
     * given a Class, a resource id, and an encoding.  This is
     * utilized to fetch Hecl files stored inside the .apk.
     *
     * @param cl a <code>Class</code> value
     * @param resid an <code>int</code> value
     * @param encoding a <code>String</code> value
     * @return a <code>String</code> value
     * @exception IOException if an error occurs
     */
    private String getResourceAsString(Class cl, int resid, String encoding)
	throws IOException {
	byte[] buf = getResourceAsBytes(cl, resid);
	if(encoding != null) {
	    return new String(buf, encoding);
	}
	return new String(buf);
    }


    private byte[] getResourceAsBytes(Class cl, int resid)
	throws IOException {
	DataInputStream is = new DataInputStream(getResourceAsStream(cl, resid));
	byte[] buf = new byte[1024];
	int bytesread = 0;
	byte[] result = new byte[bytesread];
	int i = 0;
	int n = 0;

	while((n = is.read(buf,0,buf.length)) > 0) {
	    byte[] newres = new byte[n+bytesread];
	    for(i=0; i<bytesread; ++i) {
		newres[i] = result[i];
	    }
	    for(i=0; i<n; ++i, ++bytesread) {
		newres[bytesread] = buf[i];
	    }
	    result = newres;
	}
	is.close();
	return result;
    }

    private InputStream getResourceAsStream(Class cl, int resid) {
	return getResources().openRawResource(resid);
    }

    private static long elapsed = 0;
    public static void logTime() {
	long now = System.currentTimeMillis();
	if (elapsed == 0) {
	    Log.v("elapsed", "0");
	} else {
	    Log.v("elapsed", "" + (now - elapsed));
	}
	elapsed = now;
    }


    /**
     * The <code>getHandler</code> method returns the local handler.
     *
     * @return a <code>HeclHandler</code> value
     */
    public HeclHandler getHandler() {
	return heclHandler;
    }

}
