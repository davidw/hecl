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
 * The <code>ClassCommandInfo</code> holds information about a class command
 * that may be attached to an interpreter.
 * 
 * @author <a href="mailto:wlgang.kechel@data2c.com">Wolfgang S. Kechel </a>
 * @version 1.0
 */

public class ClassCommandInfo {
    /**
     * Constructor to create an instance holding a reference to the specified
     * class and comand.
     *
     * @param clazz The class to generate the instance for.
     * @param cmd The command.
     */
    ClassCommandInfo(Class clazz,ClassCommand cmd) {
	this.clazz = clazz;
	this.cmd = cmd;
    }
    
    /**
     * Gets the class of instances this command operates on.
     *
     * @return The <code>Class<code> on which methods on references in
     * <code>ObjectThing</code>s must be assignable for the command to be
     * called during evaluation.
     */
    public Class forClass() {return this.clazz;}

    /**
     * Gets the <code>ClassCommand</code> to be called for methodes on
     * instances. This function is used by the interpreter to call the Java
     * implementation of methods on <code>ObjectThing</code>s holding a
     * reference to instances of the class stored in this descriptor.
     *
     * @return The <code>ClassCommand</code>.
     */
    public ClassCommand getCommand() {return this.cmd;}

    void setCommand(ClassCommand cmd) {this.cmd = cmd;}
    
    
    private Class clazz;
    private ClassCommand cmd;
}
