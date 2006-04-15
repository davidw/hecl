/* Copyright 2004-2006 David N. Welton

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
import java.io.IOException;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;

import org.hecl.Interp;
import org.hecl.Thing;

import org.hecl.http.*;
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

    private boolean started = false;

    public Hecl() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void exitApp() {
	destroyApp(true);
	notifyDestroyed();
    }

    public void pauseApp() {}

    public void startApp() {
	if (!started) {
	    int ch;
	    StringBuffer script = new StringBuffer("");


	    display = Display.getDisplay(this);
	    Form f = new Form("Welcome to Hecl");
	    display.setCurrent(f);
	    f.append("Loading Hecl, please wait ");

	    /* Fetch the script. */
	    int i = 0;
	    DataInputStream is =
		new DataInputStream(this.getClass().getResourceAsStream("/script.hcl"));
	    try {
		while ((ch = is.read()) != -1) {
		    i ++;
		    script.append((char)ch);
		    if (i % 100 == 0) {
			f.append(".");
		    }
		}
		is.close();
	    } catch (IOException e) {
		f.append("error reading init script: " + e.toString());
	    }

	    started = true;
	    try {
		interp = new Interp();

		new HttpModule().loadModule(interp);
		new RMSModule().loadModule(interp);

		GUI cmds = new GUI();
		cmds.display = display;
		cmds.interp = interp;
		cmds.midlet = this;

		interp.commands.put("alert",
				    new GUICmdFacade(GUI.ALERTCMD, cmds));
		interp.commands.put("choicegroup",
				    new GUICmdFacade(GUI.CHOICEGROUPCMD, cmds));
		interp.commands.put("cmd",
				    new GUICmdFacade(GUI.CMDCMD, cmds));
		interp.commands.put("datefield",
				    new GUICmdFacade(GUI.DATEFIELDCMD, cmds));
		interp.commands.put("form",
				    new GUICmdFacade(GUI.FORMCMD, cmds));
		interp.commands.put("gauge",
				    new GUICmdFacade(GUI.GAUGECMD, cmds));
		interp.commands.put("listbox",
				    new GUICmdFacade(GUI.LISTBOXCMD, cmds));
		interp.commands.put("string",
				    new GUICmdFacade(GUI.STRINGCMD, cmds));
		interp.commands.put("stringitem",
				    new GUICmdFacade(GUI.STRINGITEMCMD, cmds));
		interp.commands.put("textbox",
				    new GUICmdFacade(GUI.TEXTBOXCMD, cmds));
		interp.commands.put("textfield",
				    new GUICmdFacade(GUI.TEXTFIELDCMD, cmds));

		interp.commands.put("getprop",
				    new GUICmdFacade(GUI.GETPROPCMD, cmds));
		interp.commands.put("setprop",
				    new GUICmdFacade(GUI.SETPROPCMD, cmds));
		interp.commands.put("getindex",
				    new GUICmdFacade(GUI.GETINDEXCMD, cmds));
		interp.commands.put("setindex",
				    new GUICmdFacade(GUI.SETINDEXCMD, cmds));
		interp.commands.put("setcurrent",
				    new GUICmdFacade(GUI.SETCURRENTCMD, cmds));
		interp.commands.put("noscreen",
				    new GUICmdFacade(GUI.NOSCREENCMD, cmds));
		interp.commands.put("screenappend",
				    new GUICmdFacade(GUI.SCREENAPPENDCMD, cmds));
		/* interp.commands.put("mem", cmds); */
		interp.commands.put("exit",
				    new GUICmdFacade(GUI.EXITCMD, cmds));

		interp.eval(new Thing(script.toString()));
		script = null;
		f = null;
	    } catch (Exception e) {
		f.append(e.toString());
	    }
	}
    }

    public void runScript(String s) {
	try {
	    interp.eval(new Thing(s));
	} catch (Exception e) {
	    /* e.printStackTrace(); */
	    System.err.println("Error in runScript: " + e);
	}
    }
}
