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

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Image;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.StringThing;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

public class ChoiceGroupCmd extends ItemCmd {
    public static void load(Interp ip) {
	ip.addCommand(CMDNAME,cmd);
	ip.addClassCmd(ChoiceGroup.class,cmd);
    }
    public static void unload(Interp ip) {
	ip.removeCommand(CMDNAME);
	ip.removeClassCmd(ChoiceGroup.class);
    }
    

    protected ChoiceGroupCmd() {}
    
    public Thing cmdCode(Interp interp,Thing[] argv) throws HeclException {
	Properties p = WidgetInfo.defaultProps(ChoiceGroup.class);
	p.setProps(argv,1);

	if (p.getProp(WidgetInfo.NTYPE).toString().equals("implicit")) {
	    throw new HeclException("ChoiceGroup can't be 'implicit'");
	}

	ChoiceGroup cg = new ChoiceGroup(p.getProp(WidgetInfo.NLABEL).toString(),
					 WidgetInfo.toChoiceType(p.getProp(WidgetInfo.NTYPE)));
	p.delProp(WidgetInfo.NLABEL);
	p.delProp(WidgetInfo.NTYPE);
	return ObjectThing.create(setInstanceProperties(interp,cg,p));
    }
    

    public Thing cget(Interp ip,Object target,String optname)
	throws HeclException {
	ChoiceGroup cg = (ChoiceGroup)target;
	
	/*
	if(optname.equals(WidgetInfo.NTYPE))
	    return WidgetInfo.fromChoiceType(cg.getType());
	*/
	if(optname.equals(WidgetInfo.NFIT))
	    return WidgetInfo.fromWrap(cg.getFitPolicy());
	return super.cget(ip,target,optname);
    }


    public void cset(Interp ip,Object target,String optname,Thing optval)
	throws HeclException {
	ChoiceGroup cg = (ChoiceGroup)target;
	
	if(optname.equals(WidgetInfo.NFIT)) {
	    cg.setFitPolicy(WidgetInfo.toWrap(optval));
	    return;
	}
	super.cset(ip,target,optname,optval);
    }


    public Thing itemcget(Interp ip,Object target,int itemno,String optname)
	throws HeclException {
	ChoiceGroup cg = (ChoiceGroup)target;
	
	if(optname.equals(WidgetInfo.NFONT))
	    return FontMap.fontThing(cg.getFont(itemno));
	if(optname.equals(WidgetInfo.NTEXT))
	    return StringThing.create(cg.getString(itemno));
	if(optname.equals(WidgetInfo.NIMAGE))
	    return ObjectThing.create(cg.getImage(itemno));
	if(optname.equals("-selected"))
	    return IntThing.create(cg.isSelected(itemno));
	return super.itemcget(ip,target,itemno,optname);
    }


    public void itemcset(Interp ip,Object target,int itemno,String optname,Thing optval)
	throws HeclException {
	ChoiceGroup cg = (ChoiceGroup)target;
	
	if(optname.equals(WidgetInfo.NFONT)) {
	    cg.setFont(itemno,FontMap.get(optval));
	    return;
	}
	if(optname.equals(WidgetInfo.NTEXT)) {
	    cg.set(itemno,optval.toString(),cg.getImage(itemno));
	    return;
	}
	if(optname.equals(WidgetInfo.NIMAGE)) {
	    cg.set(itemno,cg.getString(itemno),WidgetInfo.asImage(optval,true,true));
	    return;
	}
	if(optname.equals("-selected")) {
	    cg.setSelectedIndex(itemno,HeclUtils.thing2bool(optval));
	    return;
	}
	super.itemcset(ip,target,itemno,optname,optval);
    }
	
	
    public Thing handlecmd(Interp ip,Object target,String subcmd,
			   Thing[] argv,int startat)
	throws HeclException {
	ChoiceGroup cg = (ChoiceGroup)target;
	
	//System.err.println("-->ChoiceGroupCmd::handlecmd("+subcmd+")\n\tcg="+cg);

	if(subcmd.equals("selection")) {
	    int n = startat+1;
	    if(argv.length < n) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, "selection <clear>|<index>|<get>|<set index...>");
	    }
	    String selcmd = argv[startat].toString();
	    
	    /*
	    if(selcmd.equals("index")) {
		ip.setResult(IntThing.create(l.getSelectedIndex()));
		return null;
	    }
	    */
	    if(selcmd.equals("get")) {
		int m = cg.size();
		boolean[] b = new boolean[m];
		cg.getSelectedFlags(b);
		Vector v = new Vector();
		for(int i=0; i<m; ++i) {
		    if(b[i])
			v.addElement(IntThing.create(i));
		}
		return ListThing.create(v);
	    }
	    if(selcmd.equals("set")) {
		// Need index arg.
		++n;
		++startat;
		if(argv.length != n) {
		    throw HeclException.createWrongNumArgsException(
			argv, n, "selection set index");
		}
		cg.setSelectedIndex(HeclUtils.thing2int(argv[startat],true,0),true);
		return null;
	    }
	    if(selcmd.equals("clear")) {
		// Need index arg.
		++n;
		++startat;
		if(startat+1 < argv.length) {
		    cg.setSelectedIndex(HeclUtils.thing2int(argv[startat],true,0),false);
		} else {
		    // clear all selected values
		    int lsize = cg.size();
		    boolean sels[] = new boolean[lsize];
		    for(int i=0; i<lsize; ++i) {
			sels[i] = false;
		    }
		    cg.setSelectedFlags(sels);
		}
		return null;
	    }
	    // unknown selection subcmd
	    return null;
	}
	if(subcmd.equals("size"))
	    return IntThing.create(cg.size());
	if(subcmd.startsWith("item",0)) {
	    // item<something> requires an item as parameter
	    int n = startat+1;

	    if(argv.length < n) {
		throw HeclException.createWrongNumArgsException(
		    argv, n-1, "itemcget optname or itemconfigure [optname optval] ...");
	    }

	    int itempos = HeclUtils.thing2int(argv[n-1],true,0);
	    if(itempos < 0 || itempos >= cg.size()) {
		throw new IndexOutOfBoundsException("Invalid ChoiceGroup element '"
						    +itempos+"'.");
	    }
	    //System.out.println("ChoiceGroup element#="+itempos);

	    if(subcmd.equals("itemconfigure")) {
		for(int i = n; i<argv.length; i+= 2) {
		    itemcset(ip,cg,itempos,argv[i].toString(),argv[i+1]);
		}
		return null;
	    }
	    if(subcmd.equals("itemcget")) {
		++n;
		if(argv.length != n) {
		    throw HeclException.createWrongNumArgsException(
			argv, n, "item itemoptname");
		}
		return itemcget(ip,cg,itempos,argv[n-1].toString());
	    }
	}
	if(subcmd.equals("delete")) {
	    int n = startat+1;
	    if(argv.length != n) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, "item");
	    }
	    int itempos = HeclUtils.thing2int(argv[n-1],true,0);
	    cg.delete(itempos);
	    return null;
	}
	if(subcmd.equals("deleteall")) {
	    cg.deleteAll();
	    return null;
	}

	if(subcmd.equals("append")) {
	    if(startat+1 != argv.length && startat+2 != argv.length) {
		throw HeclException.createWrongNumArgsException(
		    argv, startat, subcmd+" string [image]");
	    }
	    cg.append(argv[startat].toString(),
		      startat+1 < argv.length ?
		      GUICmds.asImage(argv[startat+1],true) : null);
	    return null;
	}

	if(subcmd.equals("insert")) {
	    int n = startat + 2;
	    if(argv.length != n) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, "item string [image]");
	    }
	    int itemno = HeclUtils.thing2int(argv[startat],true,0);
	    cg.insert(itemno,argv[startat+1].toString(),
		     null/*thing2image(argv[startat+2])*/);
	    return null;
	}
	return super.handlecmd(ip,target,subcmd,argv,startat);
    }

    private static ChoiceGroupCmd cmd = new ChoiceGroupCmd();
    private static final String CMDNAME = "lcdui.choicegroup";
}

// Variables:
// mode:java
// coding:utf-8
// End:
