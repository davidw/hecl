/*
 * Copyright 2005-2007
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

import java.lang.StringBuffer;
import java.util.Vector;
import java.util.Stack;

//#ifdef j2se
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Font;
import java.awt.geom.Point2D;
//import java.awt.Rectangle;
//#else

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.awt.Color;
import org.awt.Dimension;
import org.awt.geom.Point2D;
import org.awt.Rectangle;
//#endif

import org.graphics.Draw;
import org.graphics.Drawable;

import org.hecl.DoubleThing;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

public class GraphicsCmd extends OptionCmd {
    public static void load(Interp ip) {
	ip.addClassCmd(Drawable.class,cmd);
    }
    public static void unload(Interp ip) {
	ip.removeClassCmd(Drawable.class);
    }
    
    public Thing cmdCode(Interp interp,Thing[] argv) throws HeclException {
	throw new HeclException("Invalid direct call of GraphicsCmd.cmdCode");
    };

    public static Thing createDrawable(Graphics g,int width,int height) {
	return ObjectThing.create(new Drawable(g,width,height));
    }
    
    protected GraphicsCmd() {}

    public Thing cget(Interp ip,Object target,String optname) throws HeclException {
	Drawable d = (Drawable)target;
	
	if(optname.equals(WidgetInfo.NCLIPHEIGHT))
	    return IntThing.create(d.getClipBounds().height);
	if(optname.equals(WidgetInfo.NCLIPWIDTH))
	    return IntThing.create(d.getClipBounds().width);
	if(optname.equals(WidgetInfo.NCLIPX))
	    return IntThing.create(d.getClipBounds().x);
	if(optname.equals(WidgetInfo.NCLIPY))
	    return IntThing.create(d.getClipBounds().y);
	if(optname.equals(WidgetInfo.NCOLOR))
	    return WidgetInfo.fromColor(d.getColor());
	if(optname.equals("-background"))
	    return WidgetInfo.fromColor(d.getColor());
	if(optname.equals(WidgetInfo.NFONT))
	    return FontMap.fontThing(d.getFont());
	if(optname.equals("-grayscale"))
	    return IntThing.create(d.getGrayScale());
//#ifdef notdef
	if(optname.equals("-blue"))
	    return IntThing.create(d.getBlueComponent());
	if(optname.equals("-green"))
	    return IntThing.create(d.getGreenComponent());
	if(optname.equals("-red"))
	    return IntThing.create(d.getRedComponent());
//#endif
	if(optname.equals(WidgetInfo.NLINETYPE))
	    return IntThing.create((int)d.getLineType());
//#ifdef notdef
	if(optname.equals("-translatex"))
	    return DoubleThing.create(d.getTranslateX());
	if(optname.equals("-translatey"))
	    return DoubleThing.create(d.getTranslateY());
//#endif
	return super.cget(ip,target,optname);
    }



    public void cset(Interp ip,Object target,String optname,Thing optval)
	throws HeclException {
	Drawable d = (Drawable)target;
	
	if(optname.equals(WidgetInfo.NCOLOR)) {
	    d.setColor(new Color(WidgetInfo.toColor(optval)));
	    return;
	}
	if(optname.equals("-background")) {
	    d.setBackground(new Color(WidgetInfo.toColor(optval)));
	    return;
	}
	if(optname.equals("-grayscale")) {
	    d.setGrayScale(IntThing.get(optval));
	    return;
	}
	if(optname.equals(WidgetInfo.NFONT)) {
	    d.setFont(FontMap.get(optval));
	    return;
	}
	if(optname.equals(WidgetInfo.NLINETYPE)) {
	    d.setLineStipple((short)HeclUtils.thing2int(optval,false,0));
	    return;
	}
//#ifdef notdef
	if(optname.equals("-translatex")) {
	    d.translate(IntThing.get(optval),d.getTranslateY());
	    return;
	}
	if(optname.equals("-translatey")) {
	    d.translate(d.getTranslateX(),IntThing.get(optval));
	    return;
	}
//#endif
	super.cset(ip,target,optname,optval);
    }


    public synchronized Thing handlecmd(Interp ip,Object target,
					String subcmd, Thing[] argv,int startat)
	throws HeclException {
	return sequence(ip,new Stack(),(Drawable)target,subcmd,argv,startat);
    }
    

    private synchronized Thing sequence(Interp ip,Stack st,Drawable d,
					String subcmd, Thing[] argv,int startat)
	throws HeclException {
	int n = startat;

	// Stack manipulation
	if(subcmd.equals("push")) {
	    st.push(new Context(d));
	    return null;
	}
	if(subcmd.equals("pop")) {
	    if(st.empty()) {
		throw new HeclException("Empty context stack.");
	    }
	    ((Context)st.pop()).restore(d);
	    return null;
	}
	if(subcmd.equals("draw")) {
	    StringBuffer errs = null;
	    for(; startat+1 <= argv.length; ++startat) {
		Vector v = ListThing.get(argv[startat]);
		int cmdlen = v.size();
		for(int pos = 0; pos < cmdlen; ++pos) {
		    Vector cmd = ListThing.get((Thing)v.elementAt(pos));
		    Thing[] xargs = new Thing[cmd.size()];
		    cmd.copyInto(xargs);
		    try {
			sequence(ip,st,d,xargs[0].toString().toLowerCase(),xargs,1);
		    }
		    catch (HeclException e) {
			// OOPS, error
			// We create a comfortable error message with index
			// into command sequence and error message for the
			// errorneous command.
			if(errs == null) {
			    errs = new StringBuffer("Error in "+subcmd+":");
			}
			errs.append(' ').append(startat).append(',')
			    .append(pos).append('-').append(e.toString());
		    }
		}
	    }
	    if(errs != null) {
		throw new HeclException(errs.toString());
	    }
	    return null;
	}
	
	// Draw commands
	if(subcmd.equals("arc")) {
	    /* point dimension arcw, arch */
	    if(startat+4 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "point dimension arcw arch");
	    d.drawArc(HeclUtils.thing2Point(argv,startat),
		      HeclUtils.thing2Dimension(argv,startat+1),
		      IntThing.get(argv[startat+2]),
		      IntThing.get(argv[startat+3]),
		      false);
	    return null;
	}
	if(subcmd.equals("clear")) {
	    d.clear();
	    return null;
	}
	if(subcmd.equals("copyarea")) {
	    /* vsrc(x,y), dim(w,h) vdst(x,y), anchor */
	    if(startat+3 > argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "psrc dimension pdst [anchor]");
	    
	    int anchor = startat+3 < argv.length ?
		WidgetInfo.toCanvasAnchor(argv[n]) : Graphics.BOTTOM|Graphics.LEFT;
	    //System.err.println("anchor="+Integer.toHexString(anchor));
	    d.copyArea(HeclUtils.thing2Point(argv,startat),
		       HeclUtils.thing2Dimension(argv,startat+1),
		       HeclUtils.thing2Point(argv,startat+2), anchor);
	    return null;
	}
	if(subcmd.equals("image")) {
	    // image, v(x,y), anchor
	    if(startat+2 > argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "an image point [anchor]");
	    
	    int anchor = startat+2 < argv.length ?
		WidgetInfo.toCanvasAnchor(argv[startat+2]) : Graphics.BOTTOM|Graphics.LEFT;
	    //System.err.println("anchor="+Integer.toHexString(anchor));
	    d.drawImage(GUICmds.asImage(argv[startat],false),
			HeclUtils.thing2Point(argv,startat+1), anchor);
	    return null;
	}
	if(subcmd.equals("line")) {
	    // v0(x,y) v1(x,y)
	    if(startat+2 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "frompt topt");
	    Point2D v0 = new Point2D.Double();
	    Point2D v1 = new Point2D.Double();
	    HeclUtils.getPoint(v0,argv,startat);
	    HeclUtils.getPoint(v1,argv,startat+1);
	    d.drawLine(v0,v1);
	    return null;
	}
	if(subcmd.equals("xline")) {
	    // v0(x,y) v1(x,y) [...]
	    if(startat+2 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "frompt topt");
	    Point2D v0 = new Point2D.Double();
	    Point2D v1 = new Point2D.Double();
	    HeclUtils.getPoint(v0,argv,startat);
	    HeclUtils.getPoint(v1,argv,startat+1);
	    d.xline(v0,v1);
	    return null;
	}

	if(subcmd.equals("lines")) {
	    // v0(x,y) v1(x,y) [...]
	    if(startat+1 > argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "point0 point1 [...]");
	    Point2D v0 = new Point2D.Double();
	    Point2D v1 = new Point2D.Double();
	    for(; startat+1 < argv.length; startat += 2) {
		HeclUtils.getPoint(v0,argv,startat);
		HeclUtils.getPoint(v1,argv,startat+1);
		d.drawLine(v0,v1);
	    }
	    return null;
	}
	
	if(subcmd.equals("points")) {
	    // v0(x,y) [...] 
	    Point2D p = new Point2D.Double();
	    for(; startat < argv.length; ++startat) {
		d.drawPoint(HeclUtils.getPoint(p,argv,startat));
	    }
	    return null;
	}

	if(subcmd.equals("linestrip")) {
	    // p0 p1 [...]
	    if(startat+2 > argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "p0 p1 [...]");
	    
	    Point2D v0 = HeclUtils.thing2Point(argv,startat);
	    Point2D v1 = new Point2D.Double();
	    for(++startat; startat < argv.length; ++startat) {
		d.drawLine(v0,HeclUtils.getPoint(v1,argv,startat));
		v0.setLocation(v1.getX(),v1.getY());
	    }
	    return null;
	}
	
	if(subcmd.equals("polygon")) {
	    if(startat+3 > argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "p0 p1 p2 [...]");
	    Point2D[] p = getPoints(argv,startat);
	    d.drawPolygon(p.length,p,false);
	    return null;
	}
	
	if(subcmd.equals("fpolygon")) {
	    if(startat+3 > argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "p0 p1 p2 [...]");
	    Point2D[] p = getPoints(argv,startat);
	    d.drawPolygon(p.length,p,true);
	    return null;
	}


	if(subcmd.equals("rect")) {
	    // point dimension
	    if(startat+2 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "point dimension");
	    d.drawRect(HeclUtils.thing2Point(argv,startat),
		       HeclUtils.thing2Dimension(argv,startat+1),
		       false);
	    return null;
	}

	if(subcmd.equals("string")) {
	    if(startat+2 > argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "point text [anchor]");

	    int anchor = startat+2 < argv.length ?
		WidgetInfo.toCanvasAnchor(argv[startat+2]) : Graphics.BOTTOM|Graphics.LEFT;
	    
	    d.drawString(argv[startat+1].toString(),
			 HeclUtils.thing2Point(argv,startat), anchor);
	    return null;
	}

	if(subcmd.equals("htext")) {
	    if(startat+2 > argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "point text [anchor]");

	    int anchor = startat+2 < argv.length ?
		WidgetInfo.toCanvasAnchor(argv[startat+2]) : Graphics.BOTTOM|Graphics.LEFT;
	    
	    d.drawVString(argv[startat+1].toString(),
			  HeclUtils.thing2Point(argv,startat));
	    return null;
	}

	if(subcmd.equals("rrect")) {
	    /* p dim arcw arch */
	    if(startat+4 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "point dimension arcw arch");
	    d.drawRoundRect(HeclUtils.thing2Point(argv,startat),
			    HeclUtils.thing2Dimension(argv,startat+1),
			    IntThing.get(argv[startat+2]),
			    IntThing.get(argv[startat+3]));
	    return null;
	}


	if(subcmd.equals("farc")) {
	    /* x, y, w, h, arcw, argh */
	    if(startat+4 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "point dimension arcw arch");
	    d.drawArc(HeclUtils.thing2Point(argv,startat),
		      HeclUtils.thing2Dimension(argv,startat+1),
		      IntThing.get(argv[startat+2]),
		      IntThing.get(argv[startat+3]),
		      true);
	    return null;
	}

	if(subcmd.equals("frect")) {
	    // point dim
	    if(startat+2 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "point dimension");
	    d.drawRect(HeclUtils.thing2Point(argv,startat),
		       HeclUtils.thing2Dimension(argv,startat+1),
		       true);
	    return null;
	}

	if(subcmd.equals("frrect")) {
	    // point dim 
	    if(startat+4 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "point dimension arcw arch");
	    d.fillRoundRect(HeclUtils.thing2Point(argv,startat),
			    HeclUtils.thing2Dimension(argv,startat+1),
			    IntThing.get(argv[startat+2]),
			    IntThing.get(argv[startat+3]));
	    return null;
	}

	// Get commands...
	if(subcmd.startsWith("get")) {
	    /* no arguments */
	    if(startat != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "");

//#ifdef notdef
	    if(subcmd.equals("getblue")) {
		//ip.setResult(d.getBlueComponent());
		return null;
	    }
	    if(subcmd.equals("getgreen")) {
		//ip.setResult(d.getGreenComponent());
		return null;
	    }
	    if(subcmd.equals("getred")) {
		//ip.setResult(d.getRedComponent());
		return null;
	    }
//#endif
	    if(subcmd.equals("getfont"))
		return FontMap.fontThing(d.getFont());
	    if(subcmd.equals("getgrayscale"))
		return IntThing.create(d.getGrayScale());
	    if(subcmd.equals("getlinetype"))
		IntThing.create((int)d.getLineType());
	    if(subcmd.equals("getcolor"))
		return WidgetInfo.fromColor(d.getColor());
	    if(subcmd.equals("getbackground"))
		return WidgetInfo.fromColor(d.getBackground());
	}

