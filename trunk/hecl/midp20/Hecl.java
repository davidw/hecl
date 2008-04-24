/*
 * Copyright (C) 2005-2007 data2c GmbH (www.data2c.com)
 *
 * Author: Wolfgang S. Kechel - wolfgang.kechel@data2c.com
 */

import java.io.IOException;
import java.util.Vector;

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Alert;
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
    protected Interp interp = null;

    public void destroyApp(boolean b) {
	notifyDestroyed();
    }

    public void pauseApp() {
    }

    public void startApp() {
	Display display = Display.getDisplay(this);
	try {
	    interp = new Interp();
	    Vector v = new Vector();
	    for(int i = 0; i<args.length; ++i) {
		v.addElement(new Thing(args[i]));
	    }
	    interp.setVar("argv", ListThing.create(v));

	    // load extensions into interpreter...
	    RMSCmd.load(interp);
	    HttpCmd.load(interp);
//#if kxml == 1
	    org.hecl.kxml.KXMLCmd.load(interp);
//#endif
	    MidletCmd.load(interp,this);
	    String scriptcontent =
		HeclUtils.getResourceAsString(this.getClass(),"/script.hcl","UTF-8");
	    interp.evalIdle(new Thing(scriptcontent));
	}
	catch (Exception e) {
	    e.printStackTrace();
	    destroyApp(true);
	}
    }

    /**
     * The <code>runScript</code> method exists so that external
     * applications (emulators, primarily) can call into Hecl and run
     * scripts.
     *
     * @param s a <code>String</code> value
     */
    public void runScript(String s) {
	try {
	    interp.eval(new Thing(s));
	} catch (Exception e) {
	    /* At least let the user know there was an error. */
	    Alert a = new Alert("Hecl error", e.toString(),
				null, null);
	    Display display = Display.getDisplay(this);
	    display.setCurrent(a);
	    /* e.printStackTrace(); */
	    System.err.println("Error in runScript: " + e);
	}
    }

    protected String[] args = {};
    protected boolean started = false;
}

