/* Copyright 2007 by DedaSys LLC

Authors:
David N. Welton - davidw@dedasys.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.hecl.media;

import java.util.Hashtable;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.ObjectThing;
import org.hecl.Operator;
import org.hecl.Thing;

import javax.microedition.lcdui.Item;

/**
 * The <code>MediaCmd</code> class creates the commands necessary to
 * create and manage a HeclMedia instance.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class MediaCmd extends org.hecl.Operator {

    public Thing operate(int cmd, Interp interp, Thing[] argv)
	throws HeclException {
	HeclMedia hm = null;

	switch (cmd) {
	    /* Creates the new player: [media.player $url]*/
	    case PLAYER:
		return ObjectThing.create(HeclMedia.create(argv[1].toString()));
		/* Creates a widget that can be displayed on the
		 * screen: $form append [media.widget $media]  */
	    case WIDGET:
		hm = (HeclMedia)ObjectThing.get(argv[1]);
		Item mediaitem = hm.makeItem();
		return ObjectThing.create(mediaitem);
		/* Starts playing the media. */
	    case PLAY:
		hm = (HeclMedia)ObjectThing.get(argv[1]);
		hm.play();
		return null;
		/* Takes a snapshot of the media. */
	    case SNAPSHOT:
		hm = (HeclMedia)ObjectThing.get(argv[1]);
		String encoding = null;
		if (argv.length > 2) {
		    encoding = argv[2].toString();
		} else {
		    encoding = "jpeg";
		}
		byte [] data = hm.snapshot(encoding);
		return new Thing(MediaCmd.toISOBytes(data));
	    default:
		throw new HeclException("Unknown media command '"
					+ argv[0].toString() + "' with code '"
					+ cmd + "'.");
	}
    }


    /**
     * The <code>toISOBytes</code> method transforms an array of bytes
     * into a string.
     *
     * @param data a <code>byte[]</code> value
     * @return a <code>String</code> value
     */
    public static String toISOBytes(byte []data) {
	StringBuffer buf = new StringBuffer("");
	for(int i = 0; i < data.length; ++i) {
	    char ch = (char)data[i];
	    buf.append(ch);
	}
	return buf.toString();
    }

    public void close() {
        synchronized (this) {
	    System.out.println("we lost R2!");
        }
    }

    public static void load(Interp ip) throws HeclException {
	Operator.load(ip,cmdtable);
    }


    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip,cmdtable);
    }



    protected MediaCmd(int cmdcode,int minargs,int maxargs) {
	super(cmdcode, minargs, maxargs);
    }

    public static final int SNAPSHOT = 1;
    public static final int PLAYER = 2;
    public static final int WIDGET = 3;
    public static final int PLAY = 4;

    private static Hashtable cmdtable = new Hashtable();
    static {
	cmdtable.put("media.snapshot", new MediaCmd(SNAPSHOT,1,2));
	cmdtable.put("media.player", new MediaCmd(PLAYER,1,1));
	cmdtable.put("media.widget", new MediaCmd(WIDGET,1,1));
	cmdtable.put("media.play", new MediaCmd(PLAY,1,1));
    }
}
