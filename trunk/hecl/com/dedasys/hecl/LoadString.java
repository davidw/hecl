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

package com.dedasys.hecl;

import java.io.*;

/**
 * <code>LoadString</code> implements the Load interface.  It is used
 * to load code that is compiled into the system.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

public class LoadString implements Load {
    String script = null;

    /**
     * Creates a new <code>LoadString</code> instance containing the
     * hecl code in newscript.
     *
     * @param newscript a <code>String</code> value
     */
    public LoadString(String newscript) {
	script = newscript;
    }

    /**
     * <code>getscript</code> returns a string that has been compiled
     * into it.
     *
     * @param resourcename a <code>String</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    public Thing getscript(String resourcename)
    throws HeclException {
	return new Thing(script);
    }
}
