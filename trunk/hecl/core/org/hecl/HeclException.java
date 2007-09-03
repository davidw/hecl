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

import java.util.Stack;
import java.util.Vector;

/**
 * The <code>HeclException</code> class implements exceptions for Hecl.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

public class HeclException extends Exception {
    public String code = null;

    Stack stack;
    Thing value = null;
    Thing message = null;

    static final String BREAK = "BREAK";

    static final String CONTINUE = "CONT";

    static final String RETURN = "RETURN";

    static final String ERROR = "ERROR";

    /**
     * Creates a new <code>HeclException</code> instance.
     *
     * @param s a <code>String</code> value
     */

    public HeclException(String s) {
        this(s,ERROR,null);
    }

    public HeclException(String s,int lineno) {
	this(s,ERROR,null);
	try {setLine(lineno);}
	catch(HeclException ignore){}
    }
    
    /**
     * Creates a new <code>HeclException</code> instance.
     *
     * @param s a <code>String</code> value describing the error.
     * @param exception_code an <code>int</code> value
     */
    public HeclException(String s, String exception_code) {
	this(s,exception_code,null);
    }

    HeclException(String s, String exception_code, Thing value) {
	super(s);
	this.message = new Thing(s);
	this.value = value;
        this.code = exception_code;
        pushException(s);
    }

    /**
     * <code>pushException</code> adds to the exception stack.
     */
    private void pushException(String s) {
        stack = new Stack();
        Vector lst = new Vector();
        lst.addElement(new Thing(code));
        lst.addElement(message);
        stack.push(new Thing(new ListThing(lst)));
    }

    /**
     * The <code>where</code> method tells the exception what command it
     * occurred in.
     *
     * @param cmd
     *            a <code>String</code> containing the command name.
     */
    public void where(String cmd, int lineno) {
        Vector lst = new Vector();
        lst.addElement(new Thing(cmd));
        lst.addElement(IntThing.create(lineno));
        stack.push(new Thing(new ListThing(lst)));
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
            str.append(argv[i].toString());
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
                + param.toString() + "\"; should be: " + options + ".");
    }


    /**
     * The <code>setLine</code> method sets the line number of an
     * error.  FIXME - this could probably be done in a cleaner way...
     *
     * @param lineno an <code>int</code> value
     * @exception HeclException if an error occurs
     */
    public void setLine(int lineno) throws HeclException {
	Vector ex = (Vector)stack;
	Vector err;
	err = ListThing.get((Thing)ex.elementAt(0));
	Thing l = IntThing.create(lineno);
	if (err.size() == 2) {
	    err.addElement(l);
	} else {
	    err.setElementAt(l, 2);
	}
    }
}
