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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.StringThing;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

import org.hecl.rms.RMSInputStream;

public class ImageCmd extends OptionCmd {
    public static void load(Interp ip) {
	ip.addCommand(CMDNAME,cmd);
	ip.addClassCmd(Image.class,cmd);
    }
    public static void unload(Interp ip) {
	ip.removeCommand(CMDNAME);
	ip.removeClassCmd(Image.class);
    }

    public Thing cmdCode(Interp ip,Thing[] argv)
	throws HeclException {
	
	Image image = null;
	Properties p = new Properties();
	p.setProps(argv,1);
	Thing t = null;
	
	if((t = p.getProp("-file")) != null) {
	    p.delProp("-file");
	    try {
		image = Image.createImage(t.toString());
	    }
	    catch (IOException e) {
		throw new HeclException("Cannot convert resource '"
					+ t.toString()
					+ "' to image.");
	    }
	} else if((t = p.getProp("-rms")) != null) {
	    p.delProp("-rms");
	    RMSInputStream is = null;
	    try {
		is = new RMSInputStream(t.toString());
		System.err.println("creating image from rms...");
		image = Image.createImage(is);
		System.err.println("done");
		is.close();
		is = null;
	    }
	    catch(IOException e) {
		e.printStackTrace();
		if(is != null) {
		    try {
			is.close();
		    }
		    catch(Exception iox){
			iox.printStackTrace();
			}
		    is = null;
		}
		throw new HeclException(e.toString());
	    }
	} else if((t = p.getProp("-resource")) != null) {
	    p.delProp("-resource");
	    image = GUICmds.loadImage(t.toString());
	} else if((t = p.getProp("-data")) != null) {
	    p.delProp("-data");
	    String s = t.toString();
//#ifdef notdef
	    boolean success = false;
	    for(int i=0; i<ISONAMES.length; ++i) {
		try {
		    byte[] b = s.getBytes(ISONAMES[i]);
		    image = Image.createImage(b,0,b.length);
		    success = true;
		    break;
		}
		catch (Exception e) {}
	    }
	    if(!success) {
		throw new HeclException("Can't decode image data.");
	    }
//#else
	    byte[] b = asISOBytes(s);
	    image = Image.createImage(b,0,b.length);
//#endif
	} else if((t = p.getProp("-image")) != null) {
	    p.delProp("-image");
	    image = Image.createImage(GUICmds.asImage(t,false));
	} else {
	    t = p.getProp(WidgetInfo.NWIDTH);
	    Thing t2 = p.getProp(WidgetInfo.NHEIGHT);
		
	    p.delProp(WidgetInfo.NWIDTH);
	    p.delProp(WidgetInfo.NHEIGHT);
	    
	    image = Image.createImage(
		t != null ? HeclUtils.thing2int(t,true,0) : 0,
		t2 != null ? HeclUtils.thing2int(t2,true,0) : 0
		);
	}
	if(image == null) {
	    throw HeclException.createWrongNumArgsException(
		argv, 1,
		"?option value ...?");
	}
	return ObjectThing.create(image);
    }


    protected ImageCmd() {}
    

    public Thing cget(Interp ip,Object target,String optname) throws HeclException {
	Image image = (Image)target;
	
	if(optname.equals(WidgetInfo.NHEIGHT))
	    return IntThing.create(image.getHeight());
	if(optname.equals("-mutable"))
	    return IntThing.create(image.isMutable());
	if(optname.equals(WidgetInfo.NWIDTH))
	    return IntThing.create(image.getWidth());
	if(optname.equals("-data")) {
	    int w = image.getWidth();
	    int h = image.getHeight();
	    int n = w*h;
	    int buf[] = new int[n];
	    
	    image.getRGB(buf,0,w,0,0,w,h);
	    Vector v = new Vector();
	    for(int i = 0; i<n; ++i) {
		v.addElement(new Thing(Integer.toHexString(buf[i])));
	    }
	    return ListThing.create(v);
	}
	return super.cget(ip,target,optname);
    }
    

    public void cset(Interp ip,Object target,String optname,Thing optval)
	throws HeclException {
	super.cset(ip,target,optname,optval);
    }
    

    public Thing handlecmd(Interp ip,Object target,String subcmd,Thing[] argv,int startat)
	throws HeclException {
	Image image = (Image)target;

	if(subcmd.equals("thumbnail")) {
	    int destW = startat < argv.length ?
		IntThing.get(argv[startat++]) : 64;
	    int destH = startat < argv.length ?
		IntThing.get(argv[startat++]) : -1;
	    
	    int sourceWidth = image.getWidth();
	    int sourceHeight = image.getHeight();
	    if (destH == -1)
		destH = destW * sourceHeight / sourceWidth;
	    
	    Image thumb = Image.createImage(destW, destH);
	    Graphics g = thumb.getGraphics();
	    for (int y = 0; y < destH; y++) {
		for (int x = 0; x < destW; x++) {
		    g.setClip(x, y, 1, 1);
		    int dx = x * sourceWidth / destW;
		    int dy = y * sourceHeight / destH;
		    g.drawImage(image, x - dx, y - dy,
				Graphics.LEFT | Graphics.TOP);
		}
	    }
	    return ObjectThing.create(Image.createImage(thumb));
	}
	if(subcmd.equals("graphics")) {
	    if(!image.isMutable())
		throw new HeclException("immutable image");
	    return GraphicsCmd.createDrawable(image.getGraphics(),
					      image.getWidth(),
					      image.getHeight());
	}
	return super.handlecmd(ip,target,subcmd,argv,startat);
    }


    public static byte[] asISOBytes(String s) {
	byte[] buf = new byte[s.length()];
	for(int i=0; i<s.length(); ++i) {
	    char ch = s.charAt(i);
	    buf[i] = (byte)ch;
	}
	return buf;
    }
    

//#ifdef notdef
    static private final String ISONAMES[]= {
	"ISO-8859-1","ISO8859-1","ISO8859_1","ISO_8859_1","ISO-8859_1","ISO_8859-1",
	"iso-8859-1","iso8859-1","iso8859_1","iso_8859_1","iso-8859_1","iso_8859-1"
    };
//#endif    

    private static ImageCmd cmd = new ImageCmd();
    private static final String CMDNAME = "lcdui.image";
}

// Variables:
// mode:java
// coding:utf-8
// End:
