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
 * The <code>ParseState</code> class is the state of the current parse.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

public class ParseState {
    public int len;

    public int lineno = 1;

    private char[] chars;

    private int idx;

    public boolean eof;

    public boolean eoc;

    /**
     * Creates a new <code>ParseState</code> instance.
     *
     * @param in a <code>String</code> value
     */
    public ParseState(String in) {
	chars = in.toCharArray();
	len = in.length();
	idx = 0;
	eof = false;
	eoc = false;
    }

    /**
     * <code>nextchar</code> returns the next character, keeping track of
     * end-of-command and end-of-file conditions.
     *
     * @return a <code>char</code> value
     */
    public char nextchar() {
	char result;
	if (eoc) {
	    return (char) 0;
	}
	if (idx >= len) {
	    eoc = true;
	    eof = true;
	    return (char) 0;
	}
	result = chars[idx];
	idx++;
	return result;
    }

    /**
     * The <code>done</code> method returns true if either the
     * end-of-command or end-of-file condition is true.
     *
     * @return a <code>boolean</code> value
     */
    public boolean done() {
	return (eof || eoc) ? true : false;
    }

    /**
     * The <code>remaining</code> method is for debugging purposes, and
     * prints to standard output the remaining text.
     *
     */
    public void remaining() {
	System.out.println("remaining:" + chars[idx]);
    }

    /**
     * The <code>rewind</code> method "rewinds" the input by one
     * character.
     *
     */
    public void rewind() {
	idx--;
    }
}
