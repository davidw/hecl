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
import javax.microedition.lcdui.Graphics;

//#ifdef ant:j2se
import java.awt.Color;
//#else
import org.awt.Color;
//#endif

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.StringThing;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

public class CanvasCmd extends DisplayableCmd {
    public static void load(Interp ip) {
	ip.addCommand(CMDNAME,cmd);
	ip.addClassCmd(HeclCanvas.class,cmd);
    }
    public static void unload(Interp ip) {
	ip.removeCommand(CMDNAME);
	ip.removeClassCmd(HeclCanvas.class);
    }
    
    public Thing cmdCode(Interp interp,Thing[] argv) throws HeclException {
	Properties p = WidgetInfo.defaultProps(Canvas.class);
	p.setProps(argv,1);
	HeclCanvas w = new HeclCanvas(HeclUtils.thing2bool(
					  p.getProp(WidgetInfo.NSUPPRESSKEYS)));
	p.delProp(WidgetInfo.NTITLE);
	p.delProp(WidgetInfo.NSUPPRESSKEYS);
	return ObjectThing.create(setInstanceProperties(interp,w,p));
    }

    
    protected CanvasCmd() {}
    

    public Thing cget(Interp ip,Object target,String optname) throws HeclException {
	HeclCanvas c = (HeclCanvas)target;
	
	if(optname.equals(WidgetInfo.NWIDTH))
	    return IntThing.create(c.getWidth());
	if(optname.equals("-drawwidth"))
	    return IntThing.create(c.getDrawWidth());
	if(optname.equals(WidgetInfo.NHEIGHT))
	    return IntThing.create(c.getHeight());
	if(optname.equals("-drawheight"))
	    return IntThing.create(c.getDrawHeight());
	if(optname.equals("-fullwidth"))
	    return IntThing.create(c.getFullWidth());
	if(optname.equals("-fullheight"))
	    return IntThing.create(c.getFullHeight());
	if(optname.equals("-fullscreen"))
	    return IntThing.create(c.getFullScreenMode());
	if(optname.equals("-doublebuffered"))
	    return IntThing.create(c.isDoubleBuffered());
	if(optname.equals("-pointerevents"))
	    return IntThing.create(c.hasPointerEvents());
	if(optname.equals("-pointermotionevents"))
	    return IntThing.create(c.hasPointerMotionEvents());
	if(optname.equals("-repeatevents"))
	    return IntThing.create(c.hasRepeatEvents());
	if(optname.equals("-autoflush"))
	    return IntThing.create(c.getAutoFlushMode());
	if(optname.equals("-cmdbg"))
	    return WidgetInfo.fromColor(c.getCmdBgColor());
	if(optname.equals("-cmdfg"))
	    return WidgetInfo.fromColor(c.getCmdFgColor());
	//#ifdef notdef
	GraphicsCmd gcmd = c.getGraphicsCmd();
	if(gcmd != null) {
	    return gcmd.cget(ip,target,optname);
	}
	//#endif
	return super.cget(ip,target,optname);
    }
    

    public void cset(Interp ip,Object target,String optname,Thing optval)
	throws HeclException {
	HeclCanvas c = (HeclCanvas)target;

	if(optname.equals("-fullscreen")) {
	    c.setFullScreenMode(HeclUtils.thing2bool(optval));
	    return;
	}
	if(optname.equals("-autoflush")) {
	    c.setAutoFlushMode(HeclUtils.thing2bool(optval));
	    return;
	}
	if(optname.equals("-cmdbg")) {
	    c.setCmdBgColor(new Color(WidgetInfo.toColor(optval)));
	    return;
	}
	if(optname.equals("-cmdfg")) {
	    c.setCmdFgColor(new Color(WidgetInfo.toColor(optval)));
	    return;
	}
	if(optname.equals("-eventhandler")) {
	    EventHandler h = null;
	    if(optval.toString().length() > 0)
		h = new WidgetListener(ip,optval,c);
	    c.setEventHandler(h);
	    return;
	}
	super.cset(ip,target,optname,optval);
    }
    

    public Thing handlecmd(Interp ip,Object target,String subcmd,
			   Thing[] argv,int startat)
	throws HeclException {
	HeclCanvas c = (HeclCanvas)target;

	if(subcmd.equals(WidgetInfo.NREPAINT)) {
	    c.repaint();
	    return null;
	}
	if(subcmd.equals("servicerepaints")) {
	    c.serviceRepaints();
	    return null;
	}
	if(subcmd.equals("flush")) {
	    if(argv.length == startat) {
		// Simple case, flush whole buffer
		c.flushGraphics();
		return null;
	    }
	    
	    // x, y, w, h
	    if(startat+4 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "x y w h");
	    
	    c.flushGraphics(IntThing.get(argv[startat]),
			    IntThing.get(argv[startat+1]),
			    IntThing.get(argv[startat+2]),
			    IntThing.get(argv[startat+3]));
	    return null;
	}
	if(subcmd.equals("graphics"))
	    return ObjectThing.create(c.getDrawable());
	return super.handlecmd(ip,target,subcmd,argv,startat);
    }

    private static CanvasCmd cmd = new CanvasCmd();
    private static final String CMDNAME = "lcdui.canvas";
}

// Variables:
// mode:java
// coding:utf-8
// End:
