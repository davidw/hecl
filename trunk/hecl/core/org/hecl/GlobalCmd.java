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
 * <code>GlobalCmd</code> implements the "global" command.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class GlobalCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
        String varname = argv[1].getStringRep();

        if (!interp.existsVar(varname, 0)) {
            Thing newThing = new Thing("");
            interp.setVar(varname, newThing, 0);
            interp.setVar(argv[1], newThing);
        } else {
            interp.setVar(argv[1], interp.getVar(varname, 0));

        }
    }
}
