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
 * <code>IfCmd</code> implements the "if" command.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */
class IfCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {

        interp.eval(argv[1]);
        Thing result = interp.result;

        if (Thing.isTrue(result)) {
            interp.eval(argv[2]);
            return;
        }

        /*
         * We loop through to capture all else if...else if...else
         * possibilities.
         */
        if (argv.length > 3) {
            for (int i = 3; i <= argv.length; i += 3) {
                if (argv[i].getStringRep().equals("else")) {
                    /* It's an else block, evaluate it and return. */
                    interp.eval(argv[i + 1]);
                    return;
                } else if (argv[i].getStringRep().equals("elseif")) {
                    /*
                     * elseif - check and see if the condition is true, if so
                     * evaluate it and return.
                     */
                    interp.eval(argv[i + 1]);
                    result = interp.result;
                    if (Thing.isTrue(result)) {
                        interp.eval(argv[i + 2]);
                        return;
                    }
                }
            }
        }
    }
}
