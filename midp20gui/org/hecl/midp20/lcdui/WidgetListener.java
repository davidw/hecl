/*
 * Copyright 2005-2007
 * Wolfgang S. Kechel, data2c GmbH (www.data2c.com)
 * David N. Welton, DedaSys LLC
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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.ItemStateListener;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Thing;

class WidgetListener implements CommandListener, ItemStateListener,
ItemCommandListener, EventHandler {

    public WidgetListener(Interp ip,Thing fun,Canvas cv) {
	this(ip,fun);
	this.canvas = cv;
    }
    
    public WidgetListener(Interp ip,Thing fun,Form f) {
	this(ip,fun);
	this.form = f;
    }
    
    public WidgetListener(Interp ip,Thing fun) {
	this.ip = ip;
	this.fun = fun;
    }
    
    public void handleEvent(CanvasEvent e) {
	eventEval(this.canvas, e);
    }
    
   public void itemStateChanged(Item item) {
       eventEval(this.form, item);
    }

    public void commandAction(Command c, Displayable d) {
	eventEval(c, d);
    }

    public void commandAction(Command c,Item item) {
	eventEval(c, item);
    }

    /**
     * The <code>eventEval</code> method takes care of evaluating
     * callbacks from various events.
     *
     * @param o1 an <code>Object</code> value
     * @param o2 an <code>Object</code> value
     */
    private void eventEval(Object o1, Object o2) {
	Vector v = null;
	Thing code = null;
 	try {
	    v = ListThing.get(this.fun.deepcopy());
	} catch (HeclException he) {
	    System.err.println("Error transforming " + this.fun + " into a list");
	}
	v.addElement(ObjectThing.create(o1));
	v.addElement(ObjectThing.create(o2));
	this.ip.evalAsync(ListThing.create(v));
    }

    protected Interp ip;
    protected Thing fun;
    protected Form form = null;
    protected Canvas canvas = null;
}
