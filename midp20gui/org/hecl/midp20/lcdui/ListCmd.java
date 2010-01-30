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

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.StringThing;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

public class ListCmd extends ScreenCmd {
    public static void load(Interp ip) {
	ip.addCommand(CMDNAME,cmd);
	ip.addClassCmd(List.class,cmd);
    }
    public static void unload(Interp ip) {
	ip.removeCommand(CMDNAME);
	ip.removeClassCmd(List.class);
    }
    
    public Thing cmdCode(Interp interp,Thing[] argv) throws HeclException {
	Properties p = WidgetInfo.defaultProps(List.class);
	p.setProps(argv,1);
	int listtype = WidgetInfo.toListType(p.getProp(WidgetInfo.NTYPE));
	List w = new List(p.getProp(WidgetInfo.NTITLE).toString(),listtype);
	p.delProp(WidgetInfo.NTYPE);
	p.delProp(WidgetInfo.NTITLE);
	return ObjectThing.create(setInstanceProperties(interp,w,p));
    }

    protected ListCmd() {}


    public Thing cget(Interp ip,Object target,String optname) throws HeclException {
	List l = (List)target;
	
	if(optname.equals(WidgetInfo.NFIT)) {
	    return WidgetInfo.fromWrap(l.getFitPolicy());
	}
//#ifdef notdef
	if(optname.equals(WidgetInfo.NTYPE))
	    return WidgetInfo.fromChoiceType(l.getListtype());
	if(optname.equals("-selectcommand"))
	    return ObjectThing.create(l.getSelectCommand());
//#endif
	return super.cget(ip,target,optname);
    }

    public void cset(Interp ip,Object target,String optname,Thing optval)
	throws HeclException {
	List l = (List)target;

	if(optname.equals(WidgetInfo.NFIT)) {
	    l.setFitPolicy(WidgetInfo.toWrap(optval));
	    return;
	}
	if(optname.equals("-selectcommand")) {
	    String s = optval.toString();
	    Command c = null;
	    if(0 != s.length()) {
		c = WidgetInfo.asCommand(optval,true,true);
	    }
	    l.setSelectCommand(c);
	    return;
	}
	super.cset(ip,target,optname,optval);
    }

    public Thing itemcget(Interp ip,Object target,int itemno,String optname)
	throws HeclException {
	List l = (List)target;

	if(optname.equals(WidgetInfo.NFONT))
	    return FontMap.fontThing(l.getFont(itemno));
	if(optname.equals(WidgetInfo.NTEXT))
	    return StringThing.create(l.getString(itemno));
	if(optname.equals(WidgetInfo.NIMAGE))
	    return ObjectThing.create(l.getImage(itemno));
	if(optname.equals(WidgetInfo.NSELECTION))
	    return IntThing.create(l.isSelected(itemno));
	return super.itemcget(ip,target,itemno,optname);
    }

    public void itemcset(Interp ip,Object target,int itemno,String optname,Thing optval)
	throws HeclException {
	List l = (List)target;
	    
	if(optname.equals(WidgetInfo.NFONT)) {
	    l.setFont(itemno,FontMap.get(optval));
	    return;
	}
	if(optname.equals(WidgetInfo.NTEXT)) {
	    l.set(itemno,optval.toString(),l.getImage(itemno));
	    return;
	}
	if(optname.equals(WidgetInfo.NIMAGE)) {
	    l.set(itemno,l.getString(itemno),GUICmds.asImage(optval,true));
	    return;
	}
	if(optname.equals(WidgetInfo.NSELECTION)) {
	    l.setSelectedIndex(itemno,HeclUtils.thing2bool(optval));
	    return;
	}
	super.itemcset(ip,target,itemno,optname,optval);
    }
	
	
    public Thing handlecmd(Interp ip,Object target,String subcmd,
			   Thing[] argv,int startat)
	throws HeclException {
	List l = (List)target;

	//System.err.println("\tl="+l);

	if(subcmd.equals("selection")) {
	    int n = startat+1;
	    if(0 != HeclUtils.testArguments(argv,n,-1)) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, "selection <clear>|<index>|<get>|<set index...>");
	    }
	    String selcmd = argv[startat].toString();
	    
	    if(selcmd.equals("index"))
		return IntThing.create(l.getSelectedIndex());

	    if(selcmd.equals("get")) {
		int m = l.size();
		boolean[] b = new boolean[m];
		l.getSelectedFlags(b);
		Vector v = new Vector();
		int sel = l.getSelectedIndex(); //added by rnebel
		for(int i=0; i<m; ++i) {
		    if(b[i] || sel == i)
			v.addElement(IntThing.create(i));
		}

		return ListThing.create(v);
	    }
	    if(selcmd.equals("gettext")) {
		return new Thing(l.getString(l.getSelectedIndex()));
	    }
	    if(selcmd.equals("set")) {
		// Need index arg.
		++n;
		++startat;
		if(argv.length != n) {
		    throw HeclException.createWrongNumArgsException(
			argv, n, "selection set index");
		}
		l.setSelectedIndex(HeclUtils.thing2int(argv[startat],true,0),true);
		return null;
	    }
	    if(selcmd.equals("clear")) {
		// Need index arg.
		++n;
		++startat;
		if(startat+1 < argv.length) {
		    l.setSelectedIndex(HeclUtils.thing2int(argv[startat],true,0),false);
		} else {
		    // clear all selected values
		    int lsize = l.size();
		    boolean sels[] = new boolean[lsize];
		    for(int i=0; i<lsize; ++i) {
			sels[i] = false;
		    }
		    l.setSelectedFlags(sels);
		}
		return null;
	    }
	    // unknown selection subcmd
	    return null;
	}
	if(subcmd.equals("size"))
	    return IntThing.create(l.size());
	if(subcmd.startsWith("item",0)) {
	    // item<something> requires an item as parameter
	    int n = startat+1;

	    if(0 != HeclUtils.testArguments(argv,n,-1)) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, subcmd+" item [optname optval] ...");
	    }

	    int itempos = HeclUtils.thing2int(argv[n-1],true,0);
	    if(itempos < 0 || itempos >= l.size()) {
		throw new IndexOutOfBoundsException("Invalid item number +'"
						    +itempos+"'.");
	    }

	    //System.out.println("Itempos="+itempos);

	    if(subcmd.equals("itemconfigure")) {
		for(int i = n; i<argv.length; i+= 2) {
		    itemcset(ip,target,itempos,argv[i].toString(),argv[i+1]);
		}
		return null;
	    } else if(subcmd.equals("itemcget")) {
		++n;
		if(argv.length != n) {
		    throw HeclException.createWrongNumArgsException(
			argv, n, "itemcget item itemoptname");
		}
		return itemcget(ip,target,itempos,argv[n-1].toString());
	    }
	}
	if(subcmd.equals("delete")) {
	    if(startat+1 != argv.length) {
		throw HeclException.createWrongNumArgsException(
		    argv, startat+1, "delete item");
	    }
	    l.delete(HeclUtils.thing2int(argv[startat],true,0));
	    return null;
	}
	if(subcmd.equals("deleteall")) {
	    l.deleteAll();
	    return null;
	}
	if(subcmd.equals("append")) {
	    if(startat+1 != argv.length && startat+2 != argv.length) {
		throw HeclException.createWrongNumArgsException(
		    argv, startat, subcmd+" string [image]");
	    }
	    l.append(argv[startat].toString(),
		     startat+1 < argv.length ?
		     GUICmds.asImage(argv[startat+1],true) : null);
	    return null;
	}
	if(subcmd.equals("insert")) {
	    if(startat+1 != argv.length && startat+2 != argv.length) {
		throw HeclException.createWrongNumArgsException(
		    argv, startat+1, subcmd+" item string [image]");
	    }
	    l.insert(HeclUtils.thing2int(argv[startat],true,0),
		     argv[startat+1].toString(),
		     startat+1 < argv.length ?
		     GUICmds.asImage(argv[startat+1],true) : null);
	    return null;
	}
	return super.handlecmd(ip,target,subcmd,argv,startat);
    }

    private static ListCmd cmd = new ListCmd();
    private static final String CMDNAME = "lcdui.list";
}

// Variables:
// mode:java
// coding:utf-8
// End:
