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
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

public class TextGadget extends FormGadget {
    public TextGadget(String label,int maxSize,int constraints,FormCmd f) {
	super(new TextField(label,"",maxSize,constraints),f);
    }


    public void cget(Interp ip,String optname) throws HeclException {
	TextField tf = (TextField)theitem;
	
	if(optname.equals(WidgetInfo.NTYPE)) {
	    ip.setResult(WidgetInfo.fromTextType(
			     tf.getConstraints() & ~TextField.CONSTRAINT_MASK));
	    return;
	}
	if(optname.equals(WidgetInfo.NTEXT)) {
	    ip.setResult(tf.getString());
	    return;
	}
	if(optname.equals(WidgetInfo.NMAXLEN)) {
	    ip.setResult(tf.getMaxSize());
	    return;
	}
	if(optname.equals("-password")) {
	    ip.setResult(0 != (tf.getConstraints()&TextField.PASSWORD));
	    return;
	}
	if(optname.equals("-uneditable")) {
	    ip.setResult(0 != (tf.getConstraints()&TextField.UNEDITABLE));
	    return;
	}
	if(optname.equals("-sensitive")) {
	    ip.setResult(0 != (tf.getConstraints()&TextField.SENSITIVE));
	    return;
	}
	if(optname.equals("-non_predictive")) {
	    ip.setResult(0 != (tf.getConstraints()&TextField.NON_PREDICTIVE));
	    return;
	}
	if(optname.equals("-initial_caps_word")) {
	    ip.setResult(0 != (tf.getConstraints()&TextField.INITIAL_CAPS_WORD));
	    return;
	}
	if(optname.equals("-initial_caps_sentence")) {
	    ip.setResult(0 != (tf.getConstraints()&TextField.INITIAL_CAPS_SENTENCE));
	    return;
	}
	if(optname.equals("-caretposition")) {
	    ip.setResult(tf.getCaretPosition());
	    return;
	}
	super.cget(ip,optname);
    }

    public void cset(Interp ip,String optname,Thing optval) throws HeclException {
	TextField tf = (TextField)theitem;

	if(optname.equals(WidgetInfo.NTYPE)) {
	    int c = (tf.getConstraints() & TextField.CONSTRAINT_MASK);
		
	    tf.setConstraints(c | WidgetInfo.toTextType(optval));
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
	    ip.setResult(tf.setMaxSize(len));
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
	super.cset(ip,optname,optval);
    }
}

