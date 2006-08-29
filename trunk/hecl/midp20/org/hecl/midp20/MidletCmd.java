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

package org.hecl.midp20;

import java.io.IOException;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.Operator;
import org.hecl.RealThing;
import org.hecl.StringThing;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

import org.awt.Point;


public class MidletCmd extends Operator {
    public static final int EXIT = 1;
    public static final int PAUSE = 2;
    public static final int RESUME = 3;
    public static final int PLATFORMREQUEST = 4;
    public static final int CHECKPERMISSIONS = 5;
    public static final int GETPROP = 6;
    public static final int HASPROP = 7;
    public static final int RESASSTRING = 8;


    public static MIDlet midlet() {
	return themidlet;
    }


    public static Display display() {
	return Display.getDisplay(themidlet);
    }
    
    public RealThing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	String str = null;
	if(argv.length > 1)
	    str = argv[1].toString();
	switch (cmd) {
	  case EXIT:
	    themidlet.notifyDestroyed();
	    return null;
	    
	  case PAUSE:
	    themidlet.notifyPaused();
	    return null;

	  case RESUME:
	    themidlet.resumeRequest();
	    return null;

	  case PLATFORMREQUEST:
	    try {
		return themidlet.platformRequest(argv[1].toString()) ?
		    IntThing.ONE : IntThing.ZERO;
	    }
	    catch(Exception e) {
		throw new HeclException(e.toString());
	    }
	    
	  case CHECKPERMISSIONS:
	    return new IntThing(themidlet.checkPermission(argv[1].toString()));

	  case GETPROP:
	    /*
	      JSR 	Property Name            Default Value
	      30 	microedition.platform 	null
		        microedition.encoding 	ISO8859_1
		        microedition.configuration 	CLDC-1.0
		        microedition.profiles 	null
	      37 	microedition.locale 	null
		        microedition.profiles 	MIDP-1.0
	      75 	microedition.io.file.FileConnection.version 	1.0
		        file.separator 	(impl-dep)
		        microedition.pim.version 	1.0
	      118 	microedition.locale 	null
		        microedition.profiles 	MIDP-2.0
		        microedition.commports 	(impl-dep)
		        microedition.hostname 	(impl-dep)
	      120 	wireless.messaging.sms.smsc 	(impl-dep)
	      139 	microedition.platform 	(impl-dep)
		        microedition.encoding 	ISO8859-1
		        microedition.configuration 	CLDC-1.1
		        microedition.profiles 	(impl-dep)
	      177 	microedition.smartcardslots 	(impl-dep)
	      179 	microedition.location.version 	1.0
	      180 	microedition.sip.version 	1.0
	      184 	microedition.m3g.version 	1.0
	      185 	microedition.jtwi.version 	1.0
	      195 	microedition.locale 	(impl-dep)
		        microedition.profiles 	IMP-1.0
	      205 	wireless.messaging.sms.smsc 	(impl-dep)
	      205 	wireless.messaging.mms.mmsc 	(impl-dep)
	      211 	CHAPI-Version 	1.0
	    */
	    String s = themidlet.getAppProperty(str);
	    if(s == null)
		s = "";
	    return new StringThing(s);

	  case HASPROP:
	    return themidlet.getAppProperty(str) != null ?
		IntThing.ONE : IntThing.ZERO;

	  case RESASSTRING:
	    try {
		return new StringThing(
		    HeclUtils.getResourceAsString(
			themidlet.getClass(),str,
			argv.length == 2 ? argv[2].toString() : null));
	    }
	    catch(IOException e) {
		throw new HeclException("Unable to read resource '"
					+ str + "' - " + e.getMessage());
	    }

	  default:
	    throw new HeclException("Unknown midlet command '"
				    + argv[0].toString() + "' with code '"
				    + cmd + "'.");
	}
	// notreached
	//return null;
    }


    public static void load(Interp ip,MIDlet m) throws HeclException {
	themidlet = m;
	Operator.load(ip);
    }


    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip);
    }


    protected MidletCmd(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }
    


    static protected MIDlet themidlet = null;
    
    static {
        cmdtable.put("midlet.exit", new MidletCmd(EXIT,0,0));
        cmdtable.put("midlet.pause", new MidletCmd(PAUSE,0,0));
        cmdtable.put("midlet.resume", new MidletCmd(RESUME,0,0));
        cmdtable.put("midlet.platformrequest", new MidletCmd(PLATFORMREQUEST,1,1));
	cmdtable.put("midlet.checkpermissions", new MidletCmd(CHECKPERMISSIONS,1,1));
	cmdtable.put("midlet.getappproperty", new MidletCmd(GETPROP,1,1));
	cmdtable.put("midlet.hasappproperty", new MidletCmd(HASPROP,1,1));
	cmdtable.put("midlet.resourceasstring", new MidletCmd(RESASSTRING,1,2));
    }
}