//#ifdef notdef	
	if(subcmd.equals("displaycolor")) {
	    n += 1;
	    if(startat+1 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "<colorspec>");
	    Color c = new Color(WidgetInfo.toColor(argv[startat]));
	    return WidgetInfo.fromColor(d.getDisplayColor(c));
	}
//#endif

	// Set commands
	if(subcmd.equals("background")) {
	    /* colorspec */
	    if(startat+1 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "<colorspec>");
	    d.setBackground(new Color(WidgetInfo.toColor(argv[startat])));
	    return null;
	}
	if(subcmd.equals("color")) {
	    /* colorspec */
	    if(startat+1 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "<colorspec>");
	    d.setColor(new Color(WidgetInfo.toColor(argv[startat])));
	    return null;
	}
	if(subcmd.equals("font")) {
	    /* font */
	    if(startat+1 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "<font>");
	    d.setFont(FontMap.get(argv[startat]));
	    return null;
	}
	if(subcmd.equals("grayscale")) {
	    /* colorspec */
	    if(startat+1 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "<greyspec>");
	    d.setGrayScale(IntThing.get(argv[startat]));
	    return null;
	}
	if(subcmd.equals("linetype")) {
	    /* linetype */
	    if(startat+1 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "<linetype>");
	    d.setLineStipple((short)HeclUtils.thing2int(argv[startat],false,0));
	    return null;
	}

	// Misc commands
	if(subcmd.equals("clip")) {
	    // {x, y} {w, h}
	    if(startat+2 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "point dimension");
	    Point2D p = HeclUtils.thing2Point(argv,startat);
	    Dimension dim = HeclUtils.thing2Dimension(argv,startat+1);
	    d.setClip((int)Math.floor(.5+p.getX()),(int)Math.floor(.5+p.getY()),
		      dim.width,dim.height);
	    return null;
	}
	if(subcmd.equals("cliprect")) {
	    /* {x, y} {w, h} */
	    if(startat+2 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "x y w h");
	    Point2D p = HeclUtils.thing2Point(argv,startat);
	    Dimension dim = HeclUtils.thing2Dimension(argv,startat+1);
	    d.clipRect((int)Math.floor(.5+p.getX()),(int)Math.floor(.5+p.getY()),
		       dim.width,dim.height);
	    return null;
	}
