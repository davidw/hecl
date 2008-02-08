/* Copyright 2005-2007 Wojciech Kocjan, David N. Welton

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

import org.hecl.Interp;
import org.hecl.Thing;
import org.hecl.ListThing;
import org.hecl.HeclException;
import org.hecl.HeclTask;

import org.hecl.Command;
import org.hecl.ObjectThing;
import org.hecl.HeclException;
import org.hecl.IntThing;
import org.hecl.ClassCommand;
import org.hecl.ClassCommandInfo;

import org.hecl.files.FileCmds;
import org.hecl.files.HeclFile;
import org.hecl.load.LoadCmd;
import org.hecl.net.Base64Cmd;
import org.hecl.net.HttpCmd;

import org.hecl.java.HeclJavaCmd;
import org.hecl.java.NullCmd;

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
	    System.err.println("Error initializing the Hecl interpreter: " + he);
	    System.exit(1);
	}

        try {
            int i;
	    /* Add the standard packages in. */
	    FileCmds.load(interp);
	    Base64Cmd.load(interp);
	    HttpCmd.load(interp);
	    LoadCmd.load(interp);
	    HeclJavaCmd.load(interp);
	    NullCmd.load(interp);
	    Vector argv = new Vector();

            for (i = 0; i < args.length; i++) {
                //System.out.println("(running " + args[i] + ")");
		argv.addElement(new Thing(args[i]));
            }
	    interp.setVar("argv", ListThing.create(argv));
	    extend(interp);
	    if(args.length == 1) {
		HeclFile.sourceFile(interp, args[0]);
	    } else {
		interp.readEvalPrint(System.in,System.out,System.err);
	    }
        } catch (Exception e) {
	    System.err.println("Java exception: " + e);
            e.printStackTrace();
        } catch (Throwable t) {
	    System.err.println("Java error: " + t);
	    t.printStackTrace();
	}
	interp.terminate();
	System.exit(0);
    }

    /*
    static class ObjectCmd implements Command {
	ObjectCmd() {}
	public Thing cmdCode(Interp ip,Thing[] argv) throws HeclException {
	    System.err.println("ObjectCmd:");
	    for(int i=0; i<argv.length; ++i) {
		System.err.println("\targv["+i+"]="+argv[i].toString());
	    }
	    return ObjectThing.create(this);
	}
    }
    
    static class ClCmd implements ClassCommand {
	static int cnt = 0;
	public ClCmd() {}
	
	public Thing method(Interp ip,ClassCommandInfo context,
			    Thing[] argv) throws HeclException {
	    System.err.println("method, this="+argv[0].toString()
			       +", name="+argv[1].toString());
	    for(int i=2; i<argv.length; ++i) {
		System.err.println("\targv["+i+"]="+argv[i].toString());
	    }
	    return IntThing.create(--cnt);
	}
    }
    */

    public static void extend(Interp ip) throws HeclException {
	/*
	ip.addCommand("jones",new ObjectCmd());
	ip.addClassCmd(ObjectCmd.class,new ClCmd());
	*/
    }
}

