/*
 * Copyright (C) 2005-2006 data2c GmbH (www.data2c.com)
 *
 * Authors:
 * Wolfgang S. Kechel - wolfgang.kechel@data2c.com
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

package org.hecl.rms;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import javax.microedition.rms.RecordStore;

public class RMSUtils {
    private static void copyBytes(InputStream from,OutputStream to) throws IOException {
	byte[] buf = new byte[CHUNKSIZE];
	int n = 0;
	
	while((n = from.read(buf)) > 0) {
	    //System.err.println("writing a chunk of size="+n);
	    to.write(buf,0,n);
/*
	    if(enc == null) {
	    } else {
		for(int i=0; i<n; ++i)
		    to.write(buf[i] & 0xff);
	    }
*/
	}
    }
    
    public static void copyFrom(String rsname,OutputStream os)
	throws IOException {
	RMSInputStream is = null;
	try {
	    is = new RMSInputStream(rsname);
	    copyBytes(is,os);
	    InputStream iis = is;
	    is = null;
	    iis.close();
	}
	catch(IOException e) {
	    if(is != null) {
		try {is.close();}
		catch(IOException ee) {}
	    }
	    throw e;
	}
    }
    
    public static void saveTo(String rsname,InputStream is)
	throws IOException {
	RMSOutputStream os = null;
	try {
	    os = new RMSOutputStream(rsname);
	    copyBytes(is,os);
	    OutputStream oos = os;
	    os = null;
	    oos.close();
	}
	catch(IOException e) {
	    if(os != null) {
		try {os.close();}
		catch(IOException ee) {}
	    }
	    throw e;
	}
    }

    public static final int CHUNKSIZE = 1024;
}
