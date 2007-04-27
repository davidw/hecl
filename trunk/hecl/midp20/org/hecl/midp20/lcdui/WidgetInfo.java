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

import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Spacer;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Ticker;

import org.hecl.HeclException;
import org.hecl.IntThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.RealThing;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

public class WidgetInfo {

    public static Object asWidget(Thing thing,Class clazz,
				  String clazzname, boolean allownull)
	throws HeclException {
	if(allownull && thing.toString().length() == 0)
	    return null;
	RealThing rt = thing.getVal();
	if(rt instanceof ObjectThing) {
	    Object x = ((ObjectThing)rt).get();
	    if(allownull && x == null)
		return null;
	    if(clazz.isAssignableFrom(x.getClass()))
		return x;
	}
	if(clazzname != null) {
	    throw HeclException.createInvalidParameter(
		thing,"parameter",clazzname + " widget required.");
	}
    	return null;
    }
    

    public static Command asCommand(Thing thing, boolean allownull,boolean throwerror)
	throws HeclException {
	return (Command)asWidget(thing, Command.class,
				 throwerror? "Command" :null,allownull);
    }


    public static Gauge asGauge(Thing thing, boolean allownull,boolean throwerror)
	throws HeclException {
	return (Gauge)asWidget(thing, Gauge.class,
			       throwerror ? "Gauge" : null,allownull);
    }
    

    public static Image asImage(Thing thing, boolean allownull,boolean throwerror)
	throws HeclException {
	return (Image)asWidget(thing, Image.class, throwerror ? "Image" : null,allownull);
    }

    public static Item asItem(Thing thing, boolean allownull,boolean throwerror)
	throws HeclException {
	return (Item)asWidget(thing, Item.class, throwerror ? "Item" : null,allownull);
    }

    public static Ticker asTicker(Thing thing, boolean allownull,boolean throwerror)
	throws HeclException {
	return (Ticker)asWidget(thing, Ticker.class,
				throwerror ? "Ticker" : null,allownull);
    }


    public static AlertType toAlertType(Thing t) throws HeclException {
	String s = t.toString().toLowerCase();
	int l = alerttypenames.length;
	for(int i=0; i<l; ++i)
	    if(s.equals(alerttypenames[i]))
		return alerttypevals[i];
	throw new HeclException("Invalid alert type '"+s+"'!");
    }
    

    public static Thing fromAlertType(AlertType t) throws HeclException {
	int l = alerttypenames.length;
	for(int i=0; i<l; ++i)
	    if(t == alerttypevals[i])
		return new Thing(alerttypenames[i]);
	throw new HeclException("Invalid alert type value '"+t+"'!");
    }
    

    protected static int t2int(Thing t,String nametab[],int valtab[],String emsg)
	throws HeclException {
	return s2int(t.toString().toLowerCase(),nametab,valtab,emsg);
    }
    

    protected static int s2int(String s,String nametab[],int valtab[],String emsg)
	throws HeclException {
	int l = nametab.length;
	for(int i=0; i<l; ++i)
	    if(s.equals(nametab[i]))
		return valtab[i];
	throw new HeclException("Invalid " + emsg + " '" + s + "'.");
    }
    

    protected static Thing int2t(int v,String nametab[],int valtab[],String emsg)
	throws HeclException {
	return new Thing(int2s(v,nametab,valtab,emsg));
    }
    

    protected static String int2s(int v,String nametab[],int valtab[],String emsg)
	throws HeclException {
	int l = valtab.length;
	for(int i=0; i<l; ++i)
	    if(v == valtab[i])
		return nametab[i];
	throw new HeclException("Invalid " + emsg + " value '" + v + "'.");
    }
    

    public static int toColor(Thing t) throws HeclException {
	String s = t.toString();
	try {
	    return s2int(s.toLowerCase(),colornames,colorvals,"");
	}
	catch (Exception e) {
	}
	return Integer.parseInt(s,16);
    }
    
    
    public static Thing fromColor(org.awt.Color color) throws HeclException {
	return fromColor(color.getRGB());
    }
    
    
    public static Thing fromColor(int t) throws HeclException {
	try {
	    return int2t(t,colornames,colorvals,"");
	}
	catch (HeclException e) {
	}
	return new Thing(Integer.toHexString(t));
    }
    
    
    public static int toCanvasAnchor(Thing t) throws HeclException {
	return t2int(t,canchornames,canchorvals,"anchor");
    }
    

    public static Thing fromCanvasAnchor(int t) throws HeclException {
	return int2t(t,canchornames,canchorvals,"anchor");
    }
    

    public static int toChoiceType(Thing t) throws HeclException {
	return t2int(t,choicetypenames,choicetypevals,"choice type");
    }
    

    public static Thing fromChoiceType(int t) throws HeclException {
	return int2t(t,choicetypenames,choicetypevals,"choice type");
    }
    

    public static int toListType(Thing t) throws HeclException {
	int i = toChoiceType(t);
	if(i == Choice.POPUP) {
	    throw new HeclException("Invalid list type '"+t+"'!");
	}
	return i;
    }


    public static Thing fromListType(int t) throws HeclException {
	try {
	    if(t != Choice.POPUP) {
		return fromChoiceType(t);
	    }
	} catch (HeclException e) {
	}
	throw new HeclException("Invalid list type value'"+t+"'!");
    }

    public static int toTextType(Thing t) throws HeclException {
	return t2int(t,texttypenames,texttypevals,"text type");
    }


    public static Thing fromTextType(int t) throws HeclException {
	return int2t(t & ~TextField.CONSTRAINT_MASK,
		     texttypenames,texttypevals,"text type");
    }


    public static int toWrap(Thing t) throws HeclException {
	return t2int(t,wrapnames,wrapvals,"wrap type");
    }


    public static Thing fromWrap(int t) throws HeclException {
	return int2t(t,wrapnames,wrapvals,"wrap type");
    }


    public static String commandLabel(Command c,boolean shortonly) {
	String l = shortonly ? null : c.getLongLabel();

	if(l == null || l.length() == 0) {
	    l = c.getLabel();
	}
	if(l == null && l.length() == 0) {
//#ifdef notdef	    
	    // unfortunately there is no way to detect the command type :-(
	    int t = c.getType();
	    for(int i=0; i<cmdlabels.length; ++i) {
		if(t == cmdtypes[i]) {
		    l = cmdlabels[i];
		    break;
		}
	    }
//#endif
	    l = "???";
	}
	return l;
    }
    
    public static int toCommandType(Thing t) throws HeclException {
	return t2int(t,cmdtypenames,cmdtypevals,"command type");
    }
    

    public static Thing fromCommandType(int t) throws HeclException {
	return int2t(t,cmdtypenames,cmdtypevals,"command type");
    }


    public static int toFontFace(Thing t) throws HeclException {
	return t2int(t,fontfacenames,fontfacevals,"font face");
    }


    public static int toFontFace(String s) throws HeclException {
	return s2int(s.toLowerCase(),fontfacenames,fontfacevals,"font face");
    }
    

    public static Thing fromFontFace(int t) throws HeclException {
	return int2t(t,fontfacenames,fontfacevals,"font face");
    }


    public static int toFontSize(Thing t) throws HeclException {
	return t2int(t,fontsizenames,fontsizevals,"font size");
    }
    

    public static int toFontSize(String s) throws HeclException {
	return s2int(s.toLowerCase(),fontsizenames,fontsizevals,"font size");
    }
    

    public static Thing fromFontSize(int t) throws HeclException {
	return int2t(t,fontsizenames,fontsizevals,"font size");
    }


    public static int toItemAnchor(Thing t) throws HeclException {
	return t2int(t,anchornames,anchorvals,"anchor");
    }
    

    public static Thing fromItemAnchor(int t) throws HeclException {
	return int2t(t &= 0x33,anchornames,anchorvals,"anchor");
    }
    

    public static int toItemAppearance(Thing t) throws HeclException {
	return t2int(t,appearancenames,appearancevals,"appearance");
    }
    

