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

import java.lang.reflect.Constructor;

import java.util.Enumeration;
import java.util.Vector;

import android.content.Context;

import android.util.Log;

import android.view.ViewGroup;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.hecl.HeclException;
import org.hecl.IntThing;
import org.hecl.Interp;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.Thing;

public class MethodProps extends Properties {
    private LayoutParams layoutparams = null;

    public MethodProps() {
	super();
    }

    public void evalProps(Context context, Interp interp, Object target, Reflector ref)
	throws HeclException {

	specialCases(context, interp, target);

	Enumeration names = props.keys();
	while(names.hasMoreElements()) {
	    String name = (String)names.nextElement();
	    String methodname = "set" + name.substring(1);
	    /* First two need to be null to replace cmdname and subcmdname. */
	    Thing[] argv = { null, null, getProp(name) };
	    ref.evaluate(target, methodname, argv);
	}
    }

    public void specialCases(Context context, final Interp interp, Object target)
	throws HeclException {



	if (existsProp("-orientation")) {
	    String orientation = getProp("-orientation").toString();
	    if (orientation.equals("horizontal")) {
		((LinearLayout)target).setOrientation(LinearLayout.HORIZONTAL);
	    } else if (orientation.equals("vertical")) {
		((LinearLayout)target).setOrientation(LinearLayout.VERTICAL);
	    } else {
		throw new HeclException("-orientation must be either horizontal or vertical");
	    }
	    delProp("-orientation");
	}

	/* Deal with layouts.  */
	if (existsProp("-layout_width") &&
	    existsProp("-layout_height") &&
	    existsProp("-layout")) {
	    ViewGroup layout = (ViewGroup)ObjectThing.get(getProp("-layout"));
	    Thing w = getProp("-layout_width");
	    Thing h = getProp("-layout_height");
	    String ws = w.toString();
	    String hs = h.toString();
	    int width = 0;
	    int height = 0;

	    if (ws.equals("fill_parent")) {
		width = LayoutParams.FILL_PARENT;
	    } else if (ws.equals("wrap_content")) {
		width = LayoutParams.WRAP_CONTENT;
	    } else {
		width = IntThing.get(w);
	    }

	    if (hs.equals("fill_parent")) {
		height = LayoutParams.FILL_PARENT;
	    } else if (hs.equals("wrap_content")) {
		height = LayoutParams.WRAP_CONTENT;
	    } else {
		height = IntThing.get(h);
	    }

 	    try {
		/* Create the right kind of LayoutParams. */
		Class layoutclass = Class.forName(layout.getClass().getName() +
						  "$LayoutParams");
		Constructor cons = layoutclass.getConstructor(new Class[] {int.class, int.class});
		layoutparams = (ViewGroup.LayoutParams)cons.newInstance(width, height);
	    } catch (Exception e) {
		throw new HeclException(e.toString());
	    }
	    layout.addView((View) target, layoutparams);
	    delProp("-layout_width");
	    delProp("-layout_height");
	    delProp("-layout");
	}

	/* For anything that can be clicked on. */
	if (existsProp("-onclick")) {
	    final Thing script = getProp("-onclick");

	    OnClickListener ocl = new OnClickListener() {
		public void onClick(View view) {
		    Vector v = null;
		    try {
			v = ListThing.get(script.deepcopy());
			v.add(ObjectThing.create(view));
			interp.eval(ListThing.create(v));
		    } catch (HeclException he) {
			Log.v("hecl onclick callback", he.toString());
		    }
		}
	    };
	    ((View)target).setOnClickListener(ocl);
	    delProp("-onclick");
	}

	if (existsProp("-itemlist")) {
	    Thing list = getProp("-itemlist");

//	    ThingAdapter ta = new ThingAdapter(list);
	    Spinner s = (Spinner)target;
	    ArrayAdapter<CharSequence> adapter = new ArrayAdapter(
		context, android.R.layout.simple_spinner_item, ListThing.getArray(list));
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    s.setAdapter(adapter);
	    delProp("-itemlist");
	}

    }
    public LayoutParams getLayoutParams() {
	return layoutparams;
    }
}
