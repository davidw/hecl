/*
 * Copyright (C) 2005, 2006 data2c GmbH (www.data2c.com)
 *
 * Author: Wolfgang S. Kechel - wolfgang.kechel@data2c.com
 */

package org.graphics;

//#ifdef j2se
import java.awt.Graphics;
import java.awt.Point;
//#else
import javax.microedition.lcdui.Graphics;
import org.awt.Point;
//#endif

import java.util.Hashtable;

public class Draw {
    public static void drawPolygon(Graphics g,int n,Point[] points,boolean filled) {
	if(n < 2)
	    return;
	if(filled) {
	    fillPolygon(g,n,points,true);
	}
	for(int i=0, j=1; j<n; ++i, ++j) {
	    g.drawLine(points[i].x,points[i].y,points[j].x,points[j].y);
	}
	g.drawLine(points[n-1].x,points[n-1].y,points[0].x,points[0].y);
    }
    

    public static void translate(Graphics g,int x,int y) {
//#ifdef j2se
	// Is this really not cumulative??????
	g.translate(x,y);
//#else
	g.translate(x-g.getTranslateX(),y-g.getTranslateY());
//#endif
    }


    public static void translate(Graphics g,Point p) {
	translate(g,p.x,p.y);
    }


    protected static void fillSpans(Graphics g,int count,Point[] points,int[] width) {
	//System.err.println("drawing "+count+" spans");
	for(int i=0; i<count; ++i) {
	    int len = width[i];
	    
	    if(len > 0) {
		int x = points[i].x;
		int y = points[i].y;
		
//#ifdef notdef
		int last = x+len;
		// clip if completely outside
		if(y < 0 || y >= h || x >= w || last < 0)
		    continue;
		drawSpan(y,x,last,'*');
//#else
		g.drawLine(x,y,x+len,y);
//#endif
	    }
	}
    }


    protected static void fillPolygon(Graphics g,int count,Point[] ptsIn,
				      boolean evenoddrule) {
	if(count < 3)
	    return;
//#ifndef j2se
	if(count == 3) {
	    g.fillTriangle(ptsIn[0].x,ptsIn[0].y,
			   ptsIn[1].x,ptsIn[1].y,ptsIn[2].x,ptsIn[2].y);
	    return;
	}
//#endif

//#ifdef notdef
	// Desktop code, does not work for J2ME
	// we currently use our own code!
	Polygon poly = new Polygon();
	
	for(int i=0; i<count; ++i)
	    poly.addPoint(ptsIn[i]);
	g.fillPolygon(poly);
//#endif

	//System.err.println("building edgetable...");
	EdgeTable ET = new EdgeTable(count,ptsIn);
	//System.err.println("edgetable done!");
	
	ScanLineList pSLL = ET.scanlines.next;
	EdgeTableEntry pPrevAET = null;
	EdgeTableEntry pAET = null;
	
	Point[] ptsOut = new Point[NUMPTSTOBUFFER];
	int[] width = new int[NUMPTSTOBUFFER];
	int nPts = 0;
	
	
	if(evenoddrule) {
	    /*
	     *  for each scanline
	     */
	    for (int y = ET.ymin; y < ET.ymax; y++) {
		//System.err.println("etmin="+ET.ymin+", etmax="+ET.ymax+", y="+y);
		
		/*
		 *  Add a new edge to the active edge table when we
		 *  get to the next edge.
		 */
		if (pSLL != null && y == pSLL.scanline) {
		    //System.err.println("loading AET...");
		    ET.loadAET(pSLL.edgelist);
		    pSLL = pSLL.next;
		    //System.err.println("AET loaded!");
		}
		pPrevAET = ET.aet;
		pAET = ET.aet.next;
		
		/*
		 *  for each active edge
		 */
		while (pAET != null) {
		    if(ptsOut[nPts] == null)
			    ptsOut[nPts] = new Point(pAET.bres.minor,y);
		    else
			ptsOut[nPts].move(pAET.bres.minor,y);
		    width[nPts] = pAET.next.bres.minor - pAET.bres.minor;
		    //System.err.println("Point["+nPts+"]=(" + ptsOut[nPts].x+", "+ptsOut[nPts].y + "), w="+width[nPts]);
		    nPts++;
		    
		    /*
		     *  send out the buffer when its full
		     */
		    if (nPts == NUMPTSTOBUFFER) {
			fillSpans(g,nPts, ptsOut, width);
			nPts = 0;
		    }
		    
		    if (pAET.ymax == y) {
			/* leaving this edge */
			pPrevAET.next = pAET.next;
			pAET = pPrevAET.next;
			if (pAET != null)
			    pAET.back = pPrevAET;
		    } else {
			pAET.bres.increment();
			pPrevAET = pAET;
			pAET = pAET.next;
		    }
		    if (pAET.ymax == y) {
			/* leaving this edge */
			pPrevAET.next = pAET.next;
			pAET = pPrevAET.next;
			if (pAET != null)
			    pAET.back = pPrevAET;
		    } else {
			pAET.bres.increment();
			pPrevAET = pAET;
			pAET = pAET.next;
		    }
		}
		//System.err.println("insertionSort...");
		ET.insertionSort();
	    }
	} else {
	    // default to windingrule
	    boolean fixWAET = false;
	    EdgeTableEntry pWETE = null;
	    
	    // for each scanline
	    for (int y = ET.ymin; y < ET.ymax; y++) {
		/*
		 *  Add a new edge to the active edge table when we
		 *  get to the next edge.
		 */
		if (pSLL != null && y == pSLL.scanline) {
		    ET.loadAET(pSLL.edgelist);
		    ET.computeWAET();
		    pSLL = pSLL.next;
		}
		pPrevAET = ET.aet;
		pAET = ET.aet.next;
		pWETE = pAET;
		
		// for each active edge
		while (pAET != null) {
		    /*
		     *  if the next edge in the active edge table is
		     *  also the next edge in the winding active edge
		     *  table.
		     */
		    if (pWETE == pAET) {
			if(ptsOut[nPts] == null)
			    ptsOut[nPts] = new Point(pAET.bres.minor,y);
			else
			    ptsOut[nPts].move(pAET.bres.minor,y);
			width[nPts] = pAET.nextWETE.bres.minor - pAET.bres.minor;
			nPts++;
			
			// send out the buffer
			if (nPts == NUMPTSTOBUFFER) {
			    fillSpans(g,nPts,ptsOut,width);
			    nPts = 0;
			}
			
			pWETE = pWETE.nextWETE;
			while (pWETE != pAET) {
			    if (pAET.ymax == y) {
				// leaving this edge
				pPrevAET.next = pAET.next;
				pAET = pPrevAET.next;
				fixWAET = true;
				if (pAET != null)
				    pAET.back = pPrevAET;
			    } else {
				pAET.bres.increment();
				pPrevAET = pAET;
				pAET = pAET.next;
			     }
			}
			pWETE = pWETE.nextWETE;
		    }
		    while (pWETE != pAET) {
			if (pAET.ymax == y) {
			    // leaving this edge
			    pPrevAET.next = pAET.next;
			    pAET = pPrevAET.next;
			    fixWAET = true;
			    if (pAET != null)
				pAET.back = pPrevAET;
			} else {
			    pAET.bres.increment();
			    pPrevAET = pAET;
			    pAET = pAET.next;
			}
		    }
		}

		/*
		 *  reevaluate the Winding active edge table if we
		 *  just had to resort it or if we just exited an edge.
		 */
		if (ET.insertionSort() || fixWAET) {
		    ET.computeWAET();
		    fixWAET = false;
		}
	    }
	}
	/* Get any spans that we missed by buffering */
	fillSpans(g,nPts, ptsOut, width);
    }
	

    static class BresInfo {
	/*
	 *  In scan converting polygons, we want to choose those pixels
	 *  which are inside the polygon.  Thus, we add .5 to the starting
	 *  x coordinate for both left and right edges.  Now we choose the
	 *  first pixel which is inside the pgon for the left edge and the
	 *  first pixel which is outside the pgon for the right edge.
	 *  Draw the left pixel, but not the right.
	 *
	 *  How to add .5 to the starting x coordinate:
	 *      If the edge is moving to the right, then subtract dy from the
	 *  error term from the general form of the algorithm.
	 *      If the edge is moving to the left, then add dy to the error term.
	 *
	 *  The reason for the difference between edges moving to the left
	 *  and edges moving to the right is simple:  If an edge is moving
	 *  to the right, then we want the algorithm to flip immediately.
	 *  If it is moving to the left, then we don't want it to flip until
	 *  we traverse an entire pixel.
	 */
	BresInfo() {
	    minor = Integer.MIN_VALUE;
	    d = m = m1 = incr1 = incr2 = 0;
	}
	
	BresInfo(int dy,int x1,int x2) {
	    /*
	     *  if the edge is horizontal, then it is ignored
	     *  and assumed not to be processed.  Otherwise, do this stuff.
	     */
	    if (dy != 0) {
		minor = x1;
		int dx = x2 - minor;
		if (dx < 0) {
		    m = dx / dy;
		    m1 = m - 1;
		    incr1 = -2 * dx + 2 * dy * m1;
		    incr2 = -2 * dx + 2 * dy * m;
		    d = 2 * m * dy - 2 * dx - 2 * dy;
		} else {
		    m = dx / dy;
		    m1 = m + 1;
		    incr1 = 2 * dx - 2 * dy * m1;
		    incr2 = 2 * dx - 2 * dy * m;
		    d = -2 * m * dy + 2 * dx;
		}
	    }
	}
	
	void increment() {
	    if (m1 > 0) {
		if (d > 0) {
		    minor += m1;
		    d += incr1;
		} else {
		    minor += m;
		    d += incr2;
		}
	    } else {
		if (d >= 0) {
		    minor += m1;
		    d += incr1;
		} else {
		    minor += m;
		    d += incr2;
		}
	    }
	}
	
	int minor;			    /* minor axis        */
	int d;				    /* decision variable */ 
	int m, m1;			    /* slope and slope+1 */
	int incr1, incr2;		    /* error increments */
    }


    static class EdgeTableEntry {
	EdgeTableEntry() {
	    ymax = -1;
	    next = back = null;
	    nextWETE = null;
	    ClockWise = false;
	    bres = new BresInfo();
	}
	EdgeTableEntry(int y,int dy,int topx,int bottomx,boolean clockwise) {
	    ymax = y;
	    bres = new BresInfo(dy,topx,bottomx);
	    next = back = null;
	    nextWETE = null;
	    ClockWise = clockwise;
	}
	
	int ymax;		      /* ycoord at which we exit this edge. */
	BresInfo bres;		      /* Bresenham info to run the edge     */
	EdgeTableEntry next;	      /* next in the list     */
	EdgeTableEntry back;	      /* for insertion sort   */
	EdgeTableEntry nextWETE;      /* for winding num rule */
	boolean ClockWise;	      /* flag for winding number rule       */
    }

    static class ScanLineList{
	ScanLineList(int y,ScanLineList xnext) {
	    scanline = y;
	    edgelist = null;
	    next = xnext;
	}
	
	int scanline;			    /* the scanline represented */
	EdgeTableEntry edgelist;	    /* header node              */
	ScanLineList next;		    /* next in the list       */
    };
   
    static class EdgeTable {
	public EdgeTable(int count,Point[] ptsIn) {
	    ymax = Integer.MIN_VALUE;
	    ymin = Integer.MAX_VALUE;
	    aet = new EdgeTableEntry();
	    // create head of scanlinelist with dummy entry
	    scanlines = new ScanLineList(0,null);

	    Point bottom = null;
	    Point top = null;
	    boolean clockwise = false;

	    for(int currpt=0, prevpt = count-1;
		currpt < count; ++currpt) {
		
		if (ptsIn[prevpt].y > ptsIn[currpt].y) {
		    bottom = ptsIn[prevpt];
		    top = ptsIn[currpt];
		    clockwise = false;
		} else {
		    bottom = ptsIn[currpt];
		    top = ptsIn[prevpt];
		    clockwise = true;
		}
		
		/*
		 * don't add horizontal edges to the Edge table.
		 */
		if (bottom.y != top.y) {
		    /*
		     *  initialize integer edge algorithm
		     */
		    int dy = bottom.y - top.y;
		    /* -1 for y so we don't get last scanline */
		    insertEdge(new EdgeTableEntry(bottom.y-1,
						  dy,top.x,bottom.x,
						  clockwise),
			       top.y);
		    if(ptsIn[prevpt].y > ymax)
			ymax = ptsIn[prevpt].y;
		    if(ptsIn[prevpt].y < ymin)
			ymin = ptsIn[prevpt].y;
		}
		prevpt = currpt;
	    }
	}