//#ifdef notdef
	if(subcmd.equals("translate")) {
	    // point
	    if(startat+1 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "point");
	    d.translate(HeclUtils.thing2Point(argv,startat));
	    return null;
	}
//#endif
	return super.handlecmd(ip,d,subcmd,argv,startat);
    }


    private Point2D[] getPoints(Thing[] argv,int startat)
	throws HeclException {
	int n = (argv.length - startat);
	Point2D[] points = new Point2D[n];
	for(int i=0; startat<argv.length; ++startat, ++i) {
	    points[i] = HeclUtils.thing2Point(argv,startat);
	    //System.out.println("point["+i+"]="+points[i].getX()+", "+points[i].getY());
	}
	return points;
    }
    
    protected boolean needflush = true;
//#ifdef notdef
    private int mytx = 0;
    private int myty = 0;
//#endif
    private Drawable drawable;

    private static GraphicsCmd cmd = new GraphicsCmd();
}


class Context {
    protected Context(Drawable d) {
	cliprect = new Rectangle();
	Graphics g = d.getGraphics();
	
	fcol = d.getColor();
	bcol = d.getBackground();
	gray = d.getGrayScale();
	font = d.getFont();
	linestipple = d.getLineStipple();
	linewidth = d.getLineWidth();
	d.getClipBounds(cliprect);
//#ifdef notdef
	mytx = d.getTranslateX();
	myty = d.getTranslateY();
//#endif
    }
    
    
    protected void restore(Drawable d) {
	Graphics g = d.getGraphics();
	
	d.setColor(fcol);
	d.setBackground(bcol);
	d.setGrayScale(gray);
	d.setLineStipple(linestipple);
	d.setLineWidth(linewidth);
	d.setClip(cliprect);
	d.setFont(font);
//#ifdef notdef
	d.translate(mytx,myty);
//#endif
    }
    
    private Color fcol;
    private Color bcol;
    private int gray;
    private Font font;
    private short linestipple;
    private int linewidth;
    private Rectangle cliprect;
//#ifdef notdef
    private double mytx;
    private double myty;
//#endif
}

// Variables:
// mode:java
// coding:utf-8
// End:
