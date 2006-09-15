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
import javax.microedition.lcdui.Graphics;

//#ifdef ant:j2se
import java.awt.Color;
//#else
import org.awt.Color;
//#endif

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.Properties;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

public class CanvasCmd extends DisplayableCmd {
    public static final org.hecl.Command CREATE = new org.hecl.Command() {
	    public void cmdCode(Interp interp,Thing[] argv) throws HeclException {
		Properties p = WidgetInfo.defaultProps(Canvas.class);
		p.setProps(argv,1);
		HeclCanvas w = new HeclCanvas(HeclUtils.thing2bool(
						  p.getProp(WidgetInfo.NSUPPRESSKEYS)));
		p.delProp(WidgetInfo.NTITLE);
		p.delProp(WidgetInfo.NSUPPRESSKEYS);
		WidgetMap.addWidget(interp,null,w,new CanvasCmd(interp,w,p));
	    }
	};

    
    protected CanvasCmd(final Interp ip,HeclCanvas acanvas,Properties p)
	throws HeclException {
	super(ip,acanvas,p);
	callbacks = new Hashtable();
	canvasg = acanvas.getGraphics();
	acanvas.setEventHandler(new EventHandler() {
		public void handleEvent(CanvasEvent e) {
		    String cb = (String)callbacks.get(new Integer(e.reason));
		    if(cb != null) {
			HeclCanvas c = (HeclCanvas)e.canvas;
			WidgetMap wm = WidgetMap.mapOf(ip);
			String canvasname = wm.nameOf(c);
			
			if(canvasname != null) {
			    // Perform %-substitution and call the callback
			    // %W --> e.canvas
			    // %T --> e.reason
			    // %x --> e.x
			    // %y --> e.y
			    // %w --> e.width
			    // %h --> e.height
			    // %k --> e.keycode
			    char expandchars[] = {'W','T','x','y','w','h','k','K','g'};
			    String expansions[] = {
				canvasname, CanvasEvent.eventName(e.reason),
				    String.valueOf(e.x), String.valueOf(e.y),
				    String.valueOf(e.width), String.valueOf(e.height),
				    String.valueOf(e.keycode),
				    "none","none"
			    };
			    try {
				expansions[7] = c.getKeyName(e.keycode);
				expansions[8] = String.valueOf(c.getGameAction(e.keycode));
			    }
			    catch(IllegalArgumentException illgl) {
			    }
			    String todo = WidgetMap.expandPercent(cb,expandchars,expansions);
			    //System.out.println("we evaluate: "+todo+"<<");
			    ip.evalAsync(new Thing(todo));
			}
		    }
		}
	    });
    }
    

    public void cget(Interp ip,String optname) throws HeclException {
	HeclCanvas c = (HeclCanvas)getData();
	
	if(optname.equals(WidgetInfo.NWIDTH)) {
	    ip.setResult(c.getWidth());
	    return;
	}
	if(optname.equals(WidgetInfo.NHEIGHT)) {
	    ip.setResult(c.getHeight());
	    return;
	}
	if(optname.equals("-fullwidth")) {
	    ip.setResult(c.getFullWidth());
	    return;
	}
	if(optname.equals("-fullheight")) {
	    ip.setResult(c.getFullHeight());
	    return;
	}
	if(optname.equals("-fullscreen")) {
	    ip.setResult(c.getFullScreenMode());
	    return;
	}
	if(optname.equals("-doublebuffered")) {
	    ip.setResult(c.isDoubleBuffered());
	    return;
	}
	if(optname.equals("-pointerevents")) {
	    ip.setResult(c.hasPointerEvents());
	    return;
	}
	if(optname.equals("-pointermotionevents")) {
	    ip.setResult(c.hasPointerMotionEvents());
	    return;
	}
	if(optname.equals("-repeatevents")) {
	    ip.setResult(c.hasRepeatEvents());
	    return;
	}
	if(optname.equals("-autoflush")) {
	    ip.setResult(getAutoFlushMode());
	    return;
	}
	if(optname.equals("-cmdbg")) {
	    ip.setResult(WidgetInfo.fromColor(c.getCmdBgColor()));
	    return;
	}
	if(optname.equals("-cmdfg")) {
	    ip.setResult(WidgetInfo.fromColor(c.getCmdFgColor()));
	    return;
	}
	GraphicsCmd gcmd = c.getGraphicsCmd();
	if(gcmd != null) {
	    try {
		gcmd.cget(ip,optname);
		return;
	    }
	    catch (CmdException e) {
	    }
	}
	super.cget(ip,optname);
    }
    