    public static Thing fromItemAppearance(int t) throws HeclException {
	return int2t(t &= 0x33,appearancenames,appearancevals,"appearance");
    }
    

    public static int toDateFieldMode(Thing t) throws HeclException {
	return t2int(t,dfmodenames,dfmodevals,"date field mode");
    }
    

    public static Thing fromDateFieldMode(int t) throws HeclException {
	return int2t(t &= 0x33,dfmodenames,dfmodevals,"date field mode");
    }
    

    public static int toGaugeInitial(Thing t) throws HeclException {
	if(Character.isDigit(t.toString().charAt(0))) {
	    return HeclUtils.thing2int(t,true,0);
	}
	return t2int(t,gaugeinitialnames,gaugeinitialvals,"gauge initval");
    }
    

    public static Thing fromGaugeInitial(int t) throws HeclException {
	for(int i=0; i<gaugeinitialvals.length; ++i) {
	    if(i == gaugeinitialvals[i])
		return new Thing(gaugeinitialnames[i]);
	}
	return IntThing.create(t);
    }
    

    public static int toGaugeMax(Thing t) throws HeclException {
	if(Character.isDigit(t.toString().charAt(0))) {
	    return HeclUtils.thing2int(t,true,0);
	}
	return t2int(t,gaugemaxnames,gaugemaxvals,"gauge initval");
    }
    

    public static Thing fromGaugeMax(int t) throws HeclException {
	for(int i=0; i<gaugemaxvals.length; ++i) {
	    if(i == gaugeinitialvals[i])
		return new Thing(gaugemaxnames[i]);
	}
	return IntThing.create(t);
    }
    

    public static void showProps(Class c) {
	Vector v = (Vector)widgetprops.get(c);
	int n = v.size();
	System.err.println("showProps("+c+")=");
	for(int i=0; i<n; ++i) {
	    String s = "<null>";
	    
	    WidgetProp wp = (WidgetProp)v.elementAt(i);
	    try {
		s = wp.defaultvalue.toString();
	    }
	    catch(NullPointerException e) {}
	    System.err.println("\t"+wp.name+": "+s);
	}
    }
    
    public static Properties defaultProps(Class c) {
	Properties p = new Properties();
	Vector v = (Vector)widgetprops.get(c);
	int n = v.size();
	for(int i=0; i<n; ++i) {
	    WidgetProp wp = (WidgetProp)v.elementAt(i);
	    p.setProp(wp.name,wp.defaultvalue);
	}
	return p;
    }
    
    /* 
     * Some command names (in alphabetical order)
     */
    public static final String NADDCOMMAND = "addcommand";
    public static final String NAPPEND = "append";
    public static final String NCGET = "cget";
    public static final String NCONF = "conf";
    public static final String NCONFIGURE = "configure";
    public static final String NCREATE = "create";
    public static final String NDELETE = "delete";
    public static final String NITEM = "item";
    public static final String NITEMCGET = "itemcget";
    public static final String NITEMCONF = "itemconf";
    public static final String NITEMCONFIGURE = "itemconfigure";
    public static final String NREMOVECOMMAND = "removecommand";
    public static final String NREPAINT = "repaint";
    public static final String NSETCURRENT = "setcurrent";
    public static final String NSIZE = "size";
    

    /*
     * Some property names (in alphabetical order)
     */
    public static final String NAPPEARANCE = "-appearance";
    public static final String NCLIPHEIGHT = "-clipheight";
    public static final String NCLIPWIDTH = "-clipwidth";
    public static final String NCLIPX = "-clipx";
    public static final String NCLIPY = "-clipy";
    public static final String NCODE = "-code";
    public static final String NCOLOR = "-color";
    public static final String NCOMMAND = "-command";
    public static final String NCOMMANDACTION = "-commandaction";
    public static final String NEXPAND = "-expand";
    public static final String NFIT = "-fit";
    public static final String NFONT = "-font";
    public static final String NHEIGHT = "-height";
    public static final String NIMAGE = "-image";
    public static final String NINTERACTIVE = "-interactive";
    public static final String NLABEL = "-label";
    public static final String NLINETYPE = "-linetype";
    public static final String NLONGLABEL = "-longlabel";
    public static final String NMAXLEN = "-maxlength";
    public static final String NMAXVALUE = "-maxvalue";
    public static final String NMINHEIGHT = "-minheight";
    public static final String NMINWIDTH = "-minwidth";
    public static final String NPRIO = "-priority";
    public static final String NPREFERREDWIDTH = "-preferredwidth";
    public static final String NPREFERREDHEIGHT = "-preferredheight";
    public static final String NSELECTMODE = "-selectmode";
    public static final String NSELECTION = "-selection";
    public static final String NSUPPRESSKEYS = "-suppresskeys";
    public static final String NTEXT = "-text";
    public static final String NTICKER = "-ticker";
    public static final String NTITLE = "-title";
    public static final String NTYPE = "-type";
    public static final String NVALUE = "-value";
    public static final String NVEXPAND = "-vexpand";
    public static final String NWIDTH = "-width";
    
    static final Thing DEFAULTTHING = new Thing("default");
    static final Thing ANYTHING = new Thing("any");
    static final Thing ZERO = IntThing.create(0);
    static final Thing ONE = IntThing.create(1);
    
    /*
     * Common Widget properties and default values.
     */
    public static final WidgetProp codeprop = new WidgetProp(NCODE,Thing.EMPTYTHING); 
    public static final WidgetProp textprop = new WidgetProp(NTEXT,Thing.EMPTYTHING); 
    public static final WidgetProp labelprop = new WidgetProp(NLABEL,Thing.EMPTYTHING); 
    public static final WidgetProp longlabelprop = new WidgetProp(NLONGLABEL,Thing.EMPTYTHING); 
    public static final WidgetProp titleprop = new WidgetProp(NTITLE,Thing.EMPTYTHING); 
    public static final WidgetProp fitprop = new WidgetProp(NFIT,DEFAULTTHING);
    public static final WidgetProp selectprop = new WidgetProp(NSELECTMODE,
							       new Thing("exclusive")); 
    public static final WidgetProp tickerprop = new WidgetProp(NTICKER,Thing.EMPTYTHING);
    public static final WidgetProp prioprop = new WidgetProp(NPRIO,ONE);
    public static final WidgetProp appearanceprop = new WidgetProp(NAPPEARANCE,
								   new Thing("plain"));
    public static final WidgetProp minwidthprop = new WidgetProp(NMINWIDTH,ZERO);
    public static final WidgetProp minheightprop = new WidgetProp(NMINHEIGHT,ZERO);

    /*
     * WIDGET attribute conversion tables (parallel arrays
     */
    static final String colornames[] = {"red","green","blue",
					    "yellow","cyan","magenta",
					    "white","black"
    };
    static final int colorvals[] = {0x0ff0000,0x0ff00,0x0ff,
					0x0ffff00, 0x0ffff, 0x0ff00ff,
					0x0ffffff,0
    };
    

    // Alert type
    static final String alerttypenames[] = {"none","info","warning",
						"error","alarm","confirmation",""};
    static final AlertType alerttypevals[] = {null,AlertType.INFO,
						  AlertType.WARNING,AlertType.ERROR,
						  AlertType.ALARM,
						  AlertType.CONFIRMATION,null};
    
    // Canvas stuff
    // Linetype
    static String clinetypenames[] = {"solid","dotted","default"};
    static int clinetypevals[] = {Graphics.SOLID,Graphics.DOTTED,Graphics.SOLID};
    // Anchor points
    static String canchornames[] = {"n","ne","e","se","s",
					"sw","w","nw","center","default",
					"bl","bc","br"};
    static int canchorvals[] = {Graphics.TOP|Graphics.HCENTER,
				    Graphics.TOP|Graphics.RIGHT,
				    Graphics.VCENTER|Graphics.RIGHT,
				    Graphics.BOTTOM|Graphics.RIGHT,
				    Graphics.BOTTOM|Graphics.HCENTER,
				    Graphics.BOTTOM|Graphics.LEFT,
				    Graphics.VCENTER|Graphics.LEFT,
				    Graphics.TOP|Graphics.LEFT,
				    Graphics.VCENTER|Graphics.HCENTER,
				    Graphics.TOP|Graphics.LEFT,
				    Graphics.LEFT|Graphics.BASELINE,
				    Graphics.HCENTER|Graphics.BASELINE,
				    Graphics.RIGHT|Graphics.BASELINE,
    };
    

    // Choice types
    static final String choicetypenames[] = {"exclusive","multiple","implicit","popup"};
    static final int choicetypevals[] = {Choice.EXCLUSIVE,Choice.MULTIPLE,
					     Choice.IMPLICIT,Choice.POPUP};

    // Textfield type
    static final String texttypenames[] = {
	"any","emailaddr", "numeric", "phonenumber","decimal"
    };

   static final int texttypevals[] = {
       TextField.ANY, TextField.EMAILADDR, TextField.NUMERIC,
	   TextField.PHONENUMBER, TextField.DECIMAL
   };

    // Choice wrap specification
    static final String wrapnames[] = {"default","on","off"};
    static final int wrapvals[] = {Choice.TEXT_WRAP_DEFAULT,Choice.TEXT_WRAP_ON,
					   Choice.TEXT_WRAP_OFF};

    // Command types
    static final String cmdtypenames[] = {"screen","back","cancel","ok",
					      "help", "stop","exit","item"};
    static final String cmdlabels[] = {"Screen","Back","Cancel","OK",
					      "Help", "Stop","Exit","Item"};
    static final int cmdtypevals[] = {Command.SCREEN,Command.BACK,
					  Command.CANCEL,Command.OK,
					  Command.HELP,Command.STOP,
					  Command.EXIT,Command.ITEM};

    // Font face names
    static final String fontfacenames[] = {"system","proportional","monospace"};
    static final int fontfacevals[] = {Font.FACE_SYSTEM, Font.FACE_PROPORTIONAL,
					   Font.FACE_MONOSPACE};

    // Font sizes
    static final String fontsizenames[] = {"small","medium","large"};
    static final int fontsizevals[] = {Font.SIZE_SMALL, Font.SIZE_MEDIUM,
					   Font.SIZE_LARGE};

    // Item anchor position (part of item layout).
    static String anchornames[] = {"n","ne","e","se","s",
				       "sw","w","nw","center","default"};
    static int anchorvals[] = {Item.LAYOUT_TOP|Item.LAYOUT_CENTER,
				   Item.LAYOUT_TOP|Item.LAYOUT_LEFT,
				   Item.LAYOUT_VCENTER|Item.LAYOUT_RIGHT,
				   Item.LAYOUT_BOTTOM|Item.LAYOUT_RIGHT,
				   Item.LAYOUT_BOTTOM|Item.LAYOUT_CENTER,
				   Item.LAYOUT_BOTTOM|Item.LAYOUT_LEFT,
				   Item.LAYOUT_VCENTER|Item.LAYOUT_LEFT,
				   Item.LAYOUT_TOP|Item.LAYOUT_LEFT,
				   Item.LAYOUT_VCENTER|Item.LAYOUT_CENTER,
				   Item.LAYOUT_DEFAULT};
    
		    
    // Item appearance
    static String appearancenames[] = {"plain","button","hyperlink"};
    static int appearancevals[] = {Item.PLAIN, Item.BUTTON, Item.HYPERLINK};
    
		    
    // DateField modes
    static String dfmodenames[] = {"date","date_time","time"};
    static int dfmodevals[] = {DateField.DATE, DateField.DATE_TIME, DateField.TIME};
    
    
    static String gaugeinitialnames[] = {"continuous-idle",
					     "continuous-running",
					     "incremental-idle",
					     "incremental-updating"};
    static int gaugeinitialvals[] = {Gauge.CONTINUOUS_IDLE,
					 Gauge.CONTINUOUS_RUNNING,
					 Gauge.INCREMENTAL_IDLE,
					 Gauge.INCREMENTAL_UPDATING};
    
    static String gaugemaxnames[] = {"indefinite"};
    static int gaugemaxvals[] = {Gauge.INDEFINITE};
		    
    // A table holding widget property descriptions
    public static final Hashtable widgetprops = new Hashtable();
    
    static {
	/* Alert defaults */
	Vector v = new Vector();
	v.addElement(new WidgetProp(NTITLE,new Thing("Alert")));
	v.addElement(new WidgetProp(NTYPE,new Thing("info"),true));
	v.addElement(textprop);
	widgetprops.put(Alert.class,v);

	/* Canvas defaults */
	v = new Vector();
	v.addElement(new WidgetProp(NTITLE,new Thing("Canvas")));
	v.addElement(new WidgetProp(NSUPPRESSKEYS,new Thing("false"),true));
	widgetprops.put(Canvas.class,v);

	/* Command defaults */
	v = new Vector();
	v.addElement(labelprop);
	v.addElement(longlabelprop);
	v.addElement(new WidgetProp(NTYPE,new Thing("back"),true));
	v.addElement(prioprop);
	widgetprops.put(Command.class,v);

	/* Form defaults */
	v = new Vector();
	v.addElement(new WidgetProp(NTITLE,new Thing("Form")));
	widgetprops.put(Form.class,v);

	/* List defaults */
	v = new Vector();
	v.addElement(new WidgetProp(NTITLE,new Thing("List")));
	v.addElement(new WidgetProp(NTYPE,new Thing("implicit"),true));
	v.addElement(fitprop);
	widgetprops.put(List.class,v);

	/* TextBox defaults */
	v = new Vector();
	v.addElement(new WidgetProp(NTITLE,new Thing("TextBox")));
	v.addElement(textprop);
	v.addElement(tickerprop);
	v.addElement(new WidgetProp(NTYPE,ANYTHING,true));
	v.addElement(new WidgetProp(NMAXLEN,IntThing.create(256)));
	widgetprops.put(TextBox.class,v);

	/* Ticker defaults */
	v = new Vector();
	v.addElement(textprop);
	widgetprops.put(Ticker.class,v);

	/* ChoiceGroup defaults */
	v = new Vector();
	v.addElement(new WidgetProp(NLABEL,Thing.EMPTYTHING));
	v.addElement(new WidgetProp(NTYPE,new Thing("exclusive"),true));
	v.addElement(fitprop);
	widgetprops.put(ChoiceGroup.class,v);

	/* ImageItem defaults */
	v = new Vector();
	v.addElement(textprop);
	v.addElement(labelprop);
	v.addElement(appearanceprop);
	widgetprops.put(ImageItem.class,v);

	/* Spacer defaults */
	v = new Vector();
	v.addElement(minwidthprop);
	v.addElement(minheightprop);
	widgetprops.put(Spacer.class,v);

	/* StringItem defaults */
	v = new Vector();
	v.addElement(textprop);
	v.addElement(labelprop);
	v.addElement(appearanceprop);
	widgetprops.put(StringItem.class,v);

	/* TextField defaults */
	v = new Vector();
	v.addElement(labelprop);
	v.addElement(new WidgetProp(NMAXLEN,IntThing.create(256)));
	v.addElement(new WidgetProp(NTYPE,ANYTHING,true));
	widgetprops.put(TextField.class,v);

	/* DateField defaults */
	v = new Vector();
	v.addElement(labelprop);
	v.addElement(new WidgetProp(NTYPE,new Thing(dfmodenames[1])));
	widgetprops.put(DateField.class,v);

	/* Gauge defaults */
	v = new Vector();
	v.addElement(labelprop);
	v.addElement(new WidgetProp(NINTERACTIVE,IntThing.create(0),true));
	v.addElement(new WidgetProp(NVALUE,new Thing("continuous-running")));
	v.addElement(new WidgetProp(NMAXVALUE,new Thing("indefinite")));
	widgetprops.put(Gauge.class,v);
    }
}

// Variables:
// mode:java
// coding:utf-8
// End:
