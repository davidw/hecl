/*
 * Copyright (C) 2005, 2006 data2c GmbH (www.data2c.com)
 *
 * Author: Wolfgang S. Kechel - wolfgang.kechel@data2c.com
 */

package org.graphics;

import java.io.IOException;

import java.util.Hashtable;
//#ifdef ant:j2se
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.Point;
import java.awt.Rectangle;
//#else
import org.awt.Color;
import org.awt.Dimension;
import org.awt.geom.Point2D;
import org.awt.image.ImageObserver;
import org.awt.Point;
import org.awt.Rectangle;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
//#endif

import org.graphics.Draw;
import org.graphics.VFont;

/*
 * A Drawable operates on virtual device coordinates with 0,0 in the lower
 * left corner!
 */
public class Drawable {
    public static final short LT_SOLID = (short)0xffff;
    public static final short LT_DOTTED = (short)0xaaaa;
    
    public Drawable(Graphics agraphics,int width,int height) {
	super();
	g = agraphics;
	resize(width,height);
	setColor(Color.BLACK);
	bgcol = Color.WHITE;
	tx = ty = .0;
	setDrawParams();
	//Draw.translate(g,0,0);
	currvf = null;
//#ifdef ant:j2se
	setFont(new Font("SansSerif",Font.PLAIN,12));
//#else
	setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_PLAIN,Font.SIZE_SMALL));
//#endif
	try {
	    currvf = loadVFont(defvfont,null);
	}
	catch(Exception e) {
	}
    }


    public void clear() {
	//System.err.println("-->Drawable.clear()");

	// save status, erase, restore status
	Rectangle r = getClipBounds();
	//System.err.println("old clip=" + r.x + "," + r.y + "," + r.width + "," + r.height);
	//double x = getTranslateX();
	//double y = getTranslateY();
	Color oldfg = getColor();
	setColor(bgcol);
	//translate(0,0);
	g.setClip(0,0,w,h);
	g.fillRect(0,0,w,h);
	setColor(oldfg);
	//translate(x,y);
	setClip(r);
	//System.err.println("trans="+x+","+y +", clip=("+r.x+","+r.y+","+r.width+","+r.height+")");
	this.needflush = true;
    }
    

    public void clipRect(int x,int y,int width,int height) {
	g.clipRect(toX(x), toY(y), width, height);
    }
    

    public void clipRect(Rectangle r) {
	clipRect(r.x, r.y, r.width, r.height);
    }
    

    public void copyArea(Point2D p,Dimension d,Point2D dst,int anchor) {
//#ifdef ant:j2se
	g.copyArea(toX(p),toY(p),d.width,d.height,toX(dst),toY(dst));
//#else
	g.copyArea(toX(p),toY(p),d.width,d.height,toX(dst),toY(dst),anchor);
//#endif
	this.needflush = true;
    }
    

    public void drawArc(Point2D p,Dimension d,int startAngle,int arcAngle,boolean filled) {
	int x = toX(p);
	int y = toY(p);
	
	if(filled)
	    g.fillArc(x,y,d.width,d.height,startAngle,arcAngle);
	g.drawArc(x,y,d.width,d.height,startAngle,arcAngle);
	this.needflush = true;
    }


    public void drawChar(char character, int x,int y, int anchor) {
//#ifdef ant:j2se
	// ignore anchor
	char[] cbuf = new char[1];
	cbuf[0] = character;
	g.drawChars(cbuf,0,1,toX(x),toY(y));
//#else
	g.drawChar(character,toX(x),toY(y),anchor);
//#endif
    }
    

    public void drawChar(char character, Point2D p, int anchor) {
	drawChar(character,round(p.getX()),round(p.getY()),anchor);
    }
    
    
    public void drawChars(char[] data,int offset, int length, int x, int y, int anchor) {
//#ifdef ant:j2se
	// ignore anchor
	g.drawChars(data,offset,length,toX(x),toY(y));
//#else
	g.drawChars(data,offset,length,toX(x),toY(y),anchor);
//#endif
    }
    

    public void drawChars(char[] data,int offset, int length, Point2D p, int anchor) {
	drawChars(data,offset,length,round(p.getX()),round(p.getY()),anchor);
    }
    
    
    public void drawImage(Image img, int x, int y, ImageObserver observer) {
//#ifdef ant:j2se
	g.drawImage(img,toX(x),toY(y),observer);
//#else
	g.drawImage(img,toX(x),toY(y),Graphics.TOP|Graphics.LEFT);
//#endif
	this.needflush = true;
    }
    
    
    public void drawImage(Image img, Point2D p, ImageObserver observer) {
	drawImage(img,round(p.getX()),round(p.getY()),observer);
    }
    
    
    public void drawImage(Image img, int x, int y, int anchor) {
//#ifdef ant:j2se
	drawImage(img,x,y,null);
//#else
	g.drawImage(img,toX(x),toY(y),anchor);
//#endif
    }
    
    
    public void drawImage(Image img, Point2D p, int anchor) {
	drawImage(img,round(p.getX()),round(p.getY()),anchor);
    }
    
    
    public void drawPoint(Point2D p) {
	int x = toX(p);
	int y = toY(p);
	g.drawLine(x,y,x,y);
	this.needflush = true;
    }
    

    public void drawLine(int fromx,int fromy,int tox,int toy) {
	g.drawLine(toX(fromx),toY(fromy),toX(tox),toY(toy));
	this.needflush = true;
    }
    

    public void drawLine(Point2D from,Point2D to) {
	g.drawLine(toX(from),toY(from),toX(to),toY(to));
	this.needflush = true;
    }
    

    public void xline(Point2D from,Point2D to) {
	xline(round(from.getX()),round(from.getY()),
	      round(to.getX()),round(to.getY()));
    }
    public void xline(int fromx,int fromy,int tox,int toy) {
	g.drawLine(fromx,fromy,tox,toy);
	this.needflush = true;
    }
    

    public void drawPolygon(int n,Point2D[] points,boolean filled) {
	Point[] p = new Point[n];
	for(int i=0; i<n; ++i) {
	    p[i] = new Point(toX(points[i]),toY(points[i]));
	}
	Draw.drawPolygon(g,n,p,filled);
    }


    public void drawRect(int x,int y,int width,int height, boolean filled) {
	int dx = toX(x);
	int dy = toY(y+height);
	if(filled)
	    g.fillRect(dx, dy, width, height);
	g.drawRect(dx, dy, width, height);
	this.needflush = true;
    }
    

    public void drawRect(Point2D p,Dimension d,boolean filled) {
	drawRect(round(p.getX()), round(p.getY()), d.width, d.height,filled);
    }


    public void drawRoundRect(int x, int y, int width, int height,
			      int arcWidth, int arcHeight) {
	g.drawRoundRect(toX(x),toY(y+height),
			width,height,arcHeight,arcHeight);
	this.needflush = true;
    }
    
    
    public void drawRoundRect(Point2D p,Dimension d, int arcWidth, int arcHeight) {
	drawRoundRect(round(p.getX()),round(p.getY()),d.width,d.height,arcHeight,arcHeight);
    }
    

    public void drawString(String str, int x, int y, int anchor) {
//#ifdef ant:j2se
	// ignore anchor
	g.drawString(str,toX(x),toY(y));
//#else
	g.drawString(str,toX(x),toY(y),anchor);
//#endif
	this.needflush = true;
    }
    

    public void drawString(String str, Point2D p, int anchor) {
//#ifdef ant:j2se
	// ignore anchor
	g.drawString(str,toX(p),toY(p));
//#else
	g.drawString(str,toX(p),toY(p),anchor);
//#endif
	this.needflush = true;
    }
    

    public void fillRoundRect(int x, int y, int width, int height,
			      int arcWidth, int arcHeight) {
	g.fillRoundRect(toX(x),toY(y+height-1),width,height,arcHeight,arcHeight);
	this.needflush = true;
    }
    
    
    public void fillRoundRect(Point2D p, Dimension d,
			      int arcWidth, int arcHeight) {
	fillRoundRect(round(p.getX()),round(p.getY()),d.width,d.height,arcHeight,arcHeight);
    }
    

    public Font getFont() {
	return g.getFont();
    }
    

    public VFont getVFont() {
	return currvf;
    }
    

//#ifndef ant:j2se
    public void drawRegion(Image src, int srcx,int srcy,int width,int height,
			   int transform, int dstx,int dsty, int anchor) {
	g.drawRegion(src,srcx,srcy,width,height,transform,toX(dstx),toY(dsty),anchor);
	this.needflush = true;
    }
    

    public void drawRegion(Image src, Point2D psrc,
			   Dimension d, int transform,
			   Point2D dest, int anchor) {
	drawRegion(src,round(psrc.getX()),round(psrc.getY()),
		   d.width,d.height,transform,
		   round(dest.getX()),round(dest.getY()),anchor);
    }
    

    public void drawRGB(int[] rgbData, int offset, int scanlength,
			int x, int y, int width, int height,
			boolean processAlpha) {
	g.drawRGB(rgbData,offset,scanlength,toX(x),toY(y),width,height,processAlpha);
	this.needflush = true;
    }
    

    public void drawRGB(int[] rgbData, int offset, int scanlength,
			Point2D p,Dimension d, boolean processAlpha) {
	drawRGB(rgbData,offset,scanlength,round(p.getX()),round(p.getY()),d.width,d.height,processAlpha);
    }
    

    public Rectangle getClipBounds(Rectangle r) {
	r.width = g.getClipWidth();
	r.height = g.getClipHeight();
	r.x = fromX(g.getClipX());
	r.y = fromY(g.getClipY() + (r.height > 0 ? r.height - 1 : 0));
//#ifdef debug
	System.err.println("Drawable.getclipbounds clip=" + r.x
			   + ", " + r.y + " - " + g.getClipY()
			   + ", " + r.width
			   + ", " + r.height);
//#endif
	return r;
    }
    

    public int getGrayScale() {
	return g.getGrayScale();
    }
    

    public Graphics getGraphics() {
	return g;
    }
    

    public int getLineType() {
	return g.getStrokeStyle();
    }
    

    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
	g.fillTriangle(toX(x1),toY(y1),toX(x2),toY(y2),toX(x3),toY(y3));
	this.needflush = true;
    }
    
    public void fillTriangle(Point p1,Point p2,Point p3) {
	fillTriangle(p1.x,p1.y,p2.x,p2.y,p3.x,p3.y);
    }
    

    public void resize(int newWidth,int newHeight) {
	w = newWidth;
	h = newHeight;
	h1 = h - 1;
	setClip(0,0,w,h);
    }
    
    public void setGrayScale(int value) {
	g.setGrayScale(value);
    }
    

//#else
    public Rectangle getClipBounds(Rectangle r) {
	Rectangle r = g.getClipBounds(r);
	r.y = fromY(g.getClipY()) + (r.height > 0 ? r.height-1 : 0);
	return r;
    }
//#endif


    public void drawVString(String str, Point2D p) {
	drawVString(str,p,VFont.BOTTOM|VFont.LEFT);
    }
    

    public void drawVString(String str, Point2D p,int anchor) {
	if(currvf == null)
	    return;
	currvf.drawString(str,toX(p),toY(p),null,g);
	this.needflush = true;
    }
    

    public void drawVString(String str, Point2D p,int anchor,
			    double sx,double sy,
			    double rotindegree,double slantintanrad) {
	if(currvf == null)
	    return;
	currvf.drawString(str,toX(p),toY(p),anchor,sx,sy,rotindegree,slantintanrad,null,g);
	this.needflush = true;
    }
    

    public Rectangle extentVString(String str, Point2D p,int anchor,Rectangle r) {
	if(currvf == null)
	    return null;
	r = currvf.extent(str,toX(p),toY(p),anchor,r);
	r.y = fromY(r.y);
	return r;
    }
    

    public Color getBackground() {
	return bgcol;
    }
    

    public Rectangle getClipBounds() {
	return getClipBounds(new Rectangle());
    }
    

    public Color getColor() {
	return fgcol;
    }
    

    public short getLineStipple() {
	return linestipple;
    }
    

    public int getLineWidth() {
	return linewidth;
    }
    
    /*
    public double getTranslateX() {
	return tx;
    }
    
	    
    public double getTranslateY() {
	return ty;
    }
    */
	    
    public boolean needsFlush() {
	return needflush;
    }
    
    public void flush() {
	needflush = false;
    }
    
    public void setClip(int x, int y, int width, int height) {
//#ifdef debug
	System.err.println("Drawable.setClip to: "+x+" - "+toX(x)
			   +", "+y+" - "+toY(y+(height > 0 ? height-1:0))
			   +", "+width
			   +", "+height);
//#endif
	g.setClip(toX(x),toY(y+(height > 0 ? height-1:0)),width,height);
    }
    

    public void setClip(Rectangle r) {
	setClip(r.x,r.y,r.width,r.height);
    }
    

    public void setColor(Color c) {
	fgcol = c;
//#ifdef ant:j2se
	g.setColor(c);
//#else
	g.setColor(c.getRGB());
//#endif
    }
    

    public void setBackground(Color c) {
	if(c != bgcol) {
	    bgcol = c;
	}
    }
    
    
    public void setFont(Font f) {
	g.setFont(f);
    }
    

    public void setVFont(String name) {
	VFont vf = loadVFont(name,null);
    }
    
    public void setVFont(VFont vf) {
	if(vf != null)
	    currvf = vf;
    }
    

    public void setLineStipple(short stipple) {
	setLineStipple(linestipplefactor,stipple);
    }
    

    public void setLineStipple(int factor,short stipple) {
	if(stipple != linestipple || factor != linestipplefactor) {
	    linestipple = stipple;
	    factor = factor;
	    setDrawParams();
	}
    }


    public void setLineWidth(int width) {
	if(width != linewidth) {
	    linewidth = width;
	    setDrawParams();
	}
    }
    

    /*
    public void translate(int x, int y) {
	translate((double)x,(double)y);
    }


    public void translate(double x, double y) {
	tx = x;
	ty = y;
	Draw.translate(g,round(tx),toY(round(ty)));
    }

    public void translate(Point2D p) {
	translate(p.getX(),p.getY());
    }
    */


    private static VFont loadVFont(String name,String resname) {
	VFont vf = (VFont)vfonttab.get(name);
	if(vf == null) {
	    String myname = resname;
	    if(resname == null)
		myname = "/"+name+".vf";
	    try {
		vf = new VFont(name,myname);
	    }
	    catch(IOException e) {
		e.printStackTrace();
	    }
	    if(vf != null)
		vfonttab.put(name,vf);
	}
	return vf;
    }

    protected int toX(Point2D p) {
	return round(p.getX());
    }
    
    protected int toX(int x) {
	return x;
    }

    protected int fromX(int x) {
	return x;
    }

    
    protected int toY(Point2D p) {
	return toY(round(p.getY()));
    }
    
    protected int toY(int y) {
	//System.err.println("toY("+y+") - h="+h+ ", h1="+h1+ " --> " + (h1-y));
	return h1-y;
    }
    
    protected int fromY(int y) {
	return y-h1;
    }
    
    protected static int round(double x) {
	return (int)Math.floor(.5+x);
    }
    
    private void setDrawParams() {
//#ifdef ant:j2se
	if(linestipple == LT_SOLID)
	    ((Graphics2D)g).setStroke(new BasicStroke(linewidth));
	else {
	    // everything else is dotted!
	    float[] dashes = new float[2];
	    dashes[0] = linestipplefactor;
	    dashes[1] = linestipplefactor;
	    ((Graphics2D)g).setStroke(new BasicStroke(linewidth,
						      BasicStroke.CAP_SQUARE,
						      BasicStroke.JOIN_MITER,
						      10.0f,
						      dashes,.0f));
	}
//#else
	g.setStrokeStyle(linestipple == LT_SOLID ? Graphics.SOLID : Graphics.DOTTED);
//#endif
    }


    public static final String defvfont = "futural";
    //public static final String defvfont = "timesr";
    protected static Hashtable vfonttab = new Hashtable();

    protected Graphics g = null;
    protected int w = 1;		    // screen width
    protected int h = 1;		    // screen height
    private int h1 = 0;			    // screen height-1
    protected short linestipple = LT_SOLID;   // line stipple
    protected int linestipplefactor = 1;
    protected int linewidth = 1;
    protected double tx = 0;		    // x translation
    protected double ty = 0;		    // y translation
    protected Color fgcol = Color.black;    // foreground color
    protected Color bgcol = Color.white;    // background color
    protected VFont currvf = null;
    protected Font currf = null;
    protected boolean needflush = true;
}
