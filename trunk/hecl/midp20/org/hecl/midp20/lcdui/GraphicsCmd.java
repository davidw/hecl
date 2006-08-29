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

import java.lang.StringBuffer;
import java.util.Vector;
import java.util.Stack;

//#ifdef ant:j2se
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
import org.hecl.Properties;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

public class GraphicsCmd extends ThingCmd {
    protected static class Context {
	protected Context(GraphicsCmd gcmd) {
	    cliprect = new Rectangle();
	    Drawable d = (Drawable)gcmd.getData();
	    Graphics g = d.getGraphics();
	    
	    fcol = d.getColor();
	    bcol = d.getBackground();
	    gray = d.getGrayScale();
	    font = d.getFont();
	    linestipple = d.getLineStipple();
	    linewidth = d.getLineWidth();
	    d.getClipBounds(cliprect);
//#ifdef notdef
	    tx = d.getTranslateX();
	    ty = d.getTranslateY();
//#endif
	}
	

	protected void restore(GraphicsCmd gcmd) {
	    Drawable d = (Drawable)gcmd.getData();
	    Graphics g = d.getGraphics();

	    d.setColor(fcol);
	    d.setBackground(bcol);
	    d.setGrayScale(gray);
	    d.setLineStipple(linestipple);
	    d.setLineWidth(linewidth);
	    d.setClip(cliprect);
	    d.setFont(font);
//#ifdef notdef
	    d.translate(tx,ty);
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
	private double tx;
	private double ty;
//#endif
    }
    

    protected GraphicsCmd(Graphics g,int width,int height) throws HeclException {
	super(new Drawable(g,width,height));
	w = width;
	h = height;
//#ifdef notdef
	tx = ty = 0;
//#endif
    }


    public void cget(Interp ip,String optname) throws HeclException {
	Drawable d = (Drawable)getData();
	
	if(optname.equals(WidgetInfo.NCLIPHEIGHT)) {
	    ip.setResult(d.getClipBounds().height);
	    return;
	}
	if(optname.equals(WidgetInfo.NCLIPWIDTH)) {
	    ip.setResult(d.getClipBounds().width);
	    return;
	}
	if(optname.equals(WidgetInfo.NCLIPX)) {
	    ip.setResult(d.getClipBounds().x);
	    return;
	}
	if(optname.equals(WidgetInfo.NCLIPY)) {
	    ip.setResult(d.getClipBounds().y);
	    return;
	}
	if(optname.equals(WidgetInfo.NCOLOR)) {
	    ip.setResult(WidgetInfo.fromColor(d.getColor().getRGB()));
	    return;
	}
	if(optname.equals("-background")) {
	    ip.setResult(WidgetInfo.fromColor(d.getColor().getRGB()));
	    return;
	}
	if(optname.equals(WidgetInfo.NFONT)) {
	    FontMap.setResult(ip,d.getFont());
	    return;
	}
	if(optname.equals("-grayscale")) {
	    ip.setResult(d.getGrayScale());
	    return;
	}
//#ifdef notdef
	if(optname.equals("-blue")) {
	    ip.setResult(d.getBlueComponent());
	    return;
	}
	if(optname.equals("-green")) {
	    ip.setResult(d.getGreenComponent());
	    return;
	}
	if(optname.equals("-red")) {
	    ip.setResult(d.getRedComponent());
	    return;
	}
//#endif
	if(optname.equals(WidgetInfo.NLINETYPE)) {
	    ip.setResult((int)d.getLineType());
	    return;
	}
//#ifdef notdef
	if(optname.equals("-translatex")) {
	    ip.setResult(DoubleThing.create(d.getTranslateX()));
	    return;
	}
	if(optname.equals("-translatey")) {
	    ip.setResult(DoubleThing.create(d.getTranslateY()));
	    return;
	}
//#endif
	super.cget(ip,optname);
    }



    public void cset(Interp ip,String optname,Thing optval) throws HeclException {
	Drawable d = (Drawable)getData();
	
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
	super.cset(ip,optname,optval);
    }


    public synchronized void handlecmd(Interp ip,String subcmd, Thing[] argv,int startat)
	throws HeclException {

	if(!sequence(new Stack(),ip,subcmd,argv,startat)) {
	    super.handlecmd(ip,subcmd,argv,startat);
	}
    }
    

    private synchronized boolean sequence(Stack st,Interp ip,
					  String subcmd, Thing[] argv,int startat)
	throws HeclException {
	Drawable d = (Drawable)getData();
	int n = startat;

	// Stack manipulation
	if(subcmd.equals("push")) {
	    st.push(new Context(this));
	    return true;
	}
	if(subcmd.equals("pop")) {
	    if(st.empty()) {
		throw new HeclException("Empty context stack.");
	    }
	    ((Context)st.pop()).restore(this);
	    return true;
	}
	if(subcmd.equals("draw")) {
	    Vector errs = new Vector();
	    for(; startat+1 <= argv.length; ++startat) {
		Vector v = ListThing.get(argv[startat]);
		int cmdlen = v.size();
		for(int pos = 0; pos < cmdlen; ++pos) {
		    Vector cmd = ListThing.get((Thing)v.elementAt(pos));
		    Thing[] xargs = new Thing[cmd.size()];
		    cmd.copyInto(xargs);
		    try {
			sequence(st,ip,xargs[0].toString().toLowerCase(),xargs,1);
		    }
		    catch (HeclException e) {
			errs.addElement(new Integer(startat));
			errs.addElement(new Integer(pos));
			errs.addElement(e);
		    }
		}
	    }
	    
	    if(errs.size() > 0) {
		// OOPS, error
		// We create a comfortable error message with index into
		// command sequence and error message for the errorneous
		// command.
		StringBuffer s = new StringBuffer("Error in "+subcmd+":");
		
		n = errs.size();
		for(int i=0; i<n; i += 3) {
		    int drawcmd = ((Integer)errs.elementAt(i)).intValue();
		    int idx = ((Integer)errs.elementAt(i+1)).intValue();
		    s.append(" ").append(drawcmd).append(",").
			append(idx).append(": ").
			append(((Exception)errs.elementAt(i+2)).getMessage());
		}
		throw new HeclException(s.toString());
	    }
	    return true;
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
	    return true;
	}
	if(subcmd.equals("clear")) {
	    d.clear();
	    return true;
	}
	if(subcmd.equals("copyarea")) {
	    /* vsrc(x,y), dim(w,h) vdst(x,y), anchor */
	    if(startat+3 > argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "psrc dimension pdst [anchor]");
	    
	    int anchor = startat+3 < argv.length ?
		WidgetInfo.toCanvasAnchor(argv[n]) : Graphics.TOP|Graphics.LEFT;
	    System.err.println("anchor="+Integer.toHexString(anchor));
	    d.copyArea(HeclUtils.thing2Point(argv,startat),
		       HeclUtils.thing2Dimension(argv,startat+1),
		       HeclUtils.thing2Point(argv,startat+2), anchor);
	    return true;
	}
	if(subcmd.equals("image")) {
	    // image, v(x,y), anchor
	    if(startat+2 > argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "animage point [anchor]");
	    
	    int anchor = startat+2 < argv.length ?
		WidgetInfo.toCanvasAnchor(argv[n]) : Graphics.TOP|Graphics.LEFT;
	    System.err.println("anchor="+Integer.toHexString(anchor));
	    d.drawImage(ImageMap.mapOf(ip).asImage(argv[startat],false),
			HeclUtils.thing2Point(argv,startat+1), anchor);
	    return true;
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
	    return true;
	}
	
	if(subcmd.equals("points")) {
	    // v0(x,y) [...] 
	    Point2D p = new Point2D.Double();
	    for(; startat < argv.length; ++startat) {
		d.drawPoint(HeclUtils.getPoint(p,argv,startat));
	    }
	    return true;
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
	}
	
	if(subcmd.equals("polygon")) {
	    if(startat+3 > argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "p0 p1 p2 [...]");
	    Point2D[] p = getPoints(argv,startat);
	    d.drawPolygon(p.length,p,false);
	}
	
	if(subcmd.equals("fpolygon")) {
	    if(startat+3 > argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "p0 p1 p2 [...]");
	    Point2D[] p = getPoints(argv,startat);
	    d.drawPolygon(p.length,p,true);
	    return true;
	}


	if(subcmd.equals("rect")) {
	    // point dimension
	    if(startat+2 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "point dimension");
	    d.drawRect(HeclUtils.thing2Point(argv,startat),
		       HeclUtils.thing2Dimension(argv,startat+1),
		       false);
	    return true;
	}

	if(subcmd.equals("string")) {
	    if(startat+2 > argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "point text [anchor]");

	    int anchor = startat+2 < argv.length ?
		WidgetInfo.toCanvasAnchor(argv[startat+2]) : Graphics.TOP|Graphics.LEFT;
	    
	    d.drawString(argv[startat+1].toString(),
			 HeclUtils.thing2Point(argv,startat), anchor);
	    return true;
	}

	if(subcmd.equals("htext")) {
	    if(startat+2 > argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "point text [anchor]");

	    int anchor = startat+2 < argv.length ?
		WidgetInfo.toCanvasAnchor(argv[startat+2]) : Graphics.TOP|Graphics.LEFT;
	    
	    d.drawVString(argv[startat+1].toString(),
			  HeclUtils.thing2Point(argv,startat));
	    return true;
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
	    return true;
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
	    return true;
	}

	if(subcmd.equals("frect")) {
	    // point dim
	    if(startat+2 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "point dimension");
	    d.drawRect(HeclUtils.thing2Point(argv,startat),
		       HeclUtils.thing2Dimension(argv,startat+1),
		       true);
	    return true;
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
	    return true;
	}

	// Get commands...
	if(subcmd.startsWith("get")) {
	    /* no arguments */
	    if(startat != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "");

//#ifdef notdef
	    if(subcmd.equals("getblue")) {
		ip.setResult(d.getBlueComponent());
		return true;
	    }
	    if(subcmd.equals("getgreen")) {
		ip.setResult(d.getGreenComponent());
		return true;
	    }
	    if(subcmd.equals("getred")) {
		ip.setResult(d.getRedComponent());
		return true;
	    }
//#endif
	    if(subcmd.equals("getfont")) {
		FontMap.setResult(ip,d.getFont());
		return true;
	    }
	    if(subcmd.equals("getgrayscale")) {
		ip.setResult(d.getGrayScale());
		return true;
	    }
	    if(subcmd.equals("getlinetype")) {
		ip.setResult((int)d.getLineType());
		return true;
	    }
	    if(subcmd.equals("getcolor")) {
		ip.setResult(WidgetInfo.fromColor(d.getColor().getRGB()));
		return true;
	    }
	    if(subcmd.equals("getbackground")) {
		ip.setResult(WidgetInfo.fromColor(d.getBackground().getRGB()));
		return true;
	    }
	}

//#ifdef notdef	
	if(subcmd.equals("displaycolor")) {
	    n += 1;
	    if(startat+1 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "<colorspec>");
	    Color c = new Color(WidgetInfo.toColor(argv[startat]));
	    ip.setResult(WidgetInfo.fromColor(d.getDisplayColor(c)));
	    return true;
	}
//#endif

	// Set commands
	if(subcmd.equals("background")) {
	    /* colorspec */
	    if(startat+1 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "<colorspec>");
	    d.setBackground(new Color(WidgetInfo.toColor(argv[startat])));
	    return true;
	}
	if(subcmd.equals("color")) {
	    /* colorspec */
	    if(startat+1 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "<colorspec>");
	    d.setColor(new Color(WidgetInfo.toColor(argv[startat])));
	    return true;
	}
	if(subcmd.equals("font")) {
	    /* font */
	    if(startat+1 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "<font>");
	    d.setFont(FontMap.get(argv[startat]));
	    return true;
	}
	if(subcmd.equals("grayscale")) {
	    /* colorspec */
	    if(startat+1 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "<greyspec>");
	    d.setGrayScale(IntThing.get(argv[startat]));
	    return true;
	}
	if(subcmd.equals("linetype")) {
	    /* linetype */
	    if(startat+1 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "<linetype>");
	    d.setLineStipple((short)HeclUtils.thing2int(argv[startat],false,0));
	    return true;
	}

	// Misc commands
	if(subcmd.equals("clip")) {
	    // x, y, w, h
	    if(startat+4 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "point dimension");
	    Point2D p = HeclUtils.thing2Point(argv,startat);
	    Dimension dim = HeclUtils.thing2Dimension(argv,startat+1);
	    d.setClip((int)Math.floor(.5+p.getX()),(int)Math.floor(.5+p.getY()),
		      dim.width,dim.height);
	    return true;
	}
	if(subcmd.equals("cliprect")) {
	    /* x, y, w, h, */
	    if(startat+4 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "x y w h");
	    Point2D p = HeclUtils.thing2Point(argv,startat);
	    Dimension dim = HeclUtils.thing2Dimension(argv,startat+1);
	    d.clipRect((int)Math.floor(.5+p.getX()),(int)Math.floor(.5+p.getY()),
		       dim.width,dim.height);
	    return true;
	}
//#ifdef notdef
	if(subcmd.equals("translate")) {
	    // point
	    if(startat+1 != argv.length)
		throw HeclException.createWrongNumArgsException(
		    argv, startat, "point");
	    d.translate(HeclUtils.thing2Point(argv,startat));
	    return true;
	}
//#endif
	super.handlecmd(ip,subcmd,argv,startat);
	return false;
    }


    private Point2D[] getPoints(Thing[] argv,int startat)
	throws HeclException {
	int n = (argv.length - startat);
	Point2D[] points = new Point2D[n];
	for(int i=0; startat<argv.length; ++startat, ++i) {
	    points[i] = HeclUtils.thing2Point(argv,startat);
	    System.out.println("point["+i+"]="+points[i].getX()+", "+points[i].getY());
	}
	return points;
    }
    
    private int w;
    private int h;
//#ifdef notdef
    private int tx;
    private int ty;
//#endif
}
