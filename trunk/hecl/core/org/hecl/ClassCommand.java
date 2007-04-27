/* Copyright 2007 Wolfgang S. Kechel

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
 * The <code>ClassCommand</code> interface is the template for all commands
 * implemented in Java working on Hecl <code>ObjectThing</code>.
 * 
 * @author <a href="mailto:wlgang.kechel@data2c.com">Wolfgang S. Kechel </a>
 * @version 1.0
 */

public interface ClassCommand {

    /**
     * The <code>method</code> method takes an interpreter, a class
     * information and an array of Things, performs some calculations, and
     * returns a <code>Thing</code> which may be <code>null</code>. The
     * interpreter calls this method when it detects an
     * <code>ObjectThing</code> as first argument of a command on the script
     * level and detects a command handler for the class of the value of the
     * <code>ObjectThing</code>.
     * 
     * @param interp
     *            an <code>Interp</code> value
     * @param context A reference to the <code>ClassInfo></code> describing
     * further details of th emethod.
     * @param argv A <code>Thing[]</code> array holding the parameters for the
     * method. The 0th element is the object, the 1st element is the method.
     *
     * @exception HeclException
     *                if an error occurs
     */
    public Thing method(Interp interp, ClassCommandInfo context, Thing[] argv)
	throws HeclException;
}
