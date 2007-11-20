/*
 * Copyright (C) 2005, 2006 data2c GmbH (www.data2c.com)
 *
 * Author: Wolfgang S. Kechel - wolfgang.kechel@data2c.com
 *
 */

package org.graphics;

import java.io.EOFException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.Hashtable;

/*
 * Set internal define to make module widely independant of build environment
 */
//#ifndef ant:j2se
//#define ISMIDLET
//#endif

//#ifndef ISMIDLET
/*
 * Control usage of HERSHEY font stuff. Comment next line to disable
 * creation of vector font from hershey font.
 */
//#define USEHERSHEY

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

import java.net.URL;

import java.util.Enumeration;

//#ifdef USEHERSHEY
import org.hershey.HersheyFont;
//#endif

//#else
import javax.microedition.lcdui.Graphics;

import org.awt.Point;
import org.awt.Rectangle;
//#endif

public class VFont {
    public final static double ITALICS = 0.75;

//#ifndef ISMIDLET
    public final static int HCENTER = 1;
    public final static int VCENTER = 2;
    public final static int LEFT = 4;
    public final static int RIGHT = 8;
    public final static int TOP = 16;
    public final static int BOTTOM = 32;
    public final static int BASELINE = 64;
    //public final static int CAP = 0x80;
    protected static double dpi = 100;
//#else
    public final static int HCENTER = Graphics.HCENTER;
    public final static int LEFT = Graphics.LEFT;
    public final static int RIGHT = Graphics.RIGHT;
    public final static int TOP = Graphics.TOP;
    public final static int VCENTER = Graphics.VCENTER;
    public final static int BOTTOM = Graphics.BOTTOM;
    public final static int BASELINE = Graphics.BASELINE;
    //public final static int CAP = 2;
    protected static double dpi = 125;
//#endif

    // Target size of font in point (default: 12pt)
    protected static double normsize = 12.0;
    // Hold precalculated value to scale font to normsize
    // accoring to display resolution in dpi.
    protected static double scaling = (normsize * dpi) / 72.0;
    

    public static int getDPI() {
	return (int)dpi;
    }
    
    public static void setDPI(int dotsperinch) {
	dpi = dotsperinch;
	scaling = (normsize * dpi) / 72.0;
    }
    
    // Class to hold all parameters needed for drawing a string
    public static class DrawParams {
	public DrawParams() {
	    this(BOTTOM|LEFT,1,1,0,0);
	}
	

	public DrawParams(int anchorspec,double w,double h,double rot,double italics) {
	    anchor = anchorspec;
	    width = w;
	    height = h;
	    theta = rot;
	    slant = italics;
	}

	
	void setup(double fontheight) {
	    scale = scaling / fontheight;
	    xscale = width * scale;
	    yscale = height * scale;
	    if(theta != .0) {
		double tmp = (double)(-Math.PI / 180.0 * theta);
		costheta = (double)Math.cos(tmp);
		sintheta = (double)Math.sin(tmp);
	    } else {
		costheta = 1.0;
		sintheta = .0;
	    }
	    finalslant = slant * yscale;
/*
	    System.err.println("fh="+fontheight
			       +", w="+width +" / xs="+xscale
			       +", h="+height +" / ys="+yscale
			       +", sl="+slant +" / fsl="+finalslant);
*/
	}
	
	
	public int anchor;
	public double width;
	public double height;
	public double theta;		    // rotation in degrees
	public double slant;		    // slant as tan(slantangle)
	double costheta = 1.0;
	double sintheta = .0;
	double finalslant = .0;
	double scale;
	double xscale;
	double yscale;
    }
    
    public static class Glyph
//#ifndef ISMIDLET
    implements Cloneable
//#endif
    {
	// A glyph describes a character.
	// The coordinate system has (0,0) in the lower left corner increasing
	// to the left (x) and upwards (y).
	public Glyph(char ch,int cwidth,char[] strokes) {
	    this(ch,cwidth,0,strokes);
	}
	

	public Glyph(char ch,int cwidth,int ml,char[] strokes) {
	    this.ch = ch;
	    this.cwidth = (short)cwidth;
	    this.midline = (short)ml;
	    this.strokes = strokes;
	}

	public int draw(int xp, int yp, int rotpx, int rotpy, DrawParams dp,
			Rectangle r, Graphics g) {
	    int npts = 0;
	    Point[] pts = null;
	    for(int i=0; i<strokes.length;) {
		int what = (int)strokes[i++];
		switch(what) {
		  case 'v':
		  case 'l':
		    npts = 2;
		    //System.out.println("ex v 2");
		    pts = getPoints(npts,i,xp,yp,rotpx,rotpy,dp,r);
		    line(pts[0].x,pts[0].y,pts[1].x,pts[1].y,g);
		    i += 2 * npts;
		    break;
		  case 'V':
		  case 'p':
		  case 'P':
		    npts = (int)strokes[i++];
		    //System.out.println("ex p "+npts);
		    pts = getPoints(npts,i, xp,yp,rotpx,rotpy,dp,r);
		    switch(what) {
		      case 'V':
			lines(pts,g);
			break;
		      case 'p':
			if(g != null)
			    Draw.drawPolygon(g,npts,pts,false);
			break;
		      case 'P':
			if(g != null)
			    Draw.drawPolygon(g,npts,pts,true);
			break;
		    }
		    i += 2 * npts;
		    break;
		  default:
		    i = strokes.length;
		    break;
		}
	    }
	    return round(dp.xscale/*dp.width*/ * cwidth);
	}


	protected Point[] getPoints(int npts,int startat,
				    int xp, int yp, int rotpx, int rotpy,
				    DrawParams dp, Rectangle r) {
	    int x;
	    int y;
	    Point[] p = new Point[npts];
	    for(int i=0; i<npts; ++i, startat += 2) {
		x = xform(xp, strokes[startat], dp.xscale);
		if(0.0 != dp.finalslant) {
		    //System.out.println("y="+(int)strokes[startat+1] +"corr="+(strokes[startat+1]*finalslant));
		    // add italics offset to the "normal" point transformation
		    x += strokes[startat+1]*dp.finalslant;
		}
		// calculate the y coordinate
		y = xform(yp, -strokes[startat+1], dp.yscale);
		
		if(dp.theta != .0) {
		    // apply the rotation matrix ...
		    // transform the coordinate to the rotation center point
		    double xd = x - rotpx;
		    double yd = y - rotpy;
		    
		    // rotate
		    double xd2 = xd * dp.costheta - yd * dp.sintheta;
		    double yd2 = xd * dp.sintheta + yd * dp.costheta;
		    
		    // transform back
		    x = round(xd2) + rotpx;
		    y = round(yd2) + rotpy;
		}
		if(r != null) {
		    if(x < r.x) {
			r.x = x;
		    }
		    if(y < r.y) {
			r.y = y;
		    }
		    if(x > r.width) {
			r.width = x;
		    }
		    if(y > r.height) {
			r.height = y;
		    }
		}
		p[i] = new Point(x,y);
	    }
	    return p;
	}
	

	protected void lines(Point[] points,Graphics g) {
	    if(points == null || g == null)
		return;
	    for(int i=0; i<points.length-1; ++i) {
		line(points[i].x,points[i].y,points[i+1].x,points[i+1].y,g);
	    }
	}
	
	
	protected void line(int x1, int y1, int x2, int y2, Graphics g) {
	    if(g == null)
		return;
	    
//#ifdef notdef
	    if(width > 1) {
//#ifndef ISMIDLET
		// if the width is greater than one
		Polygon filledPolygon = new Polygon();
		
		int offset = width / 2;
		
		// this does not generate a true "wide line" but it seems to
		// look OK for font lines
		filledPolygon.addPoint(x1 - offset, y1 + offset);
		filledPolygon.addPoint(x1 + offset, y1 - offset);
		filledPolygon.addPoint(x2 + offset, y2 - offset);
		filledPolygon.addPoint(x2 - offset, y2 + offset);
		// draw a polygon
		g.fillPolygon(filledPolygon);
//#else
		System.out.println("NO THICK LINES!!!");
//#endif
	    }
//#endif
	    // draw a line
//#ifdef ISMIDLET
	    int oldstroke = g.getStrokeStyle();
	    g.setStrokeStyle(Graphics.SOLID);
//#endif
	    g.drawLine(x1, y1, x2, y2);
//#ifdef ISMIDLET
	    g.setStrokeStyle(oldstroke);
//#endif
	}
    
//#ifndef ISMIDLET
	public Glyph(Glyph g) {
	    this(g.ch,g.cwidth,g.midline,copyStrokes(g.strokes));
	}
	

	public Object clone() {
	    return new Glyph(this);
	}
	

	public char[] getStrokes() {
	    return strokes;
	}


	public void setStrokes(char[] newstrokes) {
	    strokes = newstrokes;
	}
	
	public PrintStream storeOn(PrintStream s) {
	    s.print("# "+Integer.toString(ch) + " w " + cwidth);
	    if(midline != 0 && midline != cwidth/2)
		s.print(" m "+midline);
	    if(strokes != null) {
		for(int i=0; i<strokes.length; ) {
		    char ch = strokes[i++];
		    s.print(' ');
		    s.print(ch);
		    switch(ch) {
		      case 'v':
		      case 'l':
			s.print(" "+((int)strokes[i++]));
			s.print(" "+((int)strokes[i++]));
			s.print(" "+((int)strokes[i++]));
			s.print(" "+((int)strokes[i++]));
			break;
		      case 'V':
		      case 'p':
		      case 'P':
			int n = (int)strokes[i++];
			s.print(" " + String.valueOf(n));
			while(n-- > 0) {
			    s.print(" "+((int)strokes[i++]));
			    s.print(" "+((int)strokes[i++]));
			}
			break;
		      default:
			i = strokes.length;
			break;
		    }
		}
	    }
	    s.print(";");
	    return s;
	}


	public int getMidLine() {
	    return midline;
	}
	
	public void setWidth(int val) {
	    if(val >= 0)
		cwidth = (short)val;
	}
    
	public void setMidLine(int val) {
	    if(val >= 0)
		midline = (short)val;
	}

	private static char[] copyStrokes(char[] strokes) {
	    char[] newstrokes = new char[strokes.length];
	    System.arraycopy(strokes,0,newstrokes,0,strokes.length);
	    return newstrokes;
	}
//#endif


	public int getWidth() {
	    return cwidth;
	}
    

	static protected final int xform(int offset, int val, double mag) {
	    return round(offset + val * mag);
	}
	

	char ch;
	short cwidth;
	short midline = 0;
	char[] strokes = null;
    }


    public VFont(String name,String resourcename) throws IOException {
	this(name,
	     new InputStreamReader(
		 checkis(resourcename.getClass().getResourceAsStream(resourcename),
			 resourcename)));
    }


    public VFont(String name,InputStreamReader is) throws IOException {
	fontname = name;
	
	// read format
	int format = getc(is);
	int unused = getc(is);
	int ch;
	
	while((ch = getc(is)) != '{' && ch != -1)
	    ;
	if(ch == -1)
	    // error: premature eof
	    throw new EOFException("Premature EOF in font file.");
	if(format != 'A' || unused != 'X')
	    // error: unknown format
	    throw new IOException("Font file format error.");

	while((ch = getc(is)) != '#' && ch != -1) {
	    switch (ch) {
	      case 'n':
		// ignore
		readInt(is);
		//h.numchars = readInt(is);
		/* note that number of characters is one more than max characters */
		break;
	      case 'h':
		fontheight = readInt(is);
		break;
	      case 'b':
		fontbaseline = readInt(is);
		break;
	      case 'x':
		fontxline = readInt(is);
		break;
	      case 'c':
		fontcapline = readInt(is);
		break;
	      case 'w':
		fontcwidth = readInt(is);
		break;
	      case 'T':
		fontthickness = readInt(is);
		break;
	      case ' ':
	      default:
		break;
	    }
	}
	if (ch == -1) {
	    // error: premature eof
	    throw new EOFException("Premature EOF in font file header.");
	}
	ungetc(is,ch);
    
	boolean done = false;
	while(!done) {
	    while((ch = getc(is)) != '#' && ch != '}' && ch != -1) {
		//skip to #
		;
	    }
	    
	    if(ch == '#') {
		readStrokes((char)readInt(is),is);
	    } else {
		done = true;
	    }
	}
	//if(fontcwidth
	makeDefaultGlyph();
    }
    
    
//#ifndef ISMIDLET
    public VFont(File file) throws IOException {
	this(file.getName(),new InputStreamReader(makeis(file)));
    }
    
    public VFont(String name,File filename) throws IOException {
	this(name, new InputStreamReader(makeis(filename)));
    }

    
//#ifdef USEHERSHEY
    public VFont(String name,HersheyFont hf) {
	fontname = name;
	
	int miny = hf.characterSetMinY;
	int maxy = hf.characterSetMaxY;

	fontheight = maxy - miny;
	fontcwidth = 0;
	fontcapline = fontxline = fontthickness = 0;
	
	for(int i=0; i<hf.charactersInSet; ++i) {
	    char thechar = (char)(' ' + i);
	    StringBuffer all = new StringBuffer();
	    StringBuffer sb = new StringBuffer();
	    int minx = hf.characterMinX[i];
	    int maxx = hf.characterMaxX[i];
	    int cw = maxx - minx;
	    
	    // Remind widest character
	    if(cw > fontcwidth)
		fontcwidth = cw;
	    
	    char n = 0;
	    int hfnpts = hf.numberOfPoints[i];
	    for(int k=1; k<hfnpts; ++k) {
		char[] xvals = hf.characterVectors[i][HersheyFont.X];
		char[] yvals = hf.characterVectors[i][HersheyFont.Y];
		
		if(xvals[k] == (int)' ') {
		    if(n > 0) {
			if(n > 2)
			    all.append('V').append(n).append(sb.toString());
			else
			    all.append('v').append(sb.toString());
			n = 0;
			sb = new StringBuffer();
		    }
		} else {
		    sb.append((char)(((int)xvals[k])-minx));
		    sb.append((char)(fontheight-(((int)yvals[k])-miny)));
		    ++n;
		}
	    }
	    if(n > 0) {
		if(n > 2)
		    all.append('V').append((char)n).append(sb.toString());
		else
		    all.append('v').append(sb.toString());
	    }
	    setGlyph(thechar,new Glyph(thechar,cw, all.toString().toCharArray()));
	}
	makeDefaultGlyph();
    }
//#endif


    public int getFontBaseLine() {
	return fontbaseline;
    }
    

    public int getFontCapLine() {
	return fontcapline;
    }
    

    public int getFontCharWidth() {
	return fontcwidth;
    }
    

    public int getFontHeight() {
	return fontheight;
    }

    public int getFontXLine() {
	return  fontxline;
    }


    public void setFontBaseLine(int v) {
	fontbaseline = v;
    }
    

    public void setFontCapLine(int v) {
	fontcapline = v;
    }
    

    public void setFontCharWidth(int v) {
	fontcwidth = v;
    }
    

    public void setFontHeight(int v) {
	fontheight = v;
    }


    public void setFontXLine(int v) {
	fontxline = v;
    }


    public void setDefaultGlyph(Glyph glyph) {
	// Silently ignore null default glyph!
	if(glyph != null)
	    defaultglyph = glyph;
    }
//#endif

    public String getName() {
	return fontname;
    }
    

    public int getHeight() {
	return round(scaling);
    }
    
    public void setGlyph(char thechar,Glyph glyph) {
	// Tricky: make sure the Glyph really describes the character for
	// which it is stored!
	glyph.ch = thechar;
	if(thechar >= 0 && thechar < 256)
	    isoglyphs[thechar] = glyph;
	else {
	    glyphtab.put(new Character(thechar),glyph);
	}
    }
    

    public Glyph getDefaultGlyph() {
	return defaultglyph;
    }
    

    public Glyph getGlyph(char thechar) {
	Glyph glyph = null;
	
	if(thechar >= 0 && thechar < 256)
	    glyph = isoglyphs[thechar];
	else {
	    glyph = (Glyph)glyphtab.get(new Character(thechar));
	}
	if(glyph == null)
	    glyph = defaultglyph;
	//System.out.println("thechar="+thechar+", glyph="+glyph);
	//glyph.printOn(System.out);
	return glyph;
    }
	    

    public int glyphWidth(char thechar) {
	return getGlyph(thechar).getWidth();
    }


    public void drawString(String text, int xc, int yc,
			   Rectangle r,Graphics g) {
	drawString(text,xc,yc,new DrawParams(BOTTOM|LEFT,1.0,1.0,.0,.0),r,g);
    }
    

    public void drawString(String text, int xc, int yc,int anchor,
			   Rectangle r,Graphics g) {
	drawString(text,xc,yc,new DrawParams(anchor,1.0,1.0,.0,.0),r,g);
    }
    
    
    public void drawString(String text, int xc, int yc, int anchor,
			   double width, double height,
			   Rectangle r, Graphics g) {
	drawString(text,xc,yc,new DrawParams(anchor,width,height,.0,.0),r,g);
    }
    

    public void drawString(String text, int xc, int yc, int anchor,
			   double width, double height, double theta, double slant,
			   Rectangle r, Graphics g) {
	drawString(text,xc,yc,new DrawParams(anchor,width,height,theta,slant),r,g);
    }


    public void drawString(String text,int xc,int yc,DrawParams dp,
			   Rectangle r,Graphics g) {
    
	// starting position
	int xp = xc, yp = yc;
	// set the position to do all rotations about
	int rotpx = xc, rotpy = yc;
	// set the flag to true if the angle is not 0.0
	double verticalOffsetFactor = .0;
	
	// if we are to do a rotation
	dp.setup(fontheight);
	
	// if we are not going to actually draw the string
	if(r != null) {
	    // set up to initialize the bounding rectangle
	    r.x = r.width = xp;
	    r.y = r.height = yp;
	}
	
	if((dp.anchor & BASELINE) == BASELINE) {
	    verticalOffsetFactor = .0f;
	    yp -= round(dp.yscale/*dp.height*/ * fontbaseline);
	} else {
	    if((dp.anchor & TOP) == TOP) {
		verticalOffsetFactor = 1.0;
	    }
	    if((dp.anchor & VCENTER) == VCENTER) {
		verticalOffsetFactor = 0.5;
	    }
	    if((dp.anchor & BOTTOM) == BOTTOM) {
		verticalOffsetFactor = .0;
	    }
	    // move the y position based on the vertical alignment
	    yp -= round(verticalOffsetFactor * dp.yscale/*dp.height*/ * fontheight);
	}
	
	char[] chars = text.toCharArray();
	if(chars.length > 0) {
	    // if we have a non-standard horizontal alignment
	    if((dp.anchor & LEFT) != LEFT) {
		// find the length of the string in pixels ...
		int len = 0;
		
		for(int j = 0; j < chars.length; j++) {
		    // the character's number in the array ...
		    len += glyphWidth(chars[j]);
		}
		len = round(dp.xscale/*dp.width*/ * len);
		
		// if we are center aligned
		if((dp.anchor & HCENTER) == HCENTER) {
		    // move the starting point half to the left
		    xp -= len / 2;
		} else {
		    // alignment is right, move the start all the way to the left
		    xp -= len;
		}
	    }
	
	    // loop through each character in the string ...
	    for(int j = 0; j < chars.length; j++) {
		// advance the starting coordinate
		xp += getGlyph(chars[j]).draw(xp, yp, rotpx, rotpy, dp, r, g);
	    }
	}
	   
	// Correct rectangle dimension
	if(r != null) {
	    r.width = r.width - r.x + 1;
	    r.height = r.height - r.y + 1;
	}
    }


