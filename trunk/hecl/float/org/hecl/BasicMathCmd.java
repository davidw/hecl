/* Copyright 2004-2005 David N. Welton

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

package org.hecl;

/**
 * <code>BasicMathCmd</code> implements the basic math commands, +, -, *, and /.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class BasicMathCmd implements Command {

    /**
     * The <code>argPromotion</code> method fills in an array of
     * doubles with the values of their arguments.
     *
     * @param argv a <code>Thing[]</code> value
     * @param dargv a <code>double[]</code> value
     * @return a <code>boolean</code> value
     * @exception HeclException if an error occurs
     */
    private void argPromotion(Thing[] argv, double [] dargv)
	throws HeclException {

	boolean promote = false;
	for (int i = 1; i < argv.length; i++ ) {
	    dargv[i-1] = DoubleThing.promote(argv[i]);
	}
    }


    /**
     * The <code>cmdCode</code> method implements the basic math
     * commands present in Hecl, +, -, * and / - this is the floating
     * point version.
     *
     * @param interp an <code>Interp</code> value
     * @param argv a <code>Thing[]</code> value
     * @exception HeclException if an error occurs
     */
    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
        char cmd = (argv[0].getStringRep()).charAt(0);
	double res = 0;
	double[] dargv = new double[argv.length - 1];

	argPromotion(argv, dargv);
        switch (cmd) {
            case '+' :
		res = 0;
		for (int i = 0; i < dargv.length; i ++) {
		    res += dargv[i];
		}
                break;
            case '-' :
		res = dargv[0] - dargv[1];
                break;
            case '*' :
		res = dargv[0] * dargv[1];
                break;
            case '/' :
		res = dargv[0] / dargv[1];
                break;
        }

	/* If it's been promoted, return it as a double, otherwise
	 * turn it back into an integer. */
	if (res != Math.rint(res)) {
	    interp.setResult(DoubleThing.create(res));
	} else {
	    interp.setResult(IntThing.create((int)res));
	}
    }
}
