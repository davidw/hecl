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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.ListThing;
import org.hecl.Properties;
import org.hecl.Thing;

public class ImageMap extends CmdDataMap {
    public static final String NAME = "LCDUI::IMAGEMAP";

    public static ImageMap mapOf(Interp ip) {
	ImageMap map = (ImageMap)AuxDataMap.mapOf(ip,NAME);
	if(map == null)
	    map = new ImageMap(ip);
	return map;
    }
    

    public static Image asImage(Interp ip,Thing t,boolean allownull)
	throws HeclException {
	Image img = null;
	ImageMap m = mapOf(ip);
	
	if(m != null) {
	    img = m.asImage(t,true);
	    if(img == null) {
		String name = t.toString();
		if(!name.startsWith("/"))
		    name = "/"+name;
		if(!name.endsWith(".png"))
		    name += ".png";
		try {
		    img = Image.createImage(name);
		    m.put(t.toString(),img,new ImageCmd(ip,img,new Properties()));
		}
		catch(IOException e) {
		    e.printStackTrace();
		}
	    }
	}
	
	if(!allownull && img == null)
	    invalidImage(t.toString());
	return img;
    }
    
    public Image asImage(Thing t,boolean allownull) throws HeclException {
	String s = t.toString();
	if(allownull && s.length() == 0) {
	    return null;
	}
	Image image = (Image)valueOf(s);
	if(null == image && !allownull) {
	    invalidImage(s);
	}
	return image;
    }
    

    private ImageMap(Interp ip) {
	super(ip,NAME,"image");
    }
    

    private static void invalidImage(String s) throws HeclException {
	throw new HeclException("Invalid image '" + s + "'.");
    }
    

    private static int cnt = 0;
}
