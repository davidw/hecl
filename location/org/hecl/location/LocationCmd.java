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

package org.hecl.location;

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
import org.hecl.IntThing;
import org.hecl.HashThing;
import org.hecl.ListThing;
import org.hecl.Operator;
import org.hecl.Properties;
import org.hecl.StringThing;
import org.hecl.Thing;

/**
 * The <code>LocationCmd</code> class implements the location API
 * commands, which utilize a GPS (or other methods) to obtain location
 * information.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class LocationCmd extends Operator {
    public static final int GET = 1;

    public Thing operate(int cmd, Interp interp, Thing[] argv) throws HeclException {
	switch(cmd) {
	    case GET: {
		if (argv.length == 2) {
		    /* Simply fetch the information, blocking the application. */
		    return getLocation(IntThing.get(argv[1]));
		} else if (argv.length > 2) {
		    Properties props = new Properties();
		    props.setProps(argv, 1);
		    /* Use a callback in a separate thread. */
		    int timeout = IntThing.get(props.getProp("-timeout", IntThing.create(100)));

		    LocationRequest locationrequest =
			new LocationRequest(interp,
					    props.getProp("-callback"),
					    props.getProp("-onerror"),
					    timeout);
		    locationrequest.start();
		    return Thing.emptyThing();
		}
	    }

	  default:
	    throw new HeclException("Unknown browser command '"
				    + argv[0].toString() + "' with code '"
				    + cmd + "'.");

	}
    }

    /**
     * The <code>getLocation</code> method does the actual API call to
     * obtain the location information, and package it up in a
     * HashThing for the consumption of the user.
     *
     * @param timeout an <code>int</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    protected static final Thing getLocation(int timeout) throws HeclException {
	try {
	    LocationProvider lp = LocationProvider.getInstance(new Criteria());
	    Location loc = lp.getLocation(timeout);
	    QualifiedCoordinates c = loc.getQualifiedCoordinates();
	    Hashtable h = new Hashtable();
	    h.put("lat", DoubleThing.create(c.getLatitude()));
	    h.put("lon", DoubleThing.create(c.getLongitude()));
	    h.put("alt", DoubleThing.create(c.getAltitude()));
	    h.put("haccuracy", DoubleThing.create(c.getHorizontalAccuracy()));
	    h.put("vaccuracy", DoubleThing.create(c.getVerticalAccuracy()));
	    h.put("location_method", locationMethod(loc.getLocationMethod()));
	    h.put("speed", DoubleThing.create(loc.getSpeed()));
	    h.put("course", DoubleThing.create(loc.getCourse()));
	    return HashThing.create(h);
	} catch (LocationException le) {
	    throw new HeclException("location.get error: " + le.toString());
	} catch (InterruptedException ie) {
	    throw new HeclException("location.get error: " + ie.toString());
	}
    }


    /**
     * The <code>locationMethod</code> method creates a HashThing
     * reporting some information about the location information
     * lookup.
     *
     * @param lmethod an <code>int</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    private static final Thing locationMethod(int lmethod) throws HeclException {
	Hashtable r = new Hashtable();
	if ((Location.MTA_ASSISTED & lmethod) > 0) {
	    r.put("ASSISTED", IntThing.create(1));
	}
	if ((Location.MTA_UNASSISTED & lmethod) > 0) { r.put("UNASSISTED", IntThing.create(1)); }
	if ((Location.MTE_ANGLEOFARRIVAL & lmethod) > 0) { r.put("ANGLEOFARRIVAL", IntThing.create(1)); }
	if ((Location.MTE_CELLID & lmethod) > 0) { r.put("CELLID", IntThing.create(1)); }
	if ((Location.MTE_SATELLITE & lmethod) > 0) { r.put("SATELLITE", IntThing.create(1)); }
	if ((Location.MTE_SHORTRANGE & lmethod) > 0) { r.put("SHORTRANGE", IntThing.create(1)); }
	if ((Location.MTE_TIMEDIFFERENCE & lmethod) > 0) { r.put("TIMEDIFFERENCE", IntThing.create(1)); }
	if ((Location.MTE_TIMEOFARRIVAL & lmethod) > 0) { r.put("TIMEOFARRIVAL", IntThing.create(1)); }
	if ((Location.MTY_NETWORKBASED & lmethod) > 0) { r.put("NETWORKBASED", IntThing.create(1)); }
	if ((Location.MTY_TERMINALBASED & lmethod) > 0) { r.put("TERMINALBASED", IntThing.create(1)); }
	return HashThing.create(r);
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
	    cmdtable.put("location.get", new LocationCmd(GET,1,6));
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Can't create browser commands.");
	}

    }
}