    public Rectangle extent(String text, int xc, int yc,int anchor,Rectangle r) {
	return extent(text,xc,yc,new DrawParams(anchor,1.0,1.0,.0,.0),r);
    }
    
    
    public Rectangle extent(String text, int xc, int yc, int anchor,
		       double width, double height, Rectangle r) {
	return extent(text,xc,yc,new DrawParams(anchor,width,height,.0,.0),r);
    }
    

    public Rectangle extent(String text, int xc, int yc, int anchor,
			    double width, double height,
			    double theta, double slant,Rectangle r) {
	return extent(text,xc,yc,new DrawParams(anchor,width,height,theta,slant),r);
    }
    

    public Rectangle extent(String text, int xc, int yc,DrawParams dp,Rectangle r) {
	if(r == null)
	    r = new Rectangle();
	drawString(text,xc,yc,dp,r,null);
	return r;
    }
    

//#ifndef ISMIDLET
    public int getNumberOfGlyphs() {
	int numglyphs = 0;
	
	for(int i=0; i<isoglyphs.length; ++i) {
	    if(isoglyphs[i] != null)
		++numglyphs;
	}
	Enumeration e = glyphtab.elements();
	while(e.hasMoreElements()) {
	    e.nextElement();
	    ++numglyphs;
	}
	return numglyphs;
    }
	

    public boolean hasGlyph(char thechar) {
	if(thechar >= 0 && thechar < 256)
	    return isoglyphs[thechar] != null;
	return null != glyphtab.get(new Character(thechar));
    }


    public PrintStream storeOn(PrintStream s) {
	StringBuffer sb = new StringBuffer();
	
	sb.append("AX\n{\n");
	// number of glyphs
	sb.append("\tn ").append(getNumberOfGlyphs()).append('\n');
	// font height
	sb.append("\th ").append(fontheight).append('\n');
	// font baseline position
	sb.append("\tb ").append(fontbaseline).append('\n');
	// font Xline
	sb.append("\tx ").append(fontxline).append('\n');
	// font capline
	sb.append("\tc ").append(fontcapline).append('\n');
	// font character width (average??)
	sb.append("\tw ").append(fontcwidth).append('\n');
	s.print(sb.toString());
	for(int i=0; i<isoglyphs.length; ++i) {
	    if(isoglyphs[i] != null) {
		s.print('\t');
		isoglyphs[i].storeOn(s).println();
	    }
	}
	Enumeration e = glyphtab.elements();
	while(e.hasMoreElements()) {
	    s.print('\t');
	    ((Glyph)e.nextElement()).storeOn(s).println();
	}
	s.println("}");
	return s;
    }
//#endif


    protected void makeDefaultGlyph() {
	StringBuffer sb = new StringBuffer();
	int w = fontcwidth/2;
	
	// make sure character has some extent...
	if(w < 3) w = 3;

	// generate a glyph that is a box
	sb.append('V').append((char)5);
	sb.append((char)1).append((char)1);
	sb.append((char)(w-2)).append((char)1);
	sb.append((char)(w-2)).append((char)(fontheight-2));
	sb.append((char)(1)).append((char)(fontheight-2));
	sb.append((char)1).append((char)1);
	defaultglyph = new Glyph((char)0,w, sb.toString().toCharArray());
    }

	    
    protected int getc(InputStreamReader is) throws IOException {
	int ch;
	
	if(is.markSupported()) {
	    is.mark(1);
	    ch = lastchar = is.read();
	} else {
	    if(pushback) {
		pushback = false;
		ch = lastchar;
	    } else {
		ch = lastchar = is.read();
	    }
	}
	return ch;
    }

