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
 * a j2me app.  The GUI is now written entirely in Hecl itself!
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

public class Hecl
    extends MIDlet {

    private static Display display;

    /* This is the script that the user sees and can modify. */
    private static String script = "set num 0;" +
" set bckbtn {cmd label Back code back type back} ;" +
" proc puttext {tf} { string [getprop $tf text] } ;" +
" proc putnum {num} { incr &num; string $num } ;" +
" proc back {} {global newform; setcurrent $newform } ; " +
" proc maketb {} { " +
"     global bckbtn; setcurrent [textbox label {New TextBox} text defaulttext len 100 code $bckbtn]" +
" } ;" +
" proc makeform {} { global bckbtn; setcurrent [form code $bckbtn] } ;" +
" set newform [form label hello code {" +
"    stringitem label {Hecl Demo} text {};" +
"    set tf [textfield label text:];" +
"    set tfeval [textfield label {eval hecl code:}];" +
"    cmd label {Print Text} code [list puttext $tf]; " +
"    cmd label {Eval} code {string [eval [getprop $tfeval text]]}; " +
"    cmd label {Print Number} code [list putnum &num] ;" +
"    cmd label {Make Textbox} code maketb;" +
"    cmd label {Make Form} code makeform;" +
"    cmd label {Exit} type exit;" +
"}];" +
"setcurrent $newform;"
    ;

    /* And this is the main script itself! */
    private static String mainscript = "" +
    " proc err {txt} {global errf; setcurrent $errf; string $txt};" +
    " proc run {} {global main; if { catch {upeval [getprop $main text]} problem } {err $problem} };" +
    " set errf [form label Error code {cmd label Back type back code {setcurrent $main}}];" +
    " set main [textbox label Hecl code {" +
    "     cmd label Switch code [list setcurrent $errf] ;" +
    "     cmd label Run code run ;" +
    "} text {" + script + "} len 1000];" +
    "setcurrent $main ;"
    ;

    private Interp interp;
    private Eval eval;

    private boolean started = false;

    public Hecl() {
    }

    public void destroyApp(boolean unconditional) {}

     public void pauseApp() {}

    public void startApp() {
	if (!started) {
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

		Eval.eval(interp, new Thing(mainscript));
	    } catch (Exception e) {
		System.err.println(e);
	    }
	}
    }
}
