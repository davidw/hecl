/* Copyright 2006 David N. Welton

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

package org.hecl;

import java.util.Hashtable;

class HashCmds extends Operator {
    public static final int HASH = 1;
    public static final int HGET = 2;
    public static final int HSET = 3;

    private HashCmds(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }

    public RealThing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	Thing result = null;
	Hashtable hash;
	String key;

	switch (cmd) {
	  case HASH:
	    result = argv[1];
	    HashThing.get(result);
	    break;
	    
	  case HGET:
	    hash = HashThing.get(argv[1]);
	    key = argv[2].toString();
	    result = (Thing)hash.get(key);
	    break;
	    
	  case HSET:
	    hash = HashThing.get(argv[1]);
	    key = argv[2].toString();
	    result = argv[3];
	    hash.put(key, result);
	    break;
	}
	interp.setResult(result);
	return null;
    }
    
    public static void load(Interp ip) throws HeclException {
	Operator.load(ip);
    }

    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip);
    }

    static {
        cmdtable.put("hash", new HashCmds(HASH,1,1));
        cmdtable.put("hget", new HashCmds(HGET,2,2));
        cmdtable.put("hset", new HashCmds(HSET,3,3));
    }
}
