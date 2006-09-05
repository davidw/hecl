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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Item;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

public abstract class FormGadget implements Gadget {
    protected FormGadget(Item i,FormCmd owner) {
	theitem = i;
	formcmd = owner;
    }

    
    public Item getItem() {
	return theitem;
    }
    

    public boolean isOwnedBy(FormCmd f) {
	return formcmd == f;
    }
    

    public void configure(Interp ip,Thing[] argv,int start,int n)
	throws HeclException {
	int count = n-start;
	if(count < 0 || count % 2 != 0) {
	    throw new HeclException("configure needs name-value pairs");
	}
	// deal with option/value pairs
	for(int i = start ; i<n; i += 2) {
	    //System.err.println("formgadget.cset for "+argv[i].toString() +", val="+argv[i+1].toString());
	    cset(ip,argv[i].toString(),argv[i+1]);
	}
    }
    
    public void cget(Interp ip,String optname) throws HeclException {
	if(optname.equals(WidgetInfo.NLABEL)) {
	    ip.setResult(theitem.getLabel());
	    return;
	}
	if(optname.equals("-anchor")) {
	    ip.setResult(WidgetInfo.fromItemAnchor(theitem.getLayout()));
	    return;
	}
	if(optname.equals("-shrink")) {
	    ip.setResult(0 != (theitem.getLayout()&Item.LAYOUT_SHRINK));
	    return;
	}
	if(optname.equals("-vshrink")) {
	    ip.setResult(0 != (theitem.getLayout()&Item.LAYOUT_VSHRINK));
	    return;
	}
	if(optname.equals(WidgetInfo.NEXPAND)) {
	    ip.setResult(0 != (theitem.getLayout()&Item.LAYOUT_EXPAND));
	    return;
	}
	if(optname.equals(WidgetInfo.NVEXPAND)) {
	    ip.setResult(0 != (theitem.getLayout()&Item.LAYOUT_VEXPAND));
	    return;
	}
	if(optname.equals("-newlinebefore")) {
	    ip.setResult(0 != (theitem.getLayout()&Item.LAYOUT_NEWLINE_BEFORE));
	    return;
	}
	if(optname.equals("-newlineafter")) {
	    ip.setResult(0 != (theitem.getLayout()&Item.LAYOUT_NEWLINE_AFTER));
	    return;
	}
	if(optname.equals("-layout2")) {
	    ip.setResult(0 != (theitem.getLayout()&Item.LAYOUT_2));
	    return;
	}
	if(optname.equals(WidgetInfo.NMINWIDTH)) {
	    ip.setResult(theitem.getMinimumWidth());
	    return;
	}
	if(optname.equals(WidgetInfo.NMINHEIGHT)) {
	    ip.setResult(theitem.getMinimumHeight());
	    return;
	}
	if(optname.equals(WidgetInfo.NPREFERREDWIDTH)) {
	    ip.setResult(theitem.getPreferredWidth());
	    return;
	}
	if(optname.equals(WidgetInfo.NPREFERREDHEIGHT)) {
	    ip.setResult(theitem.getPreferredHeight());
	    return;
	}
	if(optname.equals("-defaultcommand")) {
	    WidgetMap.setWidgetResult(ip,defcmd);
	    return;
	}
	if(optname.equals("-changedcallback")) {
	    ip.setResult(changedcallback != null ? changedcallback : "");
	    return;
	}
	throw new HeclException("Unknown cget option '"+optname+"'");
    }


    public void cset(Interp ip,String optname,Thing optval) throws HeclException {
	if(optname.equals(WidgetInfo.NLABEL)) {
	    theitem.setLabel(optval.toString());
	    return;
	}
	if(optname.equals("-anchor")) {
	    int t = theitem.getLayout() & ~0x33;
	    theitem.setLayout(t | WidgetInfo.toItemAnchor(optval));
	    return;
	}
	if(optname.equals("-shrink")) {
	    int t = theitem.getLayout() & ~Item.LAYOUT_SHRINK;
	    theitem.setLayout(t | (HeclUtils.thing2bool(optval) ? Item.LAYOUT_SHRINK : 0));
	    return;
	}
	if(optname.equals("-vshrink")) {
	    int t = theitem.getLayout() & ~Item.LAYOUT_VSHRINK;
	    theitem.setLayout(t | (HeclUtils.thing2bool(optval) ? Item.LAYOUT_VSHRINK : 0));
	    return;
	}
	if(optname.equals(WidgetInfo.NEXPAND)) {
	    int t = theitem.getLayout() & ~Item.LAYOUT_EXPAND;
	    theitem.setLayout(t | (HeclUtils.thing2bool(optval) ? Item.LAYOUT_EXPAND : 0));
	    return;
	}
	if(optname.equals(WidgetInfo.NVEXPAND)) {
	    int t = theitem.getLayout() & ~Item.LAYOUT_VEXPAND;
	    theitem.setLayout(t | (HeclUtils.thing2bool(optval) ? Item.LAYOUT_VEXPAND : 0));
	    return;
	}
	if(optname.equals("-newlinebefore")) {
	    int t = theitem.getLayout() & ~Item.LAYOUT_NEWLINE_BEFORE;
	    theitem.setLayout(t | (HeclUtils.thing2bool(optval) ? Item.LAYOUT_NEWLINE_BEFORE : 0));
	    return;
	}
	if(optname.equals("-newlineafter")) {
	    int t = theitem.getLayout() & ~Item.LAYOUT_NEWLINE_AFTER;
	    theitem.setLayout(t | (HeclUtils.thing2bool(optval) ? Item.LAYOUT_NEWLINE_AFTER : 0));
	    return;
	}
	if(optname.equals("-layout2")) {
	    int t = theitem.getLayout() & ~Item.LAYOUT_2;
	    theitem.setLayout(t | (HeclUtils.thing2bool(optval) ? Item.LAYOUT_2 : 0));
	    return;
	}
	if(optname.equals(WidgetInfo.NPREFERREDWIDTH)) {
	    theitem.setPreferredSize(IntThing.get(optval),theitem.getPreferredHeight());
	    return;
	}
	if(optname.equals(WidgetInfo.NPREFERREDHEIGHT)) {
	    theitem.setPreferredSize(theitem.getPreferredWidth(),IntThing.get(optval));
	    return;
	}
	//if(optname.equals("-preferredsize")) {
	//theitem.setLayout();
	//  return;
	//}
	if(optname.equals("-defaultcommand")) {
	    String s = optval.toString();
	    Command c = null;
	    if(0 != s.length()) {
		c = WidgetMap.mapOf(ip).asCommand(optval,true,true);
	    }
	    defcmd = c;
	    theitem.setDefaultCommand(c);
	    return;
	}
	if(optname.equals("-changedcallback")) {
	    changedcallback = optval.toString();
	    if(changedcallback.length() == 0)
		changedcallback = null;
	    //System.err.println("*** set to " + changedcallback);
	    return;
	}
	throw new HeclException("unknown configure option '"+optname+"'");
    }
    

    public void itemcget(Interp ip,int itemno,String optname) throws HeclException {
	throw new HeclException("Unknown item cget option '"+optname+"'");
    }
    

    public void itemcset(Interp ip,int itemno,String optname,Thing optval)
	throws HeclException {
	throw new HeclException("unknown item option '"+optname+"'");
    }


    public void handlecmd(Interp ip,String subcmd, Thing[] argv,int startat)
	throws HeclException {
	WidgetMap wm = WidgetMap.mapOf(ip);
	
	if(subcmd.equals(WidgetInfo.NADDCOMMAND)) {
	    int n = startat+1;
	    if(argv.length != n) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, subcmd+" command");
	    }
	    theitem.addCommand(wm.asCommand(argv[2],false,true));
	    return;
	}
	if(subcmd.equals(WidgetInfo.NREMOVECOMMAND)) {
	    int n = startat+1;
	    if(argv.length != n) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, subcmd+" command");
	    }
	    Command c = wm.asCommand(argv[2],false,true);
	    if(c == defcmd) {
		defcmd = null;
	    }
	    theitem.removeCommand(c);
	    return;
	}
	throw new HeclException("Invalid command '"+subcmd+"'!");
    }

    
    public String getChangedCallback() {
	return changedcallback;
    }

    public String getCommandAction() {
	return commandaction;
    }
    

    
    protected Item theitem;
    protected FormCmd formcmd;
    protected Command defcmd = null;
    protected String changedcallback = null;
    protected String commandaction = null;
}
