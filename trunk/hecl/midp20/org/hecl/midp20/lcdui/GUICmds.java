/*
 * Copyright 2005-2007
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

import java.io.IOException;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.ObjectThing;
import org.hecl.Operator;
import org.hecl.RealThing;
import org.hecl.Thing;

public class GUICmds {
    public static final String SELVARNAME = "lcdui.select_command";
    public static final String DISMISSVARNAME = "lcdui.dismiss_command";

    public static void load(Interp ip,Display display) throws HeclException {
	// Add some default widgets
	ip.setVar(SELVARNAME,ObjectThing.create(List.SELECT_COMMAND));
	ip.setVar(DISMISSVARNAME,ObjectThing.create(Alert.DISMISS_COMMAND));
	
	// Add widget commands...
	AlertCmd.load(ip);
	CanvasCmd.load(ip);
	ChoiceGroupCmd.load(ip);
	CommandCmd.load(ip);
	DateFieldCmd.load(ip);
	EventCmd.load(ip);
	FontMap.load(ip);
	FormCmd.load(ip);
	GaugeCmd.load(ip);
	GraphicsCmd.load(ip);
	ImageCmd.load(ip);
	ImageItemCmd.load(ip);
	ListCmd.load(ip);
	SettingsCmd.load(ip,display);
	SpacerCmd.load(ip);
	StringItemCmd.load(ip);
	TextBoxCmd.load(ip);
	TextFieldCmd.load(ip);
	TickerCmd.load(ip);
    }
    

    public static void unload(Interp ip) throws HeclException {
	for(int i=0; i<varnames.length; ++i) {
	    try {
		ip.unSetVar(varnames[i],0);
	    }
	    catch(HeclException he){
	    }
	}
	AlertCmd.unload(ip);
	CanvasCmd.unload(ip);
	ChoiceGroupCmd.unload(ip);
	CommandCmd.unload(ip);
	DateFieldCmd.unload(ip);
	EventCmd.unload(ip);
	FontMap.unload(ip);
	FormCmd.unload(ip);
	GaugeCmd.unload(ip);
	GraphicsCmd.unload(ip);
	ImageCmd.unload(ip);
	ImageItemCmd.unload(ip);
	ListCmd.unload(ip);
	SettingsCmd.unload(ip);
	SpacerCmd.unload(ip);
	StringItemCmd.unload(ip);
	TextBoxCmd.unload(ip);
	TextFieldCmd.unload(ip);
	TickerCmd.unload(ip);
    }

    // Image stuff
    public static Image loadImage(String name)
	throws HeclException {
	Image im = null;
	while(true) {
	    //System.err.println("IMAGE TRYLOAD for name="+name);
	    try {
		im = Image.createImage(name);
		return im;
	    }
	    catch(IOException e) {
		if(im == null && !name.endsWith(PNGSUFFIX)) {
		    name = name + PNGSUFFIX;
		    continue;
		}
		if(im == null && !name.startsWith("/")) {
		    name = '/' + name;
		    continue;
		}
		throw new HeclException("Can't load image '"
					+name+"': "+e.toString());
	    }
	}
    }
    
    
    public static Image asImage(Thing t,boolean allownull) throws HeclException {
	RealThing rt = t.getVal();
	if(rt instanceof ObjectThing) {
	    Object x = ((ObjectThing)rt).get();
	    if((allownull && x == null) || (x instanceof Image))
		return (Image)x;
	}
	throw new HeclException("Invalid image '" + t.toString() + "'");
    }
    
    private static String PNGSUFFIX = ".png";

    private static final String varnames[] = new String[]{SELVARNAME,DISMISSVARNAME};
}

// Variables:
// mode:java
// coding:utf-8
// End:
