package com.dedasys.hecl;

import java.util.*;
import java.lang.*;

/**
 * <code>Eval</code> takes care of evaluating code.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

public class Eval {

    /**
     * The <code>eval</code> method evaluates some Hecl code passed to
     * it.
     *
     * @param interp an <code>Interp</code>.
     * @param in a <code>Thing</code> value representing the text to
     * evaluate.
     * @return a <code>Thing</code> value - the result of the evaluation.
     * @exception HeclException if an error occurs.
     */

    public Thing eval(Interp interp, Thing in)
	throws HeclException
    {
	if (in.type != Thing.CODE && in.type != Thing.SUBST) {
	    Parse hp = new Parse(interp, in.toString());

	    in.setCode(hp.parseToCode());
	}

	in.getCode().run(interp);
	return interp.getResult();
    }

}
