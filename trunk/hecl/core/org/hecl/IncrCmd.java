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
 * <code>IncrCmd</code> implements the "incr" command.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class IncrCmd implements Command {
    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
        int m = IntThing.get(argv[1]);
        int n = 1;
        if (argv.length > 2) {
            n = IntThing.get(argv[2]);
        }
        ((IntThing) argv[1].val).set(m + n);
        interp.result = argv[1];
    }
}