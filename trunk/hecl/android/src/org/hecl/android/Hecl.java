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

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.os.Bundle;

import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Thing;

import org.hecl.net.HttpCmd;

public class Hecl extends Activity
{
    private Interp interp;
    private Thing menuCreateCode;
    private Thing menuItemSelected;

    /* Do something with error messages.  */
    private void errmsg(String msg) {
	Log.v("hecl", msg);
	NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	nm.notifyWithText(1, msg,
			  NotificationManager.LENGTH_LONG, null);
	showAlert("Hecl Error", msg, "dismiss", false);
    }

    public static void logStacktrace(Exception e) {
	Log.v("stacktrace", e.toString());
	StackTraceElement elements[] = e.getStackTrace();
	for (int i = 0; i < elements.length ; i ++) {
	    Log.v("stacktrace", elements[i].toString());
	}
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle heclApp)
    {
	Log.v("hecl", "Starting application");
	int layoutid = 0;
        super.onCreate(heclApp);

 	try {
	    interp = new Interp();
	    AndroidCmd.load(interp, this);
	    HttpCmd.load(interp);
	    String script = getResourceAsString(this.getClass(),"/script.hcl","UTF-8");
	    interp.eval(new Thing(script));
	} catch (Exception e) {
	    logStacktrace(e);
	    errmsg("Hecl Error: " + e.toString());
	}
    }

/*     @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

	menu.add(0, REVERT_ID, R.string.menu_revert).setShortcut(KeyEvent.KEYCODE_0, 0, KeyEvent.KEYCODE_R);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(Menu.Item item) {
        // Handle all of the possible menu actions.
        switch (item.getId()) {
        return super.onOptionsItemSelected(item);
    }  */


    private String getResourceAsString(Class cl,String resname,String encoding)
	throws IOException {
	byte[] buf = getResourceAsBytes(cl,resname);
	if(encoding != null) {
	    return new String(buf,encoding);
	}
	return new String(buf);
    }


    private byte[] getResourceAsBytes(Class cl,String resname)
	throws IOException {
	DataInputStream is = new DataInputStream(getResourceAsStream(cl,resname));
	byte[] buf = new byte[512];
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
	    System.gc();
	}
	is.close();
	return result;
    }

    public InputStream getResourceAsStream(Class cl,String resname) {
	return getResources().openRawResource(R.raw.script);
    }

}
