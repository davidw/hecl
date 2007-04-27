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

import javax.microedition.lcdui.StringItem;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.StringThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.StringThing;
import org.hecl.Thing;

public class StringItemCmd extends ItemCmd {
    public static void load(Interp ip) {
	ip.addCommand(CMDNAME,cmd);
	ip.addClassCmd(StringItem.class,cmd);
    }
    public static void unload(Interp ip) {
	ip.removeCommand(CMDNAME);
	ip.removeClassCmd(StringItem.class);
    }
    
    /*
    public StringItemCmd(String label,int appearanceMode,FormCmd f) {
	super(new StringItem(label,"",appearanceMode),f);
    }
    */
    protected StringItemCmd() {}

    public Thing cmdCode(Interp interp,Thing[] argv) throws HeclException {
	Properties p = WidgetInfo.defaultProps(StringItem.class);
	p.setProps(argv,1);
	StringItem si = new StringItem(p.getProp(WidgetInfo.NLABEL).toString(),
				       p.getProp(WidgetInfo.NTEXT).toString(),
				       WidgetInfo.toItemAppearance(
					   p.getProp(WidgetInfo.NAPPEARANCE)));
	p.delProp(WidgetInfo.NLABEL);
	p.delProp(WidgetInfo.NTEXT);
	p.delProp(WidgetInfo.NAPPEARANCE);
	return ObjectThing.create(setInstanceProperties(interp,si,p));
    }
    

    public Thing cget(Interp ip,Object target,String optname)
	throws HeclException {
	StringItem si = (StringItem)target;
	
	if(optname.equals(WidgetInfo.NAPPEARANCE))
	    return WidgetInfo.fromItemAppearance(si.getAppearanceMode());
	if(optname.equals(WidgetInfo.NTEXT))
	    return StringThing.create(si.getText());
	if(optname.equals(WidgetInfo.NFONT))
	    return FontMap.fontThing(si.getFont());
	return super.cget(ip,target,optname);
    }


    public void cset(Interp ip,Object target,String optname,Thing optval)
	throws HeclException {
	StringItem si = (StringItem)target;

	if(optname.equals(WidgetInfo.NTEXT)) {
	    si.setText(optval.toString());
	    return;
	}
	if(optname.equals(WidgetInfo.NFONT)) {
	    si.setFont(FontMap.get(optval));
	    return;
	}
	super.cset(ip,target,optname,optval);
    }

    private static StringItemCmd cmd = new StringItemCmd();
    private static final String CMDNAME = "lcdui.stringitem";
}

// Variables:
// mode:java
// coding:utf-8
// End:

