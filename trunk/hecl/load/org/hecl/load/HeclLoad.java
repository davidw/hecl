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


package org.hecl.load;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.Thing;


/**
 * The <code>HeclLoad</code> class implements a module that can load
 * external code dynamically.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class HeclLoad implements org.hecl.modules.HeclModule {

    /**
     * The <code>loadModule</code> method initializes the 'load'
     * command.
     *
     * @param interp an <code>Interp</code> value
     * @exception HeclException if an error occurs
     */
    public void loadModule(Interp interp) throws HeclException {
	LoadCmd lc = new LoadCmd();
	interp.commands.put("load", lc);
    }

    /**
     * The <code>unloadModule</code> method unloads the module.
     *
     * @param interp an <code>Interp</code> value
     * @exception HeclException if an error occurs
     */
    public void unloadModule(Interp interp) throws HeclException {
        interp.commands.remove("load");
    }
}
