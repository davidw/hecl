/* Copyright 2005-2006 by data2c.com

Authors:
Wolfgang S. Kechel - wolfgang.kechel@data2c.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.hecl.net;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.HashThing;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.Operator;
import org.hecl.RealThing;
import org.hecl.StringThing;
import org.hecl.Thing;

public class HttpCmd extends org.hecl.Operator {
    public Thing operate(int cmd, Interp interp, Thing[] argv)
	throws HeclException {
	Hashtable h = null;
	boolean validate = false;
	
	switch(cmd) {
	  case FORMATQUERY:
	    if((argv.length % 2) != 1)
		throw new HeclException("?key value? ...");
	    String[] keyvaluepairs = new String[argv.length-1];
	    for(int i=1; i<argv.length; ++i)
		keyvaluepairs[i-1] = argv[i].toString();
	    return new Thing(HttpRequest.urlencode(keyvaluepairs));
	    
	  case GETURL:
	    h = new Hashtable();
	    String qdata = null;
	    Thing t = null;
	    try {
		t = interp.getVar("http.useragent",0);
	    }
	    catch(HeclException e) {
	    }
	    h.put(useragentheader,t != null ? t.toString() : defuseragent);
	    if (deflocale != null) {
		h.put(contentlanguageheader, deflocale);
	    }

	    for(int i = 2; i<argv.length; i += 2) {
		String key = argv[i].toString();
		if(key.equals("-query")) {
		    qdata = argv[i+1].toString();
		} else if(key.equals("-validate")) {
		    validate = IntThing.get(argv[i+1]) != 0 ? true : false;
		} else if(key.equals("-binary")) {
		    // ignore
		} else if(key.equals("-headers")) {
		    Vector v = ListThing.get(argv[i+1]);
		    
		    int n = v.size();
		    for(i=0; i<n; i+= 2) {
			h.put(((Thing)v.elementAt(i)).toString(),
			      ((Thing)v.elementAt(i+1)).toString());
		    }
		} else {
		    throw new HeclException("Unknown option '"+key+"'.");
		}
	    }
	    HttpRequest r = new HttpRequest(argv[1].toString(), qdata,
					    validate, h);
	    r.start();

 	    try {
		Thread.sleep(1L);
	    } catch(Exception e) {}

	    // (Jan 2007, rene+wke: Nokia n70 yield bug - needs join on first call!
	    if(firsttime) {
		try {
		    r.join();
		    firsttime = false;
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    } else {
		while(r.isAlive()) {
		    interp.doOneEvent(Interp.ALL_EVENTS|Interp.DONT_WAIT);
		    // allow for other threads to do some work
		    Thread.currentThread().yield();
		}
	    }

	    int status = r.getStatus();
	    //System.err.println("status="+status);
	    if(status != HttpRequest.OK) {
		Exception e = r.getException();
		// wke 31.08.2006
		// need a stringbuffer here, otherwise error message is not
		// complete. May be a compiler problem.
		StringBuffer s = new StringBuffer();
		s.append("HTTP geturl failed '").append(HttpRequest.getStatusText(status))
		    .append("' - ").append(e != null ? e.toString() : "");
		System.err.println("msg="+s.toString());
		throw new HeclException(s.toString());
	    }
		
	    try {
		int retcode = r.getRC();
		Hashtable ht = new Hashtable();
		
		ht.put("status",new Thing(HttpRequest.getStatusText(status)));
		ht.put("ncode",IntThing.create(retcode));
		
		Enumeration e = r.getResponseFieldNames();
		while(e.hasMoreElements()) {
		    String key = (String)e.nextElement();
		    ht.put(key,new Thing(r.getResponseFieldValue(key)));
		}
		ht.put("data",new Thing(new StringThing(r.getBody())));
		return HashThing.create(ht);
	    }
	    catch (Exception e) {
		throw new HeclException(e.getMessage());
	    }

	  case GETDATA:
	    h = HashThing.get(argv[1]);
	    return (Thing)h.get("data");
	    
	  case GETNCODE:
	    h = HashThing.get(argv[1]);
	    return (Thing)h.get("ncode");
		
	  case GETSTATUS:
	    h = HashThing.get(argv[1]);
	    return (Thing)h.get("status");
		
	  default:
	    throw new HeclException("Unknown http command '"
				    + argv[0].toString() + "' with code '"
				    + cmd + "'.");
	}
    }
    

    public static void load(Interp ip) throws HeclException {
	Operator.load(ip,cmdtable);
    }


    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip,cmdtable);
    }


    protected HttpCmd(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }

    public static final int GETURL = 1;
    public static final int FORMATQUERY = 2;
    public static final int GETDATA = 3;
    public static final int GETNCODE = 4;
    public static final int GETSTATUS = 5;
    
    public static final String useragentheader = "User-Agent";
    public static final String contentlanguageheader = "Content-Language";

    public static final String defcharset = "ISO8859-1";
    private static String defuseragent = "Hecl http-module";
    private static String deflocale = "en-US";
    private static boolean firsttime = true;

    private static Hashtable cmdtable = new Hashtable();

    static {
//#ifdef j2me
	String conf = System.getProperty("microedition.configuration");
	String prof = System.getProperty("microedition.profiles");

	if (prof == null) {
	    prof = "none";
	}
	if (conf == null) {
	    conf = "none";
	}

	int space = prof.indexOf(' ');
	if (space != -1) {
	    prof = prof.substring(0, space -1);
	}
	defuseragent = "Profile/" + prof + " Configuration/" + conf;
	try {
	    deflocale = System.getProperty("microedition.locale");
	} catch (Exception ace) {
	    System.err.println("Access control exception: " + ace.toString());
	}
//#else
	// No special treatment for j2se at the moment.
//#endif 
	if(deflocale == null) {
	    deflocale = "en-US";
	}
	cmdtable.put("http.geturl", new HttpCmd(GETURL,1,-1));
        cmdtable.put("http.formatQuery", new HttpCmd(FORMATQUERY,0,-1));
        cmdtable.put("http.data", new HttpCmd(GETDATA,0,-1));
        cmdtable.put("http.ncode", new HttpCmd(GETNCODE,0,-1));
        cmdtable.put("http.status", new HttpCmd(GETSTATUS,0,-1));
	
    }
    
}
