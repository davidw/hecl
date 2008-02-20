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

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.ObjectThing;
import org.hecl.Thing;

import org.hecl.net.HttpCmd;
import org.hecl.net.Base64Cmd;

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
    private Thing menuCreateCode;
    private Thing menuItemSelected;

    protected HeclHandler heclHandler;


    /* Public for the time being.  These are accessed from
     * AndroidCmd. */
    public Thing menuvar = null;
    public Thing menucode = null;

    public Thing menucallbackvar = null;
    public Thing menucallbackcode = null;

    /**
     *  <code>mailBox</code> is used as a place to stash scripts being
     *  passed to SubHecls.
     *
     */
    private static Thing mailBox = null;

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

	/* We don't want SubHecl to do this. */
	if (this.getClass() == Hecl.class) {
	    try {
		String script;
		interp = new Interp();
		loadLibs(interp);
		script = getResourceAsString(this.getClass(), R.raw.script, "UTF-8");
		interp.eval(new Thing(script));
	    } catch (Exception e) {
		logStacktrace(e);
		errmsg("Hecl Error: " + e.toString());
	    }
	}
	heclHandler = new HeclHandler(interp);
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
     }

    @Override
    protected void onPause() {
	super.onPause();
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
        super.onCreateOptionsMenu(menu);

	if (menuvar == null || menucode == null) {
	    return false;
	}

	try {
	    interp.setVar(menuvar, ObjectThing.create(menu));
	    interp.eval(menucode);
	} catch (HeclException he) {
	    errmsg(he.toString());
	}

        return true;
    }

    /**
     * <code>onOptionsItemSelected</code> is called when the user
     * selects a menu item.  The callbacks are set up via the
     * menucallback command, defined in AndroidCmd.
     *
     * @param item a <code>Menu.Item</code> value
     * @return a <code>boolean</code> value
     */
    @Override
    public boolean onOptionsItemSelected(Menu.Item item) {

	if (menucallbackvar == null || menucallbackcode == null) {
	    return false;
	}

	try {
	    interp.setVar(menucallbackvar, ObjectThing.create(item));
	    interp.eval(menucallbackcode);
	} catch (HeclException he) {
	    errmsg(he.toString());
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
