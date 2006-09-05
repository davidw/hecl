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

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.Properties;
import org.hecl.Thing;

public class CommandCmd extends OwnedThingCmd {
    public static final org.hecl.Command CREATE = new org.hecl.Command() {
	    public void cmdCode(Interp interp,Thing[] argv) throws HeclException {
		int prio = -1;
		Properties p = WidgetInfo.defaultProps(Command.class);
		
		p.setProps(argv,1);
		try {
		    prio = IntThing.get(p.getProp(WidgetInfo.NPRIO));
		}
		catch (HeclException e) {
		    prio = -1;
		}
		if(prio < 0) {
		    throw new HeclException("invalid command priority");
		}
		Command w = new Command(
		    p.getProp(WidgetInfo.NLABEL).toString(),
		    p.getProp(WidgetInfo.NLONGLABEL).toString(),
		    WidgetInfo.toCommandType(p.getProp(WidgetInfo.NTYPE)),
		    prio);
		
		p.delProp(WidgetInfo.NPRIO);
		p.delProp(WidgetInfo.NLABEL);
		p.delProp(WidgetInfo.NLONGLABEL);
		p.delProp(WidgetInfo.NTYPE);
		WidgetMap.addWidget(interp,null,w,new CommandCmd(interp,w,p));
	    }
	};
    

    protected CommandCmd(Interp ip,Command c,Properties p) throws HeclException {
	super(ip,c,p);
    }
    

    public void cget(Interp ip,String optname) throws HeclException {
	Command cmd = (Command)getData();
	
	if(optname.equals(WidgetInfo.NLABEL)) {
	    String s = cmd.getLabel();
	    ip.setResult(new Thing(s != null ? s : ""));
	    return;
	}
	if(optname.equals(WidgetInfo.NLONGLABEL)) {
	    String s = cmd.getLongLabel();
	    ip.setResult(new Thing(s != null ? s : ""));
	    return;
	}
	if(optname.equals(WidgetInfo.NTYPE)) {
	    ip.setResult(WidgetInfo.fromCommandType(cmd.getCommandType()));
	    return;
	}
	if(optname.equals(WidgetInfo.NPRIO)) {
	    ip.setResult(IntThing.create(cmd.getPriority()));
	    return;
	}
	super.cget(ip,optname);
    }
    

    public void cset(Interp ip,String optname,Thing optval) throws HeclException {
	super.cset(ip,optname,optval);
    }
}
    