    protected void ungetc(InputStreamReader is,int ch) throws IOException {
	if(is.markSupported())
	    is.reset();
	else {
	    pushback = true;
	    lastchar = ch;
	}
    }
    
    protected void skipWS(InputStreamReader is) throws IOException {
	// skip whitespace
	int ch;
	do {
	    ch = getc(is);
	} while(ch == ' ' || ch == '\t' || ch == '\n');
	if(ch != ' ' && ch != '\t' && ch != '\n' && ch != -1)
	    ungetc(is,ch);
    }
    
    protected int readInt(InputStreamReader is) throws IOException {
	int ch;
	int val = 0;
	boolean neg = false;
	int numdigits = 0;

	skipWS(is);
	ch = getc(is);
	if(ch == '+')
	    ch = getc(is);
	else if(ch == '-') {
	    neg = true;
	    ch = getc(is);
	}
	while(ch != -1 && Character.isDigit((char)ch)) {
	    ++numdigits;
	    val = 10 * val + Character.digit((char)ch,10);
	    ch = getc(is);
	}
	if(neg)
	    val = -val;
	if(ch != -1 && numdigits > 0)
	    ungetc(is,ch);
	return val;
    }
    
    
    protected void readStrokes(char thechar,InputStreamReader is)
	throws IOException {
	StringBuffer sb = new StringBuffer();
	int i = 0;
	int c;
	int cwidth = fontcwidth;
	int midline = 0;
	
	while ((c = getc(is)) != ';' && c != '}' && c != -1) {
	    switch (c) {
	      case 'w':
		// character width
		cwidth = readInt(is);
		break;
	      case 'm':
		// font character centerline/midline
		midline = readInt(is);
		break;
	      case 'l':
	      case 'v':
		// draw lline (with thickness)
		sb.append((char)c);	/* insert the opcode */
		/* read first argument */
		sb.append((char)readInt(is));
		sb.append((char)readInt(is));
		/* read second argument */
		sb.append((char)readInt(is));
		sb.append((char)readInt(is));
		break;
	      case 'V':
	      case 'p':
	      case 'P':
	      case 'B':
		sb.append((char)c);	/* insert the opcode */
		i = readInt(is);
		sb.append((char)i);
		for (int j = 0; j < i; j++) {
		    sb.append((char)readInt(is));
		    sb.append((char)readInt(is));
		}
		break;
	      case ' ':		/* space between input chars, ignore */
	      case '\t':
	      default:
		break;
	    }
	}
	if (c == -1 || c == '}')
	    throw new IOException("EOF or format error in font file.");
	setGlyph(thechar,new Glyph(thechar,cwidth,
				   midline,sb.toString().toCharArray()));
    }
    

    private static InputStream checkis(InputStream is,String name)
	throws IOException {
	if(is == null)
	    throw new IOException("Font '"+name+"' not found.");
	return is;
    }
	
	    
//#ifndef ISMIDLET
    private static InputStream makeis(File file) throws IOException {
	return checkis(new FileInputStream(file),file.getCanonicalPath());
    }
//#endif


    private static int round(double x) {
	return (int)Math.floor(.5+x);
    }
    
    protected String fontname;
    protected int fontheight = 1;
    protected int fontbaseline = 0;
    protected int fontcwidth = 0;
    protected int fontcapline = 0;	    // unused
    protected int fontxline = 0;	    // unused
    protected int fontthickness = 0; 	    // unused

    protected Hashtable glyphtab = new Hashtable();
    protected Glyph[] isoglyphs = new Glyph[256];
    protected Glyph defaultglyph;
    protected boolean pushback = false;
    protected int lastchar = 0;
}
