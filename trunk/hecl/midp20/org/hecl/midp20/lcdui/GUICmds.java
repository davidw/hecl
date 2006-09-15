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

import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Thing;
import org.hecl.HeclModule;

public class GUICmds implements HeclModule {
    public void loadModule(Interp ip) throws HeclException {
	System.err.println("Loading new phone");

	// Add some default widgets
	ip.setVar(SELVARNAME,
		  new Thing(
		      WidgetMap.addWidget(ip,SELVARNAME,List.SELECT_COMMAND,
					  new CommandCmd(ip,List.SELECT_COMMAND,null))),
		  0);
	ip.setVar(DISMISSVARNAME,
		  new Thing(
		      WidgetMap.addWidget(ip,DISMISSVARNAME,Alert.DISMISS_COMMAND,
					  new CommandCmd(ip,Alert.DISMISS_COMMAND,null))),
		  0);
	
	// Add font commands...
	ip.addCommand("lcdui.font",FontMap.FONTCMD);

	// Image map and command
	ip.addCommand("lcdui.image",ImageCmd.CREATE);

	// configuration commands
	ip.addCommand("lcdui.settings",new SettingsCmd());
	
	// Add widget commands...
	ip.addCommand("lcdui.alert",AlertCmd.CREATE);
	ip.addCommand("lcdui.canvas",CanvasCmd.CREATE);
	ip.addCommand("lcdui.command",CommandCmd.CREATE);
	ip.addCommand("lcdui.form",FormCmd.CREATE);
	ip.addCommand("lcdui.list",ListCmd.CREATE);
	ip.addCommand("lcdui.textbox",TextBoxCmd.CREATE);
	ip.addCommand("lcdui.ticker",TickerCmd.CREATE);
	ip.addCommand("lcdui.widgetmap",WidgetMap.CMD);
    }
    

    public void unloadModule(Interp ip) throws HeclException {
	for(int i=0; i<varnames.length; ++i) {
	    try {
		ip.unSetVar(varnames[i],0);
	    }
	    catch(HeclException he){
	    }
	}
	ip.removeCommand("lcdui.font");
	ip.removeCommand("lcdui.image");

	ip.removeCommand("lcdui.settings");
	
	ip.removeCommand("lcdui.alert");
	ip.removeCommand("lcdui.canvas");
	ip.removeCommand("lcdui.command");
	ip.removeCommand("lcdui.form");
	ip.removeCommand("lcdui.list");
	ip.removeCommand("lcdui.textbox");
	ip.removeCommand("lcdui.ticker");
	ip.removeCommand("lcdui.widgetmap");

    }
    public static final String SELVARNAME = "lcdui.select_command";
    public static final String DISMISSVARNAME = "lcdui.dismiss_command";
    private static final String varnames[] = new String[]{SELVARNAME,DISMISSVARNAME};
}
