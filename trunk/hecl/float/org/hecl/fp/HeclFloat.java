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

import org.hecl.*;

/**
 * <code>HeclFloat</code> implements various floating point commands
 * for J2SE Hecl that cannot be present in J2ME.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class HeclFloat implements org.hecl.modules.HeclModule {

    public void loadModule(Interp interp) throws HeclException {
        interp.commands.put("round", new FloatCmd());
    }

    public void unloadModule(Interp interp) throws HeclException {
        interp.commands.remove("round");
    }
}
