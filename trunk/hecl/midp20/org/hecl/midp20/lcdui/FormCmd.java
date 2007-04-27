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
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.RealThing;
import org.hecl.StringThing;
import org.hecl.Thing;

import org.hecl.midp20.MidletCmd;
import org.hecl.misc.HeclUtils;

public class FormCmd extends ScreenCmd {
    public static void load(Interp ip) {
	ip.addCommand(CMDNAME,cmd);
	ip.addClassCmd(Form.class,cmd);
    }
    public static void unload(Interp ip) {
	ip.removeCommand(CMDNAME);
	ip.removeClassCmd(Form.class);
    }

    public Thing cmdCode(Interp interp,Thing[] argv) throws HeclException {
	Properties p = WidgetInfo.defaultProps(Form.class);
	p.setProps(argv,1);
	Form w = new Form(p.getProp(WidgetInfo.NTITLE).toString());
	p.delProp(WidgetInfo.NTITLE);
	return ObjectThing.create(setInstanceProperties(interp,w,p));
    }


    protected FormCmd() {}

    
    public Thing cget(Interp ip,Object target,String optname)
	throws HeclException {
	Form f = (Form)target;
	
	return super.cget(ip,target,optname);
    }
    
    public void cset(Interp ip,Object target,String optname,Thing optval)
	throws HeclException {
	Form f = (Form)target;

	if(optname.equals("-itemstatehandler")) {
	    ItemStateListener listener = null;
	    if(optval.toString().length() > 0)
		listener = new WidgetListener(ip,optval,f);
	    f.setItemStateListener(listener);
	    return;
	}
	super.cset(ip,target,optname,optval);
    }
    

    //#ifdef notdef
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
    //#endif
	
	
    public Thing handlecmd(Interp ip,Object target,String subcmd,
			   Thing[] argv,int startat)
	throws HeclException {
	Form f = (Form)target;
	
	int n = startat+1;
	
	if(subcmd.equals(WidgetInfo.NSIZE))
	    return IntThing.create(f.size());
	if(subcmd.equals(WidgetInfo.NAPPEND)) {
	    if(argv.length < n) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, "append item|string|image");
	    }
	    // append <item|string|image>
	    Thing arg = argv[startat];
	    RealThing rt = arg.getVal();
	    try {
		if(rt instanceof ObjectThing) {
		    if(((ObjectThing)rt).get() != null) {
			Item item = WidgetInfo.asItem(arg,false,false);
			if(item != null) {
			    f.append(item);
			} else {
			    Image im = WidgetInfo.asImage(arg,false,false);
			    if(im != null)
				f.append(im);
			}
		    } else {
			throw new HeclException("cannot append null");
		    }
		} else {
		    f.append(arg.toString());
		}
	    }
	    catch(Exception ex) {
		throw new HeclException("unable to append: "+ex);
	    }
	    return null;
	}
	if(subcmd.equals(WidgetInfo.NITEM)) {
	    // item<something> requires an item as parameter
	    if(argv.length < n) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, "item itemnumber");
	    }
	    return ObjectThing.create(f.get(IntThing.get(argv[startat])));
	}
	if(subcmd.equals(WidgetInfo.NDELETE)) {
	    if(argv.length != n) {
		throw HeclException.createWrongNumArgsException(
		    argv, n, "delete itemnumber");
	    }
	    f.delete(IntThing.get(argv[startat]));
	    return null;
	}
	if(subcmd.equals("deleteall")) {
	    f.deleteAll();
	    return null;
	}
	if(subcmd.equals(WidgetInfo.NSETCURRENT)) {
	    // extension: setcurrent itemnumber
	    if(argv.length == n) {
		// start j2mepolish break down
		// to allow correct preprocessing
		Display displ = MidletCmd.getDisplay();
		Item item = f.get(IntThing.get(argv[startat]));
		displ.setCurrentItem(item);
		// end j2mepolish break down
		return null;
	    }
	}
	return super.handlecmd(ip,target,subcmd,argv,startat);
    }


    private static FormCmd cmd = new FormCmd();
    private static final String CMDNAME = "lcdui.form";
}

// Variables:
// mode:java
// coding:utf-8
// End:
