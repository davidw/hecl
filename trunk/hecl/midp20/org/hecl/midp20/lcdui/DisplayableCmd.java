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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Properties;
import org.hecl.Thing;

import org.hecl.midp20.MidletCmd;

public abstract class DisplayableCmd extends OwnedThingCmd implements CommandListener {
    protected DisplayableCmd(Interp ip,Displayable displayable,Properties p)
	throws HeclException {
	super(ip,displayable,p);
	displayable.setCommandListener(this);
    }


    public void commandAction(Command c,Displayable d) {
	WidgetMap wm = WidgetMap.mapOf(getCreator());
	String dname = wm.nameOf(d);
	String cname = wm.nameOf(c);
	//System.out.println(" -->"+getClass().getName()+"::commandAction(" +cname+" - "+c.getLabel() +", "+dname+")");
	String expansions[] = {cname,dname};
	wm.commandAction((OwnedThingCmd)wm.commandOf(c),
			 commandActionExpandChars, expansions);
    }
    

    public void cget(Interp ip,String optname) throws HeclException {
	Displayable d = (Displayable)getData();
	
	if(optname.equals(WidgetInfo.NTICKER)) {
	    WidgetMap.setWidgetResult(ip,d.getTicker());
	    return;
	}
	if(optname.equals(WidgetInfo.NTITLE)) {
	    ip.setResult(d.getTitle());
	    return;
	}
	if(optname.equals(WidgetInfo.NWIDTH)) {
	    ip.setResult(d.getWidth());
	    return;
	}
	if(optname.equals(WidgetInfo.NHEIGHT)) {
	    ip.setResult(d.getHeight());
	    return;
	}
	super.cget(ip,optname);
    }
    
    public void cset(Interp ip,String optname,Thing optval) throws HeclException {
	Displayable d = (Displayable)getData();

	if(optname.equals(WidgetInfo.NTICKER)) {
	    d.setTicker(WidgetMap.mapOf(ip).asTicker(optval,true,true));
	    return;
	}
	if(optname.equals(WidgetInfo.NTITLE)) {
	    d.setTitle(optval.toString());
	    return;
	}
	super.cset(ip,optname,optval);
    }
    
    public void handlecmd(Interp ip,String subcmd, Thing[] argv,int startat)
	throws HeclException {
	Displayable d = (Displayable)getData();
	WidgetMap wm = WidgetMap.mapOf(ip);

	if(subcmd.equals(WidgetInfo.NADDCOMMAND)) {
	    int n = startat+1;
	    if(argv.length != n) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, "command");
	    }
	    d.addCommand(wm.asCommand(argv[2],false,true));
	    return;
	}
	if(subcmd.equals(WidgetInfo.NREMOVECOMMAND)) {
	    int n = startat+1;
	    if(argv.length != n) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, "command");
	    }
	    d.removeCommand(wm.asCommand(argv[2],false,true));
	    return;
	}
	if(subcmd.equals(WidgetInfo.NSETCURRENT)) {
	    Display.getDisplay(MidletCmd.midlet()).setCurrent(d);
	    return;
	}
	if(subcmd.equals("delete")) {
	    String name = wm.nameOf(getData());
	    System.err.println("v="+getData());
	    if(name != null) {
		System.err.println("name="+name);
		wm.remove(name);
	    }
	    return;
	}
	
	super.handlecmd(ip,subcmd,argv,startat);
    }

    private static final char commandActionExpandChars[] = {'W','D'};
}
