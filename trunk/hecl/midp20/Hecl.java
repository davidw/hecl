/*
 * Copyright (C) 2005-2007 data2c GmbH (www.data2c.com)
 *
 * Author: Wolfgang S. Kechel - wolfgang.kechel@data2c.com
 */

import java.io.IOException;
import java.util.Vector;

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

import org.hecl.Interp;
import org.hecl.HeclException;
import org.hecl.ListThing;
import org.hecl.Thing;
import org.hecl.midp20.MidletCmd;
import org.hecl.misc.HeclUtils;
import org.hecl.net.HttpCmd;
import org.hecl.rms.RMSCmd;

/**
 * <code>Hecl</code> is the main class for the MIDP2.0 Hecl.jar.  Use
 * this as an example if you want to create your own custom
 * application.
 *
 * @version 1.0
 */
public class Hecl extends MIDlet {
    public void destroyApp(boolean b) {
	notifyDestroyed();
    }

    public void pauseApp() {
    }

    public void startApp() {
	System.err.println("-->startApp()");
	Display display = Display.getDisplay(this);
	try {
	    ip = new Interp();
	    Vector v = new Vector();
	    for(int i = 0; i<args.length; ++i) {
		v.addElement(new Thing(args[i]));
	    }
	    ip.setVar("argv",ListThing.create(v));

	    // load extensions into interpreter...
	    RMSCmd.load(ip);
	    HttpCmd.load(ip);
	    MidletCmd.load(ip,this);
	    String scriptcontent =
		HeclUtils.getResourceAsString(this.getClass(),"/script.hcl","UTF-8");
	    ip.evalIdle(new Thing(scriptcontent));
	}
	catch (Exception e) {
	    e.printStackTrace();
	    destroyApp(true);
	}
    }

    protected Interp ip = null;
    protected String[] args = {};
    protected boolean started = false;
}

