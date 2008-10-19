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
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

class GaugeCmd extends ItemCmd {
    public static void load(Interp ip) {
	ip.addCommand(CMDNAME,gaugecmd);
	ip.addClassCmd(Gauge.class,gaugecmd);
    }
    public static void unload(Interp ip) {
	ip.removeCommand(CMDNAME);
	ip.removeClassCmd(Gauge.class);
    }

    protected GaugeCmd() {}

    public Thing cmdCode(Interp interp,Thing[] argv) throws HeclException {
	Properties p = WidgetInfo.defaultProps(Gauge.class);
	p.setProps(argv,1);
//#ifdef notdef
	System.err.println("GAUGE");
	System.err.println("gauge: "
			   +p.getProp(WidgetInfo.NLABEL).toString()+", "
			   +p.getProp(WidgetInfo.NINTERACTIVE).toString()+", "
			   +p.getProp(WidgetInfo.NVALUE).toString()+", "
			   +p.getProp(WidgetInfo.NMAXVALUE).toString());
	System.err.println("GAUGE2");
//#endif
	Gauge g = new Gauge(p.getProp(WidgetInfo.NLABEL).toString(),
			    HeclUtils.thing2bool(
				p.getProp(WidgetInfo.NINTERACTIVE)),
			    WidgetInfo.toGaugeMax(
				p.getProp(WidgetInfo.NMAXVALUE)),
			    WidgetInfo.toGaugeInitial(
				p.getProp(WidgetInfo.NVALUE)));
	p.delProp(WidgetInfo.NINTERACTIVE);
	p.delProp(WidgetInfo.NLABEL);
	p.delProp(WidgetInfo.NVALUE);
	p.delProp(WidgetInfo.NMAXVALUE);
	return ObjectThing.create(setInstanceProperties(interp,g,p));
    }

    public Thing cget(Interp ip,Object target,String optname)
	throws HeclException {
	Gauge g = (Gauge)target;

	if(optname.equals(WidgetInfo.NVALUE))
	    return WidgetInfo.fromGaugeInitial(g.getValue());
	if(optname.equals(WidgetInfo.NMAXVALUE))
	    return WidgetInfo.fromGaugeMax(g.getMaxValue());
	if(optname.equals(WidgetInfo.NINTERACTIVE))
	    return IntThing.create(g.isInteractive());
	return super.cget(ip,target,optname);
    }


    public void cset(Interp ip,Object target,String optname,Thing optval)
	throws HeclException {
	Gauge g = (Gauge)target;

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
	super.cset(ip,target,optname,optval);
    }


    private static GaugeCmd gaugecmd = new GaugeCmd();
    private static final String CMDNAME = "lcdui.gauge";
}

// Variables:
// mode:java
// coding:utf-8
// End:
