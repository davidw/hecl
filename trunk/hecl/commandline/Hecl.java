import org.hecl.Eval;
import org.hecl.Interp;

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

            for (i = 0; i < args.length; i++) {
                System.out.println("(running " + args[i] + ")");
                Eval.eval(interp, interp.getResAsThing(args[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}