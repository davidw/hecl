/* Copyright 2004-2007 David N. Welton, DedaSys LLC

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

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;

import org.hecl.HeclTask;
import org.hecl.Interp;
import org.hecl.Thing;
import org.hecl.rms.RMSCmd;

//import org.hecl.http.*;
import org.hecl.net.Base64Cmd;
import org.hecl.net.HttpCmd;
import org.hecl.rms.*;


/**
 * <code>Hecl</code> is the entry point into the main version of the
 * J2ME code.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

public class Hecl extends MIDlet {

    private static Display display;

    private Interp interp;
    private HeclTask evaltask = null;

    private boolean started = false;

    public Hecl() {
    }

    /**
     * The <code>destroyApp</code> method is called when the
     * application is exiting.
     *
     * @param unconditional a <code>boolean</code> value
     */
    public void destroyApp(boolean unconditional) {
    }


    /**
     * The <code>exitApp</code> method destroys the current midlet.
     *
     */
    public void exitApp() {
	destroyApp(true);
	notifyDestroyed();
    }

    public void pauseApp() {
    }

    /**
     * The <code>startApp</code> method is called when the application
     * is started.
     *
     */
    public void startApp() {
	if (!started) {
	    int ch;
	    StringBuffer script = new StringBuffer("");

	    display = Display.getDisplay(this);
	    Form f = new Form("Welcome to Hecl");
	    display.setCurrent(f);
	    f.append("Loading Hecl, please wait ");
	    /* Fetch the script. */
	    DataInputStream is = new DataInputStream(
		this.getClass().getResourceAsStream("/script.hcl"));

	    byte[] buf = new byte[512];
	    int bytesread = 0;
	    byte[] result = new byte[bytesread];
	    int i = 0;
	    int n = 0;
	    try {
		while((n = is.read(buf, 0, buf.length)) > 0) {
		    byte[] newres = new byte[n+bytesread];
		    for(i = 0; i < bytesread; ++i) {
			newres[i] = result[i];
		    }
		    for(i = 0; i < n; ++i, ++bytesread) {
			newres[bytesread] = buf[i];
		    }
		    result = newres;
		}
		is.close();
	    } catch (IOException e) {
		f.append("error reading init script: " + e.toString());
		return;
	    }
	    script.append(new String(result));

	    started = true;
	    try {
		interp = new Interp();
		Base64Cmd.load(interp);
		HttpCmd.load(interp);
		//new HttpModule().loadModule(interp);
		RMSCmd.load(interp);
	    } catch (Exception e) {
		f.append(e.toString());
	    }

	    GUI cmds = new GUI();
	    cmds.display = display;
	    cmds.interp = interp;
	    cmds.midlet = this;

	    interp.addCommand("alert",
			      new GUICmdFacade(GUI.ALERTCMD, cmds));
	    interp.addCommand("choicegroup",
			      new GUICmdFacade(GUI.CHOICEGROUPCMD, cmds));
	    interp.addCommand("cmd",
			      new GUICmdFacade(GUI.CMDCMD, cmds));
	    interp.addCommand("datefield",
			      new GUICmdFacade(GUI.DATEFIELDCMD, cmds));
	    interp.addCommand("form",
			      new GUICmdFacade(GUI.FORMCMD, cmds));
	    interp.addCommand("gauge",
			      new GUICmdFacade(GUI.GAUGECMD, cmds));
	    interp.addCommand("listbox",
			      new GUICmdFacade(GUI.LISTBOXCMD, cmds));
	    interp.addCommand("string",
			      new GUICmdFacade(GUI.STRINGCMD, cmds));
	    interp.addCommand("stringitem",
			      new GUICmdFacade(GUI.STRINGITEMCMD, cmds));
	    interp.addCommand("textbox",
			      new GUICmdFacade(GUI.TEXTBOXCMD, cmds));
	    interp.addCommand("textfield",
			      new GUICmdFacade(GUI.TEXTFIELDCMD, cmds));

	    interp.addCommand("getprop",
			      new GUICmdFacade(GUI.GETPROPCMD, cmds));
	    interp.addCommand("setprop",
			      new GUICmdFacade(GUI.SETPROPCMD, cmds));
	    interp.addCommand("getindex",
			      new GUICmdFacade(GUI.GETINDEXCMD, cmds));
	    interp.addCommand("setindex",
			      new GUICmdFacade(GUI.SETINDEXCMD, cmds));
	    interp.addCommand("setcurrent",
			      new GUICmdFacade(GUI.SETCURRENTCMD, cmds));
	    interp.addCommand("noscreen",
			      new GUICmdFacade(GUI.NOSCREENCMD, cmds));
	    interp.addCommand("screenappend",
			      new GUICmdFacade(GUI.SCREENAPPENDCMD, cmds));
	    interp.addCommand("exit",
			      new GUICmdFacade(GUI.EXITCMD, cmds));
//#if sms == "true"
	    interp.addCommand("sms",
			      new GUICmdFacade(GUI.SMSCMD, cmds));
//#endif

	    f.append("\nOK - executing");
	    evaltask = interp.evalIdle(new Thing(script.toString()));
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
	    display.setCurrent(a);
	    /* e.printStackTrace();  */
	    System.err.println("Error in runScript: " + e);
	}
    }
}
