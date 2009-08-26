/* Copyright 2004-2006 David N. Welton

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

/* $Id$ */

package org.hecl;

/**
 * The <code>RealThing</code> interface is the actual value contained within a
 * Thing. It can be of several different types - integers, strings, lists,
 * hashes, and so on.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */
public abstract interface RealThing {
    public String thingclass();

    /**
     * The <code>deepcopy</code> method must copy a RealThing and any values
     * it contains.
     *
     * @return a <code>RealThing</code> value
     * @throws HeclException
     */
    public RealThing deepcopy() throws HeclException;

    /**
     * The <code>getStringRep</code> method returns the string representation
     * of a <code>RealThing</code>.
     *
     * @return a <code>String</code> representation of the value
     */
    public String getStringRep();
}
