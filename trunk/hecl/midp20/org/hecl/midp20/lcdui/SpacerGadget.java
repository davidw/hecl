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

import javax.microedition.lcdui.Spacer;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.Thing;

public class SpacerGadget extends FormGadget {
    public SpacerGadget(int minWidth,int minHeight,FormCmd f) {
	super(new Spacer(minWidth,minHeight),f);
    }


    Spacer getSpacer() {
	return (Spacer)getItem();
    }
    

    public void cget(Interp ip,String optname) throws HeclException {
	if(optname.equals(WidgetInfo.NLABEL)) {
	    // no label
	    ip.setResult(Thing.EMPTYTHING);
	    return;
	}
	super.cget(ip,optname);
    }

    public void cset(Interp ip,String optname,Thing optval) throws HeclException {
	Spacer spacer = (Spacer)theitem;

	if(optname.equals(WidgetInfo.NLABEL)) {
	    // Ignore
	    return;
	}
	if(optname.equals(WidgetInfo.NMINWIDTH)) {
	    int newval = IntThing.get(optval);
	    if(newval < 0)
		newval = 0;
	    spacer.setMinimumSize(newval,spacer.getMinimumHeight());
	    return;
	}
	if(optname.equals(WidgetInfo.NMINHEIGHT)) {
	    int newval = IntThing.get(optval);
	    if(newval < 0)
		newval = 0;
	    spacer.setMinimumSize(spacer.getMinimumWidth(),newval);
	    return;
	}
	super.cset(ip,optname,optval);
    }


    public void handlecmd(Interp ip,String subcmd, Thing[] argv,int startat)
	throws HeclException {
	if(subcmd.equals(WidgetInfo.NADDCOMMAND)) {
	    // Ignore
	    //throw new HeclException("Spacer does not support commands.");
	}
	if(subcmd.equals(WidgetInfo.NREMOVECOMMAND)) {
	    // Ignore
	    //throw new HeclException("Spacer does not support commands.");
	}
	super.handlecmd(ip,subcmd,argv,startat);
    }

}

