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

package org.hecl;

/**
 * The <code>Command</code> interface is the template for all commands
 * implemented in Hecl.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

/* This 'abstract' apparently needs to be here for j2me stuff. */

public abstract interface Command {

    /**
     * The <code>cmdCode</code> method takes an interpreter and an array of
     * Things, performs some calculations, and returns a <code>Thing</code>
     * representing the computed value, or <code>null</code>.
     * 
     * @param interp
     *            an <code>Interp</code> value
     * @param argv
     *            a <code>Thing[]</code> value
     * @return A <code>Thing</code> representing the computed value, or
     * <code>null</code>.
     * @exception HeclException
     *                if an error occurs
     */

    //void cmdCode(Interp interp, Thing[] argv) throws HeclException;
    Thing cmdCode(Interp interp, Thing[] argv) throws HeclException;
}
