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
 * <code>SourceHereCmd</code> implements the "sourcehere" command, which
 * executes the code in a given external resource.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class SourceHereCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
        String filename = interp.getCurrentScriptName();
        String newfl;
        int i, j;

        i = -1;
        j = filename.lastIndexOf('/');
        if ((j >= 0) && (j > i))
            i = j;
        j = filename.lastIndexOf('\\');
        if ((j >= 0) && (j > i))
            i = j;
        if (i >= 0)
            filename = filename.substring(0, i + 1);
        Eval.eval(interp, interp.getResAsThing(filename
                + argv[1].getStringRep()));
    }
}