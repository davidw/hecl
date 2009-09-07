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

import java.lang.InterruptedException;

import javax.microedition.location.Coordinates;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;
import javax.microedition.location.LocationException;

import java.util.Hashtable;
import java.util.Vector;

import org.hecl.DoubleThing;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.ListThing;
import org.hecl.Operator;
import org.hecl.StringThing;
import org.hecl.Thing;


class LocationCmd extends Operator {
    public static final int GET = 1;

    public Thing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	switch(cmd) {
	    case GET: {
		try {
		    LocationProvider lp = LocationProvider.getInstance(new Criteria());
		    Location loc = lp.getLocation(IntThing.get(argv[1]));
		    Coordinates c = loc.getQualifiedCoordinates();
		    Vector v = new Vector();
		    v.addElement(DoubleThing.create(c.getLatitude()));
		    v.addElement(DoubleThing.create(c.getLongitude()));
		    v.addElement(DoubleThing.create(c.getAltitude()));
		    return ListThing.create(v);
		} catch (LocationException le) {
		    throw new HeclException("location.get error: " + le.toString());
		} catch (InterruptedException ie) {
		    throw new HeclException("location.get error: " + ie.toString());
		}
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

    protected LocationCmd(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }

    private static Hashtable cmdtable = new Hashtable();
    static {
	try {
	    cmdtable.put("location.get", new LocationCmd(GET,1,1));
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Can't create browser commands.");
	}

    }
}