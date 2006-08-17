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

import java.util.Vector;

public class HeclTask {
    protected final char sep = '#';
    
    public HeclTask(Thing script,long generation,String name) {
	tscript = script;
	tgeneration = generation;
	tname = (name != null ? name : "task") + sep + ++tasknum;
    }


    public void execute(Interp ip) {
	try {
	    //System.err.println("exec="+tscript.toString());
	    ip.eval(tscript);
	    result = ip.getResult();
	}
	catch(Exception e) {
	    error = e;
	    if(showbgerror) {
		try {
		    Vector v = new Vector();
		    v.addElement(new Thing("bgerror"));
		    v.addElement(new Thing(e.getMessage()));
		    ip.eval(ListThing.create(v));
		}
		catch(Exception e2) {
		    System.err.println("Hecl severe bg error: "+e.getMessage());
		    e2.printStackTrace();
		}
	    }
	}
	finally {
	    // awake all threads waiting for this task
	    //System.err.println("notify for "+this);
	    synchronized(this) {
		done = true;
		notifyAll();
	    }
	}
    }


    public Exception getError() {
	return error;
    }
    
    public String getType() {
	return tname.substring(0,tname.lastIndexOf('#'));
    }
    
    public long getGeneration() {
	return tgeneration;
    }


    public String getName() {
	return tname;
    }


    public Thing getResult() {
	return result;
    }

    
    public Thing getScript() {
	return tscript;
    }

    public boolean isDone() {
	return done;
    }
    
    public void setErrorPrint(boolean onoff) {
	showbgerror = onoff;
    }

    /*
    public void setScript(Thing script) {
	tscript = script;
    }
    */

    protected Thing tscript;		    // scriot to eval
    protected long tgeneration;		    // generation #, timestamp
    protected String tname;		    // task name
    protected Thing result = null;	    // result of execution
    protected Exception error = null;
    protected boolean showbgerror = true;
    protected volatile boolean done = false;
    
    protected static long tasknum;
}
