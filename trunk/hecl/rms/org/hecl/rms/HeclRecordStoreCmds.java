/* Copyright 2005-2006 David N. Welton <davidw@dedasys.com>

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

package org.hecl.rms;

import java.util.Vector;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.Operator;
import org.hecl.RealThing;
import org.hecl.StringThing;
import org.hecl.Thing;

import javax.microedition.rms.RecordStore;

/**
 * The <code>HeclRecordStoreCmds</code> class implements the rs_list,
 * rs_get and rs_put commands.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class HeclRecordStoreCmds extends Operator {
    public static final int RS_LIST = 1;
    public static final int RS_GET = 2;
    public static final int RS_PUT = 3;

    public static final int RS_SIZE = 4;
    public static final int RS_SIZEAVAIL = 5;

    protected HeclRecordStoreCmds(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }

    public RealThing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	RecordStore rs;
	byte[] data;
	String name = null;

	switch (cmd) {
	    case RS_LIST:
		Vector v = new Vector();
		String[] names = RecordStore.listRecordStores();
		if (names != null) {
		    for (int i = 0; i < names.length; i++) {
			v.addElement(new Thing(names[i]));
		    }
		}
		return new ListThing(v);

	    case RS_GET:
		try {
		    rs =  RecordStore.openRecordStore(argv[1].toString(), false);
		    data = rs.getRecord(1); /* The first one. */
		    rs.closeRecordStore();
		} catch (Exception e) {
		    throw new HeclException(e.toString());
		}
		return new StringThing(new String(data));

	    case RS_PUT:
		data = argv[2].toString().getBytes();
		name = argv[1].toString();
		try {
		    RecordStore.deleteRecordStore(name);
		} catch (Exception e) {
		    /* Ignore it - we just want to start with a fresh one. */
		}
		try {
		    rs =  RecordStore.openRecordStore(name, true);
		    rs.addRecord(data, 0, data.length);
		    rs.closeRecordStore();
		} catch (Exception e) {
		    throw new HeclException(e.toString());
		}
		return new IntThing(data.length);

	    case RS_SIZE:
	    case RS_SIZEAVAIL:
		int result = 0;
		name = argv[1].toString();
		try {
		    rs =  RecordStore.openRecordStore(name, true);
		    if (cmd == RS_SIZE) {
			result = rs.getSize();
		    } else {
			result = rs.getSizeAvailable();
		    }
		    rs.closeRecordStore();
		} catch (Exception e) {
		    throw new HeclException(e.toString());
		}

		return new IntThing(result);
	    default:
		throw new HeclException("Unknown rms command '"
					+ argv[0].toString() + "' with code '"
					+ cmd + "'.");
	}
	// notreached
	// return null;
    }

    public static void load(Interp ip) throws HeclException {
	Operator.load(ip);
    }

    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip);
    }

    static {
        cmdtable.put("rs_list", new HeclRecordStoreCmds(RS_LIST,0,0));
        cmdtable.put("rs_get", new HeclRecordStoreCmds(RS_GET,1,1));
        cmdtable.put("rs_put", new HeclRecordStoreCmds(RS_PUT,2,2));

        cmdtable.put("rs_size", new HeclRecordStoreCmds(RS_SIZE,1,1));
        cmdtable.put("rs_sizeavail", new HeclRecordStoreCmds(RS_SIZEAVAIL,1,1));
    }
}
