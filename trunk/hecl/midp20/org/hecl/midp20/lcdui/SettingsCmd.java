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

import javax.microedition.lcdui.Display;

import org.awt.Color;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.Thing;
import org.hecl.misc.HeclUtils;

public class SettingsCmd extends OptionCmd {
    public SettingsCmd(Display d) {
	super();
	display = d;
    }
    
    public void cget(Interp ip,String optname) throws HeclException {
	if(optname.equals("-color")) {
	    ip.setResult(display.isColor());
	    return;
	}
	if(optname.equals("-alphalevels")) {
	    ip.setResult(display.numAlphaLevels());
	    return;
	}
	if(optname.equals("-alertimagewidth")) {
	    ip.setResult(display.getBestImageWidth(Display.ALERT));
	    return;
	}
	if(optname.equals("-alertimageheight")) {
	    ip.setResult(display.getBestImageHeight(Display.ALERT));
	    return;
	}
	if(optname.equals("-listimagewidth")) {
	    ip.setResult(display.getBestImageWidth(Display.LIST_ELEMENT));
	    return;
	}
	if(optname.equals("-listimageheight")) {
	    ip.setResult(display.getBestImageHeight(Display.LIST_ELEMENT));
	    return;
	}
	if(optname.equals("-choiceimagewidth")) {
	    ip.setResult(display.getBestImageWidth(Display.CHOICE_GROUP_ELEMENT));
	    return;
	}
	if(optname.equals("-choiceimageheight")) {
	    ip.setResult(display.getBestImageHeight(Display.CHOICE_GROUP_ELEMENT));
	    return;
	}
	if(optname.equals("-bg")) {
	    setResult(ip,display.getColor(Display.COLOR_BACKGROUND));
	    return;
	}
	if(optname.equals("-fg")) {
	    setResult(ip,display.getColor(Display.COLOR_FOREGROUND));
	    return;
	}
	if(optname.equals("-hilightbg")) {
	    setResult(ip,display.getColor(Display.COLOR_HIGHLIGHTED_BACKGROUND));
	    return;
	}
	if(optname.equals("-hilightfg")) {
	    setResult(ip,display.getColor(Display.COLOR_HIGHLIGHTED_FOREGROUND));
	    return;
	}
	if(optname.equals("-border")) {
	    setResult(ip,display.getColor(Display.COLOR_BORDER));
	    return;
	}
	if(optname.equals("-hilightborder")) {
	    setResult(ip,display.getColor(Display.COLOR_HIGHLIGHTED_BORDER));
	    return;
	}
	if(optname.equals("-borderstyle")) {
	    ip.setResult(display.getBorderStyle(false));
	    return;
	}
	if(optname.equals("-hilightborderstyle")) {
	    ip.setResult(display.getBorderStyle(true));
	    return;
	}
	if(optname.equals(NCVFULLSCREEN)) {
	    ip.setResult(cvallowfullscreen);
	    return;
	}
	if(optname.equals(NCVDOCMDS)) {
	    ip.setResult(cvdocmds);
	    return;
	}
	if(optname.equals(NCVKEEPCMDSINFULLSCREEN)) {
	    ip.setResult(cvkeepcmdsinfullscreen);
	    return;
	}
	if(optname.equals(NCVCMDBGCOLOR)) {
	    ip.setResult(WidgetInfo.fromColor(cvcmdbgcolor));
	    return;
	}
	if(optname.equals(NCVCMDFGCOLOR)) {
	    ip.setResult(WidgetInfo.fromColor(cvcmdfgcolor));
	    return;
	}
	if(optname.equals("-skleft")) {
	    ip.setResult(HeclCanvas.KEYCODE_LEFT_SK);
	    return;
	}
	if(optname.equals("-skright")) {
	    ip.setResult(HeclCanvas.KEYCODE_RIGHT_SK);
	    return;
	}
	super.cget(ip,optname);
    }
    

    public void cset(Interp ip,String optname,Thing optval) throws HeclException {
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
	super.cset(ip,optname,optval);
    }


    protected void handlecmd(Interp ip,String subcmd, Thing[] argv,int startat)
	throws HeclException {

	if(subcmd.equals("flash")) {
	    if(argv.length == startat+1) {
		display.flashBacklight(IntThing.get(argv[startat]));
		return;
	    }
	    throw HeclException.createWrongNumArgsException(argv, startat, "duration");
	}
	if(subcmd.equals("vibrate")) {
	    if(argv.length == startat+1) {
		display.vibrate(IntThing.get(argv[startat]));
		return;
	    }
	    throw HeclException.createWrongNumArgsException(argv, startat, "duration");
	}
	super.handlecmd(ip,subcmd,argv,startat);
    }


    private static void setResult(Interp ip,int c) throws HeclException {
	ip.setResult(WidgetInfo.fromColor(c));
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

    private Display display;
}

