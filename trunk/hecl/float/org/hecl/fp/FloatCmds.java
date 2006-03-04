/* Copyright 2005 David N. Welton

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

package org.hecl.fp;

import org.hecl.DoubleThing;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Thing;


/**
 * <code>FloatCmds</code> implements a variety of commands for the
 * floating point system.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class FloatCmds {

    public static final int ADD = 1;
    public static final int SUB = 2;
    public static final int MUL = 3;
    public static final int DIV = 4;
    public static final int MOD = 5;

    public static final int EQ =  9;
    public static final int NE =  10;
    public static final int GT =  11;
    public static final int LT =  12;

    public static final int ROUND = 13;


    static void dispatch(int cmd, Interp interp, Thing[] argv) throws HeclException {
	double res = 0;
	double[] dargv = new double[argv.length - 1];

	DoubleThing.argPromotion(argv, dargv);

	switch (cmd) {

            case ADD:
		res = 0;
		for (int i = 0; i < dargv.length; i ++) {
		    res += dargv[i];
		}
                break;
            case SUB:
		if (dargv.length == 1) {
		    res = -dargv[0];
		} else {
		    res = dargv[0];
		    for (int i = 1; i < dargv.length; i ++) {
			res -= dargv[i];
		    }
		}
                break;
            case MUL:
		res = 1;
		for (int i = 0; i < dargv.length; i ++) {
		    res *= dargv[i];
		}
                break;

            case DIV:
		if(dargv.length < 2) {
		    throw HeclException.createWrongNumArgsException(
			argv, 3, "/ arg arg ?args ...?");
		}
		res = dargv[0];
		for (int i = 1; i < dargv.length; i ++) {
		    res /= dargv[i];
		}
                break;

	    case MOD:
		if(dargv.length != 2) {
		    throw HeclException.createWrongNumArgsException(
			argv, 3, "% needs exactly 2 arguments");
		}
		res = dargv[0] % dargv[1];
		break;


	    case EQ:
	    case GT:
	    case NE:
	    case LT:
		boolean result = false;
		double l = 0;
		double r = 0;
		l = DoubleThing.promote(argv[1]);
		r = DoubleThing.promote(argv[2]);
		switch (cmd) {
		    case EQ:
			result = l == r;
			break;
		    case GT:
			result = l > r;
			break;
		    case LT:
			result = l < r;
			break;
		    case NE:
			result = l != r;
			break;
		}
		interp.setResult(result);
		return;

	    case ROUND:
		interp.setResult((int)Math.rint(DoubleThing.get(argv[1])));
		return;

	}

	/* If it's been promoted, return it as a double, otherwise
	 * turn it back into an integer. */
	if (res != Math.rint(res)) {
	    interp.setResult(DoubleThing.create(res));
	} else {
	    interp.setResult((int)res);
	}
    }
}
