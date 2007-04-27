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

import javax.microedition.lcdui.Canvas;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ObjectThing;
import org.hecl.StringThing;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

public class EventCmd extends OptionCmd {
    public static void load(Interp ip) {
	ip.addClassCmd(CanvasEvent.class,cmd);
    }
    public static void unload(Interp ip) {
	ip.removeClassCmd(CanvasEvent.class);
    }
    
    public Thing cmdCode(Interp interp,Thing[] argv) throws HeclException {
	throw new HeclException("cannot create event");
    }

    protected EventCmd() {}
    
    public Thing cget(Interp ip,Object target,String optname) throws HeclException {
	CanvasEvent e = (CanvasEvent)target;
	
	Canvas c = e.canvas;
	if(optname.equals("-canvas"))
	    return ObjectThing.create(c);
	if(optname.equals("-reason"))
	    return IntThing.create(e.reason);
	if(optname.equals("-x"))
	    return IntThing.create(e.x);
	if(optname.equals("-y"))
	    return IntThing.create(e.y);
	if(optname.equals("-width"))
	    return IntThing.create(e.width);
	if(optname.equals("-height"))
	    return IntThing.create(e.height);
	if(optname.equals("-keycode"))
	    return IntThing.create(e.keycode);
	if(optname.equals("-keyname")) {
	    String s = null;
	    try {
		s = c.getKeyName(e.keycode);
	    }
	    catch(IllegalArgumentException ex) {
	    }
	    return StringThing.create(s != null ? s : "none");
	}
	if(optname.equals("-gameaction")) {
	    try {
		return IntThing.create(c.getGameAction(e.keycode));
	    }
	    catch(IllegalArgumentException ex) {
	    }
	    return IntThing.create(-1);
	}
	return super.cget(ip,target,optname);
    }

    public void cset(Interp ip,Object target,String optname,Thing optval)
	throws HeclException {
	//CanvasEvent e = (CanvasEvent)target;
	super.cset(ip,target,optname,optval);
    }

    private static EventCmd cmd = new EventCmd();
    //private static final String CMDNAME = "lcdui.event";
}

// Variables:
// mode:java
// coding:utf-8
// End:
