import org.hecl.Eval;
import org.hecl.Interp;
import org.hecl.StringThing;
import org.hecl.Thing;

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
public class StandaloneHecl {

    private static String script = "for {set i 0} {< $i 10} {incr &i} { puts $i }";

    /**
     * This is the <code>main</code> method, an example of how to integrate
     * Hecl into your own programs.
     * 
     * @param args
     *            a <code>String[]</code> value
     */
    public static void main(String[] args) {
        try {
            Interp interp = new Interp();
            Eval eval = new Eval();
            Eval.eval(interp, new Thing(new StringThing(script)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

