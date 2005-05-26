import java.util.Vector;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import org.hecl.Eval;
import org.hecl.Interp;
import org.hecl.Thing;
import org.hecl.ListThing;
import org.hecl.HeclException;

/*
 * Created on 2005-03-04
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author zoro
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Hecl {
    public static void main(String[] args) {
        try {
            int i;
            Interp interp = new Interp();
            Eval eval = new Eval();
	    Vector argv = new Vector();

            for (i = 0; i < args.length; i++) {
                //System.out.println("(running " + args[i] + ")");
		argv.addElement(new Thing(args[i]));
            }
	    interp.setVar("argv", ListThing.create(argv));
	    if (args.length > 0) {
		Eval.eval(interp, interp.getResAsThing(args[0]));
	    } else {
		Hecl.commandLine(interp);
	    }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
		if (interp.result != null) {
		    System.out.println(interp.result);
		}
	    } catch (HeclException he) {
		System.out.println(he);
	    }
	}
    }
}

