/* Copyright 2005 Wojciech Kocjan, David N. Welton

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

import java.util.Vector;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import org.hecl.Compare;
import org.hecl.Eval;
import org.hecl.Interp;
import org.hecl.Thing;
import org.hecl.ListThing;
import org.hecl.HeclException;

import org.hecl.files.HeclFile;
import org.hecl.http.HttpModule;
import org.hecl.load.HeclLoad;


/**
 * <code>Hecl</code> - this class implements the main Hecl command
 * line interpreter.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class Hecl {
    /**
     * <code>main</code> is what actually runs things.
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(String[] args) {
        try {
            int i;
            Interp interp = new Interp();
	    /* Add the standard packages in. */
	    new HeclFile().loadModule(interp);
	    new HeclLoad().loadModule(interp);
            Eval eval = new Eval();
	    Vector argv = new Vector();

            for (i = 0; i < args.length; i++) {
                //System.out.println("(running " + args[i] + ")");
		argv.addElement(new Thing(args[i]));
            }
	    interp.setVar("argv", ListThing.create(argv));
	    if (args.length > 0) {
		HeclFile.sourceFile(interp, args[0]);
	    } else {
		Hecl.commandLine(interp);
	    }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The <code>commandLine</code> method implements a
     * Read/Eval/Print Loop.  This could be improved by having
     * something akin to Tcl's "is command complete?" function so that
     * it would be possible to accept a command over more than one
     * line.
     *
     * @param interp an <code>Interp</code> value
     * @exception IOException if an error occurs
     */
    private static void commandLine (Interp interp) throws IOException {
	BufferedReader buff = new
	    BufferedReader(new InputStreamReader(System.in));
	String line = null;
	int availbytes = 0;
	while (true) {
	    System.out.print("hecl> ");
	    System.out.flush();
	    line = buff.readLine();
	    /* Exit on end of file. */
	    if (line == null) {
		System.exit(0);
	    }
	    try {
		Eval.eval(interp, new Thing(line));
		if (interp.result != null &&
		    Compare.compareString(interp.result, new Thing("")) != 0) {
		    System.out.println(interp.result);
		}
	    } catch (HeclException he) {
		System.out.println(he);
	    }
	}
    }
}

