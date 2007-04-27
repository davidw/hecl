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

import javax.microedition.lcdui.Spacer;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.Properties;
import org.hecl.ObjectThing;
import org.hecl.Thing;

public class SpacerCmd extends ItemCmd {
    public static void load(Interp ip) {
	ip.addCommand(CMDNAME,cmd);
	ip.addClassCmd(Spacer.class,cmd);
    }
    public static void unload(Interp ip) {
	ip.removeCommand(CMDNAME);
	ip.removeClassCmd(Spacer.class);
    }
    

    public Thing cmdCode(Interp interp,Thing[] argv)
	throws HeclException {
	Properties p = WidgetInfo.defaultProps(Spacer.class);
	p.setProps(argv,1);
	Spacer sp = new Spacer(IntThing.get(p.getProp(WidgetInfo.NMINWIDTH)),
			       IntThing.get(p.getProp(WidgetInfo.NMINHEIGHT)));
	p.delProp(WidgetInfo.NMINWIDTH);
	p.delProp(WidgetInfo.NMINHEIGHT);
	return ObjectThing.create(setInstanceProperties(interp,sp,p));
    }
    

    protected SpacerCmd() {}
    

    public Thing cget(Interp ip,Object target,String optname)
	throws HeclException {
	if(optname.equals(WidgetInfo.NLABEL))
	    // no label
	    return Thing.EMPTYTHING;
	return super.cget(ip,target,optname);
    }


    public void cset(Interp ip,Object target,String optname,Thing optval)
	throws HeclException {
	Spacer spacer = (Spacer)target;

	if(optname.equals(WidgetInfo.NLABEL)) {
	    // Ignore
	    return;
	}
	if(optname.equals(WidgetInfo.NMINWIDTH)) {
	    int newval = IntThing.get(optval);
	    if(newval < 0)
		newval = 0;
	    spacer.setMinimumSize(newval,spacer.getMinimumHeight());
	    return;
	}
	if(optname.equals(WidgetInfo.NMINHEIGHT)) {
	    int newval = IntThing.get(optval);
	    if(newval < 0)
		newval = 0;
	    spacer.setMinimumSize(spacer.getMinimumWidth(),newval);
	    return;
	}
	super.cset(ip,target,optname,optval);
    }


    public Thing handlecmd(Interp ip,Object target,String subcmd,
			   Thing[] argv,int startat)
	throws HeclException {
	if(subcmd.equals(WidgetInfo.NADDCOMMAND)) {
	    // Ignore
	    //throw new HeclException("Spacer does not support commands.");
	}
	if(subcmd.equals(WidgetInfo.NREMOVECOMMAND)) {
	    // Ignore
	    //throw new HeclException("Spacer does not support commands.");
	}
	return super.handlecmd(ip,target,subcmd,argv,startat);
    }

    private static SpacerCmd cmd = new SpacerCmd();
    private static final String CMDNAME = "lcdui.spacer";
}

// Variables:
// mode:java
// coding:utf-8
// End:
