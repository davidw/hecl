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

import java.util.Vector;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import org.hecl.Compare;
import org.hecl.Interp;
import org.hecl.Thing;
import org.hecl.ListThing;
import org.hecl.HeclException;
import org.hecl.HeclTask;

import org.hecl.files.FileCmds;
import org.hecl.files.HeclFile;
//import org.hecl.http.HttpModule;
import org.hecl.net.Base64Cmd;
import org.hecl.net.HttpCmd;

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
	Interp interp = null;
	try {
	    interp = new Interp();
	} catch (HeclException he) {
	    System.err.println("Error initializing the Hecl interpreter!");
	    System.exit(1);
	}

        try {
            int i;
	    /* Add the standard packages in. */
	    FileCmds.load(interp);
	    //new HttpModule().loadModule(interp);
	    Base64Cmd.load(interp);
	    HttpCmd.load(interp);
	    new org.hecl.load.HeclLoad().loadModule(interp);
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
	interp.terminate();
	System.exit(0);
    }

    static final String PROMPT = "hecl> ";
    static final String PROMPT2 = "hecl+ ";

    /**
     * The <code>commandLine</code> method implements a
     * Read/Eval/Print Loop.
     *
     * @param interp an <code>Interp</code> value
     * @exception IOException if an error occurs
     */
    private static void commandLine (Interp interp) throws IOException {
	BufferedReader buff = new
	    BufferedReader(new InputStreamReader(System.in));
	String line = null;
	/* Normal prompt to use. */
	/* Prompt to use when we need more input. */
	String prompt = PROMPT;
	String morebuffer = "";

	while (true) {
	    System.out.print(prompt);
	    System.out.flush();
	    line = buff.readLine();
	    /* Exit on end of file. */
	    if (line == null)
		break;

	    try {
		Thing res = interp.evalAsyncAndWait(new Thing(morebuffer + line));
		if(res != null
		   && 0 != Compare.compareString(res, Thing.EMPTYTHING)) {
		    System.out.println(interp.result);
		}
	    }
	    catch (HeclException he) {
		if (he.code.equals("PARSE_ERROR")) {
		    //System.err.println("paser error: "+he.getMessage());
		    //he.printStackTrace();
		    
		    /* When we need more input, stash the current
		     * input in morebuffer, and change the prompt. */
		    prompt = PROMPT2;
		    morebuffer = morebuffer + "\n" + line;
		} else {
		    System.out.println(he);
		    morebuffer = "";
		    prompt = PROMPT;
		}
	    }
	    catch(Exception e) {
		e.printStackTrace();
	    }
	}
	interp.terminate();
	System.exit(0);
    }
}

