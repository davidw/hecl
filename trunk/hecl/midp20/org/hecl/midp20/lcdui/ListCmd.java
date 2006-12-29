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

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.Properties;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

public class ListCmd extends ScreenCmd {
    public static final org.hecl.Command CREATE = new org.hecl.Command() {
	    public void cmdCode(Interp interp,Thing[] argv) throws HeclException {
		Properties p = WidgetInfo.defaultProps(List.class);
		p.setProps(argv,1);
		int listtype = WidgetInfo.toListType(p.getProp(WidgetInfo.NTYPE));
		List w = new List(p.getProp(WidgetInfo.NTITLE).toString(),listtype);
		p.delProp(WidgetInfo.NTYPE);
		p.delProp(WidgetInfo.NTITLE);
		WidgetMap.addWidget(interp,null,w,new ListCmd(interp,w,p,listtype));
	    }
	};


    protected ListCmd(Interp ip,List a,Properties p,int lt) throws HeclException {
	super(ip,a,p);
	listtype = lt;
    }


    public void cget(Interp ip,String optname) throws HeclException {
	List l = (List)getData();
	
	if(optname.equals(WidgetInfo.NTYPE)) {
	    ip.setResult(WidgetInfo.fromChoiceType(listtype));
	    return;
	}
	if(optname.equals(WidgetInfo.NFIT)) {
	    ip.setResult(WidgetInfo.fromWrap(l.getFitPolicy()));
	    return;
	}
	if(optname.equals("-selectcommand")) {
	    WidgetMap.setWidgetResult(ip,selectcommand/*l.getSelectCommand()*/);
	    return;
	}
	super.cget(ip,optname);
    }

    public void cset(Interp ip,String optname,Thing optval) throws HeclException {
	List l = (List)getData();

	if(optname.equals(WidgetInfo.NFIT)) {
	    l.setFitPolicy(WidgetInfo.toWrap(optval));
	    return;
	}
	if(optname.equals("-selectcommand")) {
	    String s = optval.toString();
	    Command c = null;
	    if(0 != s.length()) {
		c = WidgetMap.mapOf(ip).asCommand(optval,true,true);
	    }
	    l.setSelectCommand(c);
	    selectcommand = c;
	    return;
	}
	super.cset(ip,optname,optval);
    }

    public void itemcget(Interp ip,int itemno,String optname) throws HeclException {
	List l = (List)getData();

	if(optname.equals(WidgetInfo.NFONT)) {
	    FontMap.setResult(ip,l.getFont(itemno));
	    return;
	}
	if(optname.equals(WidgetInfo.NTEXT)) {
	    ip.setResult(l.getString(itemno));
	    return;
	}
	if(optname.equals(WidgetInfo.NIMAGE)) {
	    ip.setResult(ImageMap.mapOf(ip).nameOf(l.getImage(itemno)));
	    return;
	}
	if(optname.equals(WidgetInfo.NSELECTION)) {
	    ip.setResult(l.isSelected(itemno));
	    return;
	}
	super.itemcget(ip,itemno,optname);
    }

    public void itemcset(Interp ip,int itemno,String optname,Thing optval)
	throws HeclException {
	List l = (List)getData();
	    
	if(optname.equals(WidgetInfo.NFONT)) {
	    l.setFont(itemno,FontMap.get(optval));
	    return;
	}
	if(optname.equals(WidgetInfo.NTEXT)) {
	    l.set(itemno,optval.toString(),l.getImage(itemno));
	    return;
	}
	if(optname.equals(WidgetInfo.NIMAGE)) {
	    l.set(itemno,l.getString(itemno),ImageMap.asImage(ip,optval,true));
	    return;
	}
	if(optname.equals(WidgetInfo.NSELECTION)) {
	    l.setSelectedIndex(itemno,HeclUtils.thing2bool(optval));
	    return;
	}
	super.itemcset(ip,itemno,optname,optval);
    }
	
	
    public void handlecmd(Interp ip,String subcmd, Thing[] argv,int startat)
	throws HeclException {
	List l = (List)getData();

	//System.err.println("\tl="+l);

	if(subcmd.equals("selection")) {
	    int n = startat+1;
	    if(0 != HeclUtils.testArguments(argv,n,-1)) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, "selection <clear>|<index>|<get>|<set index...>");
	    }
	    String selcmd = argv[startat].toString();
	    
	    /*
	    if(selcmd.equals("index")) {
		ip.setResult(IntThing.create(l.getSelectedIndex()));
		return;
	    }
	    */
	    if(selcmd.equals("get")) {
		Vector v = new Vector();
		boolean[] b = new boolean[l.size()];
		if(l.getSelectedFlags(b) > 0) {
		    for(int i=0; i<b.length; ++i) {
			if(b[i])
			    v.addElement(IntThing.create(i));
		    }
		} else {
		    if(l.getSelectedIndex() >= 0)
			v.addElement(IntThing.create(l.getSelectedIndex()));
		}
		ip.setResult(ListThing.create(v));
		return;
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
		return;
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
		return;
	    }
	    // unknown selection subcmd
	    return;
	}
	if(subcmd.equals("size")) {
	    ip.setResult(l.size());
	    return;
	}
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
		    itemcset(ip,itempos,argv[i].toString(),argv[i+1]);
		}
		return;
	    } else if(subcmd.equals("itemcget")) {
		++n;
		if(argv.length != n) {
		    throw HeclException.createWrongNumArgsException(
			argv, n, "itemcget item itemoptname");
		}
		itemcget(ip,itempos,argv[n-1].toString());
		return;
	    }
	}
	if(subcmd.equals("delete")) {
	    if(startat+1 != argv.length) {
		throw HeclException.createWrongNumArgsException(
		    argv, startat+1, "delete item");
	    }
	    l.delete(HeclUtils.thing2int(argv[startat],true,0));
	    return;
	}
	if(subcmd.equals("deleteall")) {
	    l.deleteAll();
	    return;
	}
	if(subcmd.equals("append")) {
	    if(startat+1 != argv.length && startat+2 != argv.length) {
		throw HeclException.createWrongNumArgsException(
		    argv, startat, subcmd+" string [image]");
	    }
	    l.append(argv[startat].toString(),
		     startat+1 < argv.length ?
		     ImageMap.asImage(ip,argv[startat+1],true) : null);
	    return;
	}
	if(subcmd.equals("insert")) {
	    if(startat+1 != argv.length && startat+2 != argv.length) {
		throw HeclException.createWrongNumArgsException(
		    argv, startat+1, subcmd+" item string [image]");
	    }
	    l.insert(HeclUtils.thing2int(argv[startat],true,0),
		     argv[startat+1].toString(),
		     startat+1 < argv.length ?
		     ImageMap.asImage(ip,argv[startat+1],true) : null);
	    return;
	}
	super.handlecmd(ip,subcmd,argv,startat);
    }

    protected int listtype;
    protected Command selectcommand = null;
}
