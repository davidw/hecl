/* Copyright 2005 Wojciech Kocjan

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

import org.hecl.Interp;
import org.hecl.HeclException;

/**
 * The <code>HeclModule</code> interface describes what modules
 * implement in order to be loaded into Hecl.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public interface HeclModule {

    /**
     * The <code>loadModule</code> method usually takes care of
     * creating commands that are present in this module.
     *
     * @param interp an <code>Interp</code> value
     * @exception HeclException if an error occurs
     */
    public void loadModule(Interp interp) throws HeclException;

    /**
     * The <code>unloadModule</code> method takes care of any clean up
     * that's necessary, such as unloading commands created by this
     * module.
     *
     * @param interp an <code>Interp</code> value
     * @exception HeclException if an error occurs
     */
    public void unloadModule(Interp interp) throws HeclException;
}
