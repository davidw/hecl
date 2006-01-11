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

import java.util.Stack;
import java.util.Vector;

/**
 * The <code>HeclException</code> class implements exceptions for Hecl.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

public class HeclException extends Exception {
    int code = 0;

    Stack stack;

    String txt;

    static final int BREAK = 1;

    static final int CONTINUE = 2;

    static final int RETURN = 3;

    static final int ERROR = 4;

    /**
     * Creates a new <code>HeclException</code> instance.
     * 
     * @param s
     *            a <code>String</code> value
     */

    public HeclException(String s) {
        super(s);
        txt = s;
        code = ERROR;
        pushException();
    }

    /**
     * Creates a new <code>HeclException</code> instance.
     * 
     * @param s
     *            a <code>String</code> value describing the error.
     * @param exception_code
     *            an <code>int</code> value
     */

    public HeclException(String s, int exception_code) {
        code = exception_code;
        txt = s;
        pushException();
    }

    /**
     * Creates a new <code>HeclException</code> instance.
     * 
     * @param exception_code
     *            an <code>int</code> value
     */

    public HeclException(int exception_code) {
        code = exception_code;
        txt = "???";
        pushException();
    }

    /**
     * <code>pushException</code> adds to the exception stack.
     *  
     */
    private void pushException() {
        stack = new Stack();
        Vector lst = new Vector();
        lst.addElement(new Thing(codeToString()));
        lst.addElement(new Thing(txt));

        stack.push(new Thing(new ListThing(lst)));
    }

    /**
     * The <code>where</code> method tells the exception what command it
     * occurred in.
     * 
     * @param cmd
     *            a <code>String</code> containing the command name.
     */
    public void where(String cmd) {
        stack.push(new Thing(cmd));
    }

    /**
     * The <code>codeToString</code> method returns a string that describes
     * the error code.
     * 
     * @return a <code>String</code> value
     */
    public String codeToString() {
        switch (code) {
            case BREAK :
                return "BREAK";
            case CONTINUE :
                return "CONTINUE";
            case RETURN :
                return "RETURN";
            case ERROR :
                return "ERROR";
            default :
                return "BOGUS ERROR!";
        }
    }

    /**
     * The <code>toString</code> method turns the exception stack into a
     * string.
     * 
     * @return a <code>String</code> value
     */

    public String toString() {
        return getStack().toString();
    }

    /**
     * The <code>getStack</code> method returns the exception as a Thing.
     * 
     * @return a <code>Thing</code> value
     */

    public Thing getStack() {
        return ListThing.create((Vector) stack);
    }

    public static HeclException createWrongNumArgsException(Thing argv[],
            int count, String message) throws HeclException {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < count && i < argv.length; i++) {
            str.append(argv[i].getStringRep());
            str.append(" ");
        }
        return new HeclException("wrong # args: should be \"" + str + message + "\"");
    }
    /**
     * 
     * @param param
     *            <code>Thing</code> specifying the actual parameter.
     * @param type
     *            String saying the type - ie <i>option </i>, <i>command </i>.
     * @param options
     *            A comma-separated list of options that can be supplied.
     * @return a new HeclException
     * @throws HeclException
     */
    public static HeclException createInvalidParameter(Thing param,
            String type, String options) throws HeclException {
        return new HeclException("invalid " + type + " specified \""
                + param.getStringRep() + "\"; should be: " + options + ".");
    }
}
