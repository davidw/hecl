/*
 * Copyright 2005-2006
 * Wolfgang S. Kechel, data2c GmbH (www.data2c.com)
 * 
 * Author: Wolfgang S. Kechel - wolfgang.kechel@data2c.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hecl.midp20.lcdui;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.Ticker;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.ListThing;
import org.hecl.Thing;

public class WidgetMap extends CmdDataMap {
    public static final String NULLWIDGETNAME = "";


    public static final org.hecl.Command CMD = new org.hecl.Command() {
	    public void cmdCode(Interp interp,Thing[] argv) throws HeclException {
		WidgetMap wm = WidgetMap.mapOf(interp);
		Vector v = new Vector();
		
		Enumeration e = wm.keys();
		System.err.println("Widget Table:\n****** forward");
		while(e.hasMoreElements()) {
		    String n = (String)e.nextElement();
		    v.addElement(new Thing(n));
		    System.err.println("\t" + n + " ->" + wm.valueOf(n));
		}
		e = wm.values();
		System.err.println("****** backward");
		while(e.hasMoreElements()) {
		    Object o = e.nextElement();
		    System.err.println("\t" + o + " ->" + wm.nameOf(o));
		}
		interp.setResult(ListThing.create(v));
	    }
	};

    public static String addWidget(Interp ip,String name,Object widget,
				   org.hecl.Command acmd) {
	if(false) {
	    System.err.println("addwidget - ip="+ip
			       +"name="+name
			       +"widget="+widget
			       +"acmd="+acmd);
	}
	return mapOf(ip).put(name,widget,acmd);
    }
    

    public static String expandPercent(String before,char[] what,String replace[]) {
	StringBuffer res = new StringBuffer("");
	int from = 0;
	int pos = 0;
	int len = before.length();
	int numwhat = what.length;
	
	//System.err.println("-->before="+before);
	while(from < len) {
	    pos = before.indexOf('%',from);
	    if(pos >= 0 && pos < len-1) {
		// %-sequence, process it
		res.append(before.substring(from,pos));
		++pos;			    // skip %
		char ch = before.charAt(pos);
		int i = 0;
		for(i = 0; i<numwhat; ++i) {
		    if(ch == what[i]) {
			//System.err.println("replacing "+ch+" with "+replace[i]);
			res.append(replace[i]);
			break;
		    }
		}
		if(i >= numwhat) {
		    res.append('%');
		    res.append(ch);
		}
		from = pos+1;
	    } else {
		res.append(before.substring(from));
		break;
	    }
	    //System.err.println("*** " + res.toString());
	}
	//System.err.println("<--res="+res.toString());
	return res.toString();
    }
    

    public static WidgetMap mapOf(Interp ip) {
	WidgetMap map = (WidgetMap)mapOf(ip,NAME);
	if(map == null) {
	    map = new WidgetMap(ip);
	}
	return map;
    }
    

    public static void setWidgetResult(Interp ip,Object w) {
	ip.setResult(mapOf(ip).nameOf(w));
    }
    

    static void eval(Interp ip,String s,char[] expchars,String[] replace) {
	if(s != null && s.length() > 0) {
	    //System.err.println("WidgetMap::eval(" + ip + ", "+s);
	    // Perform %-substitution and evaluate
	    ip.evalAsync(new Thing(expandPercent(s, expchars, replace)));
	}
    }


    public String nameOf(Object w) {
	String s = super.nameOf(w);
	return s != null ? s : NULLWIDGETNAME;
    }
	    

    public Object asWidget(Thing thing,Class clazz,
			   String clazzname, boolean allownull)
	throws HeclException {
	
	String s = thing.toString();
	if(allownull && s.equals(WidgetMap.NULLWIDGETNAME))
	    return null;
	
	Object o = valueOf(s);
	
	if(o != null && clazz.isInstance(o)) {
	    return o;
	}
	if(clazzname != null) {
	    throw HeclException.createInvalidParameter(
		thing,"parameter",clazzname + " widget required.");
	}
    	return null;
    }
    

    public Ticker asTicker(Thing thing, boolean allownull,boolean throwerror)
	throws HeclException {
	return (Ticker)asWidget(thing, Ticker.class,
				throwerror ? "Ticker" : null,allownull);
    }
    

    public Gauge asGauge(Thing thing, boolean allownull,boolean throwerror)
	throws HeclException {
	return (Gauge)asWidget(thing, Gauge.class,
			       throwerror ? "Gauge" : null,allownull);
    }
    

    public Command asCommand(Thing thing, boolean allownull,boolean throwerror)
	throws HeclException {
	return (Command)asWidget(thing, Command.class,
				 throwerror? "Command" :null,allownull);
    }


    public Item asItem(Thing thing, boolean allownull,boolean throwerror)
	throws HeclException {
	return (Item)asWidget(thing, Item.class, throwerror ? "Item" : null,allownull);
    }


    // instance vars for implementation of interface WidgetMap
    private WidgetMap(Interp ip) {
	super(ip,NAME,"w");
    }

    private static final String NAME = "LCDUI::WIDGETMAP";

    private Hashtable wcb = new Hashtable();

}
