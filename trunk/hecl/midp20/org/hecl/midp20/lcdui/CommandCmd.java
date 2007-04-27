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

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.StringThing;
import org.hecl.Thing;

public class CommandCmd extends OptionCmd {
    public static void load(Interp ip) {
	ip.addCommand(CMDNAME,cmd);
	ip.addClassCmd(Command.class,cmd);
    }
    public static void unload(Interp ip) {
	ip.removeCommand(CMDNAME);
	ip.removeClassCmd(Command.class);
    }
    
    public Thing cmdCode(Interp interp,Thing[] argv) throws HeclException {
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
	return ObjectThing.create(setInstanceProperties(interp,w,p));
    }
    

    protected CommandCmd() {}
    

    public Thing cget(Interp ip,Object target,String optname)
	throws HeclException {
	Command cmd = (Command)target;
	
	if(optname.equals(WidgetInfo.NLABEL))
	    return StringThing.create(cmd.getLabel());
	if(optname.equals(WidgetInfo.NLONGLABEL))
	    return StringThing.create(cmd.getLongLabel());
	if(optname.equals(WidgetInfo.NTYPE))
	    return WidgetInfo.fromCommandType(cmd.getCommandType());
	if(optname.equals(WidgetInfo.NPRIO))
	    return IntThing.create(cmd.getPriority());
	return super.cget(ip,target,optname);
    }
    

    private static CommandCmd cmd = new CommandCmd();
    private static final String CMDNAME = "lcdui.command";
}
    
// Variables:
// mode:java
// coding:utf-8
// End:
