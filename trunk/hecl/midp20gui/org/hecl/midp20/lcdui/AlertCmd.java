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

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Gauge;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.StringThing;
import org.hecl.Thing;

import org.hecl.midp20.MidletCmd;

import org.hecl.misc.HeclUtils;


public class AlertCmd extends ScreenCmd {
    public static void load(Interp ip) {
	ip.addCommand(CMDNAME,cmd);
	ip.addClassCmd(Alert.class,cmd);
    }
    public static void unload(Interp ip) {
	ip.removeCommand(CMDNAME);
	ip.removeClassCmd(Alert.class);
    }
    
    public Thing cmdCode(Interp interp,Thing[] argv) throws HeclException {
	Properties p = WidgetInfo.defaultProps(Alert.class);
	p.setProps(argv,1);
	Alert w = new Alert(p.getProp(WidgetInfo.NTITLE).toString(),
			    p.getProp(WidgetInfo.NTEXT).toString(),
			    null,
			    WidgetInfo.toAlertType(p.getProp(WidgetInfo.NTYPE)));
	p.delProp(WidgetInfo.NTITLE);
	p.delProp(WidgetInfo.NTEXT);
	p.delProp(WidgetInfo.NTYPE);
	return ObjectThing.create(setInstanceProperties(interp,w,p));
    };
    
    private AlertCmd() {}
    
    public Thing cget(Interp ip,Object target,String optname)
	throws HeclException {
	Alert a = (Alert)target;
	
	if(optname.equals(WidgetInfo.NTYPE))
	    return WidgetInfo.fromAlertType(a.getType());
	if(optname.equals(WidgetInfo.NTEXT))
	    return StringThing.create(a.getString());
	if(optname.equals("-timeout"))
	    return IntThing.create(a.getTimeout());
	if(optname.equals("-indicator")) {
	    return IntThing.create(a.getIndicator() != null);
	}
	return super.cget(ip,target,optname);
    }

    public void cset(Interp ip,Object target,String optname,Thing optval)
	throws HeclException {
	Alert a = (Alert)target;

	if(optname.equals(WidgetInfo.NTEXT)) {
	    a.setString(optval.toString());
	    return;
	}
	if(optname.equals(WidgetInfo.NTYPE)) {
	    a.setType(WidgetInfo.toAlertType(optval));
	    return;
	}
	if(optname.equals("-timeout")) {
	    int timeout = Alert.FOREVER-1;
		
	    if(optval.toString().equals("forever")) {
		timeout = Alert.FOREVER;
	    } else {
		timeout = HeclUtils.thing2int(optval,true,timeout);
	    }
	    if(timeout <= Alert.FOREVER-1) {
		throw new HeclException("Invalid timeout '"+optval.toString()+"'!");
	    }
	    a.setTimeout(timeout);
	    return;
	}
	if(optname.equals("-indicator")) {
	    Gauge indicator = a.getIndicator();
	    if(HeclUtils.thing2bool(optval)) {
		if(indicator == null)
		    indicator = new Gauge(null, false, Gauge.INDEFINITE,
					  Gauge.CONTINUOUS_RUNNING);
	    } else {
		indicator = null;
	    }
	    a.setIndicator(indicator);
	    return;
	}
	super.cset(ip,target,optname,optval);
    }

    public Thing handlecmd(Interp ip,Object target,String subcmd,
			   Thing[] argv,int startat)
	throws HeclException {
	if(subcmd.equals(WidgetInfo.NSETCURRENT)) {
	    // extension: setcurrent alert ?nextdisplayable?
	    if(argv.length == startat+1) {
		Displayable d = (Displayable)WidgetInfo.asWidget(argv[startat],Displayable.class,
								"Displayable",false);
		if(d != null) {
		    // start j2mepolish break down
		    // to allow correct preprocessing
		    Alert alert = (Alert)target;
		    Display displ = MidletCmd.getDisplay();

		    /*
		     * There is a problem with the WTK2.5.2 emulator:
		     * When switching from a fullscreen canvas to something else, the
		     * fullscreen mode of the canvas gets lost. We extract the old
		     * status and set it properly on display switch.
		     */
		    Displayable oldd = displ.getCurrent();
		    if(oldd != null && oldd instanceof HeclCanvas) {
			boolean flag = ((HeclCanvas)d).getFullScreenMode();
			displ.setCurrent(alert,d);
			((HeclCanvas)oldd).setFullScreenMode(flag);
		    } else {
			displ.setCurrent(alert,d);
		    }
		    // end j2mepolish break down
		    return null;
		}
		throw new HeclException("Invalid displayable.");
	    }
	    // fall thru, baseclass 
	}
	return super.handlecmd(ip,target,subcmd,argv,startat);
    }

    private static AlertCmd cmd = new AlertCmd();
    private static final String CMDNAME = "lcdui.alert";
}

// Variables:
// mode:java
// coding:utf-8
// End:
