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

import java.util.Vector;
import android.app.DatePickerDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TimePicker;
import org.hecl.DoubleThing;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.LongThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.StringThing;
import org.hecl.Thing;

/**
 * The <code>HeclCallback</code> class is utilized as a way to create
 * callbacks for various GUI things that require them.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class HeclCallback implements
			  android.app.DatePickerDialog.OnDateSetListener,
			  android.app.TimePickerDialog.OnTimeSetListener,
			  android.hardware.SensorListener,
			  android.widget.AdapterView.OnItemClickListener,
			  android.widget.AdapterView.OnItemSelectedListener,
			  android.widget.CompoundButton.OnCheckedChangeListener,
			  android.widget.DatePicker.OnDateChangedListener,
			  android.widget.TimePicker.OnTimeChangedListener,
			  android.view.View.OnClickListener {

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

    public void onAccuracyChanged(int sensor, int accuracy) {
	try {
	    Vector vec = ListThing.get(script.deepcopy());
	    vec.add(IntThing.create(sensor));
	    vec.add(IntThing.create(accuracy));
	    interp.eval(ListThing.create(vec));
	} catch (HeclException he) {
	    Hecl.logStacktrace(he);
	    Log.v("hecl onAccuracyChanged callback", he.toString());
	}
    }

    public void onSensorChanged(int sensor, float[] values) {
	try {
	    Vector vec = ListThing.get(script.deepcopy());
	    vec.add(IntThing.create(sensor));
 	    for (float f : values) {
		vec.add(DoubleThing.create(f));
	    }
	    interp.eval(ListThing.create(vec));
	} catch (HeclException he) {
	    Hecl.logStacktrace(he);
	    Log.v("hecl onSensorChanged callback", he.toString());
	}
    }

    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	try {
	    Vector vec = ListThing.get(script.deepcopy());
	    vec.add(ObjectThing.create(view));
	    vec.add(IntThing.create(year));
	    vec.add(IntThing.create(monthOfYear));
	    vec.add(IntThing.create(dayOfMonth));
	    interp.eval(ListThing.create(vec));
	} catch (HeclException he) {
	    Hecl.logStacktrace(he);
	    Log.v("hecl datechanged callback", he.toString());
	}
    }

    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	try {
	    Vector vec = ListThing.get(script.deepcopy());
	    vec.add(ObjectThing.create(view));
	    vec.add(IntThing.create(year));
	    vec.add(IntThing.create(monthOfYear));
	    vec.add(IntThing.create(dayOfMonth));
	    interp.eval(ListThing.create(vec));
	} catch (HeclException he) {
	    Hecl.logStacktrace(he);
	    Log.v("hecl dateset callback", he.toString());
	}
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	try {
	    Vector vec = ListThing.get(script.deepcopy());
	    vec.add(ObjectThing.create(view));
	    vec.add(IntThing.create(hourOfDay));
	    vec.add(IntThing.create(minute));
	    interp.eval(ListThing.create(vec));
	} catch (HeclException he) {
	    Hecl.logStacktrace(he);
	    Log.v("hecl timeset callback", he.toString());
	}
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
	try {
	    Vector vec = ListThing.get(script.deepcopy());
	    vec.add(ObjectThing.create(view));
	    vec.add(IntThing.create(hourOfDay));
	    vec.add(IntThing.create(minute));
	    interp.eval(ListThing.create(vec));
	} catch (HeclException he) {
	    Hecl.logStacktrace(he);
	    Log.v("hecl timechanged callback", he.toString());
	}
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	try {
	    Vector vec = ListThing.get(script.deepcopy());
	    vec.add(ObjectThing.create(buttonView));
	    vec.add(IntThing.create(isChecked));
	    interp.eval(ListThing.create(vec));
	} catch (HeclException he) {
	    Hecl.logStacktrace(he);
	    Log.v("hecl oncheckedchanged callback", he.toString());
	}
    }

    public void onItemClick(AdapterView parent, View v, int position, long id) {
	try {
	    Vector vec = ListThing.get(script.deepcopy());
	    vec.add(ObjectThing.create(parent));
	    vec.add(ObjectThing.create(v));
	    vec.add(IntThing.create(position));
	    vec.add(LongThing.create(id));
	    interp.eval(ListThing.create(vec));
	} catch (HeclException he) {
	    Hecl.logStacktrace(he);
	    Log.v("hecl onitemclick callback", he.toString());
	}
    }

    public void onItemSelected(AdapterView parent, View v, int position, long id) {
	try {
	    Vector vec = ListThing.get(script.deepcopy());
	    vec.add(ObjectThing.create(parent));
	    vec.add(ObjectThing.create(v));
	    vec.add(IntThing.create(position));
	    vec.add(LongThing.create(id));
	    interp.eval(ListThing.create(vec));
	} catch (HeclException he) {
	    Hecl.logStacktrace(he);
	    Log.v("hecl onitemselected callback", he.toString());
	}
    }

    public void onNothingSelected(AdapterView parent) {
	try {
	    Vector vec = ListThing.get(script.deepcopy());
	    vec.add(ObjectThing.create(parent));
	    interp.eval(ListThing.create(vec));
	} catch (HeclException he) {
	    Hecl.logStacktrace(he);
	    Log.v("hecl onnothingselected callback", he.toString());
	}
    }
}
