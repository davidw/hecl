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

import java.util.Vector;

import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.DatePicker;
import android.widget.TimePicker;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.StringThing;
import org.hecl.Thing;

public class HeclCallback implements android.view.View.OnClickListener,
			  android.widget.DatePicker.OnDateSetListener,
			  android.widget.TimePicker.OnTimeSetListener,
			  android.widget.TimePicker.OnTimeChangedListener {

    public static Interp interp;

    public Thing script;

    public HeclCallback(Thing callback) {
	script = callback;
    }

    public void onClick(View view) {
	try {
	    Vector vec = ListThing.get(script.deepcopy());
	    vec.add(ObjectThing.create(view));
	    interp.eval(ListThing.create(vec));
	} catch (HeclException he) {
	    Hecl.logStacktrace(he);
	    Log.v("hecl onclick callback", he.toString());
	}
    }

    public void dateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	try {
	    Vector vec = ListThing.get(script.deepcopy());
	    vec.add(ObjectThing.create(view));
	    vec.add(IntThing.create(year));
	    vec.add(IntThing.create(monthOfYear));
	    vec.add(IntThing.create(dayOfMonth));
	    interp.eval(ListThing.create(vec));
	} catch (HeclException he) {
	    Hecl.logStacktrace(he);
	    Log.v("hecl onclick callback", he.toString());
	}
    }

    public void timeSet(TimePicker view, int hourOfDay, int minute) {
	try {
	    Vector vec = ListThing.get(script.deepcopy());
	    vec.add(ObjectThing.create(view));
	    vec.add(IntThing.create(hourOfDay));
	    vec.add(IntThing.create(minute));
	    interp.eval(ListThing.create(vec));
	} catch (HeclException he) {
	    Hecl.logStacktrace(he);
	    Log.v("hecl onclick callback", he.toString());
	}
    }

    public void timeChanged(TimePicker view, int hourOfDay, int minute) {
	try {
	    Vector vec = ListThing.get(script.deepcopy());
	    vec.add(ObjectThing.create(view));
	    vec.add(IntThing.create(hourOfDay));
	    vec.add(IntThing.create(minute));
	    interp.eval(ListThing.create(vec));
	} catch (HeclException he) {
	    Hecl.logStacktrace(he);
	    Log.v("hecl onclick callback", he.toString());
	}
    }
}
