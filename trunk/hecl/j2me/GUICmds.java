/* Copyright 2005 David N. Welton

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

import javax.microedition.lcdui.*;

import java.util.Hashtable;
import java.util.Vector;

import org.hecl.Eval;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.Thing;


/**
 * <code>GUICmds</code> implements the high level lcdui commands.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class GUICmds implements org.hecl.Command, CommandListener {

    public Display display;
    public Interp interp;

    private static Hashtable callbacks = new Hashtable();
    private static Hashtable widgets = new Hashtable();
    private static int uniqueid = 0;

    private static Screen screen;
    private static Item item;

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {

        String cmdname = argv[0].getStringRep();
	Form f = (Form)screen;

	if (cmdname.equals("form")) {
	    String title = argv[1].toString();
	    f = new Form(title);
	    f.setCommandListener(this);
	    screen = (Screen)f;

	    /* Evaluate what's "inside" the form. */
	    Eval.eval(interp, argv[2]);

	    display.setCurrent(f);
	    returnUniqueId(interp, f);
	} else if (cmdname.equals("textfield")) {
	    TextField tf = new TextField(argv[1].toString(), argv[2].toString(), 20, 0);
	    f.append(tf);
	    returnUniqueId(interp, tf);
	} else if (cmdname.equals("stringitem")) {
	    StringItem si = new StringItem(argv[1].toString(), argv[2].toString());
	    f.append(si);
	    returnUniqueId(interp, si);
	} else if (cmdname.equals("string")) {
	    f.append(argv[1].toString());
	} else if (cmdname.equals("cmd")) {
	    String cmdName = argv[1].toString();
	    Command c = new Command(cmdName, Command.SCREEN, 0);
	    f.addCommand(c);
//	    callbacks.put(cmdName, c);
	} else if (cmdname.equals("getprop")) {
	    widgetGet(interp, argv[1].toString(), argv[2].toString());
	}
    }

    private void returnUniqueId(Interp interp, Object obj) {
	uniqueid ++;
	widgets.put(new Integer(uniqueid).toString(), obj);
	interp.setResult(IntThing.create(uniqueid));
    }

    public void widgetGet(Interp interp, String widgetid, String property) {
	Object widget = widgets.get(widgetid);
	if (widget instanceof TextField) {
	    if (property.equals("text")) {
		TextField tf = (TextField)widget;
		interp.setResult(new Thing(tf.getString()));
	    }
	}
    }

    public void commandAction(Command c, Displayable s) {
	String procName = c.getLabel();
	try {
	    Eval.eval(interp, new Thing(procName));
	} catch (Exception e) {
	    Hecl.displayError(e.toString());
	}
    }
}
