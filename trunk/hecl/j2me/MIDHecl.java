/* Copyright 2004 David N. Welton

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
 * <code>MIDHecl</code> is a small app to demonstrate the use of Hecl
 * in a j2me app.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

public class MIDHecl
    extends MIDlet
    implements CommandListener {

    private Display display;
    private Form resultFrame;
    private TextBox inBox;

    /* Initial script to use. */
    private static String script = "for {set i 0} {< &i 10} {incr &i} { formappend $i }";
    private Interp interp;

    private boolean started = false;
    private boolean runscreen = false;

    private static final Command RUN_COMMAND =
    new Command("Run", Command.SCREEN, 0);

    private static final Command CLEAR_COMMAND =
    new Command("Clear", Command.SCREEN, 0);

    private static final Command SWITCH_COMMAND =
    new Command("Switch", Command.BACK, 0);

    private static final Command EXIT_COMMAND =
    new Command("Exit", Command.EXIT, 0);

    public MIDHecl() {
    }


    public void startApp() {
	if (!started) {
	    resultFrame = new Form("Output");
	    inBox = new TextBox("script input", script, 1000, TextField.ANY);

	    resultFrame.addCommand(EXIT_COMMAND);
	    resultFrame.addCommand(SWITCH_COMMAND);
	    resultFrame.addCommand(CLEAR_COMMAND);

	    inBox.addCommand(EXIT_COMMAND);
	    inBox.addCommand(SWITCH_COMMAND);
	    inBox.addCommand(RUN_COMMAND);

	    resultFrame.setCommandListener(this);
	    inBox.setCommandListener(this);

	    display = Display.getDisplay(this);
	    display.setCurrent(inBox);

	    started = true;
	    try {
		interp = new Interp();
		FormAppendCmd fac = new FormAppendCmd();
		fac.mainForm = resultFrame;
		interp.addCommand("formappend", fac);
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
	    if (runscreen) {
		display = Display.getDisplay(this);
		display.setCurrent(inBox);
		runscreen = false;
	    } else {
		display = Display.getDisplay(this);
		display.setCurrent(resultFrame);
		runscreen = true;
	    }
	} else if (c == RUN_COMMAND) {
	    /* Switch to the other screen and execute the current
	     * script. */
	    display = Display.getDisplay(this);
	    display.setCurrent(resultFrame);
	    runscreen = true;
	    try {
		Eval eval = new Eval();
		Eval.eval(interp, new Thing(inBox.getString()));
	    } catch (Exception e) {
		System.err.println(e);
	    }
	} else if (c == CLEAR_COMMAND) {
	    /* Clear the result frame. */
	    int sz = resultFrame.size();
	    for(int i = 0; i < sz; i++) {
		resultFrame.delete(0);
	    }
	}
    }
}
