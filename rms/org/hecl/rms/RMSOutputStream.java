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

import java.io.OutputStream;
import java.io.IOException;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public class RMSOutputStream extends OutputStream {
    public RMSOutputStream(String storename) throws IOException {
	super();
	rsname = storename;

	try {
	    rs = RecordStore.openRecordStore(rsname, true);
	    // clear up any old content!
	    RecordEnumeration records = rs.enumerateRecords(null,null,false);
	    while(records.hasNextElement()) {
		rs.deleteRecord(records.nextRecordId());
	    }
	    records.destroy();
	}
	catch(Exception e) {
	    throw new IOException(e.toString());
	}
    }

    public void close() throws IOException {
	if(rs != null) {
	    flush();
	    try {
		rs.closeRecordStore();
	    }
	    catch(Exception e) {
	    }
	    finally {
		rs = null;
		buf = null;
	    }
	    super.close();
	}
	//System.err.println("RMSOutputStream closed, written="+nwritten);
    }
	
    public void flush() throws IOException {
	if(rs != null && bufferpos > 0) {
	    try {
		rs.addRecord(buf,0,bufferpos);
		nwritten += bufferpos;
	    }
	    //RecordStoreNotOpenException
	    //RecordStoreException
	    //RecordStoreFullException
	    //SecurityException
	    catch(Exception e) {
		throw new IOException(e.toString());
	    }
	    bufferpos = 0;
	}
    }
	
    public void write(int b) throws IOException {
	if(rs == null)
	    throw new IOException("stream closed.");
	buf[bufferpos++] = (byte)(b&0xff);
	if(bufferpos >= BUFSIZE) {
	    flush();
	}
	    
    }
	
    private static final int BUFSIZE = 1024;

    private String rsname;
    private RecordStore rs;
    private int bufferpos = 0;
    private int nwritten = 0;
    private byte[] buf = new byte[BUFSIZE];
}
