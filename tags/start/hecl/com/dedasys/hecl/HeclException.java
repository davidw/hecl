package com.dedasys.hecl;

import java.lang.*;
import java.util.*;

/**
 * The <code>HeclException</code> class implements exceptions for
 * Hecl.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
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
     * @param s a <code>String</code> value
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
     * @param s a <code>String</code> value describing the error.
     * @param exception_code an <code>int</code> value
     */

    public HeclException(String s, int exception_code) {
	code = exception_code;
	txt = s;
	pushException();
    }

    /**
     * Creates a new <code>HeclException</code> instance.
     *
     * @param exception_code an <code>int</code> value
     */

    public HeclException(int exception_code) {
	code = exception_code;
	txt = new String("???");
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

	stack.push(new Thing(lst));
    }

    /**
     * The <code>where</code> method tells the exception what command
     * it occurred in.
     *
     * @param cmd a <code>String</code> containing the command name.
     */
    public void where(String cmd) {
	stack.push(new Thing(cmd));
    }


    /**
     * The <code>codeToString</code> method returns a string that
     * describes the error code.
     *
     * @return a <code>String</code> value
     */
    public String codeToString() {
	switch (code) {
	    case BREAK:
		return "BREAK";
	    case CONTINUE:
		return "CONTINUE";
	    case RETURN:
		return "RETURN";
	    case ERROR:
		return "ERROR";
	    default:
		return "BOGUS ERROR!";
	}
    }

    /**
     * The <code>toString</code> method turns the exception stack into
     * a string.
     *
     * @return a <code>String</code> value
     */

    public String toString() {
	return getStack().toString();
    }

    /**
     * The <code>getStack</code> method returns the exception as a
     * Thing.
     *
     * @return a <code>Thing</code> value
     */

    public Thing getStack() {
	return new Thing((Vector)stack);
    }
}
