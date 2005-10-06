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
import java.io.InputStream;
import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
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
    private Eval eval;

    private boolean started = false;

    public Hecl() {
    }

    public void destroyApp(boolean unconditional) {}

     public void pauseApp() {}

    public void startApp() {
	if (!started) {
	    int ch;
	    StringBuffer script = new StringBuffer("");
	    DataInputStream is = new DataInputStream(
		this.getClass().getResourceAsStream("/script.hcl"));

	    try {
		while ((ch = is.read()) != -1) {
		    script.append((char)ch);
		}
		is.close();
	    } catch (IOException e) {
		System.err.println("error reading init script: " + e.toString());
	    }

	    display = Display.getDisplay(this);

	    started = true;
	    try {
		interp = new Interp();

		GUICmds cmds = new GUICmds();
		cmds.display = display;
		cmds.interp = interp;

		interp.commands.put("form", cmds);
		interp.commands.put("textbox", cmds);
		interp.commands.put("stringitem", cmds);
		interp.commands.put("string", cmds);
		interp.commands.put("cmd", cmds);
		interp.commands.put("textfield", cmds);
		interp.commands.put("getprop", cmds);
		interp.commands.put("setprop", cmds);
		interp.commands.put("setcurrent", cmds);

		Eval.eval(interp, new Thing(script.toString()));
	    } catch (Exception e) {
		System.err.println(e);
	    }
	}
    }
}
