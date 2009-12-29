/*
 * Copyright 2009
 * DedaSys LLC - http://www.dedasys.com
 *
 * Author: David N. Welton <davidw@dedasys.com>
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

package org.hecl.blackberry;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.AddressBookArguments; /* FIXME - TODO */
import net.rim.blackberry.api.invoke.ApplicationArguments;
import net.rim.blackberry.api.invoke.CalculatorArguments;
import net.rim.blackberry.api.invoke.CalendarArguments; /* FIXME - TODO */
//#if jde.version > 4.3
import net.rim.blackberry.api.invoke.CameraArguments;
//#endif
import net.rim.blackberry.api.invoke.PhoneArguments;

import java.util.Hashtable;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Operator;
import org.hecl.StringThing;
import org.hecl.Thing;

public class InvokeCmds extends Operator {
    public static final int CALL = 10;
    public static final int CAMERA = 20;
    public static final int VIDEO = 30;
    public static final int CALCULATOR = 40;

    public Thing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	switch(cmd) {
	    case CALL: {
		Invoke.invokeApplication(Invoke.APP_TYPE_PHONE,
					 new PhoneArguments(PhoneArguments.ARG_CALL, argv[1].toString()));
		return Thing.emptyThing();
	    }

	    case CALCULATOR: {
		Invoke.invokeApplication(Invoke.APP_TYPE_CALCULATOR,
					 new CalculatorArguments());
		return Thing.emptyThing();
	    }


//#if jde.version > 4.3
	    case CAMERA: {
		Invoke.invokeApplication(Invoke.APP_TYPE_CAMERA,
					 new CameraArguments(CameraArguments.ARG_CAMERA_APP));
		return Thing.emptyThing();
	    }

	    case VIDEO: {
		Invoke.invokeApplication(Invoke.APP_TYPE_CAMERA,
					 new CameraArguments(CameraArguments.ARG_VIDEO_RECORDER));
		return Thing.emptyThing();
	    }
//#endif

	  default:
	    throw new HeclException("Unknown browser command '"
				    + argv[0].toString() + "' with code '"
				    + cmd + "'.");

	}
    }

    public static void load(Interp ip) throws HeclException {
	Operator.load(ip,cmdtable);
    }


    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip,cmdtable);
    }

    protected InvokeCmds(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }

    private static Hashtable cmdtable = new Hashtable();

    static {
	try {
	    cmdtable.put("invoke.call", new InvokeCmds(CALL,1,1));
	    cmdtable.put("invoke.calculator", new InvokeCmds(CALCULATOR,0,0));
	    cmdtable.put("invoke.camera", new InvokeCmds(CAMERA,0,0));
	    cmdtable.put("invoke.video", new InvokeCmds(VIDEO,0,0));
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Can't create browser commands.");
	}

    }
}