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
 * The <code>Load</code> interface is the template for loading external code
 * resources (typically files).
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

public abstract interface ResHandle {
    /**
     * Queries if the module will handle reading/writing the resource.
     * @param resourcename 
     * @return
     */
    public boolean handleRes(String resourcename);
    /**
     * Gets the data by this resource.
     * 
     * @param resourcename
     * @return a <code>Thing</code>
     * @throws HeclException
     */
    public Thing getRes(String resourcename) throws HeclException;
    /**
     * Returns priority at which the current <code>ResourceGetter</code>
     * should be registered.
     * 
     * @return priority at which the current <code>ResourceGetter</code>
     *         should be registered
     */
    public int getPriority();
}