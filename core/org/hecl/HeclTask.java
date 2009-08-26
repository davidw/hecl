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
	this.tscript = script;
	this.tgeneration = generation;
	this.tname = (name != null ? name : "task") + sep + ++tasknum;
    }


    public Thing execute(Interp ip) {
	try {
	    //System.err.println("exec="+tscript.toString());
	    this.result = ip.eval(tscript,0);
	} catch(Exception e) {
	    this.error = e;
	    if(this.showbgerror) {
		try {
		    e.printStackTrace();
		    Vector v = new Vector();
		    v.addElement(new Thing("bgerror"));
		    v.addElement(new Thing(e.toString()));
		    ip.eval(ListThing.create(v),0);
		} catch(Exception e2) {
		    System.err.println("Hecl severe bg error: "+e.getMessage());
		    e2.printStackTrace();
		}
	    }
	}
	finally {
	    // awake all threads waiting for this task
	    //System.err.println("notify for "+this);
	    synchronized(this) {
		this.done = true;
		notifyAll();
	    }
	}
	return this.result;
    }


    public Exception getError() {return this.error;}
    
    public String getType() {
	return this.tname.substring(0,this.tname.lastIndexOf('#'));
    }
    
    public long getGeneration() {return this.tgeneration;}


    public String getName() {return this.tname;}


    public Thing getResult() {return this.result;}

    
    public Thing getScript() {return this.tscript;}

    public boolean isDone() {return this.done;}
    
    public void setErrorPrint(boolean onoff) {this.showbgerror = onoff;}

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
