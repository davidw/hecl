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

import javax.microedition.lcdui.Display;

import org.awt.Color;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.Thing;
import org.hecl.misc.HeclUtils;

public class SettingsCmd extends OptionCmd {
    public static void load(Interp ip,Display d) {
	display = d;
	ip.addCommand(CMDNAME,cmd);
    }
    public static void unload(Interp ip) {
	ip.removeCommand(CMDNAME);
    }

    protected SettingsCmd() {}
    
    public Thing cmdCode(Interp interp,Thing[] argv) throws HeclException {
	return method(interp,null,argv);
    }
    
    public Thing cget(Interp ip,Object target,String optname) throws HeclException {
	if(optname.equals("-color"))
	    return IntThing.create(display.isColor());
	if(optname.equals("-alphalevels"))
	    return IntThing.create(display.numAlphaLevels());
	if(optname.equals("-alertimagewidth"))
	    return IntThing.create(display.getBestImageWidth(Display.ALERT));
	if(optname.equals("-alertimageheight"))
	    return IntThing.create(display.getBestImageHeight(Display.ALERT));
	if(optname.equals("-listimagewidth"))
	    return IntThing.create(display.getBestImageWidth(Display.LIST_ELEMENT));
	if(optname.equals("-listimageheight"))
	    return IntThing.create(display.getBestImageHeight(Display.LIST_ELEMENT));
	if(optname.equals("-choiceimagewidth"))
	    return IntThing.create(display.getBestImageWidth(Display.CHOICE_GROUP_ELEMENT));
	if(optname.equals("-choiceimageheight"))
	    return IntThing.create(display.getBestImageHeight(Display.CHOICE_GROUP_ELEMENT));
	if(optname.equals("-bg"))
	    return WidgetInfo.fromColor(display.getColor(Display.COLOR_BACKGROUND));
	if(optname.equals("-fg"))
	    return WidgetInfo.fromColor(display.getColor(Display.COLOR_FOREGROUND));
	if(optname.equals("-hilightbg"))
	    return WidgetInfo.fromColor(display.getColor(Display.COLOR_HIGHLIGHTED_BACKGROUND));
	if(optname.equals("-hilightfg"))
	    return WidgetInfo.fromColor(display.getColor(Display.COLOR_HIGHLIGHTED_FOREGROUND));
	if(optname.equals("-border"))
	    return WidgetInfo.fromColor(display.getColor(Display.COLOR_BORDER));
	if(optname.equals("-hilightborder"))
	    return WidgetInfo.fromColor(display.getColor(Display.COLOR_HIGHLIGHTED_BORDER));
	if(optname.equals("-borderstyle"))
	    return IntThing.create(display.getBorderStyle(false));
	if(optname.equals("-hilightborderstyle"))
	    return IntThing.create(display.getBorderStyle(true));
	if(optname.equals(NCVFULLSCREEN))
	    return IntThing.create(cvallowfullscreen);
	if(optname.equals(NCVDOCMDS))
	    return IntThing.create(cvdocmds);
	if(optname.equals(NCVKEEPCMDSINFULLSCREEN))
	    return IntThing.create(cvkeepcmdsinfullscreen);
	if(optname.equals(NCVCMDBGCOLOR))
	    return WidgetInfo.fromColor(cvcmdbgcolor);
	if(optname.equals(NCVCMDFGCOLOR))
	    return WidgetInfo.fromColor(cvcmdfgcolor);
	if(optname.equals("-skleft"))
	    return IntThing.create(HeclCanvas.KEYCODE_LEFT_SK);
	if(optname.equals("-skright"))
	    return IntThing.create(HeclCanvas.KEYCODE_RIGHT_SK);
	return super.cget(ip,null,optname);
    }
    

    public void cset(Interp ip,Object target,String optname,Thing optval)
	throws HeclException {
	if(optname.equals(NCVFULLSCREEN)) {
	    cvallowfullscreen = HeclUtils.thing2bool(optval);
	    return;
	}
	if(optname.equals(NCVDOCMDS)) {
	    cvdocmds = HeclUtils.thing2bool(optval);
	    return;
	}
	if(optname.equals(NCVKEEPCMDSINFULLSCREEN)) {
	    cvkeepcmdsinfullscreen = HeclUtils.thing2bool(optval);
	    return;
	}
	if(optname.equals(NCVCMDBGCOLOR)) {
	    cvcmdbgcolor = new Color(WidgetInfo.toColor(optval));
	    return;
	}
	if(optname.equals(NCVCMDFGCOLOR)) {
	    cvcmdfgcolor = new Color(WidgetInfo.toColor(optval));
	    return;
	}
	if(optname.equals("-skleft")) {
	    HeclCanvas.KEYCODE_LEFT_SK = IntThing.get(optval);
	    return;
	}
	if(optname.equals("-skright")) {
	    HeclCanvas.KEYCODE_RIGHT_SK = IntThing.get(optval);
	    return;
	}
	super.cset(ip,target,optname,optval);
    }


    protected Thing handlecmd(Interp ip,Object target,String subcmd,
			      Thing[] argv,int startat)
	throws HeclException {

	if(subcmd.equals("flash")) {
	    if(argv.length == startat+1) {
		display.flashBacklight(IntThing.get(argv[startat]));
		return null;
	    }
	    throw HeclException.createWrongNumArgsException(argv, startat, "duration");
	}
	if(subcmd.equals("vibrate")) {
	    if(argv.length == startat+1) {
		display.vibrate(IntThing.get(argv[startat]));
		return null;
	    }
	    throw HeclException.createWrongNumArgsException(argv, startat, "duration");
	}
	return super.handlecmd(ip,target,subcmd,argv,startat);
    }


    public static boolean cvallowfullscreen = true;
    public static boolean cvdocmds = true;
    public static boolean cvkeepcmdsinfullscreen = false;
    public static Color cvcmdbgcolor = Color.white;
    public static Color cvcmdfgcolor = Color.black;
    
    
    private static final String NCVFULLSCREEN = "-cvfullscreen";
    private static final String NCVDOCMDS = "-cvdocmds";
    private static final String NCVKEEPCMDSINFULLSCREEN = "-cvkeepcmdsinfullscreen";
    private static final String NCVCMDBGCOLOR = "-cvcmdbg";
    private static final String NCVCMDFGCOLOR = "-cvcmdfg";

    private static Display display;

    private static SettingsCmd cmd = new SettingsCmd();
    private static final String CMDNAME = "lcdui.settings";
}

// Variables:
// mode:java
// coding:utf-8
// End:
