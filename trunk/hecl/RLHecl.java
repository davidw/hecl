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


import org.gnu.readline.*;
import java.io.*;

import com.dedasys.hecl.*;

/**
 *  <code>RHHecl</code> is the main class with Readline support built
 *  in.  Borrow the code from here if you want to embed in your own
 *  system.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

public class RLHecl {

    /**
     * This is the <code>main</code> method, an example of how to
     * integrate Hecl into your own programs.
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(String [] args) {
	try {
	    Interp interp = new Interp(new LoadFile());
	    Eval eval = new Eval();

	    if (args.length > 0) {
		eval.eval(interp, interp.getscript(args[0]));
		System.exit(0);
	    }

	    try {
		Readline.load(ReadlineLibrary.GnuReadline);
	    }
	    catch (UnsatisfiedLinkError ignore_me) {
		System.err.println(
		    "Couldn't load readline lib. Using simple stdin.");
	    }

	    Readline.initReadline("hecl");

	    Runtime.getRuntime()                // if your version supports
		.addShutdownHook(new Thread() { // addShutdownHook (since 1.3)
		    public void run() {
			Readline.cleanup();
		    }
		});

	    String line = null;
	    while (true) {
		try {
		    line = Readline.readline("hecl> ");
		    if (line == null) {
			System.out.println("no input");
		    } else {
			eval.eval(interp, new Thing(line));
		    }
		}
		catch (EOFException eof) {
		    break;
		}
		catch (Exception e) {
		    System.out.println("Error: " + e);
		}
	    }
	    Readline.cleanup();  // see note above about addShutdownHook
	} catch (HeclException e) {
	    System.out.println("Error in startup: " + e);
	}
    }
}
