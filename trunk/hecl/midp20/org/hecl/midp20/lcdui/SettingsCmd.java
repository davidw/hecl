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

import org.awt.Color;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Thing;
import org.hecl.misc.HeclUtils;

public class SettingsCmd extends OptionCmd {
    public SettingsCmd() {
    }
    
    public void cget(Interp ip,String optname) throws HeclException {
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
	super.cset(ip,optname,optval);
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
    
}

