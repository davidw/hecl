/* Copyright 2005 David N. Welton <davidw@dedasys.com>

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

import org.hecl.Command;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.Thing;

import java.util.Vector;

import javax.microedition.rms.RecordStore;

/**
 * The <code>HeclRecordStoreCmd</code> class implements the rs_list,
 * rs_get and rs_put commands.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class HeclRecordStoreCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
	String cmdname = argv[0].toString();
	RecordStore rs;

	if (cmdname.equals("rs_list")) {
	    Vector v = new Vector();
	    String[] names = RecordStore.listRecordStores();
	    if (names == null) {
		interp.setResult(new Thing(""));
	    } else {
		for (int i = 0; i < names.length; i++) {
		    v.addElement(new Thing(names[i]));
		}
		interp.setResult(ListThing.create(v));
	    }
	} else if (cmdname.equals("rs_get")) {
	    byte[] data;
	    try {
		rs =  RecordStore.openRecordStore(argv[1].toString(), false);
		data = rs.getRecord(1); /* The first one. */
		rs.closeRecordStore();
	    } catch (Exception e) {
		throw new HeclException(e.toString());
	    }
	    interp.setResult(new Thing(new String(data)));
	} else if (cmdname.equals("rs_put")) {
	    byte[] data = argv[2].toString().getBytes();
	    String name = argv[1].toString();
	    try {
		RecordStore.deleteRecordStore(name);
	    } catch (Exception e) {
		/* Ignore it - we just want to start with a fresh
		 * one. */
	    }
	    try {
		rs =  RecordStore.openRecordStore(name, true);
		rs.addRecord(data, 0, data.length);
		rs.closeRecordStore();
	    } catch (Exception e) {
		throw new HeclException(e.toString());
	    }
	    interp.setResult(IntThing.create(data.length));
	}
	return;
    }
}
