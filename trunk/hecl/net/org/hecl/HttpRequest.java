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

//#ifdef ant:j2me
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
//#ifndef ant:cldc1.0
import javax.microedition.io.HttpsConnection;
//#endif
//#else
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
//#endif


public class HttpRequest extends Thread {
    static public final int SETUP = 0;
    static public final int CONNECTED = 1;
    static public final int ERROR = 2;
    static public final int TIMEOUT = 3;
    static public final int OK = 4;
    
    public interface HttpConstants
//#ifdef ant:j2me
	extends HttpConnection
//#else
	     //HttpURLConnection
//#endif
    {
	public final int MY_HTTP_UNAUTHORIZED =
//#ifdef ant:j2me
	HTTP_UNAUTHORIZED
//#else
	HttpURLConnection.HTTP_FORBIDDEN
//#endif
	;
	public final String GET = "GET";
	public final String POST = "POST";
	public final String HEAD = "HEAD";
//	public final String OPTIONS = "OPTIONS";
//	public final String PUT = "PUT";
//	public final String DELETE = "DELETE";
//	public final String TRACE = "TRACE";
}
    
    public static class MyHttpConn {
	MyHttpConn(String url) throws IOException {
	    state = SETUP;
	    is = null;
	    os = null;
	    conn = null;
//#ifndef ant:cldc1.0
	    secure = url.toLowerCase().startsWith("https");
//#endif

//#ifdef ant:j2me 
	    if(false) {
		conn = (HttpConnection)Connector.open(url,Connector.READ_WRITE,true);
	    } else {
//#ifndef ant:cldc1.0
		conn = secure ?
		    (HttpsConnection)
		    Connector.open(url,Connector.READ_WRITE, true) :
		    (HttpConnection)
		    Connector.open(url,Connector.READ_WRITE, true);
//#else
		conn = (HttpConnection)
		    Connector.open(url,Connector.READ_WRITE, true);
//#endif
	    }
//#else
	    URL myurl = new URL(url);
	    conn = secure ? (HttpsURLConnection) myurl.openConnection() :
		(HttpURLConnection)myurl.openConnection();
//#endif
	}


	public void connect(String rm, Hashtable rfields,String qdata)
	    throws IOException {
	    conn.setRequestMethod(rm);
	    System.out.println("requestMethod="+rm);
	    // Set the request fields.
	    Enumeration e = rfields.keys();
	    System.out.println("--- REQUEST -------------------------------------");
	    while (e.hasMoreElements()) {
		String key = (String)e.nextElement();
		System.out.println("key: " + key + ", value: " + (String)rfields.get(key));
		conn.setRequestProperty(key, (String)rfields.get(key));
	    }

//#ifndef ant:j2me
	    if (qdata != null) {
		conn.setDoOutput(true);
	    }
	    // Only JDK: Calling connect will open the connection
	    conn.connect();
//#endif
	    
	    if (qdata != null) {
//#ifdef ant:j2me
		os = conn.openOutputStream();
//#else
		os = conn.getOutputStream();
//#endif
		System.out.println("writing " + qdata.getBytes(defcharset).length + " bytes");
		os.write(qdata.getBytes(defcharset));
		os.flush();
		os.close();
		os = null;
	    }
	}


