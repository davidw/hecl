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

import javax.microedition.lcdui.TextField;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.StringThing;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

public class TextFieldCmd extends OptionCmd {

    public static void load(Interp ip) {
	ip.addCommand(CMDNAME,cmd);
	ip.addClassCmd(TextField.class,cmd);
    }
    public static void unload(Interp ip) {
	ip.removeCommand(CMDNAME);
	ip.removeClassCmd(TextField.class);
    }
    
    public Thing cmdCode(Interp interp,Thing[] argv) throws HeclException {
	Properties p = WidgetInfo.defaultProps(TextField.class);
	p.setProps(argv,1);
	TextField tf  = new TextField(p.getProp(WidgetInfo.NLABEL).toString(),
				      "",
				      HeclUtils.thing2len(
					  p.getProp(WidgetInfo.NMAXLEN),1),
				      WidgetInfo.toTextType(
					  p.getProp(WidgetInfo.NTYPE)));
	p.delProp(WidgetInfo.NLABEL);
	p.delProp(WidgetInfo.NMAXLEN);
	p.delProp(WidgetInfo.NTYPE);
	return ObjectThing.create(setInstanceProperties(interp,tf,p));
    }

    /*
    public TextFieldCmd(String label,int maxSize,int constraints,FormCmd f) {
	super(new TextField(label,"",maxSize,constraints),f);
    }
    */

    protected TextFieldCmd() {}
    
    public Thing cget(Interp ip,Object target,String optname) throws HeclException {
	TextField tf = (TextField)target;
	
	if(optname.equals(WidgetInfo.NTYPE))
	    return WidgetInfo.fromTextType(
		tf.getConstraints() & ~TextField.CONSTRAINT_MASK);
	if(optname.equals(WidgetInfo.NTEXT))
	    return StringThing.create(tf.getString());
	if(optname.equals(WidgetInfo.NMAXLEN))
	    return IntThing.create(tf.getMaxSize());
	if(optname.equals("-password"))
	    return IntThing.create(0 != (tf.getConstraints()&TextField.PASSWORD));
	if(optname.equals("-uneditable"))
	    return IntThing.create(0 != (tf.getConstraints()&TextField.UNEDITABLE));
	if(optname.equals("-sensitive"))
	    return IntThing.create(0 != (tf.getConstraints()&TextField.SENSITIVE));
	if(optname.equals("-non_predictive"))
	    return IntThing.create(0 != (tf.getConstraints()&TextField.NON_PREDICTIVE));
	if(optname.equals("-initial_caps_word"))
	    return IntThing.create(0 != (tf.getConstraints()&TextField.INITIAL_CAPS_WORD));
	if(optname.equals("-initial_caps_sentence"))
	    return IntThing.create(0 != (tf.getConstraints()&TextField.INITIAL_CAPS_SENTENCE));
	if(optname.equals("-caretposition"))
	    return IntThing.create(tf.getCaretPosition());
	return super.cget(ip,target,optname);
    }

    public void cset(Interp ip,Object target,String optname,Thing optval)
	throws HeclException {
	TextField tf = (TextField)target;

	if(optname.equals(WidgetInfo.NTYPE)) {
	    int c = (tf.getConstraints() & TextField.CONSTRAINT_MASK);
	    tf.setConstraints(c | WidgetInfo.toTextType(optval));
	    return;
	}

	if(optname.equals(WidgetInfo.NTRUNCATE)) {
	    int max = tf.getMaxSize();
	    String newtext = optval.toString();
	    if (newtext.length() > max) {
		newtext = newtext.substring(0, max);
	    }
	    tf.setString(newtext);
	    return;
	}

	if(optname.equals(WidgetInfo.NGROW)) {
	    int max = tf.getMaxSize();
	    String newtext = optval.toString();
	    int textlen = newtext.length();
	    if (textlen > max) {
		int newmax = tf.setMaxSize(textlen);
		if (newmax < textlen) {
		    newtext = newtext.substring(0, newmax);
		}
	    }
	    tf.setString(newtext);
	    return;
	}

	if(optname.equals(WidgetInfo.NTEXT)) {
	    tf.setString(optval.toString());
	    return;
	}
	if(optname.equals(WidgetInfo.NMAXLEN)) {
	    int len = IntThing.get(optval);
	    if(len <1)
		throw new HeclException("Invalid length specifier.");
	    tf.setMaxSize(len);
	    return;
	}
	int c = tf.getConstraints();
	if(optname.equals("-password")) {
	    c &= ~TextField.PASSWORD;
	    tf.setConstraints(c | (HeclUtils.thing2bool(optval) ?
			      TextField.PASSWORD : 0));
	    return;
	}
	if(optname.equals("-uneditable")) {
	    c &= ~TextField.UNEDITABLE;
	    tf.setConstraints(c | (HeclUtils.thing2bool(optval) ?
			      TextField.UNEDITABLE : 0));
	    return;
	}
	if(optname.equals("-sensitive")) {
	    c &= ~TextField.SENSITIVE;
	    tf.setConstraints(c | (HeclUtils.thing2bool(optval) ?
			      TextField.SENSITIVE : 0));
	    return;
	}
	if(optname.equals("-non_predictive")) {
	    c &= ~TextField.NON_PREDICTIVE;
	    tf.setConstraints(c | (HeclUtils.thing2bool(optval) ?
			      TextField.NON_PREDICTIVE : 0));
	    return;
	}
	if(optname.equals("-initial_caps_word")) {
	    c &= ~TextField.INITIAL_CAPS_WORD;
	    tf.setConstraints(c | (HeclUtils.thing2bool(optval) ?
			      TextField.INITIAL_CAPS_WORD : 0));
	    return;
	}
	if(optname.equals("-initial_caps_sentence")) {
	    c &= ~TextField.INITIAL_CAPS_SENTENCE;
	    tf.setConstraints(c | (HeclUtils.thing2bool(optval) ?
			      TextField.INITIAL_CAPS_SENTENCE : 0));
	    return;
	}
	super.cset(ip,target,optname,optval);
    }

    private static TextFieldCmd cmd = new TextFieldCmd();
    private static final String CMDNAME = "lcdui.textfield";
}

// Variables:
// mode:java
// coding:utf-8
// End:
