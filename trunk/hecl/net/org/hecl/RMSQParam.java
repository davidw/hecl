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

//#ifdef $ant:j2me
package org.hecl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.hecl.rms.RMSInputStream;

public class RMSQParam extends QParam {
    public RMSQParam(String paramName,String rmsName) {
	super(paramName,null);
	rmsname = rmsName;
    }

    public void sendData(OutputStream os) throws IOException {
	RMSInputStream is = null;
	byte[] buf2 = new byte[128];
	try {
	    is = new RMSInputStream(rmsname);
	    int n = 0;
	    while((n = is.read(buf2)) != -1) {
//#ifdef notdef
		System.err.println("encode/send, #="+n);
		for(int i=0; i<n; ++i) {
		    System.err.print(" "+Integer.toHexString(buf2[i]&0xff));
		}
		System.err.println();
//#endif
		sendBytes(os,HttpRequest.urlencode(
			      HttpRequest.IRIencode(HttpRequest.bytesToString(buf2,0,n))).getBytes());
	    }
	    RMSInputStream iis = is;
	    is = null;
	    iis.close();
	}
	catch(Exception e) {
	    e.printStackTrace();
	    if(is != null) {
		try {is.close();}
		catch(Exception ex) {}
	    }
	    throw new IOException("Can't access recordstore");
	}
    }
    

    public PrintStream printon(PrintStream s) {
	super.printon(s).print("(rms<"+rmsname+">)");
	return s;
    }
    
	
    protected String rmsname = null;
}
//#endif
