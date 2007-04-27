/*
 * Copyright 2005-2007
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
import javax.microedition.lcdui.ItemCommandListener;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.StringThing;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

public abstract class ItemCmd extends OptionCmd {
    protected ItemCmd() {}

    public Thing cget(Interp ip,Object target,String optname)
	throws HeclException {
	Item theitem = (Item)target;
	
	if(optname.equals(WidgetInfo.NLABEL))
	    return StringThing.create(theitem.getLabel());
	if(optname.equals("-anchor"))
	    return WidgetInfo.fromItemAnchor(theitem.getLayout());
	if(optname.equals("-shrink"))
	    return IntThing.create(0 != (theitem.getLayout()&Item.LAYOUT_SHRINK));
	if(optname.equals("-vshrink"))
	    return IntThing.create(0 != (theitem.getLayout()&Item.LAYOUT_VSHRINK));
	if(optname.equals(WidgetInfo.NEXPAND))
	    return IntThing.create(0 != (theitem.getLayout()&Item.LAYOUT_EXPAND));
	if(optname.equals(WidgetInfo.NVEXPAND))
	    return IntThing.create(0 != (theitem.getLayout()&Item.LAYOUT_VEXPAND));
	if(optname.equals("-newlinebefore"))
	    return IntThing.create(0 != (theitem.getLayout()&Item.LAYOUT_NEWLINE_BEFORE));
	if(optname.equals("-newlineafter"))
	    return IntThing.create(0 != (theitem.getLayout()&Item.LAYOUT_NEWLINE_AFTER));
	if(optname.equals("-layout2"))
	    return IntThing.create(0 != (theitem.getLayout()&Item.LAYOUT_2));
	if(optname.equals(WidgetInfo.NMINWIDTH))
	    return IntThing.create(theitem.getMinimumWidth());
	if(optname.equals(WidgetInfo.NMINHEIGHT))
	    return IntThing.create(theitem.getMinimumHeight());
	if(optname.equals(WidgetInfo.NPREFERREDWIDTH))
	    return IntThing.create(theitem.getPreferredWidth());
	if(optname.equals(WidgetInfo.NPREFERREDHEIGHT))
	    return IntThing.create(theitem.getPreferredHeight());
	/*
	if(optname.equals("-defaultcommand")) {
	    WidgetInfo.setWidgetResult(ip,defcmd);
	    return;
	}
	*/
	if(optname.equals(WidgetInfo.NCOMMANDACTION)) {
	    throw new HeclException("option '"
				    +WidgetInfo.NCOMMANDACTION+"' is write only");
	}
	throw new HeclException("Unknown cget option '"+optname+"'");
    }

    public void cset(Interp ip,Object target,String optname,Thing optval)
	throws HeclException {
	Item theitem = (Item)target;
	
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
		c = WidgetInfo.asCommand(optval,true,true);
	    }
	    theitem.setDefaultCommand(c);
	    return;
	}
	if(optname.equals(WidgetInfo.NCOMMANDACTION)) {
	    ItemCommandListener listener = null;
	    if(optval.toString().length() > 0) {
		listener = new WidgetListener(ip,optval);
	    }
	    theitem.setItemCommandListener(listener);
	    return;
	}
	throw new HeclException("unknown configure option '"+optname+"'");
    }

    public Thing handlecmd(Interp ip,Object target,String subcmd,
			   Thing[] argv,int startat)
	throws HeclException {
	Item theitem = (Item)target;

	if(subcmd.equals(WidgetInfo.NADDCOMMAND)) {
	    int n = startat+1;
	    if(argv.length != n) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, subcmd+" command");
	    }
	    theitem.addCommand(WidgetInfo.asCommand(argv[2],false,true));
	    return null;
	}
	if(subcmd.equals(WidgetInfo.NREMOVECOMMAND)) {
	    int n = startat+1;
	    if(argv.length != n) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, subcmd+" command");
	    }
	    theitem.removeCommand(WidgetInfo.asCommand(argv[2],false,true));
	    return null;
	}
	throw new HeclException("Invalid command '"+subcmd+"'!");
    }

    
    private static void noItemOption(String optname) throws HeclException {
	throw new HeclException("Unknown item cget option '"+optname+"'");
    }
    
	    
    protected Thing itemcget(Interp ip,Object target,int itemno,String optname)
   	throws HeclException {
	noItemOption(optname);
	return null;
    }
    
    public void itemcset(Interp ip,Object target,int itemno,String optname,Thing optval)
	throws HeclException {
	noItemOption(optname);
    }
    
}

// Variables:
// mode:java
// coding:utf-8
// End:
