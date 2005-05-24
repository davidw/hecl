/*
 * Created on 2005-03-04
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hecl.applet;
import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
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
 * @author zoro
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class HeclApplet extends Applet implements ActionListener {
    TextArea input = new TextArea();
    TextArea output = new TextArea();
    Button go = new Button("Execute code");
    GridBagLayout gb;
    GridBagConstraints gbc = new GridBagConstraints();

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

        input.setText("for {set i 0} {< $i 10} {incr &i} {\n    puts \"I=$i\"\n}\n");
    }

    String resultString = "";

    public void actionPerformed(ActionEvent action) {
        long t0, t1;
        String str = input.getText();
        Interp interp;
        Eval eval;

        if (action.getSource() == go) {
            output.setText("");
            try {
                interp = new Interp();
                eval = new Eval();
                interp.commands.put("puts", new PutsCommand());
            } catch (HeclException error) {
                output.setForeground(Color.red);
                output.setText("Error while initializing Hecl:\n"
                        + error.getMessage());
                return;
            }
            try {
                synchronized (resultString) {
                    resultString = "";
                }
                t0 = new Date().getTime();
                Eval.eval(interp, new Thing(new StringThing(str)));
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

    class PutsCommand implements Command {
        public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
            if (argv.length != 2) {
                throw HeclException.createWrongNumArgsException(argv, 1,
                        "string");
            }
            synchronized (resultString) {
                resultString += argv[1].getStringRep() + "\n";
            }
        }
    }
}
