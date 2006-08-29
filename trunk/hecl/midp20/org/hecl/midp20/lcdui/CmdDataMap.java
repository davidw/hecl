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

import org.hecl.Command;
import org.hecl.Interp;

public class CmdDataMap extends AuxDataMap {
    protected CmdDataMap(Interp ip,String mapname,String prefix) {
	super(ip,mapname);
	if(prefix != null)
	    itemprefix = prefix;
    }


    public synchronized Command commandOf(Object w) {
	return (Command)cmdtab.get(w);
    }


    public synchronized String put(String key,Object v) {
	return put(key,v,null);
    }
    
    public synchronized String put(String key,Object v,Command cmd) {
	if(key == null)
	    key = newKey();
	
	if(containsKey(key) || contains(v)) {
	    throw new IllegalArgumentException("Key or value already in table.");
	}
	super.put(key,v);
	if(cmd != null) {
	    // add command to interpreter and table
	    mapip.addCommand(key,cmd);
	    cmdtab.put(v,cmd);
	}
	mapip.setResult(key);
	return key;
    }
    

    public synchronized void remove(String key) {
	Object v = valueOf(key);
	if(v != null) {
	    // Try to remove the attached command (ininterpreter and table)
	    mapip.removeCommand(key);
	    cmdtab.remove(v);
	    super.remove(key);
	}
    }
    

    public synchronized void clear() {
	// remove all commands...
	Enumeration elems = keys();
	while(elems.hasMoreElements()) {
	    remove((String)elems.nextElement());
	}
    }

    protected String newKey() {
	return itemprefix + "-" + ++itemcounter;
    }
    
    protected String itemprefix = "cdmdataitem";
    protected Hashtable cmdtab = new Hashtable();
    private int itemcounter = 0;
}
