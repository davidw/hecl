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

import org.hecl.HeclException;

import javax.microedition.lcdui.Item;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VideoControl;



/**
 * The <code>HeclMedia</code> class creates and manages a media
 * player.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
class HeclMedia implements Runnable, PlayerListener {

    private boolean running = false;
    private Item videoItem = null;
    private static Player player = null;
    private VideoControl videocontrol = null;

    public static HeclMedia create(String url) throws HeclException {
	return new HeclMedia(url);
    }

    /**
     * The <code>makeItem</code> method creates an Item that can be
     * displayed on the screen.
     *
     * @return an <code>Item</code> value
     */
    public Item makeItem() {
	/* FIXME - Perhaps this needs to be some kind of parameter?  */
	if ((videocontrol = (VideoControl) player.getControl("VideoControl")) != null) {
	    videoItem = (Item)videocontrol.initDisplayMode(VideoControl.USE_GUI_PRIMITIVE, null);
	    System.out.println("done");
	    return videoItem;
	}
	return null;
    }


    /**
     * THe <code>play</code> method is called from MediaCmd to start
     * playing.
     *
     */
    public void play() {
	Thread t = new Thread(this);
	t.start();
    }


    /**
     * THe <code>start</code> method is called internally by the
     * thread created to run the player.
     */
    public void start() {
	try {
	    System.out.println("starting player");
	    player.start();
	} catch (Exception e) {
	    System.out.println(e);
	}
    }


    /**
     * The <code>snapshot</code> method takes an image snapshot and
     * returns it as a byte sequence.
     *
     * @param encoding a <code>String</code> value that specifies the
     * image format, such as jpeg or png.
     * @return a <code>byte[]</code> value
     * @exception HeclException if an error occurs
     */
    public byte[] snapshot(String encoding) throws HeclException {
	try {
	    byte[] data = videocontrol.getSnapshot("encoding=" + encoding);
	    System.out.println(data.length + " bytes: " + data);
	    return data;
	} catch (MediaException me) {
	    throw new HeclException(me.toString());
	}
    }


    /**
     * The <code>run</code> method is called by Thread to start a
     * thread going which runs the player.
     *
     */
    public void run() {
	if (!running) {
	    this.start();
	}
    }


    /**
     * The <code>playerUpdate</code> method is part of the
     * PlayerListener abstract class, and is called when new events
     * arrive.
     *
     * @param plyr a <code>Player</code> value
     * @param evt a <code>String</code> value
     * @param evtData an <code>Object</code> value
     */
    public void playerUpdate(Player plyr, String evt, Object evtData) {
	/* FIXME - do something with this. */
	System.out.println(evt);
    }

    /**
     * Creates a new <code>HeclMedia</code> instance.
     *
     * @param url a <code>String</code> value specifying the URL to
     * open.  
     * @exception HeclException if an error occurs
     */
    public HeclMedia(String url) throws HeclException {
	try {
	    player = Manager.createPlayer(url);
	    player.realize();
	} catch (Exception e) {
	    throw new HeclException(e.toString());
	}
    }
}
