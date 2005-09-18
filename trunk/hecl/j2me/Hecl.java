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
 * a j2me app.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

public class Hecl
    extends MIDlet
    implements CommandListener {

    private static Display display;
    private static Form errorFrame;
    private TextBox inBox;

    /* Initial script to use. */
//    private static String script = "for {set i 0} {< &i 10} {incr &i} { formappend $i }";
    private static String script = "set num 0 ; proc puttext {} { global tf; string [getprop $tf text] } ; proc putnum {} { global num ; incr &num; string $num } ; form hello { stringitem {Hecl Demo} {} ; set tf [textfield text: {}] ; cmd puttext ; cmd putnum }";
    private Interp interp;
    private Eval eval;

    private boolean started = false;
    private static boolean errorscreen = false;

    private static final Command RUN_COMMAND = new Command("Run", Command.SCREEN, 0);

    private static final Command SWITCH_COMMAND = new Command("Switch", Command.BACK, 0);

    private static final Command EXIT_COMMAND = new Command("Exit", Command.EXIT, 0);

    public Hecl() {
    }


    public void startApp() {
	if (!started) {
	    inBox = new TextBox("Script Input", script, 1000, TextField.ANY);
	    errorFrame = new Form("Error!");

	    errorFrame.addCommand(SWITCH_COMMAND);

	    inBox.addCommand(EXIT_COMMAND);
	    inBox.addCommand(SWITCH_COMMAND);
	    inBox.addCommand(RUN_COMMAND);

	    inBox.setCommandListener(this);
	    errorFrame.setCommandListener(this);

	    display = Display.getDisplay(this);
	    display.setCurrent(inBox);

	    started = true;
	    try {
		interp = new Interp();

		GUICmds cmds = new GUICmds();
		cmds.display = display;
		cmds.interp = interp;
		interp.commands.put("form", cmds);
		interp.commands.put("stringitem", cmds);
		interp.commands.put("string", cmds);
		interp.commands.put("cmd", cmds);
		interp.commands.put("textfield", cmds);
		interp.commands.put("getprop", cmds);
	    } catch (Exception e) {
		System.err.println(e);
	    }
	}
    }

    public void pauseApp() {}

    public void destroyApp(boolean unconditional) {}

    public void commandAction(Command c, Displayable s) {

	/* Exit the app. */
	if (c == EXIT_COMMAND) {
	    destroyApp(true);
	    notifyDestroyed();
	} else if (c == SWITCH_COMMAND) {
	    /* Switch to the other screen. */
	    if (errorscreen) {
		display = Display.getDisplay(this);
		display.setCurrent(inBox);
		errorscreen = false;
	    } else {
		display = Display.getDisplay(this);
		display.setCurrent(errorFrame);
		errorscreen = true;
	    }
	} else if (c == RUN_COMMAND) {
	    display = Display.getDisplay(this);
	    try {
		Eval.eval(interp, new Thing(inBox.getString()));
	    } catch (Exception e) {
		displayError(e.toString());
	    }
	}
    }

    public static void displayError(String err) {
	display.setCurrent(errorFrame);
	/* Delete existing stuff. */
	for (int i = 0; i < errorFrame.size(); i++) {
	    errorFrame.delete(0);
	}
	errorFrame.append(err);
	errorscreen = true;
    }

}
