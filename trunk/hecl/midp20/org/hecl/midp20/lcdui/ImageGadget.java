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

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

public class ImageGadget extends FormGadget {
    public ImageGadget(String label,String alttext,int appearance,FormCmd f) {
	super(new ImageItem(label,null,Item.LAYOUT_DEFAULT,alttext,appearance),f);
    }
    
    
    public void cget(Interp ip,String optname) throws HeclException {
	ImageItem item = (ImageItem)theitem;
	
	if(optname.equals(WidgetInfo.NTEXT)) {
	    ip.setResult(item.getAltText());
	    return;
	}
	if(optname.equals(WidgetInfo.NIMAGE)) {
	    ip.setResult(ImageMap.mapOf(ip).nameOf(item.getImage()));
	    return;
	}
	if(optname.equals(WidgetInfo.NAPPEARANCE)) {
	    ip.setResult(WidgetInfo.fromItemAppearance(item.getAppearanceMode()));
	    return;
	}
	super.cget(ip,optname);
    }
    
    public void cset(Interp ip,String optname,Thing optval) throws HeclException {
	ImageItem item = (ImageItem)theitem;

	if(optname.equals(WidgetInfo.NTEXT)) {
	    item.setAltText(optval.toString());
	    return;
	}
	if(optname.equals(WidgetInfo.NIMAGE)) {
	    item.setImage(ImageMap.asImage(ip,optval,false));
	    return;
	}
	super.cset(ip,optname,optval);
    }
}
