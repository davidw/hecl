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
	String name = "lcdui.select_command";
	ip.setVar("lcdui.select_command",
		  new Thing(
		      WidgetMap.addWidget(ip,name,List.SELECT_COMMAND,
					  new CommandCmd(ip,List.SELECT_COMMAND,null)))
	    );
	name = "lcdui.dismiss_command";
	ip.setVar(name,
		  new Thing(
		      WidgetMap.addWidget(ip,name,Alert.DISMISS_COMMAND,
					  new CommandCmd(ip,Alert.DISMISS_COMMAND,null)))
	    );
	
	// Add font commands...
	ip.addCommand("lcdui.font",FontMap.FONTCMD);

	// Image map and command
	ip.addCommand("lcdui.image",ImageCmd.CREATE);

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
	System.err.println("Warning: module phone does currently not support unload!");
	ip.removeCommand("lcdui.font");
	ip.unSetVar("lcdui.select_command");
	ip.unSetVar("lcdui.dismiss_command");
    }
}
