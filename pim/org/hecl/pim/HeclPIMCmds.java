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

package org.hecl.pim;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.LongThing;
import org.hecl.HashThing;
import org.hecl.Operator;
import org.hecl.Thing;

/**
 * The <code>HeclPIMCmds</code> class implements various PIM related
 * commands.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class HeclPIMCmds extends Operator {
    public static final int LISTS = 1;
    public static final int LIST_CONTACTS = 2;

    public Thing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	PIM pim = PIM.getInstance();

	switch(cmd) {
	    case LISTS: {
		int type = string2type(argv[1].toString());
		String []lists = pim.listPIMLists(type);
		Vector v = new Vector();
		for (int i = 0; i < lists.length; i++) {
		    v.addElement(new Thing(lists[i]));
		}
		return ListThing.create(v);
	    }
	    case LIST_CONTACTS: {
		ContactList clist = null;
		try {
		    clist = (ContactList)pim.openPIMList(PIM.CONTACT_LIST, PIM.READ_ONLY);
		    Enumeration e = clist.items();
		    Vector v = new Vector();
		    while (e.hasMoreElements()) {
			Contact c = (Contact)e.nextElement();
			v.addElement(contact2thing(clist, c));
		    }
		    return ListThing.create(v);
		} catch (Exception e) {
		    throw new HeclException("Error in pim.items: " + e.toString());
		} finally {
		    try {
			clist.close();
		    } catch (Exception e) {
			throw new HeclException("Problem closing contact list: " + e.toString());
		    }
		}
	    }
	    default:
		throw new HeclException("Unknown pim command '"
					+ argv[0].toString() + "' with code '"
					+ cmd + "'.");
	}
    }

    /**
     * The <code>contact2thing</code> method transforms a contact into
     * a Thing that takes this form: a hash table with data types as
     * keys, and lists as values.  For instance, you might have {Phone
     * {123456 654321}}.
     *
     * @param clist a <code>ContactList</code> value
     * @param c a <code>Contact</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    private static Thing contact2thing(ContactList clist, Contact c) throws HeclException {
	int []fields = c.getFields();
	Hashtable hres = new Hashtable();
	for (int i = 0; i < fields.length; i++) {
	    int f = fields[i];
	    int datatype = clist.getFieldDataType(f);
	    String label = clist.getFieldLabel(f);
	    int nvalues = c.countValues(f);
	    Vector values = new Vector();
	    for (int j = 0; j < nvalues; j++) {
		Thing attrs = attributes2thing(clist, c.getAttributes(f, j));
		Thing thingval = null;
		switch (datatype) {
		    case PIMItem.BINARY:
			thingval = new Thing(new String(c.getBinary(f, j)));
			break;
		    case PIMItem.BOOLEAN:
			thingval = IntThing.create(c.getBoolean(f, j));
			break;
		    case PIMItem.DATE:
			thingval = LongThing.create(c.getDate(f, j));
			break;
		    case PIMItem.INT:
			thingval = IntThing.create(c.getInt(f, j));
			break;
		    case PIMItem.STRING:
			thingval = new Thing(c.getString(f, j));
			break;
		    case PIMItem.STRING_ARRAY:
			String[] sarray = c.getStringArray(f, j);
			Vector strarrvec = new Vector();
			for (int k = 0; k < sarray.length; k++) {
			    strarrvec.addElement(new Thing(sarray[k]));
			}
			thingval = ListThing.create(strarrvec);
			break;
		    default:
			throw new HeclException("Unsupported data type: " + datatype + " for field " + label);
		}
		values.addElement(attrs);
		values.addElement(thingval);
	    }
	    hres.put(label, ListThing.create(values));
	}
	return HashThing.create(hres);
    }

    /**
     * The <code>attributes2thing</code> method walks through
     * potential values for attributes, and if they are present,
     * fetches the label and adds it to a list, which is then
     * returned.
     *
     * @param clist a <code>ContactList</code> value
     * @param attributes an <code>int</code> value
     * @return a <code>Thing</code> value
     */
    private static Thing attributes2thing(ContactList clist, int attributes) {
	Vector resv = new Vector();
	for (int i = 0; i < 10; i++) {
	    int attr = (1 << i) & attributes;
	    if (attr != 0) {
		resv.addElement(new Thing(clist.getAttributeLabel(attr)));
	    }
	}
	return ListThing.create(resv);
    }

    /**
     * The <code>string2type</code> method takes a type of list as a
     * string transforms it into an integer of the correct type.
     *
     * @param stype a <code>String</code> value
     * @return an <code>int</code> value
     * @exception HeclException if an error occurs
     */
    private static int string2type(String stype) throws HeclException {
	if (stype.equals("CONTACT")) {
	    return PIM.CONTACT_LIST;
	} else if (stype.equals("EVENT")) {
	    return PIM.EVENT_LIST;
	} else if (stype.equals("TODO")) {
	    return PIM.TODO_LIST;
	} else {
	    throw new HeclException("Unknown PIM type: " + stype);
	}
    }

    public static void load(Interp ip) throws HeclException {
	Operator.load(ip,cmdtable);
    }


    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip,cmdtable);
    }

    protected HeclPIMCmds(int cmdcode, int minargs, int maxargs) {
	super(cmdcode,minargs,maxargs);
    }

    private static Hashtable cmdtable = new Hashtable();
    static {
	try {
	    cmdtable.put("pim.lists", new HeclPIMCmds(LISTS,1,1));
	    cmdtable.put("pim.list_contacts", new HeclPIMCmds(LIST_CONTACTS,0,1));
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Can't create pim commands.");
	}
    }
}