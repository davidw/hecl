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
import java.io.IOException;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public class RMSInputStream extends InputStream {
    public RMSInputStream(String storename)
	throws IOException {
	super();
	rsname = storename;
	try {
	    rs = RecordStore.openRecordStore(rsname, false);
	    RecordEnumeration records = rs.enumerateRecords(null,null,false);
	    ids = new int[records.numRecords()];
	    int nfilled = 0;

	    // sort the record nums to be ascending
	    while(records.hasNextElement()) {
		int i;
		int tosort = records.nextRecordId();
		for(i=0; i<nfilled; ++i) {
		    if(ids[i] > tosort)
			break;
		}
		System.arraycopy(ids,i,ids,i+1,nfilled);
		ids[i] = tosort;
		nfilled++;
	    }
//#ifdef notdef
	    for(int i=0; i<ids.length; ++i) {
		System.err.println("r["+i+"]="+ids[i]);
	    }
//#endif
	    records.destroy();
	}
	catch(Exception e) {
	    e.printStackTrace();
	    if(rs != null) {
		try {
		    close();
		}
		catch(Exception ex){
		}
	    }
	    throw new IOException(e.toString());
	}
    }
	
    public int available() throws IOException {
	//System.err.println("RMSInputStream.available() --> "+nremaining);
	return nremaining;
    }
	
    public void close() throws IOException {
	if(rs != null) {
	    try {
		rs.closeRecordStore();
	    }
	    catch(Exception e) {
		e.printStackTrace();
	    }
	    finally {
		rs = null;
		buf = null;
		nremaining = -1;
	    }
	    //System.err.println("RMSInputStream.super.close()...");
	    // should not fail
	    super.close();
	    //System.err.println("RMSInputStream.super.close() done");
	}
    }

    public void mark(int readLimit) {
	if(rs != null)
	    markposition = readLimit;
    }

    public boolean markSupported() {
	return true;
    }
	
    public int read() throws IOException {
	if(rs == null)
	    throw new IOException("record store closed.");
	if(nremaining == 0) {
	    fillBuffer();
	    //System.err.println("filed buffer size="+nremaining);
	}
	if(nremaining > 0) {
	    --nremaining;
	    int abyte = buf[bufferpos++] & 0xff;
	    //System.err.print(" "+Integer.toHexString(abyte));
	    return abyte;
	}
	//System.err.println("RMSInputStream.read() --> -1");
	return -1;
    }

    public void reset() throws IOException {
	if(rs == null)
	    throw new IOException("Stream closed.");
	    
	ididx = -1;
	while(markposition >= 0) {
	    fillBuffer();
	    markposition -= nremaining;
	}
    }
	
    public long skip(long n) throws IOException {
	long nskipped = 0;
	while(n > 0) {
	    if(read() == -1) {
		return nskipped;
	    }
	    nskipped++;
	}
	return nskipped;
    }
	
    protected synchronized void fillBuffer() throws IOException {
	try {
	    if(ididx < ids.length-1) {
		buf = rs.getRecord(ids[++ididx]);
		if(buf == null) {
		    // no more data!
		    nremaining = -1;
		} else {
		    nremaining = buf.length;
		    bufferpos = 0;
		}
	    } else {
		// no more data
		buf = null;
		bufferpos = 0;
		nremaining = -1;
	    }
	}
	//RecordStoreNotOpenException,
	//InvalidRecordIDException,
	//RecordStoreException
	catch (Exception e) {
	    e.printStackTrace();
	    throw new IOException(e.toString());
	}
	//System.err.println("buffer filled, nremaining="+nremaining);
    }


    private static final int BUFSIZE = 1024;
	
    private String rsname;
    private RecordStore rs;
    private int[] ids = null;
    private int bufferpos = 0;
    private int ididx = -1;
    private int nremaining = 0;
    private int markposition = 0;
    private byte[] buf = null;
}
    
