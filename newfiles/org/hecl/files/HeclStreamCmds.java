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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

import java.io.InputStream;
import java.io.OutputStream;

import org.hecl.ClassCommand;
import org.hecl.ClassCommandInfo;
import org.hecl.HeclException;
import org.hecl.IntThing;
import org.hecl.Interp;
import org.hecl.ObjectThing;
import org.hecl.Thing;

/**
 * The <code>HeclStreamCmds</code> class implements command handlers
 * for DataInputStream and DataOutputStream, which are handled as
 * "class commands".
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class HeclStreamCmds implements ClassCommand {

    private boolean is_input_flag = false;

    public Thing method(Interp interp, ClassCommandInfo context, Thing[] argv)
	throws HeclException {
	String subcmd = argv[1].toString().toLowerCase();
	HeclChannel target = (HeclChannel)ObjectThing.get(argv[0]);
	Thing retval = null;
	Thing empty = new Thing("");

	try {
	    if (target.readable()) {
		DataInputStream dis = target.datainputstream;
		if (subcmd.equals("close")) {
		    dis.close();
		    retval = empty;
		} else if (subcmd.equals("read")) {
		    if (argv.length == 3) {
			/* Read N bytes. */
			byte[] b = new byte[IntThing.get(argv[2])];
			dis.read(b);
			retval = new Thing(new String(b));
		    } else if (argv.length == 2) {
			/* Read the entire thing. */
			return HeclFileUtils.readFileFromDis(dis);
		    }
		} else if (subcmd.equals("readln")) {
		    StringBuffer sb = new StringBuffer();
		    int c = 0;
		    int last = 0;
		    while (true) {
			last = c;
			c = dis.read();
			if (c == -1) {
			    break;
			} else if (c == Interp.eol[0] &&
				   Interp.eol.length == 1) {
			    break;
			} else if (Interp.eol.length == 2 &&
				   c == Interp.eol[1] &&
				   last == Interp.eol[0]) {
			    sb.setLength(sb.length() - 1);
			    break;
			} else {
			    sb.append((char)c);
			}
		    }
		    return new Thing(new String(sb));
		}
	    } else {
		DataOutputStream dos = target.dataoutputstream;
		if (subcmd.equals("close")) {
		    dos.close();
		    retval = empty;
		} else if (subcmd.equals("flush")) {
		    dos.flush();
		    retval = empty;
		} else if (subcmd.equals("write")) {
		    byte[] bytes = argv[2].toString().getBytes();
		    dos.write(bytes);
		    retval = IntThing.create(bytes.length);
		} else if (subcmd.equals("writeln")) {
		    byte[] bytes = argv[2].toString().getBytes();
		    dos.write(bytes);
		    dos.writeByte(Interp.eol[0]);
		    if (Interp.eol.length > 1) {
			dos.writeByte(Interp.eol[1]);
		    }
		    retval = IntThing.create(bytes.length + Interp.eol.length);
		}
	    }
	} catch (IOException ioe) {
	    throw new HeclException("IOException in " + subcmd + ": " + ioe.toString());
	}

	if (retval == null) {
	    throw new HeclException("Bad method for '" + target + "'");
	}
	return retval;
    }

    public HeclStreamCmds(boolean is_input) {
	is_input_flag = is_input;
    }

    public static void load(Interp interp) {
	interp.addClassCmd(HeclChannel.class, new HeclStreamCmds(true));
	interp.addClassCmd(HeclChannel.class, new HeclStreamCmds(false));
    }
    public static void unload(Interp interp) {
	interp.removeClassCmd(HeclChannel.class);
	interp.removeClassCmd(HeclChannel.class);
    }
}