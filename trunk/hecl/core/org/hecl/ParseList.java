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
 * <code>ParseList</code> parses up Hecl lists.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */
public class ParseList extends Parse {

    /**
     * Creates a new <code>ParseList</code> instance.
     *
     * @param in_in a <code>String</code> value
     */
    public ParseList(String in_in) {
        in = in_in;
        state = new ParseState(in);
        parselist = true;
    }

    /**
     * <code>parseLine</code> parses a line of Hecl code.
     *
     * @param state a <code>ParseState</code> value
     * @exception HeclException if an error occurs
     */
    public void parseLine(ParseState state) throws HeclException {
        char ch;

        while (true) {
            ch = state.nextchar();
            if (state.done()) {
                return;
            }
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                continue;
            }
            switch (ch) {
		/*
		    // new - used to ignore '[' and '\' in list parsing
                case '[' :
                    parseCommand(state);
                    addCurrent();
                    break;
		case '\\':
		    if (!parseEscape(state)) {
			parseWord(state);
			addCurrent();
		    }
		    break;
		    // end new
		 */
                case '{' :
                    parseBlock(state);
                    addCurrent();
                    break;
                 case '"' :
                    parseText(state);
                    addCurrent();
                    break;
                default :
                    appendToCurrent(ch);
                    parseWord(state);
                    addCurrent();
                    break;
            }
        }
    }

    /**
     * <code>parseText</code> parses some text, such as that enclosed in
     * quotes "".
     *
     * @param state a <code>ParseState</code> value
     * @exception HeclException if an error occurs
     */
    public void parseText(ParseState state) throws HeclException {
        char ch;
        while (true) {
            ch = state.nextchar();
            if (state.done()) {
		// was: 
		// return;
		throw new HeclException("Unbalanced open quote in list",
					"PARSE_ERROR");
            }
            switch (ch) {
	      case '\\' :
		parseEscape(state);
		break;
	      case '"' :
		return;
	      default :
		appendToCurrent(ch);
		break;
            }
	    // new
	    // end new
	    /* was:
	    if (ch == '"') {
		return;
	    } else {
		appendToCurrent(ch);
            }
	    */
        }
    }

    /**
     * <code>parseWord</code> parses a plain Hecl word.
     *
     * @param state a <code>ParseState</code> value
     * @exception HeclException if an error occurs
     */
    public void parseWord(ParseState state) throws HeclException {
        char ch;
        while (true) {
            ch = state.nextchar();
            if (state.done()) {
		return;
            }
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                return;
            }
	    appendToCurrent(ch);
        }
    }
}
