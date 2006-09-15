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

package org.hecl.midp20.lcdui;

import org.hecl.Command;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Thing;

public abstract class OptionCmd implements org.hecl.Command {
    protected OptionCmd() {
    }
    
    public void cmdCode(Interp ip,Thing[] argv) throws HeclException {
	if(argv.length > 1) {
	    String subcmd = argv[1].toString().toLowerCase();
	    
	    //System.out.println("OptionCmd::cmdCode("+argv[0].toString() +", "+this+"), subcmd="+subcmd);
	    
	    if(subcmd.equals(WidgetInfo.NCGET)) {
		if(argv.length != 3) {
		    throw HeclException.createWrongNumArgsException(
			argv, 2, "option");
		}
		cget(ip,argv[2].toString());
		return;
	    }
	    if(subcmd.equals(WidgetInfo.NCONF)
	       || subcmd.equals(WidgetInfo.NCONFIGURE)) {
		configure(ip,argv,2,argv.length-2);
		return;
	    }
	    handlecmd(ip,subcmd,argv,2);
	    return;
	}
	throw HeclException.createWrongNumArgsException(argv, 2, "cmd [arg...]");
    }
 

    public void configure(Interp ip,Thing[] argv,int start,int n) 
	throws HeclException {
	if(n < 0 || n % 2 != 0) {
	    throw new HeclException("configure needs name-value pairs");
	}
	// deal with option/value pairs
	for(int i = start ; n > 0; n -= 2, i += 2) {
	    cset(ip,argv[i].toString().toLowerCase(),argv[i+1]);
	}
    }
    

    protected void cget(Interp ip,String optname) throws HeclException {
	throw new OptException(optname);
    }
    

    protected void cset(Interp ip,String optname,Thing optval) throws HeclException {
	throw new OptException(optname);
    }
    

    protected void itemcget(Interp ip,int itemno,String optname) throws HeclException {
	throw new ItemOptException("item cget "+optname);
    }
    

    protected void itemcset(Interp ip,int itemno,String optname,Thing optval)
	throws HeclException {
	throw new ItemOptException("item cset "+optname);
    }


    protected void handlecmd(Interp ip,String subcmd, Thing[] argv,int startat)
	throws HeclException {
	throw new SubCmdException(subcmd);
    }
}
