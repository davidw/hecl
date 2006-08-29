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

import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.Properties;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

public class TextBoxCmd extends ScreenCmd {
    public static final org.hecl.Command CREATE = new org.hecl.Command() {
	    public void cmdCode(Interp interp,Thing[] argv) throws HeclException {
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
		WidgetMap.addWidget(interp,null,w, new TextBoxCmd(interp,w,p));
	    }
	};

    private TextBoxCmd(Interp ip,TextBox a,Properties p) throws HeclException {
	super(ip,a,p);
    }

    public void cget(Interp ip,String optname) throws HeclException {
	TextBox tb = (TextBox)getData();
	
	if(optname.equals(WidgetInfo.NTYPE)) {
	    ip.setResult(WidgetInfo.fromTextType(
			     tb.getConstraints() & ~TextField.CONSTRAINT_MASK));
	    return;
	}
	if(optname.equals(WidgetInfo.NTEXT)) {
	    ip.setResult(new Thing(tb.getString()));
	    return;
	}
	if(optname.equals(WidgetInfo.NMAXLEN)) {
	    ip.setResult(tb.getMaxSize());
	    return;
	}
	if(optname.equals("-password")) {
	    ip.setResult(0 != (tb.getConstraints()&TextField.PASSWORD));
	    return;
	}
	if(optname.equals("-uneditable")) {
	    ip.setResult(0 != (tb.getConstraints()&TextField.UNEDITABLE));
	    return;
	}
	if(optname.equals("-sensitive")) {
	    ip.setResult(0 != (tb.getConstraints()&TextField.SENSITIVE));
	    return;
	}
	if(optname.equals("-non_predictive")) {
	    ip.setResult(0 != (tb.getConstraints()&TextField.NON_PREDICTIVE));
	    return;
	}
	if(optname.equals("-initial_caps_word")) {
	    ip.setResult(0 != (tb.getConstraints()&TextField.INITIAL_CAPS_WORD));
	    return;
	}
	if(optname.equals("-initial_caps_sentence")) {
	    ip.setResult(0 != (tb.getConstraints()&TextField.INITIAL_CAPS_SENTENCE));
	    return;
	}
	if(optname.equals("-caretposition")) {
	    ip.setResult(tb.getCaretPosition());
	    return;
	}
	super.cget(ip,optname);
    }

    public void cset(Interp ip,String optname,Thing optval) throws HeclException {
	TextBox tb = (TextBox)getData();

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
	    ip.setResult(tb.setMaxSize(len));
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
	super.cset(ip,optname,optval);
    }
}

