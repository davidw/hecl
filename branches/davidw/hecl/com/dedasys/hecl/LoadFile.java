package com.dedasys.hecl;

import java.io.*;

public class LoadFile extends Load {
    public Thing getscript(String flname)
    throws HeclException {
	StringBuffer input = null;
	FileInputStream fin = null;
	int c = 0;

	try {
	    fin = new FileInputStream(flname);
	    File fl = new File(flname);
	    DataInputStream in = new DataInputStream(fin);
	    int len = (int)fl.length();
	    byte[] b = new byte[len];
	    in.readFully(b);
	    input = new StringBuffer(new String(b));
	    b = null;
	} catch (Exception e) {
	    throw new HeclException("Error reading from file: " + e);
	} finally {
	    if (fin != null) {
		try {
		    fin.close();
		} catch (Exception ex) {
		    throw new HeclException(ex.toString());
		}
	    }
	}
	return new Thing(input);
    }
}
