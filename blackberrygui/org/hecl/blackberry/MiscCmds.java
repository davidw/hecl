/*
 * Copyright 2010
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

import java.util.Hashtable;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Operator;
import org.hecl.StringThing;
import org.hecl.Thing;

import net.rim.device.api.system.DeviceInfo;

public class MiscCmds extends Operator {
    public static final int VERSION = 1;

    public Thing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	switch(cmd) {
	    /* Fetch all records into a list of hashes. */
	    case VERSION: {
		return new Thing(DeviceInfo.getPlatformVersion());
	    }

	    default:
		throw new HeclException("Unknown miscellaneous blackberry command '"
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

    protected MiscCmds(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }

    private static Hashtable cmdtable = new Hashtable();
    static {
	try {
	    cmdtable.put("misc.systemversion", new MiscCmds(VERSION,0,0));
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Can't create misc commands.");
	}
    }
}