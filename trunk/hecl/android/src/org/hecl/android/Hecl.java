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

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Thing;

import android.util.Log;

public class Hecl extends Activity
{
    Interp interp;

    /* Do something with error messages.  */
    private void errmsg(String msg) {
	NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	nm.notifyWithText(1,
			  msg,
			  NotificationManager.LENGTH_LONG, null);

	showAlert("Hecl Error", msg, "dismiss", false);
    }

    private void logStacktrace(Exception e) {
	StackTraceElement elements[] = e.getStackTrace();
	for (int i = 0; i < elements.length ; i ++) {
	    Log.v("stacktrace", elements[i].toString());
	}
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle heclApp)
    {
	Log.v("hecl", "Starting application");
	int layoutid = 0;
        super.onCreate(heclApp);

 	try {
	    interp = new Interp();
	    AndroidCmd.load(interp, this);

	    String script = getResourceAsString(this.getClass(),"/script.hcl","UTF-8");
	    interp.eval(new Thing(script));
	} catch (Exception e) {
	    logStacktrace(e);
	    errmsg("Hecl Error: " + e.toString());
	}
    }

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
