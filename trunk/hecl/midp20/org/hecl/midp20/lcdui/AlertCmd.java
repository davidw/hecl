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

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Gauge;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Properties;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

public class AlertCmd extends ScreenCmd {
    public static final org.hecl.Command CREATE = new org.hecl.Command() {
	    public void cmdCode(Interp interp,Thing[] argv) throws HeclException {
		Properties p = WidgetInfo.defaultProps(Alert.class);
		p.setProps(argv,1);
		Alert w = new Alert(p.getProp(WidgetInfo.NTITLE).toString(),
				    p.getProp(WidgetInfo.NTEXT).toString(),
				    null,
				    WidgetInfo.toAlertType(p.getProp(WidgetInfo.NTYPE)));
		p.delProp(WidgetInfo.NTITLE);
		p.delProp(WidgetInfo.NTEXT);
		p.delProp(WidgetInfo.NTYPE);
		WidgetMap.addWidget(interp,null,w, new AlertCmd(interp,w,p));
	    }
	};
    
    protected AlertCmd(Interp ip,Alert a,Properties p) throws HeclException {
	super(ip,a,p);
    }
    
    public void cget(Interp ip,String optname) throws HeclException {
	Alert a = (Alert)getData();
	
	if(optname.equals(WidgetInfo.NTYPE)) {
	    ip.setResult(WidgetInfo.fromAlertType(a.getType()));
	    return;
	}
	if(optname.equals(WidgetInfo.NTEXT)) {
	    ip.setResult(a.getString());
	    return;
	}
	if(optname.equals("-timeout")) {
	    ip.setResult(indicator != null);
	    return;
	}
	super.cget(ip,optname);
    }

    public void cset(Interp ip,String optname,Thing optval) throws HeclException {
	Alert a = (Alert)getData();

	if(optname.equals(WidgetInfo.NTEXT)) {
	    a.setString(optval.toString());
	    return;
	}
	if(optname.equals(WidgetInfo.NTYPE)) {
	    a.setType(WidgetInfo.toAlertType(optval));
	    return;
	}
	if(optname.equals("-timeout")) {
	    int timeout = Alert.FOREVER-1;
		
	    if(optval.toString().equals("forever")) {
		timeout = Alert.FOREVER;
	    } else {
		timeout = HeclUtils.thing2int(optval,true,timeout);
	    }
	    if(timeout <= Alert.FOREVER-1) {
		throw new HeclException("Invalid timeout '"+optval.toString()+"'!");
	    }
	    a.setTimeout(timeout);
	    return;
	}
	if(optname.equals("-indicator")) {
	    if(HeclUtils.thing2bool(optval)) {
		if(indicator == null)
		    indicator = new Gauge(null, false, Gauge.INDEFINITE,
					  Gauge.CONTINUOUS_RUNNING);
	    } else {
		indicator = null;
	    }
	    a.setIndicator(indicator);
	    return;
	}
	super.cset(ip,optname,optval);
    }

    Gauge indicator = null;
}
