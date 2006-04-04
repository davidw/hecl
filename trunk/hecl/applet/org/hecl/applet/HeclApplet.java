/* Copyright 2005-2006 Wojciech Kocjan, David N. Welton

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

package org.hecl.applet;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import org.hecl.Command;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.StringThing;
import org.hecl.Thing;


/**
 * <code>HeclApplet</code> implements an applet that lets you try out
 * hecl.
 *
 * @author zoro
 * @version 1.0
 */
public class HeclApplet extends Applet implements ActionListener {
    TextArea output = new TextArea();
    GridBagLayout gb;
    GridBagConstraints gbc = new GridBagConstraints();
    Interp interp;
    Runner runner;

    String script = null;

    /**
     * The <code>runScript</code> method is called from a bit of
     * Javascript.  It sets the script and runs it.  We call it via
     * javascript so as to minimize the actual applet.
     *
     * @param s a <code>String</code> value
     */
    public void runScript(String s) {
	script = s;
	runHecl();
    }

    public HeclApplet() {
        gb = new GridBagLayout();
        this.setLayout(gb);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        this.add(output);
        gb.setConstraints(output, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;

	Font fixed = new Font("Monospaced", Font.PLAIN, 10);
	output.setFont(fixed);
    }

    public void start() {
    }

    public void stop() {
	runner = null;
	interp = null;
    }

    String resultString = "";

    public void runHecl() {
	output.setText("");
	try {
	    interp = new Interp();
	    interp.commands.put("puts", new PutsCommand());
	} catch (HeclException error) {
	    output.setForeground(Color.red);
	    output.setText("Error while initializing Hecl:\n"
			   + error.getMessage());
	    return;
	}

	runner = null;
	runner = new Runner();
	runner.setCode(new Thing(new StringThing(script)));
	runner.start();
	return;
    }

    public void actionPerformed(ActionEvent action) {
    }

    class PutsCommand implements Command {
        public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
            if (argv.length != 2) {
                throw HeclException.createWrongNumArgsException(argv, 1,
								"string");
            }
            synchronized (resultString) {
                resultString += argv[1].toString() + "\n";
		output.setText(resultString);
            }
        }
    }

    /**
     * The <code>Runner</code> class provides a way to perform
     * long-running scripts without blocking the GUI.
     *
     */
    class Runner extends Thread {
	Thing code;
	public Runner() {
	}

	public void setCode (Thing c) {
	    code = c;
	}

	public void run() {
	    long t0, t1;
	    String str = "";
            try {
                synchronized (resultString) {
                    resultString = "";
                }
                t0 = new Date().getTime();
		interp.eval(code);
                t1 = new Date().getTime();
            } catch (HeclException error) {
                output.setForeground(Color.red);
                output.setText("Error while running script:\n"
			       + error.getMessage());
                return;
            }
            synchronized (resultString) {
                str = "Execution result: (time: " + (t1 - t0) + " ms)\n"
		    + resultString;
            }
            output.setForeground(Color.black);
            output.setText(str);
	    return;
        }
    }
}
