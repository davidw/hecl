/* Copyright 2005-2006 by data2c.com

Authors:
Wolfgang S. Kechel - wolfgang.kechel@data2c.com
Jörn Marcks - joern.marcks@data2c.com

Wolfgang S. Kechel, Jörn Marcks

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

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

//#ifdef j2me
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
//#if cldc > 1.0
import javax.microedition.io.HttpsConnection;
//#endif
//#else
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
//#endif

public class HttpRequest extends Thread {
    static public final short SETUP = 0;
    static public final short CONNECTED = 1;
    static public final short ERROR = 2;
    static public final short TIMEOUT = 3;
    static public final short OK = 4;
    
//#ifdef j2me
    public static final int HTTP_UNAUTHORIZED = HttpConnection.HTTP_UNAUTHORIZED;
    public static final int HTTP_FORBIDDEN = HttpConnection.HTTP_FORBIDDEN;
//#else
    public static final int HTTP_UNAUTHORIZED = HttpURLConnection.HTTP_UNAUTHORIZED;
    public static final int HTTP_FORBIDDEN = HttpURLConnection.HTTP_FORBIDDEN;
    
    public static final String GET = "GET";
    public static final String POST ="POST";
    public static final String HEAD = "HEAD";
    public static final String OPTIONS = "OPTIONS";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String TRACE = "TRACE";
//#endif
    
    

    public HttpRequest(String url, QueryParam[] params,
		       boolean validate, Hashtable headerfields) {
	qparams = params;
	setup(url,validate,headerfields);
    }
    

    public HttpRequest(String url, String queryData,
		       boolean validate, Hashtable headerfields) {
	qdata = queryData;
	setup(url,validate,headerfields);
    }

    private void setup(String url, boolean validate, Hashtable headerfields) {
	urlstr = url;
	if(headerfields != null) {
	    Enumeration e = headerfields.keys();
	    while(e.hasMoreElements()) {
		String key = (String)e.nextElement();
		requestFields.put(key,(String)headerfields.get(key));
	    }
	}
	if(qdata != null || qparams != null) {
	    requestMethod =
//#ifdef j2me
		HttpConnection.POST
//#else
		POST
//#endif
		;
	} else {
	    if(validate)
		requestMethod =
//#ifdef j2me
		    HttpConnection.HEAD
//#else
		    HEAD
//#endif
		    ;
	}
    }
    
    // Add a header field with key and value when sending a request
    // Must be called before the request.
    public void addRequestField(String key, String value) {
	requestFields.put(key, value);
    }


    // Get the value of a key after the connection is closed.
    // Must be called after the request.
    public String getResponseFieldValue(String key) {
	return (String)responseFields.get(key);
    }


    public Enumeration getResponseFieldNames() {
	return responseFields.keys();
    }
    

    public String getURL() {
	return urlstr;
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
 

    public synchronized void run() {
	MyHttpConn co = null;
	rc = -1;
	error = null;
	inData = null;

	try {
	    co = new MyHttpConn(urlstr);
	}
	catch (IOException e) {
	    status = ERROR;
	    error = e;
	    e.printStackTrace();
	    System.err.println("HttpRequest.preparation error");
	    return;
	}


	try {
	    if(DEBUGRC)
		System.err.println("Connecting...");
	    co.connect(requestMethod,requestFields,qdata,qparams);
	    status = CONNECTED;
	    if(DEBUGRC)
		System.err.println("Connected");
	    rc = co.getResponseCode();
	    if(DEBUGRC)
		System.err.println("rc="+rc);
	    if(rc == -1) {
		status = ERROR;
		return;
	    }
	    responseFields = co.readHeader();
	    inData = co.readBody();
	    status = OK;
	    
	    // If no charset is given, use the default (see below).
	    String charset = DEFCHARSET;
	    String ct = (String)responseFields.get(CONTENTTYPE);
	    String coding = (String)responseFields.get(CONTENTENCODING);
	    
	    if(ct == null) {
		// should be present, but who nows...
		ct = "text/plain";
		responseFields.put(CONTENTTYPE,ct);
	    }
	    
	    if((ct != null && !ct.toLowerCase().startsWith("text"))
	       || (coding != null &&
		   (coding.indexOf("gzip") >= 0 || coding.indexOf("compress") >= 0))
		) {
		// binary transfer
		responseFields.put("binary","1");
	    } else {
		// textual transfer
		responseFields.put("binary","0");
		if(ct != null) {
		    int begin = ct.toLowerCase().indexOf("charset=");
		    if (begin >= 0) {
			// In a midlet, an empty encoding string would result
			// in an UnsupportedEncodingException when creating a
			// string object.
			begin += 8;	    // # of chars in 'charset='
			int end = ct.indexOf(';', begin);
			if (end == -1) {
			    end = ct.length();
			}
			charset = ct.substring(begin, end);
			//System.err.println("charset in reply="+charset);
		    }
		}
	    }
	    //System.err.println("charset now="+charset +", isocharset="+isISOCharset(charset));
	    
	    // charset is now detected, create a string holding the result.
	    if(charset == DEFCHARSET || isISOCharset(charset)) {
		//System.err.println("internal ISO 8859 decode");
		body = bytesToString(inData,0,inData.length);
		//System.err.println("ISO 8859 decode done");
		charset = DEFCHARSET;
		responseFields.put("charset",charset);
	    } else {
		for(int i=0; i<3; ++i) {
		    switch(i) {
		      case 0:
			break;
		      case 1:
			charset = charset.toLowerCase();
			break;
		      case 2:
			charset = charset.toUpperCase();
			break;
		    }
		    responseFields.put("charset",charset);
		    try {
			//System.err.println("trying to decode with charset="+charset);
			body = new String(inData,charset);
			//System.err.println("decode success");
			break;
		    }
		    catch(Exception e2) {
			body = "xxx-encoding-failed-xxx\n"+e2.getMessage();
		    }
		}
		//System.err.println("decode bytelen="+inData.length);
		//System.err.println("decode strlen="+body.length());
	    }
	}
	catch (Exception e) {
	    status = ERROR;
	    error = e;
	    e.printStackTrace();
	}
	if(DEBUGBODY)
	    System.err.println(getBody());
	if(co != null) {
	    co.close();
	}
	// no longer needed
	inData = null;
    }

    public static byte[] asISOBytes(String s) {
	byte[] buf = new byte[s.length()];
	for(int i=0; i<s.length(); ++i) {
	    char ch = s.charAt(i);
	    buf[i] = (byte)ch;
	}
	return buf;
    }
    
    public static String bytesToString(byte[] buf) {
	return bytesToString(buf,0,buf.length);
    }
    
    public static String bytesToString(byte[] buf,int start,int n) {
	return bytesToStringBuffer(buf,start,n).toString();
    }
    
    public static StringBuffer bytesToStringBuffer(byte[] buf) {
	return bytesToStringBuffer(buf,0,buf.length);
    }
    
    public static StringBuffer bytesToStringBuffer(byte[] buf,int start,int n) {
	StringBuffer sb = new StringBuffer(buf.length);
	//System.err.println("old data:");
	//System.err.println(hexdump(buf));
	for(int i=start; n>0; ++i, --n) {
	    sb.append((char)buf[i]);
	}
	//System.err.println("new data:");
	//System.err.println(hexdump(asISOBytes(sb.toString())));
	return sb;
    }
    
    public int getStatus() {
	return status;
    }
    

    public Exception getException() {
	return error;
    }
    

    public String getBody() {
	return body;
    }
    
//#ifdef notdef
    public byte[] getBytes() {
	return inData;
    }
//#endif

    public int getRC() {
	return rc;
    }


    public static String getStatusText(int status) {
	switch(status) {
	  case SETUP:
	    return "setup";
	  case CONNECTED:
	    return "connected";
	  case ERROR:
	    return "error";
	  case TIMEOUT:
	    return "timeout";
	  case OK:
	    return "ok";
	  default:
	    return "unknown";
	}
    }
    
    public static byte[] IRIencode(String str) {
	int strlen = str.length();
	int utflen = 0;
 	char[] charr = new char[strlen];
	int c, count = 0;
	
	str.getChars(0, strlen, charr, 0);
 
	for (int i = 0; i < strlen; i++) {
	    c = charr[i];
	    if ((c >= 0x0001) && (c <= 0x007F)) {
		utflen++;
	    } else if (c > 0x07FF) {
		utflen += 3;
	    } else {
		utflen += 2;
	    }
	}

	byte[] bytearr = new byte[utflen];
	for (int i = 0; i < strlen; i++) {
	    c = charr[i];
	    if ((c >= 0x0001) && (c <= 0x007F)) {
		bytearr[count++] = (byte)c;
	    } else if (c > 0x07FF) {
		bytearr[count++] = (byte)(0xE0 | ((c >> 12) & 0x0F));
		bytearr[count++] = (byte)(0x80 | ((c >>  6) & 0x3F));
		bytearr[count++] = (byte)(0x80 | ((c >>  0) & 0x3F));
	    } else {
		bytearr[count++] = (byte)(0xC0 | ((c >>  6) & 0x1F));
		bytearr[count++] = (byte)(0x80 | ((c >>  0) & 0x3F));
	    }
	}
	return bytearr;
    }

    public static String urlencode(byte[] s,int start, int n) {
	StringBuffer b = new StringBuffer();
	for(int i = start; n>0; ++i, --n) {
	    b.append(urlencodemap[s[i] & 0xff]);
	}
	return b.toString();
    }

    public static String urlencode(byte[] s) {
	return urlencode(s,0,s.length);
    }

    public static String urlencode(String[] elems) {
	if(elems == null || elems.length == 0)
	    return null;
	
	StringBuffer b = new StringBuffer();
	if(elems != null) {
	    for(int i = 0; i < elems.length; ++i) {
		if(i > 0) {
		    b.append((i % 2) != 0 ? '=' : '&');
		}
		b.append(urlencode(IRIencode(elems[i])));
	    }
	}
	return b.toString();
    }


    private boolean isISOCharset(String charset) {
	String lower = charset.toLowerCase();
	
	for(int i=0; i<ISOALIASES.length; ++i) {
	    if(lower.equals(ISOALIASES[i]))
		return true;
	}
	return false;
    }
    
    private String urlstr = null;
    private byte[] inData = null;
    private String body = "";
    private String qdata = null;
    private QueryParam[] qparams = null;
    private String requestMethod =
//#ifdef j2me
    HttpConnection.GET
//#else
    GET
//#endif
    ;
    private int rc = -1;
    private short status = SETUP;
    Exception error = null;
    private Hashtable requestFields = new Hashtable();
    private Hashtable responseFields = new Hashtable();

    private static String[] urlencodemap = new String[256];
    private static String validUrlChars =
    "-_.!~*'()\"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final char[] hexchars = (new String("0123456789ABCDEF")).toCharArray();
    public static boolean DEBUGURL = false;
    public static boolean DEBUGRC = false;
    public static boolean DEBUGBODY = false;
    public static String DEFCHARSET = "ISO8859-1";
    public static final String CONTENTTYPE = "content-type";
    public static final String CONTENTENCODING = "content-encoding";
    static private final String ISOALIASES[]= {
	"iso-8859-1","iso8859-1","iso8859_1","iso_8859_1","iso-8859_1","iso_8859-1"
    };
    
    static {
	char[] cbuf = new char[3];
	for(int i = 0; i < 256; i++) {
	    char ch = (char)i;
	    int idx = validUrlChars.indexOf(ch);
	    if(idx >= 0) {
		urlencodemap[i] = validUrlChars.substring(idx, idx + 1);
	    } else {
		// !!! Do not use 
		// urlencodemap[i] = "%" + Integer.toHexString(i);
		// since it does not print leading 0s
		cbuf[0] = '%';
		cbuf[1] = hexchars[(i&0xf0)>>4];
		cbuf[2] = hexchars[i&0x0f];
		urlencodemap[i] = new String(cbuf);
	    }
	}
	urlencodemap[' '] = "+";
	//urlencodemap['\n'] = "%0D%0A";
    }

}

class MyHttpConn {
    MyHttpConn(String url) throws IOException {
	is = null;
	os = null;
	conn = null;
//#if javaversion >= 1.5 || cldc > 1.0
	secure = url.toLowerCase().startsWith("https");
//#endif

	if(HttpRequest.DEBUGURL)
	    System.err.println("url="+url);
//#ifdef j2me
//#if cldc > 1.0
	conn = secure ?
	    (HttpsConnection)
	    Connector.open(url/*,Connector.READ_WRITE, true*/) :
	    (HttpConnection)
	    Connector.open(url/*,Connector.READ_WRITE, true*/);
//#else
	conn = (HttpConnection)
	    Connector.open(url/*,Connector.READ_WRITE, true*/);
//#endif
//#else
	URL myurl = new URL(url);
	conn = secure ? (HttpsURLConnection) myurl.openConnection() :
	    (HttpURLConnection)myurl.openConnection();
//#endif
    }


    void connect(String rm, Hashtable rfields,
			String qdata,QueryParam[] qparams)
	throws IOException {
	conn.setRequestMethod(rm);
	// Set the request fields.
	Enumeration e = rfields.keys();
	//System.err.println("--- REQUEST -------------------------------------");
	while (e.hasMoreElements()) {
	    String key = (String)e.nextElement();
	    //System.err.println("key: " + key + ", value: " + (String)rfields.get(key));
	    conn.setRequestProperty(key, (String)rfields.get(key));
	}

//#ifndef j2me
	if (qdata != null || qparams != null) {
	    conn.setDoOutput(true);
	}
	// Only JDK: Calling connect will open the connection
	conn.connect();
//#endif
	    
	if (qdata != null || qparams != null) {
//#ifdef j2me
	    os = conn.openOutputStream();
//#else
	    os = conn.getOutputStream();
//#endif
	    if(qdata != null) {
		//System.err.println("writing " + qdata.getBytes(DEFCHARSET).length + " bytes");
		os.write(qdata.getBytes(/*DEFCHARSET*/));
	    } else if (qparams != null) {
		for(int i=0; i<qparams.length; ++i) {
		    //System.err.print("qparams["+i+"]: ");
		    //qparams[i].printon(System.err).println("");
		    if(i != 0)
			os.write('&');
		    qparams[i].send(os);
		}
	    }
	    os.flush();
	    os.close();
	    os = null;
	}
    }


    int getResponseCode() {
	try {
	    // Only MIDP:
	    // Getting the response code will open the connection,
	    // send the request, and read the HTTP response headers.
	    // The headers are stored until requested.
	    return conn.getResponseCode();
	}
	catch(IOException e) {
	    e.printStackTrace();
	}
	return -1;
    }
	

    Hashtable readHeader() {
	Hashtable tab = new Hashtable();
	    
	// Getting the response fields.
	int idx = 0;
	// Some implementations may treat the 0th header field as special,
	// i.e. as the status line returned by the HTTP server.
	// In this case, getHeaderField(0) returns the status line,
	// but getHeaderFieldKey(0) returns null.
	// For now, it is not clear if this happens on midlets as well.
//#ifndef j2me
	if (conn.getHeaderFieldKey(0) == null) {
	    ++idx;
	}
//#endif
	//System.err.println("--- RESPONSE HEADER-----------------");
	String key = "";
	while (key != null) {
//#ifdef j2me
	    try {
//#endif
		key = conn.getHeaderFieldKey(idx++);
		if (key != null) {
		    tab.put(key.toLowerCase(), conn.getHeaderField(key));
		    //System.err.println("key: " + key + ", value: " + conn.getHeaderField(key));
		}
//#ifdef j2me
	    }
	    catch (IOException shouldnothappen) {
		shouldnothappen.printStackTrace();
	    }
//#endif
	}
	return tab;
    }


    byte[] readBody() throws IOException {
	int len = 0;
	byte[] buf = new byte[0];
	    
//#ifdef j2me
	is = conn.openInputStream();
	len = (int)conn.getLength();
//#else
	is = conn.getInputStream();
	len = conn.getContentLength();
//#endif
	    
	int bytesread = 0;
	int actual = 0;
	if (len >= 0) {
	    buf = new byte[len];
		
	    while ((bytesread != len) && (actual != -1)) {
		actual = is.read(buf, bytesread, len - bytesread);
		bytesread += actual;
	    }
	} else {
	    buf = new byte[512];
	    do {
		if(bytesread == buf.length) {
		    byte[] newbuf = new byte[buf.length+512];
		    System.arraycopy(buf,0,newbuf,0,bytesread);
		    buf = newbuf;
		}
		actual = is.read(buf, bytesread, buf.length - bytesread);
		if(actual > 0)
		    bytesread += actual;
	    } while(actual > 0);
	}
	if(bytesread != buf.length) {
	    byte[] tmp = new byte[buf.length];
	    System.arraycopy(buf,0,tmp,0,bytesread);
	    buf = tmp;
	}
	return buf;
    }


    void close() {
	if (os != null) {
	    try {os.close();}
	    catch (IOException e) {}
	}
	if (is != null) {
	    try {is.close();}
	    catch (IOException e) {}
	}
	if (conn != null) {
//#ifdef j2me
	    try {
//#endif
//#ifndef j2me
		conn.disconnect();
//#else
		conn.close();
//#endif
//#ifdef j2me
	    }
	    catch (IOException e) {}
//#endif
	}
    }
	
//#ifdef j2me
    HttpConnection conn;
//#else
    HttpURLConnection conn;
//#endif
    boolean secure = false;
    InputStream is;
    OutputStream os;
}

