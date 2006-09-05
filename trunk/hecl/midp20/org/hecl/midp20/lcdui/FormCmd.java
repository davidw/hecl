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
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.Spacer;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Spacer;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.Properties;
import org.hecl.Thing;

import org.hecl.midp20.MidletCmd;
import org.hecl.misc.HeclUtils;

public class FormCmd extends DisplayableCmd {

    public static final org.hecl.Command CREATE = new org.hecl.Command() {
	    public void cmdCode(Interp interp,Thing[] argv) throws HeclException {
		Properties p = WidgetInfo.defaultProps(Form.class);
		p.setProps(argv,1);
		Form w = new Form(p.getProp(WidgetInfo.NTITLE).toString());
		p.delProp(WidgetInfo.NTITLE);
		WidgetMap.addWidget(interp,null,w,new FormCmd(interp,w,p));
	    }
	};


    protected FormCmd(final Interp ip,Form form,Properties p) throws HeclException {
	super(ip,form,p);
	gadgets = new Vector();
	form.setItemStateListener(new ItemStateListener() {
		public void itemStateChanged(Item item) {
		    //System.out.println("-->"+getClass().getName() +"::itemStateChanged(" + item + ")");
		    WidgetMap wm = WidgetMap.mapOf(ip);
		    String expansions[] = {null, wm.nameOf(getForm())};
		    int i = findItem(expansions,item);
		    
		    if(i >= 0) {
			wm.eval(ip,
				((FormGadget)gadgets.elementAt(i)).getChangedCallback(),
				ITEMCHANGEDEXPANDCHARS,
				expansions);
		    }
		}
	    });
	myitemcmdlistener = new ItemCommandListener() {
		public void commandAction(Command c,Item item) {
		    //System.out.println("-->FormCmd::commandAction(" + c + ", " + item + ")");
		    WidgetMap wm = WidgetMap.mapOf(ip);
		    String expansions[] = {"", wm.nameOf(c), wm.nameOf(getForm())};
		    
		    if(findItem(expansions,item) >= 0) {
			//wm.commandAction((OwnedThingCmd)wm.commandOf(c), ITEMCOMMANDACTIONEXPANDCHARS, expansions);
			wm.eval(ip,icmdact,ITEMCOMMANDACTIONEXPANDCHARS, expansions);
		    }
		}
	    };
    }

    
    public Form getForm() {
	return (Form)getData();
    }
    

    protected int append(FormGadget g) {
	//System.out.println("FormCmd::append("+g+")");
	if(g != null && !gadgets.contains(g)) {
	    if(!g.isOwnedBy(this))
		throw new IllegalStateException("Gadget owned by other Form.");
	    getForm().append(g.getItem());
	    gadgets.addElement(g);
	    return gadgets.size() - 1;
	}
	throw new IllegalStateException("Gadget '" + g + "' already present.");
    }


    protected void insert(FormGadget g,int itempos) {
	if(!gadgets.contains(g)) {
	    getForm().insert(itempos,g.getItem());
	    gadgets.insertElementAt(g,itempos);
	}
    }
    

    protected void delete(int itempos) {
	getForm().delete(itempos);
	gadgets.removeElementAt(itempos);
    }
    
    
    protected void deleteAll() {
	getForm().deleteAll();
	gadgets.removeAllElements();
    }
    

    public void cget(Interp ip,String optname) throws HeclException {
	if(optname.equals(WidgetInfo.NITEMCOMMANDACTION)) {
	    ip.setResult(new Thing(icmdact != null ? icmdact : ""));
	    return;
	}
	super.cget(ip,optname);
    }
    
    public void cget(Interp ip,String optname,Thing optval) throws HeclException {
	if(optname.equals(WidgetInfo.NITEMCOMMANDACTION)) {
	    icmdact = optval.toString();
	    if(icmdact.length() == 0)
		icmdact = null;
	    return;
	}
	super.cset(ip,optname,optval);
    }
    

    public void itemcget(Interp ip,int itemno,String optname) throws HeclException {
	Gadget g = (FormGadget)(gadgets.elementAt(itemno));
	if(g != null) {
	    g.cget(ip,optname);
	}
    }


    public void itemcset(Interp ip,int itemno,String optname,Thing optval)
	throws HeclException {
	FormGadget g = (FormGadget)(gadgets.elementAt(itemno));
	if(g != null) {
	    g.cset(ip,optname,optval);
	}
    }


    public void handleitemcmd(Interp ip,int itemno,String itemcmd,
			      Thing[] argv,int startat)
	throws HeclException {
	Gadget g = (FormGadget)(gadgets.elementAt(itemno));
	//System.err.println("FormGadget::handleitemcmd("+itemno+", "+itemcmd+")");
	if(g != null) {
	    g.handlecmd(ip,itemcmd,argv,startat);
	}
    }
    
	
    public void itemcreate(Interp ip,String what,Thing[] argv,int startat)
	throws HeclException {
	Form f = getForm();
	FormGadget g = null;
	Properties p = null;
	
	if(what.equals("choice")) {
	    p = WidgetInfo.defaultProps(ChoiceGroup.class);
	    p.setProps(argv,startat);
	    g = new ChoiceGadget(p.getProp(WidgetInfo.NLABEL).toString(),
				 WidgetInfo.toChoiceType(p.getProp(WidgetInfo.NTYPE)),
				 this);
	    p.delProp(WidgetInfo.NLABEL);
	    p.delProp(WidgetInfo.NTYPE);
	} else if(what.equals("date")) {
	    p = WidgetInfo.defaultProps(DateField.class);
	    p.setProps(argv,startat);
	    g = new DateGadget(p.getProp(WidgetInfo.NLABEL).toString(),
			       WidgetInfo.toDateFieldMode(
				   p.getProp(WidgetInfo.NTYPE)),
			       this);
	} else if(what.equals("gauge")) {
	    p = WidgetInfo.defaultProps(Gauge.class);
	    p.setProps(argv,startat);
//#ifdef notdef
	    System.err.println("GAUGE");
	    System.err.println("gauge: "
			       +p.getProp(WidgetInfo.NLABEL).toString()+", "
			       +p.getProp(WidgetInfo.NINTERACTIVE).toString()+", "
			       +p.getProp(WidgetInfo.NVALUE).toString()+", "
			       +p.getProp(WidgetInfo.NMAXVALUE).toString());
	    System.err.println("GAUGE2");
//#endif		
	    g = new GaugeGadget(p.getProp(WidgetInfo.NLABEL).toString(),
				HeclUtils.thing2bool(
				    p.getProp(WidgetInfo.NINTERACTIVE)),
				WidgetInfo.toGaugeInitial(
				    p.getProp(WidgetInfo.NVALUE)),
				WidgetInfo.toGaugeMax(
				    p.getProp(WidgetInfo.NMAXVALUE)),
				this);
	    p.delProp(WidgetInfo.NINTERACTIVE);
	} else if(what.equals("image")) {
	    p = WidgetInfo.defaultProps(ImageItem.class);
	    p.setProps(argv,startat);
	    g = new ImageGadget(p.getProp(WidgetInfo.NLABEL).toString(),
				p.getProp(WidgetInfo.NTEXT).toString(),
				WidgetInfo.toItemAppearance(
				    p.getProp(WidgetInfo.NAPPEARANCE)),
				this);
	    p.delProp(WidgetInfo.NLABEL);
	    p.delProp(WidgetInfo.NTEXT);
	    p.delProp(WidgetInfo.NAPPEARANCE);
	} else if(what.equals("spacer")) {
	    p = WidgetInfo.defaultProps(Spacer.class);
	    p.setProps(argv,startat);
	    g = new SpacerGadget(
		IntThing.get(p.getProp(WidgetInfo.NMINWIDTH)),
		IntThing.get(p.getProp(WidgetInfo.NMINHEIGHT)),
		this);
	    p.delProp(WidgetInfo.NMINWIDTH);
	    p.delProp(WidgetInfo.NMINHEIGHT);
	} else if(what.equals("string")) {
	    p = WidgetInfo.defaultProps(StringItem.class);
	    p.setProps(argv,startat);
	    g = new StringGadget(p.getProp(WidgetInfo.NLABEL).toString(),
				 WidgetInfo.toItemAppearance(
				     p.getProp(WidgetInfo.NAPPEARANCE)),
				 this);
	    p.delProp(WidgetInfo.NLABEL);
	    p.delProp(WidgetInfo.NAPPEARANCE);
	} else if(what.equals("text")) {
	    p = WidgetInfo.defaultProps(TextField.class);
	    p.setProps(argv,startat);
	    g = new TextGadget(p.getProp(WidgetInfo.NLABEL).toString(),
			       HeclUtils.thing2len(p.getProp(WidgetInfo.NMAXLEN),1),
			       WidgetInfo.toTextType(p.getProp(WidgetInfo.NTYPE)),
			       this);
	    p.delProp(WidgetInfo.NTITLE);
	    p.delProp(WidgetInfo.NMAXLEN);
	    p.delProp(WidgetInfo.NTYPE);
	} else {
	    throw new HeclException("Unknown item type '"+what+"'.");
	}
       
	if(g == null) {
	    throw new HeclException("Cannot create item '"+what+"'.");
	}
	Thing optargs[] = p.getProps();
	g.configure(ip,optargs,0,optargs.length);
	g.getItem().setItemCommandListener(myitemcmdlistener);
	ip.setResult(append(g));
    }
	
	
    public void handlecmd(Interp ip,String subcmd, Thing[] argv,int startat)
	throws HeclException {
	Form f = getForm();
	
	int n = startat+1;
	if(subcmd.equals(WidgetInfo.NSIZE)) {
	    ip.setResult(f.size());
	    return;
	}
	if(subcmd.equals(WidgetInfo.NCREATE)) {
	    if(argv.length < n) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, "class [itemcreateargs...]");
	    }
	    itemcreate(ip,argv[n-1].toString(),argv,n);
	    return;
	}

	if(subcmd.startsWith(WidgetInfo.NITEM,0)) {
	    // item<something> requires an item as parameter
	    if(argv.length < n) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, "itemcget|itemconfigure|itemop item [moreargs...]");
	    }

	    int itempos = IntThing.get(argv[n-1]);
	    checkItemPosition(itempos);

	    //System.out.println("Itempos="+itempos);
	    if(subcmd.equals(WidgetInfo.NITEMCONF)
	       || subcmd.equals(WidgetInfo.NITEMCONFIGURE)) {
		for(int i = n; i<argv.length; i+= 2) {
		    //System.err.println("itemconf for "+argv[i].toString());
		    itemcset(ip,itempos,argv[i].toString(),argv[i+1]);
		}
		return;
	    }
	    if(subcmd.equals(WidgetInfo.NITEMCGET)) {
		++n;
		if(argv.length != n) {
		    throw HeclException.createWrongNumArgsException(
			argv, n-1, "optname");
		}
		//System.err.println("itemcget for "+argv[n-1].toString());
		itemcget(ip,itempos,argv[n-1].toString());
		return;
	    }
	    if(subcmd.equals(WidgetInfo.NITEMOP)) {
		++n;
		if(argv.length < n) {
		    throw HeclException.createWrongNumArgsException(
			argv, n-1, "itemcommand [itemcommandargs...]");
		}
		handleitemcmd(ip,itempos,argv[n-1].toString(),argv,n);
		return;
	    }
	    throw new HeclException("Unknown item command '"+subcmd+"'.");
	}
	if(subcmd.equals(WidgetInfo.NDELETE)) {
	    if(argv.length != n) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, "delete item");
	    }
	    int itempos = IntThing.get(argv[n-1]);
	    checkItemPosition(itempos);
	    delete(itempos);
	    return;
	}
	if(subcmd.equals("deleteall")) {
	    deleteAll();
	    return;
	}
	if(subcmd.startsWith("move")) {
	    boolean before = subcmd.equals("movebefore");
	    boolean behind = subcmd.equals("movebehind");
	    boolean moveto = subcmd.equals("moveto");
	    if(before || behind || moveto) {
		++n;
		if(argv.length != n) {
		    throw HeclException.createWrongNumArgsException(
			argv, n, "position");
		}
		int itemtomove = IntThing.get(argv[n-2]);
		int numgadgets = f.size();
		int pos = numgadgets - 1;
		String s = argv[n-1].toString();
		
		if(s.equals("start"))
		    pos = 0;
		else if(s.equals("end"))
		    pos = numgadgets;
		else
		    pos = IntThing.get(argv[n-1]);
		checkItemPosition(itemtomove);
		checkItemPosition(pos);
		
		FormGadget g = (FormGadget)(gadgets.elementAt(itemtomove));
		delete(itemtomove);
		if(pos > itemtomove)
		    --pos;
		if(moveto || before) {
		    insert(g,pos);
		} else {
		    if(pos >= gadgets.size() -1) {
			append(g);
		    } else {
			insert(g,pos+1);
		    }
		}
		return;
	    }
	    if(subcmd.equals(WidgetInfo.NSETCURRENT)) {
		// extension: setcurrent item
		if(argv.length == n+1) {
		    int itempos = IntThing.get(argv[n]);
		    checkItemPosition(itempos);
		    
		    Display.getDisplay(MidletCmd.midlet()).setCurrentItem(
			((FormGadget)gadgets.elementAt(itempos)).getItem());
		    return;
		}
		// fall thru, baseclass 
	    }
	}
	super.handlecmd(ip,subcmd,argv,startat);
    }


    protected void checkItemPosition(int pos) throws HeclException {
	int n = gadgets.size();
	if(pos < 0 || pos >= n)
	    throw new HeclException("Invalid item position '"+pos+"'.");
    }
	

    private int findItem(String[] table,Item item) {
	int i = 0;
	int n = gadgets.size();
	for(i=0; i<n; ++i) {
	    if(item == ((FormGadget)(gadgets.elementAt(i))).getItem()) {
		table[0] = String.valueOf(i);
		return i;
	    }
	}
	i = -1;
	table[0] = String.valueOf(i);
	return i;
    }
    

    protected Vector gadgets;
    protected String icmdact = null;
    protected ItemCommandListener myitemcmdlistener = null;

    private static final char ITEMCHANGEDEXPANDCHARS[] = {'i','D'};
    private static final char ITEMCOMMANDACTIONEXPANDCHARS[] = {'i','W','D'};
}