	public int getResponseCode() {
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
	

	public Hashtable readHeader() {
	    Hashtable tab = new Hashtable();
	    
	    // Getting the response fields.
	    int idx = 0;
	    // Some implementations may treat the 0th header field as special,
	    // i.e. as the status line returned by the HTTP server.
	    // In this case, getHeaderField(0) returns the status line,
	    // but getHeaderFieldKey(0) returns null.
	    // For now, it is not clear if this happens on midlets as well.
//#ifndef ant:j2me
	    if (conn.getHeaderFieldKey(0) == null) {
		++idx;
	    }
//#endif
	    System.out.println("--- RESPONSE HEADER-----------------");
	    String key = "";
	    while (key != null) {
//#ifdef ant:j2me
		try {
//#endif
		    key = conn.getHeaderFieldKey(idx++);
		    if (key != null) {
			tab.put(key.toLowerCase(), conn.getHeaderField(key));
			//System.out.println("key: " + key + ", value: " + conn.getHeaderField(key));
		    }
//#ifdef ant:j2me
		}
		catch (IOException shouldnothappen) {
		    shouldnothappen.printStackTrace();
		}
//#endif
	    }
	    return tab;
	}


	public byte[] readBody() throws IOException {
	    int len = 0;
	    byte[] buf = new byte[0];
	    
//#ifdef ant:j2me
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


	public void close() {
	    if (os != null) {
		try {os.close();}
		catch (IOException e) {}
	    }
	    if (is != null) {
		try {is.close();}
		catch (IOException e) {}
	    }
	    if (conn != null) {
//#ifdef ant:j2me
		try {
//#endif
//#ifndef ant:j2me
		    conn.disconnect();
//#else
		    conn.close();
//#endif
//#ifdef ant:j2me
		}
		catch (IOException e) {}
//#endif
	    }
	}
	
//#ifdef ant:j2me
	public HttpConnection conn;
//#else
	public HttpURLConnection conn;
//#endif
	int state;
	boolean secure = false;
	InputStream is;
	OutputStream os;
    }


    public HttpRequest(String url, String queryData,
		       boolean validate, Hashtable headerfields) {
	urlstr = url;
	body = new String();
	qdata = queryData;
	rc = -1;
	error = null;
	status = SETUP;
	inData = null;
	requestFields = new Hashtable();
	responseFields = new Hashtable();
	if(headerfields != null) {
	    Enumeration e = headerfields.keys();
	    while(e.hasMoreElements()) {
		String key = (String)e.nextElement();
		requestFields.put(key,(String)headerfields.get(key));
	    }
	}
	requestMethod = HttpConstants.GET;
	if(qdata != null) {
	    requestMethod = HttpConstants.POST;
	} else {
	    if(validate)
		requestMethod = HttpConstants.HEAD;
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
    

    public synchronized void run() {
	MyHttpConn co = null;
	rc = -1;
	error = null;
	inData = null;
	//System.out.println("urlstr="+urlstr);
	try {
	    co = new MyHttpConn(urlstr);
	}
	catch (IOException e) {
	    status = ERROR;
	    error = e;
	    return;
	}

	try {
	    co.connect(requestMethod,requestFields,qdata);
	    status = CONNECTED;
	    //System.out.println("Connected");
	    rc = co.getResponseCode();
	    //System.out.println("rc="+rc);
	    if(rc == -1) {
		status = ERROR;
		return;
	    }
	    
	    responseFields = co.readHeader();
	    inData = co.readBody();
	    status = OK;
	    
	    // If no charset is given, use the default (see below).
	    String charset = defcharset;
	    String ct = (String)responseFields.get("content-type");
	    String coding = (String)responseFields.get("content-encoding");
	    
	    if((ct != null && !ct.toLowerCase().startsWith("text"))
	       || (coding != null &&
		   (coding.indexOf("gzip") >= 0
		    || coding.indexOf("compress") >= 0)
		   )
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
			begin += 8;
			int end = ct.indexOf(';', begin);
			if (end == -1) {
			    end = ct.length();
			}
			charset = ct.substring(begin, end);
		    }
		}
	    }
	    responseFields.put("charset",charset);
	    // charset is now detected, create a string holding the result.
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
		try {
		    body = new String(inData,charset);
		    break;
		}
		catch(Exception e2) {
		    body = "xxx-encoding-failed-xxx\n"+e2.getMessage();
		}
	    }
	}
	catch (Exception e) {
	    status = ERROR;
	    error = e;
	    e.printStackTrace();
	}
	if(co != null) {
	    co.close();
	}
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
    

    public byte[] getBytes() {
	return inData;
    }


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

	/*
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	DataOutputStream os = new DataOutputStream(baos);

	try {
	    os.writeUTF(s);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	byte[] tmp = baos.toString().getBytes();
	byte[] res = new byte[tmp.length - 2];
	for (int i = 0; i < res.length; i++) {
	    res[i] = tmp[i + 2];
	}

	return res;
	*/
    }

    public static String urlencode(byte[] s) {
	StringBuffer b = new StringBuffer();

	for(int i = 0; i < s.length; i++) {
	    int ch = s[i] & 0xff;
	    b.append(urlencodemap[ch]);
	}
	return b.toString();
    }

    public static String urlencode(String[] elems) {
	StringBuffer b = new StringBuffer();
	for (int i = 0; i < elems.length; ++i) {
	    if (i > 0) {
		b.append((i % 2) != 0 ? '=' : '&');
	    }
	    b.append(urlencode(IRIencode(elems[i])));
	}
	return b.toString();
    }


    private String urlstr;
    private byte[] inData;
    private String body;
    private String qdata;
    private String requestMethod;
    private int rc;
    private int status;
    Exception error;
    private Hashtable requestFields;
    private Hashtable responseFields;

    private static String[] urlencodemap = new String[256];
    private static String validUrlChars =
    "-_.!~*'()\"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final char[] hexchars = (new String("0123456789ABCDEF")).toCharArray();
    public static String defcharset = "ISO8859-1";
    
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
	urlencodemap['\n'] = "%0D%0A";
    }

}