    public void cset(Interp ip,String optname,Thing optval) throws HeclException {
	HeclCanvas c = (HeclCanvas)getData();

	if(optname.equals("-fullscreen")) {
	    c.setFullScreenMode(HeclUtils.thing2bool(optval));
	    return;
	}
	if(optname.equals("-autoflush")) {
	    setAutoFlushMode(HeclUtils.thing2bool(optval));
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
	GraphicsCmd gcmd = c.getGraphicsCmd();
	if(gcmd != null) {
	    try {
		gcmd.cset(ip,optname,optval);
		return;
	    }
	    catch (CmdException e) {
	    }
	}
	super.cset(ip,optname,optval);
    }
    

    public void handlecmd(Interp ip,String subcmd, Thing[] argv,int startat)
	throws HeclException {
	HeclCanvas c = (HeclCanvas)getData();

	if(subcmd.equals(WidgetInfo.NREPAINT)) {
	    c.repaint();
	    return;
	}
	if(subcmd.equals("addcallback")) {
	    if(startat < argv.length) {
		String eventname = argv[startat].toString();
		int i = CanvasEvent.eventOf(eventname);
		if(i != CanvasEvent.E_UNKNOWN)
		    addCallback(i,argv[startat+1].toString());
	    }
	    return;
	}
	if(subcmd.equals("removecallback")) {
	    if(startat < argv.length) {
		String eventname = argv[startat].toString();
		int i = CanvasEvent.eventOf(eventname);
		if(i != CanvasEvent.E_UNKNOWN)
		    removeCallback(i);
	    }
	    return;
	}
	if(subcmd.equals("servicerepaints")) {
	    c.serviceRepaints();
	    return;
	}
	if(subcmd.equals("flush")) {
	    if(argv.length == startat) {
		// Simple case, flush whole buffer
		c.flushGraphics();
		return;
	    }
	    
	    // x, y, w, h
	    if(startat+4 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "x y w h");
	    
	    c.flushGraphics(IntThing.get(argv[startat]),
			    IntThing.get(argv[startat+1]),
			    IntThing.get(argv[startat+2]),
			    IntThing.get(argv[startat+3]));
	    return;
	}

	// Draw commands go here
	try {
	    GraphicsCmd gcmd = c.getGraphicsCmd();
	    if(gcmd != null) {
		gcmd.handlecmd(ip,subcmd,argv,startat);
		if(autoflush && gcmd.needsFlush() && c.isShown()) {
		    c.flushGraphics();
		    gcmd.flush();
		}
		return;
	    }
	}
	catch (CmdException e) {
	}
	super.handlecmd(ip,subcmd,argv,startat);
    }

    public void addCallback(int eventCode,String s) {
	if(s != null)
	    callbacks.put(new Integer(eventCode),s);
	else
	    removeCallback(eventCode);
    }
	    

    public boolean getAutoFlushMode() {
	return autoflush;
    }
    

    public void removeCallback(int eventCode) {
	callbacks.remove(new Integer(eventCode));
    }
	    
    
    public void setAutoFlushMode(boolean b) {
	autoflush = b;
    }
    
    protected Graphics canvasg;
    protected Hashtable callbacks;
    private boolean autoflush = true;
}
