/* Copyright 2005-2006 by data2c.com

Authors:
Wolfgang S. Kechel - wolfgang.kechel@data2c.com
Jörn Marcks - joern.marcks@data2c.com

Wolfgang S. Kechel, Jörn Marcks

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

package org.hecl.net;

import java.util.Hashtable;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Operator;
import org.hecl.RealThing;
import org.hecl.StringThing;
import org.hecl.Thing;

public class Base64Cmd extends org.hecl.Operator {
    public Thing operate(int cmd, Interp interp, Thing[] argv)
	throws HeclException {

	switch(cmd) {
	  case ENCODE:
	    return new Thing(new String(Base64.encode(argv[1].toString().getBytes())));

	  case DECODE:
	    return new Thing(new String(Base64.decode(argv[1].toString())));
	    
	  default:
	    throw new HeclException("Unknown base64 command '"
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


    protected Base64Cmd(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }

    public static final int ENCODE = 1;
    public static final int DECODE = 2;
    
    private static Hashtable cmdtable = new Hashtable();

    static {
        cmdtable.put("base64::encode", new Base64Cmd(ENCODE,1,1));
        cmdtable.put("base64::decode", new Base64Cmd(DECODE,1,1));
    }
}
