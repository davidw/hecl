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

import java.util.Date;

import javax.microedition.lcdui.DateField;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.LongThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.Thing;

public class DateFieldCmd extends ItemCmd {
    public static void load(Interp ip) {
	ip.addCommand(CMDNAME,cmd);
	ip.addClassCmd(DateField.class,cmd);
    }
    public static void unload(Interp ip) {
	ip.removeCommand(CMDNAME);
	ip.removeClassCmd(DateField.class);
    }

    /*
    public DateGadget(String label,int mode,FormCmd f) {
	super(new DateField(label,mode),f);
    }
    */
    protected DateFieldCmd() {}
    
    
    public Thing cmdCode(Interp interp,Thing[] argv) throws HeclException {
	Properties p = WidgetInfo.defaultProps(DateField.class);
	p.setProps(argv,1);
	DateField df = new DateField(p.getProp(WidgetInfo.NLABEL).toString(),
				     WidgetInfo.toDateFieldMode(
					 p.getProp(WidgetInfo.NTYPE)));
	p.delProp(WidgetInfo.NLABEL);
	p.delProp(WidgetInfo.NTYPE);
	return ObjectThing.create(setInstanceProperties(interp,df,p));
    }


    public Thing cget(Interp ip,Object target,String optname) throws HeclException {
	DateField df = (DateField)target;

	if(optname.equals(WidgetInfo.NTYPE))
	    return WidgetInfo.fromDateFieldMode(df.getInputMode());
	if(optname.equals("-date"))
	    return LongThing.create(df.getDate().getTime());
	return super.cget(ip,target,optname);
    }


    public void cset(Interp ip,Object target,String optname,Thing optval)
	throws HeclException {
	DateField df = (DateField)target;

	if(optname.equals(WidgetInfo.NTYPE)) {
	    df.setInputMode(WidgetInfo.toDateFieldMode(optval));
	    return;
	}
	if(optname.equals("-date")) {
	    df.setDate(new Date(LongThing.get(optval)));
	    return;
	}
	super.cset(ip,target,optname,optval);
    }

    private static DateFieldCmd cmd = new DateFieldCmd();
    private static final String CMDNAME = "lcdui.date";
}

// Variables:
// mode:java
// coding:utf-8
// End:
