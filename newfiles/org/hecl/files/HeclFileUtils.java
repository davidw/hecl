/*
 * Copyright 2009
 * DedaSys LLC - http://www.dedasys.com
 *
 * Author: David N. Welton <davidw@dedasys.com>
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

package org.hecl.files;

import java.io.IOException;

//#if javaversion >= 1.5
import java.io.File;
import java.io.FileInputStream;
//#else
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
//#endif

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.InputStream;
import java.io.OutputStream;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Thing;

public class HeclFileUtils {

//#if javaversion >= 1.5
    public static Thing readFile(String filename) throws HeclException, IOException {
	File realfn = new File(filename).getAbsoluteFile();
	return readFileFromDis(new DataInputStream(new FileInputStream(realfn)));
    }
//#else
    public static Thing readFile(String filename) throws HeclException, IOException {
	FileConnection fconn = (FileConnection)Connector.open(filename);
	return readFileFromDis(fconn.openDataInputStream());
    }
//#endif

    public static Thing readFileFromDis(DataInputStream dis) throws IOException {
	int bsize = 1024;
	byte[] buf = new byte[bsize];
	byte[] acc = null;
	byte[] oldacc = null;
	int len = 0;
	int pos = 0;
	int i = 1;
	while ((len = dis.read(buf)) > -1) {
	    oldacc = acc;
	    acc = new byte[i * bsize];
	    if (oldacc != null) {
		System.arraycopy(oldacc, 0, acc, 0, (i-1) * bsize);
	    }
	    System.arraycopy(buf, 0, acc, pos, len);
	    pos += len;
	    i ++;
	}
	return new Thing(new String(acc, 0, pos));
    }

    public static void writeFile(String filename) throws HeclException {

    }

    public static void sourceFile(Interp interp, String filename)
	throws HeclException {

	interp.currentFile = new Thing(filename);

	try {
	    interp.eval(readFile(filename));
	} catch (HeclException he) {
	    throw he;
	} catch (Exception e) {
	    throw new HeclException("Error while running '" + filename + "': " + e.toString());
	}
    }
}
