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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
import javax.microedition.rms.RecordEnumeration;

/**
 * The <code>HeclRecordStoreCmds</code> class implements the rs_list,
 * rs_get and rs_put commands.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class HeclRecordStoreCmds extends Operator {
    public static final int RMS_LIST = 1;
    public static final int RMS_GET = 2;
    public static final int RMS_SET = 3;
    public static final int RMS_SIZE = 4;
    public static final int RMS_SIZEAVAIL = 5;
    public static final int RMS_DELETE = 6;
    public static final int RMS_ADD = 7;
    public static final int RMS_HSET = 8;
    public static final int RMS_HGET = 9;
    public static final int RMS_HEXISTS = 10;
    public static final int RMS_HKEYS = 11;
    public static final int RMS_HDEL = 12;

    protected HeclRecordStoreCmds(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }

    protected static void closeRS(RecordStore rs) throws HeclException {
	if(rs != null) {
	    try {
		rs.closeRecordStore();
	    }
	    catch(Exception e) {
		throw new HeclException("Can't close recordstore.");
	    }
	}
    }
    
    public RealThing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	RecordStore rs = null;
	byte[] data = null;
	String name = argv.length>1?argv[1].toString() : null;
	int recordid = 1;
	
	switch (cmd) {
	  case RMS_LIST:
	    ;				    // trick emacs indentation
	    {
		Vector v = new Vector();
		
		if(name == null) {
		    String[] names = RecordStore.listRecordStores();
		    if (names != null) {
			for (int i = 0; i < names.length; i++) {
			    v.addElement(new Thing(names[i]));
			}
		    }
		} else {
		    try {
			rs = RecordStore.openRecordStore(name,false);
			RecordEnumeration records =
			    rs.enumerateRecords(null,null,false);
			while(records.hasNextElement()) {
			    int n = records.nextRecordId();
			    v.addElement(IntThing.create(n));
			}
			records.destroy();
		    } catch (Exception e) {
			/* FIXME - we ought to do something a little bit more clever here. */
			throw new HeclException(e.toString());
		    }
		    finally {
			closeRS(rs);
		    }
		}
		return new ListThing(v);
	    }

	  case RMS_GET:
	    // rs_get name [recordid]
	    if(argv.length>2) {
		recordid = IntThing.get(argv[2]);
	    }
	    try {
		rs =  RecordStore.openRecordStore(name, false);
		data = rs.getRecord(recordid);
	    } catch (Exception e) {
		/* FIXME - we ought to do something a little bit more clever here. */
		throw new HeclException(e.toString());
	    }
	    finally {
		closeRS(rs);
	    }
	    return new StringThing(new String(data));
	    
	  case RMS_SET:
	    // rs_put name [recordid=1] data
	    System.err.println("argv.length="+argv.length);
	    
	    if(argv.length>3) {
		recordid = IntThing.get(argv[2]);
		data = argv[3].toString().getBytes();
	    } else {
		data = argv[2].toString().getBytes();
	    }
	    
	    try {
		rs = RecordStore.openRecordStore(name, true);
		//rs.addRecord(data, 0, data.length);
		rs.setRecord(recordid,data, 0, data.length);
	    } catch (Exception e) {
		throw new HeclException(e.toString());
	    }
	    finally {
		closeRS(rs);
	    }
	    return new IntThing(data.length);
	    
	  case RMS_ADD:
	    // add data
	    try {
		rs = RecordStore.openRecordStore(name, false);
		data = argv[2].toString().getBytes();
		recordid = rs.addRecord(data,0,data.length);
	    }
	    catch(Exception e) {
		throw new HeclException("Can't add record: " + e.getMessage());
	    }
	    finally {
		closeRS(rs);
	    }
	    return new IntThing(recordid);
	    
	  case RMS_SIZE:
	  case RMS_SIZEAVAIL:
	    // size recordid
	    // we reuse the recordid var!
	    try {
		rs =  RecordStore.openRecordStore(name, true);
		recordid = cmd == RMS_SIZE ? rs.getSize() : rs.getSizeAvailable();
	    } catch (Exception e) {
		throw new HeclException(e.toString());
	    }
	    finally {
		closeRS(rs);
	    }
	    return new IntThing(recordid);

	  case RMS_DELETE:
	    recordid = -1;
	    if(argv.length > 2) {
		recordid = IntThing.get(argv[2]);
	    }
	    if(recordid < 0) {
		try {
		    RecordStore.deleteRecordStore(name);
		}
		catch(Exception e) {
		    throw new HeclException("Can't delete recordstore '"
					    +name+"': " +e.getMessage());
		}
		break;
	    }
	    try {
		rs = RecordStore.openRecordStore(name, false);
		rs.deleteRecord(recordid);
	    }
	    catch(Exception e) {
		throw new HeclException("Can't delete recordstore '"
					+name+"["+recordid+"]': " +e.getMessage());
	    }
	    finally {
		closeRS(rs);
	    }
	    break;

	  case RMS_HSET:
	    // rs_hset name key value
	    data = argv[3].toString().getBytes();
	    try {
		rs = RecordStore.openRecordStore(name, true);
		//rs.addRecord(data, 0, data.length);
		String key = argv[2].toString();
		Object[] v = recordOf(rs,key);
		recordid = ((Integer)v[0]).intValue();
		data = toData(key,argv[3].toString());
		if(recordid < 0)
		    rs.addRecord(data, 0, data.length);
		else
		    rs.setRecord(recordid,data, 0, data.length);
		recordid = data.length;
		data = null;
	    } catch (Exception e) {
		throw new HeclException(e.toString());
	    }
	    finally {
		closeRS(rs);
	    }
	    return new IntThing(recordid);
	    
	  case RMS_HGET:
	    // hget name key
	    ;				    // trick emacs indentation
	    {
		Object[] v = null;
		
		try {
		    rs = RecordStore.openRecordStore(name, true);
		    v = recordOf(rs,argv[2].toString());
		    recordid = ((Integer)v[0]).intValue();
		} catch (Exception e) {
		    throw new HeclException(e.toString());
		}
		finally {
		    closeRS(rs);
		}
		return new StringThing(recordid >= 0 && v[1] != null ?
				       (String)v[1] : "");
	    }
	    
	  case RMS_HEXISTS:
	    // hget name key
	    try {
		rs = RecordStore.openRecordStore(name, true);
		Object[] v = recordOf(rs,argv[2].toString());
		recordid = ((Integer)v[0]).intValue();
	    } catch (Exception e) {
		throw new HeclException(e.toString());
	    }
	    finally {
		closeRS(rs);
	    }
	    return new IntThing(recordid >= 0 ? 1 : 0);

	  case RMS_HKEYS:
	    ;
	    {
		Vector v = null;
		
		try {
		    rs = RecordStore.openRecordStore(name, true);
		    v = hkeys(rs);
		} catch (Exception e) {
		    throw new HeclException(e.toString());
		}
		finally {
		    closeRS(rs);
		}
		if(v == null)
		    v = new Vector();
		
		int n = v.size();
		for(int i=0; i<n; ++i)
		    v.setElementAt(new Thing((String)v.elementAt(i)),i);
		return new ListThing(v);
	    }

	  case RMS_HDEL:
	    try {
		rs = RecordStore.openRecordStore(name, true);
		Object[] v = recordOf(rs,argv[2].toString());
		recordid = ((Integer)v[0]).intValue();
		if(recordid >= 0)
		    rs.deleteRecord(recordid);
	    } catch (Exception e) {
		throw new HeclException(e.toString());
	    }
	    finally {
		closeRS(rs);
	    }
	    break;
	    
	  default:
	    throw new HeclException("Unknown rms command '"
				    + argv[0].toString() + "' with code '"
				    + cmd + "'.");
	}
	return null;
    }

    private static byte[] toData(String key,String value) throws Exception {
	ByteArrayOutputStream bos = null;
	DataOutputStream dos = null;
	byte[] data = null;
	
	try {
	    bos = new ByteArrayOutputStream();
	    dos = new DataOutputStream(bos);
	    dos.writeUTF(key);
	    dos.writeUTF(value);
	    data = bos.toByteArray();
	    dos.close();
	    bos.close();
	}
	catch(Exception e) {
	    throw new HeclException("Can't create data: "+e.getMessage());
	}
	return data;
    }
    
	    
    /*
      Convert all rms records into a hashtable
    private static Hashtable asTable(RecordStore rs) throws Exception {
	Hashtable t = new Hashtable();
	
	RecordEnumeration enumeration = rs.enumerateRecords(null, null, false);
	for (int i = 0; i < enumeration.numRecords(); i++) {
	    int tmpid = enumeration.nextRecordId();
	    byte[] data = rs.getRecord(tmpid);
	    
	    ByteArrayInputStream bis = new ByteArrayInputStream(data);
	    DataInputStream dis = new DataInputStream(bis);
	    
	    t.put(dis.readUTF(),dis.readUTF());
	    dis.close();
	    bis.close();
	}
	enumeration.destroy();
	return t;
    }
    */

    private static Vector hkeys(RecordStore rs) throws Exception {
	Vector v = new Vector();
	
	RecordEnumeration enumeration = rs.enumerateRecords(null, null, false);
	for (int i = 0; i < enumeration.numRecords(); i++) {
	    int tmpid = enumeration.nextRecordId();
	    byte[] data = rs.getRecord(tmpid);
	    
	    ByteArrayInputStream bis = new ByteArrayInputStream(data);
	    DataInputStream dis = new DataInputStream(bis);
	    
	    v.addElement(dis.readUTF());
	    dis.close();
	    bis.close();
	}
	return v;
    }

    private static Object[] recordOf(RecordStore rs,String key)
	throws Exception {
	String value = null;
	int id = -1;
	
	RecordEnumeration enumeration = rs.enumerateRecords(null, null, false);
	for (int i = 0; i < enumeration.numRecords(); i++) {
	    int tmpid = enumeration.nextRecordId();
	    byte[] data = rs.getRecord(tmpid);
	    
	    ByteArrayInputStream bis = new ByteArrayInputStream(data);
	    DataInputStream dis = new DataInputStream(bis);
	    
	    String tmpkey = dis.readUTF();
	    bis.close();
	    dis.close();
	    if (tmpkey.equals(key)) {
		id = tmpid;
		value = dis.readUTF();
		break;
	    }
	}
	enumeration.destroy();
	return new Object[]{new Integer(id),value};
    }
    
    public static void load(Interp ip) throws HeclException {
	Operator.load(ip);
    }

    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip);
    }

    static {
        cmdtable.put("rms.list", new HeclRecordStoreCmds(RMS_LIST,0,1));
        cmdtable.put("rms.get", new HeclRecordStoreCmds(RMS_GET,1,2));
        cmdtable.put("rms.set", new HeclRecordStoreCmds(RMS_SET,2,3));
        cmdtable.put("rms.size", new HeclRecordStoreCmds(RMS_SIZE,1,1));
        cmdtable.put("rms.sizeavail", new HeclRecordStoreCmds(RMS_SIZEAVAIL,1,1));
        cmdtable.put("rms.delete", new HeclRecordStoreCmds(RMS_DELETE,1,2));
        cmdtable.put("rms.add", new HeclRecordStoreCmds(RMS_ADD,2,2));
        cmdtable.put("rms.hset", new HeclRecordStoreCmds(RMS_HSET,3,3));
        cmdtable.put("rms.hget", new HeclRecordStoreCmds(RMS_HGET,2,2));
        cmdtable.put("rms.hexists", new HeclRecordStoreCmds(RMS_HEXISTS,2,2));
        cmdtable.put("rms.hkeys", new HeclRecordStoreCmds(RMS_HKEYS,1,1));
        cmdtable.put("rms.hdel", new HeclRecordStoreCmds(RMS_HDEL,2,2));
    }
}
