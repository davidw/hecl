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

import org.hecl.Interp;
import org.hecl.Thing;

//import org.hecl.http.*;
import org.hecl.net.Base64Cmd;
import org.hecl.net.HttpCmd;
import org.hecl.files.*;

/**
 * <code>StandaloneHecl</code> is an example of how to use Hecl to run
 * some code that is contained within the program itself.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class StandaloneHecl {

    private static String script = "for {set i 0} {< $i 10} {incr $i} { puts $i }";

    public static void main(String[] args) {
        try {
            Interp interp = new Interp();
	    new HeclFile().loadModule(interp);
	    //new HttpModule().loadModule(interp);
	    Base64Cmd.load(interp);
	    HttpCmd.load(interp);
            interp.evalAsyncAndWait(new Thing(script));
	    interp.terminate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