	void insertEdge(EdgeTableEntry ETE,int scanline) {
	    /*
	     * find the right bucket to put the edge into
	     */
	    ScanLineList pPrevSLL = scanlines;
	    ScanLineList pSLL = pPrevSLL.next;
	    while (pSLL != null && (pSLL.scanline < scanline)) {
		pPrevSLL = pSLL;
		pSLL = pSLL.next;
	    }

	    /*
	     * reassign pSLL (pointer to ScanLineList) if necessary
	     */
	    if (pSLL == null || pSLL.scanline > scanline) {
		pSLL = new ScanLineList(scanline,pPrevSLL.next);
		pPrevSLL.next = pSLL;
	    }
	    //pSLL.scanline = scanline;

	    /*
	     * now insert the edge in the right bucket
	     */
	    EdgeTableEntry prev = null;
	    EdgeTableEntry start = pSLL.edgelist;
	    //System.err.println("adding edge to bucket");
	    while (start != null && (start.bres.minor < ETE.bres.minor)) {
		prev = start;
		start = start.next;
	    }
	    ETE.next = start;
	    
	    if (prev != null)
		prev.next = ETE;
	    else
		pSLL.edgelist = ETE;
	}
	
	
	/*
	 *     loadAET
	 *
	 *     This routine moves EdgeTableEntries from the
	 *     EdgeTable into the Active Edge Table,
	 *     leaving them sorted by smaller x coordinate.
	 */
	void loadAET(EdgeTableEntry ETEs) {
	    EdgeTableEntry pPrevAET = aet;
	    EdgeTableEntry AET = aet.next;
	    while (ETEs != null) {
		while (AET != null && (AET.bres.minor < ETEs.bres.minor)) {
		    pPrevAET = AET;
		    AET = AET.next;
		}
		EdgeTableEntry tmp = ETEs.next;
		ETEs.next = AET;
		if (AET != null)
		    AET.back = ETEs;
		ETEs.back = pPrevAET;
		pPrevAET.next = ETEs;
		pPrevAET = ETEs;
		ETEs = tmp;
	    }
	}


	/*
	 * computeWAET
	 *
	 * This routine links the AET by the
	 * nextWETE (winding EdgeTableEntry) link for
	 * use by the winding number rule.  The final 
	 * Active Edge Table (AET) might look something
	 * like:
	 *
	 * AET
	 * ----------  ---------   ---------
	 * |ymax    |  |ymax    |  |ymax    | 
	 * | ...    |  |...     |  |...     |
	 * |next    |->|next    |->|next    |->...
	 * |nextWETE|  |nextWETE|  |nextWETE|
	 * ---------   ---------   ^--------
	 *     |                   |       |
	 *     V------------------->       V---> ...
	 *
	 */
	void computeWAET() {
	    boolean inside = true;
	    int isInside = 0;
	    EdgeTableEntry AET = aet;
	    AET.nextWETE = null;
	    EdgeTableEntry pWETE = aet;
	    AET = AET.next;
	    while (AET != null) {
		if (AET.ClockWise)
		    isInside++;
		else
		    isInside--;
		
		if ((!inside && isInside == 0) || (inside && isInside != 0)) {
		    pWETE.nextWETE = AET;
		    pWETE = AET;
		    inside = !inside;
		}
		AET = AET.next;
	    }
	    pWETE.nextWETE = null;
	}

	/*
	 * InsertionSort
	 *
	 * Just a simple insertion sort using
	 * pointers and back pointers to sort the Active
	 * Edge Table.
	 *
	 */
	boolean insertionSort() {
	    EdgeTableEntry pETEchase;
	    EdgeTableEntry pETEinsert;
	    EdgeTableEntry pETEchaseBackTMP;
	    boolean changed = false;
	    
	    
	    for(EdgeTableEntry AET = aet.next; AET != null; ) {
		pETEinsert = AET;
		pETEchase = AET;
		while (pETEchase.back.bres.minor > AET.bres.minor)
		    pETEchase = pETEchase.back;
		
		AET = AET.next;
		if (pETEchase != pETEinsert) {
		    pETEchaseBackTMP = pETEchase.back;
		    pETEinsert.back.next = AET;
		    if (AET != null)
			AET.back = pETEinsert.back;
		    pETEinsert.next = pETEchase;
		    pETEchase.back.next = pETEinsert;
		    pETEchase.back = pETEinsert;
		    pETEinsert.back = pETEchaseBackTMP;
		    changed = true;
		}
	    }
	    return changed;
	}


	int ymax;			    /* ymax for the polygon     */
	int ymin;			    /* ymin for the polygon     */
	ScanLineList scanlines;		    /* header node              */
	EdgeTableEntry aet;
    }

    private static final int NUMPTSTOBUFFER = 100;
  
}
