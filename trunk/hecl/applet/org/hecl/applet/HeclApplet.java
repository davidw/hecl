/* Copyright 2005 Wojciech Kocjan

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
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import org.hecl.Command;
import org.hecl.Eval;
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
    TextArea input = new TextArea();
    TextArea output = new TextArea();
    Button go = new Button("Execute code");
    GridBagLayout gb;
    GridBagConstraints gbc = new GridBagConstraints();
    Interp interp;
    Runner runner;

    public HeclApplet() {
        gb = new GridBagLayout();
        this.setLayout(gb);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        this.add(input);
        gb.setConstraints(input, gbc);

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
        this.add(go);
        gb.setConstraints(go, gbc);

        go.addActionListener(this);

	Font fixed = new Font("Monospaced", Font.PLAIN, 12);

	input.setFont(fixed);
	output.setFont(fixed);
        input.setText("for {set i 0} {< &i 10} {incr &i} {\n    puts \"I = $i\"\n}\n");
    }

    public void start() {
    }

    public void stop() {
	runner = null;
	interp = null;
    }

    String resultString = "";

    public void actionPerformed(ActionEvent action) {
        String str = input.getText();

        if (action.getSource() == go) {
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

	    go.setEnabled(false);
	    runner = null;
	    runner = new Runner();
	    runner.setCode(new Thing(new StringThing(str)));
	    runner.start();
            return;
        }
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
		Eval.eval(interp, code);
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
	    go.setEnabled(true);
	    return;
        }
    }
}
