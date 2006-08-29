/*
 * Copyright 2005-2006
 * Wolfgang S. Kechel, data2c GmbH (www.data2c.com)
 * 
 * Author: Wolfgang S. Kechel - wolfgang.kechel@data2c.com
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

package org.hecl.midp20.lcdui;

import java.util.Enumeration;
import java.util.Hashtable;

import org.hecl.Interp;
import org.hecl.Thing;

public class AuxDataMap {
    public static AuxDataMap mapOf(Interp ip,String mapname) {
	return (AuxDataMap)ip.getAuxData(mapname);
    }


    protected AuxDataMap(Interp ip,String mapname) {
	if(ip.getAuxData(mapname) != null)
	    throw new IllegalArgumentException("AuxData '"
					       + mapname
					       + "' already attached.");
	mapip = ip;
	mapip.setAuxData(mapname,this);
    }


    public synchronized void clear() {
	n2v.clear();
	v2n.clear();
    }
    

    public boolean containsKey(String key) {
	return n2v.containsKey(key);
    }
    

    public boolean contains(Object v) {
	return v2n.containsKey(v);
    }
    

    public boolean isEmpty() {
	return n2v.isEmpty();
    }
	    

    public Enumeration keys() {
	return n2v.keys();
    }
    

    public String nameOf(Object v) {
	return (String)v2n.get(v);
    }
    

    public synchronized String put(String key,Object value) {
	if(containsKey(key) || contains(value)) {
	    throw new IllegalArgumentException("Key or value already in table.");
	}
	v2n.put(value,key);
	n2v.put(key,value);
	return key;
    }
    

    public synchronized void remove(String key) {
	Object v = n2v.remove(key);
	if(v != null)
	    v2n.remove(v);
    }
    

    public Object valueOf(String key) {
	return n2v.get(key);
    }
    

    public Enumeration values() {
	return v2n.keys();
    }


    private Hashtable n2v = new Hashtable();
    private Hashtable v2n = new Hashtable();
    protected Interp mapip;
}
