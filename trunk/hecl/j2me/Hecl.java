/* Copyright 2004-2005 David N. Welton

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

import org.hecl.Eval;
import org.hecl.Interp;
import org.hecl.Thing;

/**
 * <code>Hecl</code> is a small app to demonstrate the use of Hecl in
 * a j2me app.  The GUI is now written entirely in Hecl itself!
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

public class Hecl
    extends MIDlet {

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

		GUICmds cmds = new GUICmds();
		cmds.display = display;
		cmds.interp = interp;
		cmds.midlet = this;

		interp.commands.put("alert", cmds);
		interp.commands.put("choicegroup", cmds);
		interp.commands.put("cmd", cmds);
		interp.commands.put("form", cmds);
		interp.commands.put("gauge", cmds);
		interp.commands.put("listbox", cmds);
		interp.commands.put("string", cmds);
		interp.commands.put("stringitem", cmds);
		interp.commands.put("textbox", cmds);
		interp.commands.put("textfield", cmds);

		interp.commands.put("getprop", cmds);
		interp.commands.put("setprop", cmds);
		interp.commands.put("getindex", cmds);
		interp.commands.put("setindex", cmds);
		interp.commands.put("setcurrent", cmds);
		interp.commands.put("noscreen", cmds);
		interp.commands.put("screenappend", cmds);
		/* interp.commands.put("mem", cmds); */
		interp.commands.put("exit", cmds);

		Eval.eval(interp, new Thing(script.toString()));
		script = null;
		f = null;
	    } catch (Exception e) {
		f.append(e.toString());
	    }
	}
    }
}
