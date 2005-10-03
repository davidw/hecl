/* Copyright 2005 David N. Welton, Wojciech Kocjan

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

import org.hecl.Eval;
import org.hecl.Interp;
import org.hecl.ObjectThing;
import org.hecl.StringThing;
import org.hecl.Thing;

import org.hecl.http.*;
import org.hecl.files.*;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;

/**
 * <code>StandaloneHecl</code> is an example of how to use Hecl to run
 * some code that is contained within the program itself.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class StandaloneHecl {

//    private static String script = "for {set i 0} {< $i 10} {incr &i} { puts $i }";
    private static String script = "puts $bobvar ";

    public static void main(String[] args) {
	File f = new File("bob.txt");
	File rf = null;
	Thing t = ObjectThing.create(f);
	Thing r = null;

        try {
            Interp interp = new Interp();
	    interp.setVar("bobvar", t);

	    HeclFile.loadModule(interp);
	    HttpModule.loadModule(interp);
            Eval eval = new Eval();
            Eval.eval(interp, new Thing(script));

	    r = interp.getVar("bobvar");
	    rf = (File)ObjectThing.get(r);

	    try {
		RandomAccessFile raf = new RandomAccessFile(f, "rw");
		raf.writeChars("aString");
		raf.close();
	    } catch (IOException e) {
		System.err.println(e.toString());
	    }
	    

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

