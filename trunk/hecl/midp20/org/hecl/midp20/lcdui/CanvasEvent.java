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

import java.util.Hashtable;

import javax.microedition.lcdui.Canvas;

public class CanvasEvent {
    // Event types
    public static final int E_NONE = 0;
    public static final int E_PAINT = 1;
    public static final int E_PPRESS = 2;
    public static final int E_PRELEASE = 3;
    public static final int E_PDRAG = 4;
    public static final int E_KPRESS = 5;
    public static final int E_KRELEASE = 6;
    public static final int E_KREPEAT = 7;
    public static final int E_HIDE = 8;
    public static final int E_SHOW = 9;
    public static final int E_RESIZE = 10;
    public static final int E_UNKNOWN = -1;


    public static int eventOf(String s) {
	for(int i=0; i<eventcodes.length; ++i)
	    if(eventnames[i].equals(s))
		return eventcodes[i];
	return E_UNKNOWN;
    }
    

    public static String eventName(int eventcode) {
	for(int i=0; i<eventcodes.length; ++i)
	    if(eventcodes[i] == eventcode)
		return eventnames[i];
	return "unknown";
    }
    

    public CanvasEvent(Canvas acanvas,int reason) {
	this(acanvas,reason,0,0,0,0,0);
    }
    
    public CanvasEvent(Canvas acanvas,int reason,int x,int y,
		       int width,int height,int keycode) {
	this.canvas = acanvas;
	this.reason = reason;
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	this.keycode = keycode;
    }
    

    public String asString() {
	return "CanvasEvent-"+toString()+" - " + reason+" - "+eventName(reason);
    }
    

    public void consume() {
	consumed = true;
    }
    

    public boolean isConsumed() {
	return consumed;
    }
    
    public Canvas canvas;
    public int reason;
    public int x;
    public int y;
    public int width;
    public int height;
    public int keycode;
    public boolean consumed = false;

    private static int[] eventcodes = {
	E_NONE,E_PAINT,E_PPRESS,E_PRELEASE,E_PDRAG,
	    E_KPRESS,E_KREPEAT,E_KREPEAT,
	    E_HIDE,E_SHOW,E_RESIZE
    };
    
    private static String[] eventnames = {
	"none","paint","ppress", "prelease","pdrag",
	    "kpress", "krelease", "krepeat",
	    "hide", "show", "resize"
    };
    
}
    

