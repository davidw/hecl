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

/**
 * <code>Standard</code> adds commands to standard Hecl.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class Standard {

    /**
     * <code>init</code> is where commands are added to the
     * interpreter.  Change this if you would like to add other
     * commands.
     *
     * @param interp an <code>Interp</code> value
     * @exception HeclException if an error occurs
     */
    public static void init(Interp interp) throws HeclException {
	interp.addCommand("time", new TimeCmd());
    }

}
