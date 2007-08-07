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

import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.StringThing;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

public class TextBoxCmd extends ScreenCmd {
    public static void load(Interp ip) {
	ip.addCommand(CMDNAME,cmd);
	ip.addClassCmd(TextBox.class,cmd);
    }
    public static void unload(Interp ip) {
	ip.removeCommand(CMDNAME);
	ip.removeClassCmd(TextBox.class);
    }
    
    public Thing cmdCode(Interp interp,Thing[] argv) throws HeclException {
	Properties p = WidgetInfo.defaultProps(TextBox.class);
	p.setProps(argv,1);
	TextBox w = new TextBox(p.getProp(WidgetInfo.NTITLE).toString(),
				p.getProp(WidgetInfo.NTEXT).toString(),
				HeclUtils.thing2len(
				    p.getProp(WidgetInfo.NMAXLEN),1),
				WidgetInfo.toTextType(p.getProp(WidgetInfo.NTYPE)));
	p.delProp(WidgetInfo.NTITLE);
	p.delProp(WidgetInfo.NTEXT);
	p.delProp(WidgetInfo.NMAXLEN);
	p.delProp(WidgetInfo.NTYPE);
	return ObjectThing.create(setInstanceProperties(interp,w,p));
    }

    protected TextBoxCmd() {}

    public Thing cget(Interp ip,Object target,String optname)
	throws HeclException {
	TextBox tb = (TextBox)target;
	
	if(optname.equals(WidgetInfo.NTYPE))
	    return WidgetInfo.fromTextType(
		tb.getConstraints() & ~TextField.CONSTRAINT_MASK);
	if(optname.equals(WidgetInfo.NTEXT))
	    return StringThing.create(tb.getString());
	if(optname.equals(WidgetInfo.NMAXLEN))
	    return IntThing.create(tb.getMaxSize());
	if(optname.equals("-password"))
	    return IntThing.create(0 != (tb.getConstraints()&TextField.PASSWORD));
	if(optname.equals("-uneditable"))
	    return IntThing.create(0 != (tb.getConstraints()&TextField.UNEDITABLE));
	if(optname.equals("-sensitive"))
	    return IntThing.create(0 != (tb.getConstraints()&TextField.SENSITIVE));
	if(optname.equals("-non_predictive"))
	    return IntThing.create(0 != (tb.getConstraints()&TextField.NON_PREDICTIVE));
	if(optname.equals("-initial_caps_word"))
	    return IntThing.create(0 != (tb.getConstraints()&TextField.INITIAL_CAPS_WORD));
	if(optname.equals("-initial_caps_sentence"))
	    return IntThing.create(0 != (tb.getConstraints()&TextField.INITIAL_CAPS_SENTENCE));
	if(optname.equals("-caretposition"))
	    return IntThing.create(tb.getCaretPosition());
	return super.cget(ip,target,optname);
    }

    public void cset(Interp ip,Object target,String optname,Thing optval)
	throws HeclException {
	TextBox tb = (TextBox)target;

	if(optname.equals(WidgetInfo.NTYPE)) {
	    int c = (tb.getConstraints() & TextField.CONSTRAINT_MASK);
		
	    tb.setConstraints(c | WidgetInfo.toTextType(optval));
	    return;
	}
	if(optname.equals(WidgetInfo.NTEXT)) {
	    tb.setString(optval.toString());
	    return;
	}
	if(optname.equals(WidgetInfo.NMAXLEN)) {
	    int len = IntThing.get(optval);
	    if(len <1)
		throw new HeclException("Invalid length specifier.");
	    tb.setMaxSize(len);
	    return;
	}
	int c = tb.getConstraints();
	if(optname.equals("-password")) {
	    c &= ~TextField.PASSWORD;
	    tb.setConstraints(c | (HeclUtils.thing2bool(optval) ?
			      TextField.PASSWORD : 0));
	    return;
	}
	if(optname.equals("-uneditable")) {
	    c &= ~TextField.UNEDITABLE;
	    tb.setConstraints(c | (HeclUtils.thing2bool(optval) ?
			      TextField.UNEDITABLE : 0));
	    return;
	}
	if(optname.equals("-sensitive")) {
	    c &= ~TextField.SENSITIVE;
	    tb.setConstraints(c | (HeclUtils.thing2bool(optval) ?
			      TextField.SENSITIVE : 0));
	    return;
	}
	if(optname.equals("-non_predictive")) {
	    c &= ~TextField.NON_PREDICTIVE;
	    tb.setConstraints(c | (HeclUtils.thing2bool(optval) ?
			      TextField.NON_PREDICTIVE : 0));
	    return;
	}
	if(optname.equals("-initial_caps_word")) {
	    c &= ~TextField.INITIAL_CAPS_WORD;
	    tb.setConstraints(c | (HeclUtils.thing2bool(optval) ?
			      TextField.INITIAL_CAPS_WORD : 0));
	    return;
	}
	if(optname.equals("-initial_caps_sentence")) {
	    c &= ~TextField.INITIAL_CAPS_SENTENCE;
	    tb.setConstraints(c | (HeclUtils.thing2bool(optval) ?
			      TextField.INITIAL_CAPS_SENTENCE : 0));
	    return;
	}
	super.cset(ip,target,optname,optval);
    }

    private static TextBoxCmd cmd = new TextBoxCmd();
    private static final String CMDNAME = "lcdui.textbox";
}

// Variables:
// mode:java
// coding:utf-8
// End:

