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

import java.util.Date;

import javax.microedition.lcdui.DateField;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.LongThing;
import org.hecl.Thing;

public class DateGadget extends FormGadget {
    public DateGadget(String label,int mode,FormCmd f) {
	super(new DateField(label,mode),f);
    }

    
    public DateField getDateField() {
	return (DateField)getItem();
    }
    

    public void cget(Interp ip,String optname) throws HeclException {
	DateField df = (DateField)anitem;

	if(optname.equals(WidgetInfo.NTYPE)) {
	    ip.setResult(WidgetInfo.fromDateFieldMode(df.getInputMode()));
	    return;
	}
	if(optname.equals("-date")) {
	    ip.setResult(df.getDate().getTime());
	    return;
	}
	super.cget(ip,optname);
    }


    public void cset(Interp ip,String optname,Thing optval) throws HeclException {
	DateField df = (DateField)anitem;

	if(optname.equals(WidgetInfo.NTYPE)) {
	    df.setInputMode(WidgetInfo.toDateFieldMode(optval));
	    return;
	}
	if(optname.equals("-date")) {
	    df.setDate(new Date(LongThing.get(optval)));
	    return;
	}
	super.cset(ip,optname,optval);
    }
}

