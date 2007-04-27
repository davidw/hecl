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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
//#ifdef polish.usePolishGui 
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Screen;
import javax.microedition.lcdui.Ticker;
//#endif

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.Thing;

import org.hecl.midp20.MidletCmd;

public abstract class DisplayableCmd extends OptionCmd {
    protected DisplayableCmd() {}
    
    public Thing cget(Interp ip,Object target,String optname)
	throws HeclException {
	Displayable d = (Displayable)target;
	
	if(optname.equals(WidgetInfo.NTICKER)) {
//#ifdef polish.usePolishGui 
	    if(d instanceof Screen) {
		Screen s = (Screen)d;
		Ticker t = s.getTicker();
		return ObjectThing.create(t);
//#ifdef notdef
	    } else if(d instanceof Canvas) {
		Canvas c = (Canvas)d;
		Ticker t = c.getTicker();
		return ObjectThing.create(t);
//#endif
	    } else {
		return ObjectThing.create(null);
	    }
	    
//#else
	    return ObjectThing.create(d.getTicker());
//#endif
	}
	if(optname.equals(WidgetInfo.NTITLE)) {
	    optname = d.getTitle();
	    return new Thing(optname != null ? optname : "");
	}
	if(optname.equals(WidgetInfo.NWIDTH))
	    return IntThing.create(d.getWidth());
	if(optname.equals(WidgetInfo.NHEIGHT))
	    return IntThing.create(d.getHeight());
	if(optname.equals(WidgetInfo.NCOMMANDACTION)) {
	    throw new HeclException("option '"
				    +WidgetInfo.NCOMMANDACTION+"' is write only");
	}
	if(optname.equals("-isshown"))
	    return new Thing(d.isShown() ? IntThing.ONE : IntThing.ZERO);
	return super.cget(ip,target,optname);
    }
    
    public void cset(Interp ip,Object target,String optname,Thing optval)
	throws HeclException {
	Displayable d = (Displayable)target;

	if(optname.equals(WidgetInfo.NTICKER)) {
//#ifdef polish.usePolishGui 
	    Ticker t = WidgetInfo.asTicker(optval,true,true);
	    if(d instanceof Screen) {
		Screen s = (Screen)d;
		s.setTicker(t);
//#ifdef notdef
	    } else if(d instanceof Canvas) {
		Canvas c = (Canvas)d;
		c.setTicker(t);
	    } else {
		d.setTicker(t);
//#endif
	    }
//#else
	    d.setTicker(WidgetInfo.asTicker(optval,true,true));
//#endif
	    return;
	}
	if(optname.equals(WidgetInfo.NTITLE)) {
	    d.setTitle(optval.toString());
	    return;
	}
	if(optname.equals(WidgetInfo.NCOMMANDACTION)) {
	    CommandListener listener = null;
	    if(optval.toString().length() > 0) {
		listener = new WidgetListener(ip,optval);
	    }
	    d.setCommandListener(listener);
	    return;
	}
	super.cset(ip,target,optname,optval);
    }
    
    public Thing handlecmd(Interp ip,Object target,String subcmd, Thing[] argv,int startat)
	throws HeclException {
	Displayable d = (Displayable)target;

	if(subcmd.equals(WidgetInfo.NADDCOMMAND)) {
	    int n = startat+1;
	    if(argv.length != n) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, "command");
	    }
	    d.addCommand(WidgetInfo.asCommand(argv[2],false,true));
	    return null;
	}
	if(subcmd.equals(WidgetInfo.NREMOVECOMMAND)) {
	    int n = startat+1;
	    if(argv.length != n) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, "command");
	    }
	    d.removeCommand(WidgetInfo.asCommand(argv[2],false,true));
	    return null;
	}
	if(subcmd.equals(WidgetInfo.NSETCURRENT)) {
	    Display.getDisplay(MidletCmd.midlet()).setCurrent(d);
	    return null;
	}
	return super.handlecmd(ip,target,subcmd,argv,startat);
    }

    private static final char COMMANDACTIONEXPANDCHARS[] = {'W','D'};
}

// Variables:
// mode:java
// coding:utf-8
// End:
