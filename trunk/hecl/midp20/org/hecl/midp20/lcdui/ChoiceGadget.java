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

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Image;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

public class ChoiceGadget extends FormGadget {
    public ChoiceGadget(String label,int choiceType,FormCmd f) {
	super(new ChoiceGroup(label,choiceType),f);
	choicetype = choiceType;
    }


    public void cget(Interp ip,String optname) throws HeclException {
	ChoiceGroup cg = (ChoiceGroup)theitem;
	
	if(optname.equals(WidgetInfo.NTYPE)) {
	    ip.setResult(WidgetInfo.fromChoiceType(choicetype));
	    return;
	}
	if(optname.equals(WidgetInfo.NFIT)) {
	    ip.setResult(WidgetInfo.fromWrap(cg.getFitPolicy()));
	    return;
	}
	super.cget(ip,optname);
    }


    public void cset(Interp ip,String optname,Thing optval) throws HeclException {
	ChoiceGroup cg = (ChoiceGroup)theitem;
	
	if(optname.equals(WidgetInfo.NFIT)) {
	    cg.setFitPolicy(WidgetInfo.toWrap(optval));
	    return;
	}
	super.cset(ip,optname,optval);
    }


    public void itemcget(Interp ip,int itemno,String optname) throws HeclException {
	ChoiceGroup cg = (ChoiceGroup)theitem;
	
	if(optname.equals(WidgetInfo.NFONT)) {
	    FontMap.setResult(ip,cg.getFont(itemno));
	    return;
	}
	if(optname.equals(WidgetInfo.NTEXT)) {
	    ip.setResult(cg.getString(itemno));
	    return;
	}
	if(optname.equals(WidgetInfo.NIMAGE)) {
	    ip.setResult(ImageMap.mapOf(ip).nameOf(cg.getImage(itemno)));
	    return;
	}
	if(optname.equals("-selected")) {
	    ip.setResult(cg.isSelected(itemno));
	    return;
	}
	super.itemcget(ip,itemno,optname);
    }


    public void itemcset(Interp ip,int itemno,String optname,Thing optval)
	throws HeclException {
	ChoiceGroup cg = (ChoiceGroup)theitem;
	
	if(optname.equals(WidgetInfo.NFONT)) {
	    cg.setFont(itemno,FontMap.get(optval));
	    return;
	}
	if(optname.equals(WidgetInfo.NTEXT)) {
	    cg.set(itemno,optval.toString(),cg.getImage(itemno));
	    return;
	}
	if(optname.equals(WidgetInfo.NIMAGE)) {
	    //l.set(itemno,cg.toString(itemno),thing2image(optval));
	    return;
	}
	if(optname.equals("-selected")) {
	    cg.setSelectedIndex(itemno,HeclUtils.thing2bool(optval));
	    return;
	}
	super.itemcset(ip,itemno,optname,optval);
    }
	
	
    public void handlecmd(Interp ip,String subcmd, Thing[] argv,int startat)
	throws HeclException {
	ChoiceGroup cg = (ChoiceGroup)theitem;
	
	//System.err.println("-->ChoiceGadget::handlecmd("+subcmd+")\n\tcg="+cg);

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
		return;
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
		cg.setSelectedIndex(HeclUtils.thing2int(argv[startat],true,0),true);
		return;
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
		return;
	    }
	    // unknown selection subcmd
	    return;
	}
	if(subcmd.equals("size")) {
	    ip.setResult(cg.size());
	    return;
	}
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
		    itemcset(ip,itempos,argv[i].toString(),argv[i+1]);
		}
		return;
	    }
	    if(subcmd.equals("itemcget")) {
		++n;
		if(argv.length != n) {
		    throw HeclException.createWrongNumArgsException(
			argv, n, "item itemoptname");
		}
		itemcget(ip,itempos,argv[n-1].toString());
		return;
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
	    return;
	}
	if(subcmd.equals("deleteall")) {
	    cg.deleteAll();
	    return;
	}
	if(subcmd.equals("append")) {
	    if(startat+1 != argv.length && startat+2 != argv.length) {
		throw HeclException.createWrongNumArgsException(
		    argv, startat, subcmd+" string [image]");
	    }
	    cg.append(argv[startat].toString(),
		      startat+1 < argv.length ?
		      ImageMap.asImage(ip,argv[startat+1],true) : null);
	    return;
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
	    return;
	}
	super.handlecmd(ip,subcmd,argv,startat);
    }

    protected int choicetype;
}
