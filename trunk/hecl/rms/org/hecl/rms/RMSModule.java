/* Copyright 2005 David N. Welton <davidw@dedasys.com>

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

package org.hecl.rms;

import org.hecl.HeclException;
import org.hecl.Interp;

import org.hecl.modules.HeclModule;

/**
 * The <code>RMSModule</code> class takes care of setup and tear-down
 * of the resources needed for dealing with record stores.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class RMSModule implements HeclModule {

    public void loadModule(Interp interp) throws HeclException {
	HeclRecordStoreCmd hrs = new HeclRecordStoreCmd();
        interp.commands.put("rs_list", hrs);
        interp.commands.put("rs_get", hrs);
        interp.commands.put("rs_put", hrs);
    }

    public void unloadModule(Interp interp) throws HeclException {
        interp.commands.remove("rs_list");
        interp.commands.remove("rs_get");
        interp.commands.remove("rs_put");
    }
}
