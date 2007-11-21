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

import android.util.AttributeSet;
import android.util.Log;

import java.util.Enumeration;
import java.util.Vector;

import org.hecl.DoubleThing;
import org.hecl.HeclException;
import org.hecl.IntThing;
import org.hecl.Properties;
import org.hecl.Thing;

public class AttributeProps extends Properties implements AttributeSet {
    private Vector names;
    private Vector values;

    public AttributeProps() {
	super();
    }

    private void update() {
	Vector values = new Vector();
	Enumeration vals = props.elements();
	while(vals.hasMoreElements()) {
	    values.add(vals.nextElement());
	}

	Vector names = new Vector();
	Enumeration nms = props.keys();
	while(nms.hasMoreElements()) {
	    names.add(nms.nextElement());
	}

	Log.v("names", "are " + names);
	Log.v("values", "are " + values);
    }

    public void setProps(Thing []argv, int offset) {
	super.setProps(argv, offset);
	update();
    }

    public void setProp(String name, Thing val) {
	super.setProp(name, val);
	update();
    }

    public void delProp(String name) {
	super.delProp(name);
	update();
    }


    public boolean getAttributeBooleanValue(String namespace, String attribute, boolean defaultValue) {

	Log.v("attributeprops", "1");

	Thing val = super.getProp(attribute);
	try {
	    if (val != null) {
		return IntThing.get(val) != 0;
	    }
	} catch (HeclException he) {
	    /* Ignore it  */
	}
	return defaultValue;
    }

    public boolean getAttributeBooleanValue(int index, boolean defaultValue) {

	Log.v("attributeprops", "1");

	Thing val = (Thing)values.elementAt(index);
	try {
	    if (val != null) {
		return IntThing.get(val) != 0;
	    }
	} catch (HeclException he) {
	    /* Ignore it  */
	}
	return defaultValue;
    }

    public int getAttributeCount() {

	Log.v("attributeprops", "1");

	return values.size();
    }

    public float getAttributeFloatValue(int index, float defaultValue) {
	Log.v("attributeprops", "1");

	Thing val = (Thing)values.elementAt(index);
	try {
	    if (val != null) {
		return (float)DoubleThing.get(val);
	    }
	} catch (HeclException he) {
	    /* Ignore it  */
	}
	return defaultValue;
    }

    public float getAttributeFloatValue(String namespace, String attribute, float defaultValue) {

	Log.v("attributeprops", "1");

	Thing val = super.getProp(attribute);
	try {
	    if (val != null) {
		return (float)DoubleThing.get(val);
	    }
	} catch (HeclException he) {
	    /* Ignore it  */
	}
	return defaultValue;

    }

    public int getAttributeIntValue(int index, int defaultValue) {

	Log.v("attributeprops", "1");

	Thing val = (Thing)values.elementAt(index);
	try {
	    if (val != null) {
		return IntThing.get(val);
	    }
	} catch (HeclException he) {
	    /* Ignore it  */
	}
	return defaultValue;
    }

    public int getAttributeIntValue(String namespace, String attribute, int defaultValue) {

	Log.v("attributeprops", "1");

	Thing val = super.getProp(attribute);
	try {
	    if (val != null) {
		return IntThing.get(val);
	    } 	} catch (HeclException he) {
	    /* Ignore it  */
	}
	return defaultValue;
    }

    public int getAttributeListValue(String namespace, String attribute, String[] options, int defaultValue) {

	Log.v("attributeprops", "1");

	return 0;
	
    }

    public int getAttributeListValue(int index, String[] options, int defaultValue) {
	Log.v("attributeprops", "1");

	return 0;
	
    }

    public String getAttributeName(int index) {
	Log.v("attributeprops", "1");

	return names.elementAt(index).toString();
    }

    public int getAttributeNameResource(int index) {
	Log.v("attributeprops", "1");

	return 0; /* There's no resource... */
    }

    public int getAttributeResourceValue(String namespace, String attribute, int defaultValue) {
	Log.v("attributeprops", "1");

	return defaultValue;
    }

    public int getAttributeResourceValue(int index, int defaultValue) {
	Log.v("attributeprops", "1");

	return defaultValue;
    }

    public int getAttributeUnsignedIntValue(String namespace, String attribute, int defaultValue) {

	Log.v("attributeprops", "1");

	Thing val = super.getProp(attribute);
	try {
	    if (val != null) {
		return (int)IntThing.get(val);
	    }
	} catch (HeclException he) {
	    /* Ignore it  */
	}
	return defaultValue;
    }

    public int getAttributeUnsignedIntValue(int index, int defaultValue) {
	Log.v("attributeprops", "1");

	Thing val = (Thing)values.elementAt(index);
	try {
	    if (val != null) {
		return (int)IntThing.get(val);
	    }
	} catch (HeclException he) {
	    /* Ignore it  */
	}
	return defaultValue;
    }

    public String getAttributeValue(String namespace, String name) {
	Log.v("attributeprops", "1");

	Thing val = super.getProp(name);
	return val.toString();
    }

    public String getAttributeValue(int index) {
	Log.v("attributeprops", "1");

	Thing val = (Thing)values.elementAt(index);
	return val.toString();
    }

    public String getClassAttribute() {
	Log.v("attributeprops", "1");

	return null;
    }

    public String getIdAttribute() {
	Log.v("attributeprops", "1");

	return null;
    }

    public int getIdAttributeResourceValue(int defaultValue) {
	Log.v("attributeprops", "1");

	return defaultValue;
    }

    public String getPositionDescription() {
	Log.v("attributeprops", "1");

	return "";
    }

    public int getStyleAttribute() {
	Log.v("attributeprops", "1");

	return 0;
    }
}