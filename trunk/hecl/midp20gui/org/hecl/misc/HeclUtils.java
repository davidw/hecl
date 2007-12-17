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

package org.hecl.misc;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Vector;

import org.hecl.Command;
import org.hecl.DoubleThing;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.Thing;

//#ifdef j2se
import java.awt.geom.Point2D;
import java.awt.Point;
import java.awt.Dimension;
//#else
import org.awt.geom.Point2D;
import org.awt.Point;
import org.awt.Dimension;
//#endif

public class HeclUtils {
    public static int testArguments(Thing[] argv,int min,int max) {
	int len = argv.length;
	if(min >= 0 && len < min) {
	    return -1;
	}
	if(max >= 0 && len > max) {
	    return 1;
	}
	return 0;
    }


    public static Point2D thing2Point(Thing[] argv,int pos)
	throws HeclException {
	return getPoint(null,argv,pos);
    }
    
    public static Point2D getPoint(Point2D p,Thing[] argv,int pos)
	throws HeclException {
	if(p == null)
	    p = new Point2D.Double();
	Vector v = ListThing.get(argv[pos]);
	if(v == null || v.size() != 2)
	    throw new HeclException("Bad point '"+argv[pos]+"'.");
	
	p.setLocation(DoubleThing.get((Thing)v.elementAt(0)),
		      DoubleThing.get((Thing)v.elementAt(1)));
	return p;
    }
    
    public static Dimension thing2Dimension(Thing[] argv,int pos)
	throws HeclException {
	return getDimension(new Dimension(),argv,pos);
    }
    
    public static Dimension getDimension(Dimension dim,Thing[] argv,int pos)
	throws HeclException {
	Vector v = ListThing.get(argv[pos]);
	if(v == null || v.size() != 2)
	    throw new HeclException("Bad dimension '"+argv[pos]+"'.");
	dim.setSize(IntThing.get((Thing)v.elementAt(0)),
		    IntThing.get((Thing)v.elementAt(1)));
	return dim;
    }

    public static int thing2int(Thing thing) throws HeclException {
	return thing2int(thing,true,0);
    }
    

    public static int thing2int(Thing thing,boolean usedefault,int defaultvalue)
	throws HeclException {
	try {
	    return IntThing.get(thing);
	}
	catch (HeclException e) {
	    if(usedefault)
		return defaultvalue;
	    throw e;
	}
    }
    

    public static int thing2len(Thing thing,int minlen)
	throws HeclException {
	int len = HeclUtils.thing2int(thing,true,minlen-1);
	if(len < minlen) {
	    throw new HeclException("Invalid length '"+thing.toString()+"'.");
	}
	return len;
    }
    
    
    public static boolean thing2bool(Thing thing)
	throws HeclException {
	String s = thing.toString();
	if(s.equals("true") || s.equals("on") || s.equals("1"))
	    return true;
	if(s.equals("false") || s.equals("off") || s.equals("0"))
	    return false;
	throw new HeclException("Invalid boolean value '"+s+"'.");
    }
    

    public static String readLine(InputStreamReader is) {
	StringBuffer b = new StringBuffer();
	int ch = -1;
	
	try {
	    while ((ch = is.read()) != -1) {
		if(ch == '\r')
		    continue;
		if(ch == '\n')
		    break;
		b.append((char)ch);
	    }
	}
	catch(IOException iox) {
	}
	if(b.length() > 0 || ch != -1)
	    return b.toString();
	return null;
    }


    public static InputStream getResourceAsStream(Class cl,String resname) {
	//System.err.println("getResourceAsStream("+cl+", "+resname);
	//InputStream s = cl.getResourceAsStream(resname);
	//System.err.println("\t-->"+s);
	//return s;
	return cl.getResourceAsStream(resname);
    }
    

    public static byte[] getResourceAsBytes(Class cl,String resname)
	throws IOException {
	DataInputStream is = new DataInputStream(getResourceAsStream(cl,resname));
	byte[] buf = new byte[512];
	int bytesread = 0;
	byte[] result = new byte[bytesread];
	int i = 0;
	int n = 0;
	
	while((n = is.read(buf,0,buf.length)) > 0) {
	    byte[] newres = new byte[n+bytesread];
	    for(i=0; i<bytesread; ++i) {
		newres[i] = result[i];
	    }
	    for(i=0; i<n; ++i, ++bytesread) {
		newres[bytesread] = buf[i];
	    }
	    result = newres;
	    System.gc();
	}
	is.close();
	return result;
    }

    
    public static String hexdump(byte[] buf) {
	StringBuffer sb = new StringBuffer();
	
	for(int i=0; i<buf.length; ++i) {
	    byte b = buf[i];
	    
	    sb.append(Integer.toHexString((b&0xf0)>>4));
	    sb.append(Integer.toHexString(b&0x0f));
	    sb.append(' ');
	    if(i!=0 && ((i+1)%8) == 0)
		sb.append(' ');
	    if(i != 0 && ((i+1)%16) == 0)
		sb.append('\n');
	}
	if(sb.charAt(sb.length()-1) != '\n')
	    sb.append('\n');
	return new String(sb);
    }
 

    public static String getResourceAsString(Class cl,String resname,String encoding)
	throws IOException {
	byte[] buf = getResourceAsBytes(cl,resname);
	//System.err.println(hexdump(buf));
	//System.err.println("------------------");
	//System.err.println(hexdump((new String(buf)).getBytes()));
	//System.err.println(hexdump((new String(buf,"iso8859-1")).getBytes("iso8859-1")));
	if(encoding != null)
	    return new String(buf,encoding);
	return new String(buf);
    }
    

    public static void readEvalPrint(Interp ip,
				     InputStream in, PrintStream out,
				     PrintStream err) {
	String prompt = PROMPT;
	StringBuffer sb = new StringBuffer();
	
	InputStreamReader reader = new InputStreamReader(in);
	while(true) {
	    byte outbytes[] = null;
	    out.print(prompt);
	    out.flush();

	    String line = readLine(reader);
		
	    if(line == null)
		break;
	    if(sb.length() > 0)
		sb.append('\n');
	    sb.append(line);
	    try {
		if(sb.length() <= 0)
		    continue;
		
		Thing res = ip.evalAsyncAndWait(new Thing(sb.toString()));
		if (res != null) {
		    String s = res.toString();
		    if(s.length() > 0) {
			// It seems that DataOutputStream.println(String)
			// is broken and returns OutOfmemory when the
			// string is to long, so we convert the string
			// into bytes and write out the pure bytes
			// directly.
			outbytes = s.getBytes();
		    }
		}
		sb.delete(0,sb.length());
		prompt = PROMPT;
	    }
	    catch(HeclException he) {
		if (he.code.equals("PARSE_ERROR")) {
		    // Change prompt and get more input
		    prompt = PROMPT2;
		} else {
		    sb.delete(0,sb.length());
		    he.printStackTrace();
		    outbytes = he.getMessage().getBytes();
		    prompt = PROMPT;
		}
	    }
	    if(outbytes != null) {
		// result output
		try {
		    out.write(outbytes);
		    out.println();
		}
		catch(IOException ioex) {
		    err.println(ioex.getMessage());
		    break;
		}
		outbytes = null;
	    }
	}
    }
    

    public static String PROMPT = "hecl> ";
    public static String PROMPT2 = "hecl+ ";
}
