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

import javax.microedition.lcdui.Gauge;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.Thing;

class GaugeGadget extends FormGadget {
    public GaugeGadget(String label,boolean isinteractive,
		       int maxValue,int initialValue,FormCmd f) {
	super(new Gauge(label,isinteractive,maxValue,initialValue),f);
    }
	

    public void cget(Interp ip,String optname) throws HeclException {
	Gauge g = (Gauge)anitem;
	
	if(optname.equals(WidgetInfo.NVALUE)) {
	    ip.setResult(WidgetInfo.fromGaugeInitial(g.getValue()));
	    return;
	}
	if(optname.equals(WidgetInfo.NMAXVALUE)) {
	    ip.setResult(WidgetInfo.fromGaugeMax(g.getMaxValue()));
	    return;
	}
	if(optname.equals(WidgetInfo.NINTERACTIVE)) {
	    ip.setResult(g.isInteractive());
	    return;
	}
	super.cget(ip,optname);
    }


    public void cset(Interp ip,String optname,Thing optval) throws HeclException {
	Gauge g = (Gauge)anitem;
	
	if(optname.equals(WidgetInfo.NVALUE)) {
	    int v = WidgetInfo.toGaugeInitial(optval);
	    try {
		g.setValue(v);
	    }
	    catch (IllegalArgumentException e) {
		throw new HeclException("Invalid gauge value '"+optval.toString()+"'.");
	    }
	    return;
	}
	if(optname.equals((WidgetInfo.NMAXVALUE))) {
	    g.setMaxValue(WidgetInfo.toGaugeMax(optval));
	    return;
	}
	super.cset(ip,optname,optval);
    }


    public void handlecmd(Interp ip,String subcmd, Thing[] argv,int startat)
	throws HeclException {
	super.handlecmd(ip,subcmd,argv,startat);
    }
}
    
