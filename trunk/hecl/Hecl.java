import com.dedasys.hecl.*;

/**
 *  <code>Hecl</code> is the main class.  Borrow the code from here if
 *  you want to embed  in your own system.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

public class Hecl {

    /**
     * This is the <code>main</code> method, an example of how to
     * integrate Hecl into your own programs.
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(String [] args) {
	try {
	    Interp interp = new Interp();
	    Eval eval = new Eval();
	    eval.eval(interp, interp.getscript(args[0]));
	} catch (Exception e) {
	    System.err.println(e);
	}
    }

}

