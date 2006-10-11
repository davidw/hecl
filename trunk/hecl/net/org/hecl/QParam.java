/* Copyright 2005-2006 by daa2c.com

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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/*
 * A base class for query parameters.
 * Since memory is a scarce resource on monbile devices, we need to handle the
 * query data in a memory efficient manner, i.e. we avoid creating large
 * buffers only for the transfer.
 */
public class QParam {
    public QParam(String paramName,byte[] data) {
	if(paramName != null)
	    namebytes = HttpRequest.urlencode(
		HttpRequest.IRIencode(paramName)).getBytes();
	databytes = data;
    }
    

    public void send(OutputStream os) throws IOException {
	sendParamName(os);
	sendData(os);
    }


    protected void sendBytes(OutputStream os,byte[] buf) throws IOException {
	if(buf != null) {
//#ifdef notdef
	    System.err.println("Send byte-#="+buf.length);
	    for(int i=0; i<buf.length; ++i) {
		System.err.print((char)(buf[i]&0xff));
	    }
	    System.err.println();
//#endif
	    os.write(buf,0,buf.length);
	    //System.err.println("vytes send #="+buf.length);
	}
    }


    protected void sendData(OutputStream os) throws IOException {
	if(databytes != null)
	    sendBytes(os,databytes);
    }
    
    
    protected void sendParamName(OutputStream os) throws IOException {
	if(namebytes != null && namebytes.length > 0) {
	    os.write(namebytes,0,namebytes.length);
	    os.write((byte)'=');
	}
    }

    public PrintStream printon(PrintStream s) {
	s.print("namebytes=>");
	if(namebytes != null)
	    s.write(namebytes,0,namebytes.length);
	s.print("< - data=>");
	if(databytes != null)
	    s.write(databytes,0,databytes.length);
	s.print("<");
	return s;
    }
    
    protected byte[] namebytes = null;
    protected byte[] databytes = null;

}
