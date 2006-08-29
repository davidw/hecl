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

import javax.microedition.lcdui.StringItem;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Thing;

public class StringGadget extends FormGadget {
    public StringGadget(String label,int appearanceMode,FormCmd f) {
	super(new StringItem(label,"",appearanceMode),f);
    }


    public StringItem getStringItem() {
	return (StringItem)getItem();
    }
    

    public void cget(Interp ip,String optname) throws HeclException {
	StringItem si = getStringItem();
	
	if(optname.equals(WidgetInfo.NAPPEARANCE)) {
	    ip.setResult(WidgetInfo.fromItemAppearance(si.getAppearanceMode()));
	    return;
	}
	if(optname.equals(WidgetInfo.NTEXT)) {
	    ip.setResult(si.getText());
	    return;
	}
	if(optname.equals(WidgetInfo.NFONT)) {
	    FontMap.setResult(ip,si.getFont());
	    return;
	}
	super.cget(ip,optname);
    }


    public void cset(Interp ip,String optname,Thing optval) throws HeclException {
	StringItem si = getStringItem();

	if(optname.equals(WidgetInfo.NTEXT)) {
	    si.setText(optval.toString());
	    return;
	}
	if(optname.equals(WidgetInfo.NFONT)) {
	    si.setFont(FontMap.get(optval));
	    return;
	}
	super.cset(ip,optname,optval);
    }
}

