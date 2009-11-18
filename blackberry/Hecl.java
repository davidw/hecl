/*
 * Main BlackBerry Hecl entry point.
 *
 * Copyright (C) 2005-2009 data2c GmbH (www.data2c.com), DedaSys LLC (www.dedasys.com)
 *
 * Author: Wolfgang S. Kechel - wolfgang.kechel@data2c.com
 */

import java.io.IOException;
import java.util.Vector;

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Gauge;

import org.hecl.Interp;
import org.hecl.HeclException;
import org.hecl.HeclTask;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Thing;
import org.hecl.midp20.MidletCmd;
import org.hecl.misc.HeclUtils;
import org.hecl.net.HttpCmd;
import org.hecl.net.Base64Cmd;
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
    protected HeclTask evaltask = null;
    protected String[] args = {};
    protected boolean started = false;

    public void destroyApp(boolean b) {
	notifyDestroyed();
    }

    public void pauseApp() {
	if (interp.commandExists("midlet.onpause")) {
	    interp.evalAsync(new Thing("midlet.onpause"));
	}
    }

    public void startApp() {
	if (started) {
	    if (interp.commandExists("midlet.onresume")) {
		interp.evalAsync(new Thing("midlet.onresume"));
	    }
	    return;
	}
	started = true;

	Display display = Display.getDisplay(this);
	try {
	    Alert a = new Alert("Loading Hecl", "Loading Hecl...", null, AlertType.INFO);
	    display.setCurrent(a);
	    a.setTimeout(Alert.FOREVER);
	    a.setIndicator(new Gauge(null, false,
				     Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING));
	    interp = new Interp();
	    Vector v = new Vector();
	    for(int i = 0; i<args.length; ++i) {
		v.addElement(new Thing(args[i]));
	    }
	    interp.setVar("argv", ListThing.create(v));

	    // load extensions into interpreter...
	    RMSCmd.load(interp);
	    HttpCmd.load(interp);
	    Base64Cmd.load(interp);
	    org.hecl.blackberry.ServiceBookCmd.load(interp);
	    org.hecl.blackberry.BrowserCmd.load(interp);
	    org.hecl.blackberry.InvokeCmds.load(interp);
//#if locationapi == 1
	    try {
		Class.forName("javax.microedition.location.Location");
		org.hecl.location.LocationCmd.load(interp);
	    } catch (Exception e) {
	    }
//#endif

//#if kxml == 1
	    org.hecl.kxml.KXMLCmd.load(interp);
//#endif

//#if files == 1
	    org.hecl.files.FileCmds.load(interp);
//#endif

	    MidletCmd.load(interp,this);

//#if mwt == 1
	    org.hecl.mwtgui.MwtCmds.load(interp, this);
//#endif

	    String scriptcontent =
		HeclUtils.getResourceAsString(this.getClass(),"/script.hcl","UTF-8");

	    interp.setVar("splash", ObjectThing.create(a));
	    evaltask = interp.evalIdle(new Thing(scriptcontent));
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
	    /* First wait for the idleEval call to complete... */
	    while(evaltask == null) {
		Thread.currentThread().yield();
	    }
	    while (!evaltask.isDone()) {
		try {
		    synchronized(evaltask) {
			evaltask.wait();
		    }
		}
		catch(Exception e) {
		    // ignore
		    e.printStackTrace();
		}
	    }
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
}

