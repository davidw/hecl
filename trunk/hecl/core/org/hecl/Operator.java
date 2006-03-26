/* Copyright 2006 Wolfgang S. Kechel

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

import java.util.Enumeration;
import java.util.Hashtable;

public abstract class Operator implements Command {

    protected Operator(int cmdcode,int minargs,int maxargs) {
	this.cmdcode = cmdcode;
	this.minargs = minargs;
	this.maxargs = maxargs;
    }

    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
	checkArgCount(argv);

	// Allow operator to set result on its own an return null to indicate
	// result has already been set. Of curse, this disallows returning
	// null as result of an operator, but this is not really an issue.
	RealThing rt = operate(cmdcode,interp,argv);
	if(rt != null)
	    interp.setResult(new Thing(rt));
    }

    public abstract RealThing operate(int cmdcode,Interp interp,Thing[] argv)
	throws HeclException;

    protected void checkArgCount(Thing[] argv) throws HeclException {
	int n = argv.length-1;		    // Ignore command name
	if(minargs >= 0 && n < minargs) {
	    throw new HeclException("Too few arguments, at least "
				    + minargs + " arguments required.");
	}

	if(maxargs >= 0 && n > maxargs) {
	    throw new HeclException("Bad argument count, max. "
				    + maxargs
				    +" arguments allowed.");
	}
    }

    protected static void load(Interp ip) throws HeclException {
	Enumeration e = cmdtable.keys();
	while(e.hasMoreElements()) {
	    String k = (String)e.nextElement();
	    ip.addCommand(k,(Command)cmdtable.get(k));
	}
    }

    protected static void unload(Interp ip) throws HeclException {
	Enumeration e = cmdtable.keys();
	while(e.hasMoreElements()) {
	    ip.removeCommand((String)e.nextElement());
	}
    }


    protected int cmdcode;
    protected int minargs;
    protected int maxargs;

    static protected Hashtable cmdtable = new Hashtable();
}
