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

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;

import org.hecl.HashThing;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.LongThing;
import org.hecl.ObjectThing;
import org.hecl.Operator;
import org.hecl.Thing;


class ServiceBookCmd extends Operator {
    public static final int RECORDS = 1;

    public Thing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	switch(cmd) {
	    /* Fetch all records into a list of hashes. */
	    case RECORDS: {
		Vector v = new Vector();
		ServiceBook sb = ServiceBook.getSB();
		ServiceRecord[] records = sb.getRecords();
		Vector tmp = null;
		Hashtable res = null;

		for (int i = 0; i < records.length; i++) {
		    res = new Hashtable();
		    ServiceRecord rec = records[i];

		    res.put("APN", new Thing(rec.getAPN()));
		    byte[] ad = rec.getApplicationData();
		    try {
			res.put("ApplicationData", (ad == null) ?
				ObjectThing.create(null) :
				new Thing(new String(ad, "ISO-8859-1")));
		    } catch (UnsupportedEncodingException e) {
			System.err.println(e.toString());
		    }
		    tmp = new Vector();
		    String[] bbrhosts = rec.getBBRHosts();
		    if (bbrhosts != null) {
			for (int j = 0; j < bbrhosts.length; j++) {
			    tmp.addElement(new Thing(bbrhosts[j]));
			}
			res.put("BBRHosts", ListThing.create(tmp));
		    } else {
			res.put("BBRHosts", Thing.emptyThing());
		    }

		    tmp = new Vector();
		    int[] bbrports = rec.getBBRPorts();
		    if (bbrports != null) {
			for (int j = 0; j < bbrports.length; j++) {
			    tmp.addElement(IntThing.create(bbrports[j]));
			}
			res.put("BBRPorts", ListThing.create(tmp));
		    } else {
			res.put("BBRPorts", Thing.emptyThing());
		    }


		    res.put("CAAddress", new Thing(rec.getCAAddress()));
		    res.put("CAPort", IntThing.create(rec.getCAPort()));
		    res.put("CARealm", new Thing(rec.getCARealm()));
		    res.put("CID", new Thing(rec.getCid()));

		    /* Need more stuff here...  */

		    res.put("UID", new Thing(rec.getUid()));

		    res.put("CidHash", IntThing.create(rec.getCidHash()));
		    res.put("CompressionMode", IntThing.create(rec.getCompressionMode()));
		    res.put("DataSourceId", new Thing(rec.getDataSourceId()));
		    res.put("Description", new Thing(rec.getDescription()));
		    res.put("DisabledState", IntThing.create(rec.getDisabledState()));
		    res.put("EncryptionMode", IntThing.create(rec.getEncryptionMode()));
		    res.put("HomeAddress", new Thing(rec.getHomeAddress()));
		    res.put("Id", IntThing.create(rec.getId()));
		    res.put("KeyHashForService", IntThing.create(rec.getKeyHashForService()));
		    res.put("LastUpdated", LongThing.create(rec.getLastUpdated()));
		    res.put("Name", new Thing(rec.getName()));
		    res.put("NameHash", IntThing.create(rec.getNameHash()));
		    res.put("NetworkAddress", new Thing(rec.getNetworkAddress()));
		    res.put("NetworkType", IntThing.create(rec.getNetworkType()));
		    res.put("Source", IntThing.create(rec.getSource()));
//		    res.put("Transport", new Thing(rec.getTransport().toString()));
		    res.put("Type", IntThing.create(rec.getType()));
		    res.put("Uid", new Thing(rec.getUid()));
		    res.put("UidHash", IntThing.create(rec.getUidHash()));
		    res.put("UserId", IntThing.create(rec.getUserId()));
		    res.put("isDirty", IntThing.create(rec.isDirty()));
		    res.put("isDisabled", IntThing.create(rec.isDisabled()));
		    res.put("isEncrypted", IntThing.create(rec.isEncrypted()));
		    res.put("isInvisible", IntThing.create(rec.isInvisible()));
		    res.put("isRecordProtected", IntThing.create(rec.isRecordProtected()));
		    res.put("isRestoredFromBackup", IntThing.create(rec.isRestoredFromBackup()));
		    res.put("isRestoreDisabled", IntThing.create(rec.isRestoreDisabled()));
		    res.put("isRestoreEnabled", IntThing.create(rec.isRestoreEnabled()));
		    res.put("isSecureService", IntThing.create(rec.isSecureService()));
		    res.put("isValid", IntThing.create(rec.isValid()));
		    res.put("isWeakSecureService", IntThing.create(rec.isWeakSecureService()));
		    v.addElement(HashThing.create(res));
		}
		return ListThing.create(v);
	    }

	  default:
	    throw new HeclException("Unknown servicebook command '"
				    + argv[0].toString() + "' with code '"
				    + cmd + "'.");

	}
    }

    public static void load(Interp ip) throws HeclException {
	Operator.load(ip,cmdtable);
    }


    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip,cmdtable);
    }

    protected ServiceBookCmd(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }

    private static Hashtable cmdtable = new Hashtable();
    static {
	try {
	    cmdtable.put("servicebook.records", new ServiceBookCmd(RECORDS,0,0));
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Can't create servicebook commands.");
	}

    }
}