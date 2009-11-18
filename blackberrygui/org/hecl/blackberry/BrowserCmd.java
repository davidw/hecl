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

import java.io.UnsupportedEncodingException;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import java.util.Hashtable;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Operator;
import org.hecl.StringThing;
import org.hecl.Thing;

public class BrowserCmd extends Operator {
    public static final int OPEN = 1;

    public Thing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	switch(cmd) {
	    /* Fetch all records into a list of hashes. */
	    case OPEN: {
		BrowserSession session = Browser.getDefaultSession();
		session.displayPage(StringThing.get(argv[1]));
 		session.showBrowser();
	    }

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

    protected BrowserCmd(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }

    private static Hashtable cmdtable = new Hashtable();
    static {
	try {
	    cmdtable.put("browser.open", new BrowserCmd(OPEN,1,1));
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Can't create browser commands.");
	}

    }
}