package com.dedasys.hecl;

/**
 * <code>ParseList</code> 
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
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
     * Describe <code>parseLine</code> method here.
     *
     * @param in a <code>String</code> value
     * @param state a <code>ParseState</code> value
     * @exception HeclException if an error occurs
     */
    public void parseLine(String in, ParseState state)
	throws HeclException {
	char ch;

	while (true) {
	    ch = state.nextchar();
	    if (state.done()) {
		return;
	    }
	    if (Character.isWhitespace(ch)) {
		continue;
	    }
	    switch (ch) {
		case '{':
		    parseBlock(state);
		    addCurrent();
		    break;
		case '[':
		    parseCommand(state);
		    addCurrent();
		    break;
		case '"':
		    parseText(state);
		    addCurrent();
		    break;
		default:
		    appendToCurrent(ch);
		    parseWord(state);
		    addCurrent();
		    break;
	    }
	}
    }

    /**
     * Describe <code>parseText</code> method here.
     *
     * @param state a <code>ParseState</code> value
     * @exception HeclException if an error occurs
     */
    public void parseText(ParseState state)
	throws HeclException {
	char ch;
	while (true) {
	    ch = state.nextchar();
	    if (state.done()) {
		return;
	    }
	    switch (ch) {
		case '\\':
		    ch = state.nextchar();
		    if (state.done()) {
			return;
		    }
		    appendToCurrent(ch);
		    break;
		case '"':
		    return;
		default:
		    appendToCurrent(ch);
		    break;
	    }
	}
    }


    /**
     * Describe <code>parseWord</code> method here.
     *
     * @param state a <code>ParseState</code> value
     * @exception HeclException if an error occurs
     */
    public void parseWord(ParseState state)
	throws HeclException {
	char ch;
	while (true) {
	    ch = state.nextchar();
	    if (state.done()) {
		return;
	    }
	    if (Character.isWhitespace(ch)) {
		return;
	    }
	    switch(ch) {
		case '[':
		    addCommand();
		    break;
		case '$':
		    addDollar(true);
		    break;
		case '&':
		    addDollar(false);
		    break;
		case '"':
		    addCurrent();
		    parseText(state);
		    appendCurrent();
		    break;
		case '\\':
		    ch = state.nextchar();
		    if (state.done()) {
			return;
		    }
		    appendToCurrent(ch);
		    break;
		default:
		    appendToCurrent(ch);
		    break;
	    }
	}
    }

}
