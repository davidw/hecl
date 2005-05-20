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

import java.util.Vector;

/**
 * The <code>Parse</code> class takes care of parsing Hecl scripts.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

public class Parse {
    protected Vector outList;

    protected ParseState state;

    protected Interp interp = null;

    protected String in;

    protected Thing currentOut;

    protected boolean parselist = false;

    /**
     * The <code>more</code> method returns a boolean value indicating whether
     * there is more text to be parsed or not.
     * 
     * @return a <code>boolean</code> value
     */
    public boolean more() {
        return !state.eof;
    }

    /**
     * Creates a new <code>Parse</code> instance. Not actually used by
     * anything.
     *  
     */
    public Parse() {
    }

    /**
     * Creates a new <code>Parse</code> instance.
     * 
     * @param interp_in
     *            a <code>Interp</code> value
     * @param in_in
     *            a <code>String</code> value
     */
    public Parse(Interp interp_in, String in_in) {
        interp = interp_in;
        in = in_in;
        state = new ParseState(in);
    }

    /**
     * The <code>parse</code> method runs the parser on the text added by
     * creating a new Parse instance.
     * 
     * @return a <code>Vector</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public Vector parse() throws HeclException {
        outList = new Vector();
        currentOut = new Thing("");
        state.eoc = false;

        parseLine(in, state);
        if (outList.size() > 0) {
            //System.out.println("outlist is : " + outList);
            return outList;
        }
        return null;
    }

    public CodeThing parseToCode() throws HeclException {
        CodeThing code = new CodeThing();
        Command command = null;
        String cmdName = null;
        int i = 0;

        while (more()) {
            cmdName = null;
            Vector cmd = new Vector();
            cmd = parse();
            //System.out.println("CMD is " + cmd);

            if (cmd == null || cmd.size() == 0) {
                continue;
            }

            Thing[] argv = new Thing[cmd.size()];
            for (i = 0; i < cmd.size(); i++) {
                argv[i] = (Thing) cmd.elementAt(i);
            }
            cmdName = ((Thing) cmd.elementAt(0)).getStringRep();

            // System.out.println("CMD is " + cmdName);
            // System.out.println("ARGS ARE " + Arrays.asList(argv));

            command = interp.getCommand(cmdName);
            code.addStanza(command, argv);
            // command.cmdCode(interp, argv);
        }
        return code;
    }

    /**
     * The <code>addCurrent</code> method adds a new element to the command
     * parsed.
     *  
     */
    protected void addCurrent() {
        outList.addElement(currentOut);
        currentOut = new Thing("");
    }

    /**
     * The <code>appendToCurrent</code> method adds a character to the group
     * object.
     * 
     * @param ch
     *            a <code>char</code>
     */

    /* FIXME - this could be reworked. */

    protected void appendToCurrent(char ch) throws HeclException {
        currentOut.appendToGroup(ch);
    }

    /**
     * The <code>addCurrent</code> method adds a new Thing to the out list,
     * and sets the current output collector to an empty Thing.
     * 
     * @param newthing
     *            a <code>Thing</code> value
     */
    protected void addCurrent(Thing newthing) {
        outList.addElement(newthing);
        currentOut = new Thing("");
    }

    /**
     * The <code>addCommand</code> method adds a command to the current
     * output.
     * 
     * @exception HeclException
     *                if an error occurs
     */
    protected void addCommand() throws HeclException {
        Thing saveout = currentOut;
        currentOut = new Thing("");
        parseCommand(state);
        saveout.appendToGroup(currentOut);
        saveout.appendToGroup(new Thing(""));
        currentOut = saveout;
    }

    /**
     * The <code>addDollar</code> method adds a $var lookup to the current
     * output.
     * 
     * @param docopy
     *            a <code>boolean</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public void addDollar(boolean docopy) throws HeclException {
        Thing saveout = currentOut;
        currentOut = new Thing("");
        parseDollar(state, docopy);
        saveout.appendToGroup(currentOut);
        saveout.appendToGroup(new Thing(""));
        currentOut = saveout;
    }

    /**
     * The <code>parseLine</code> method is where parsing starts on a new
     * line.
     * 
     * @param in
     *            a <code>String</code> value
     * @param state
     *            a <code>ParseState</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public void parseLine(String in, ParseState state) throws HeclException {
        char ch;

        while (true) {
            ch = state.nextchar();
            if (state.done()) {
                return;
            }
            switch (ch) {
                case '{' :
                    parseBlock(state);
                    addCurrent();
                    break;
                case '[' :
                    parseCommand(state);
                    addCurrent();
                    break;
                case '$' :
                    parseDollar(state, true);
                    addCurrent();
                    break;
                case '&' :
                    parseDollar(state, false);
                    addCurrent();
                    break;
                case '"' :
                    parseText(state);
                    addCurrent();
                    break;
                case ' ' :
                    break;
                case '	' :
                    break;
                case '#' :
                    parseComment(state);
                    return;
                case '\r' :
                    return;
                case '\n' :
                    return;
                case ';' :
                    return;
                default :
                    appendToCurrent(ch);
                    //		    state.rewind();
                    parseWord(state);
                    addCurrent();
                    break;
            }
        }
    }

    /**
     * The <code>parseComment</code> method keeps reading until a newline,
     * this 'eating' the comment.
     * 
     * @param state
     *            a <code>ParseState</code> value
     */
    private void parseComment(ParseState state) {
        char ch;
        while (true) {
            ch = state.nextchar();
            if (((ch == '\n') || (ch == '\r')) || state.done()) {
                return;
            }
        }
    }

    /**
     * The <code>parseDollar</code> method parses a $foo variable. These can
     * also be of the form ${foo} so we deal with that case too.
     * 
     * @param state
     *            a <code>ParseState</code> value
     * @param docopy
     *            a <code>boolean</code> value
     * @exception HeclException
     *                if an error occurs
     */
    private void parseDollar(ParseState state, boolean docopy)
            throws HeclException {
        char ch;
        ch = state.nextchar();
        if (ch == '{') {
            parseVarBlock(state);
        } else {
            /* Variable names use this range here. */
            while ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')
                    || ch == '_') {
                appendToCurrent(ch);
                ch = state.nextchar();
            }
            if (!state.done()) {
                state.rewind();
            }
        }
        /*
         * System.out.println("parser vvvv"); Thing.printThing(argv[1]);
         * System.out.println("parser ^^^^");
         */
        Thing strcopy = currentOut.deepcopy();
        StringThing.get(strcopy);
        currentOut.setVal(new SubstThing(strcopy.getStringRep(), !docopy));
    }

    /**
     * <code>parseBlock</code> parses a {} block.
     * 
     * @param state
     *            a <code>ParseState</code> value
     * @exception HeclException
     *                if an error occurs
     */
    protected void parseBlock(ParseState state) throws HeclException {
        parseBlockOrCommand(state, true, false);
    }

    protected void parseVarBlock(ParseState state) throws HeclException {
	parseBlockOrCommand(state, true, true);
    }

    /**
     * <code>parseCommand</code> parses a [] command.
     * 
     * @param state
     *            a <code>ParseState</code> value
     * @exception HeclException
     *                if an error occurs
     */
    protected void parseCommand(ParseState state) throws HeclException {
        parseBlockOrCommand(state, false, false);
    }

    /**
     * <code>parseBlockOrCommand</code> is what parseCommand and parseBlock
     * use internally.
     * 
     * @param state
     *            a <code>ParseState</code> value
     * @param block
     *            a <code>boolean</code> value
     * @exception HeclException
     *                if an error occurs
     */
    protected void parseBlockOrCommand(ParseState state, boolean block, boolean invar)
            throws HeclException {
        int level = 1;
        char ldelim, rdelim;
        char ch;

        if (block == true) {
            ldelim = '{';
            rdelim = '}';
        } else {
            ldelim = '[';
            rdelim = ']';
        }

        while (true) {
            ch = state.nextchar();
            if (state.done()) {
                return;
            }
            if (ch == ldelim) {
                level++;
            } else if (ch == rdelim) {
                level--;
            }
            if (level == 0) {
                /* It's just a block, return it. */
                if (block || parselist) {
		    ch = state.nextchar();
		    /* If we are not dealing with a variable parse
		     * such as ${foo}, and the next character
		     * isn't a space, we have a problem. */
		    if (!invar && ch != ' ' && ch != '	' &&
			ch != '\n' && ch != '\n' && ch != 0) {
			throw new HeclException("extra characters after close-brace");
		    }
		    state.rewind();
                    //return new Thing(out);
                    return;
                } else {
                    /* We parse it up for later consumption. */
                    Parse hp = new Parse(interp, currentOut.getStringRep());
                    CodeThing code = hp.parseToCode();
                    code.marksubst = true;
                    currentOut.setVal(code);
                    return;
                }
            } else {
                appendToCurrent(ch);
            }
        }
    }

    /**
     * <code>parseText</code> parses a "string in quotes".
     * 
     * @param state
     *            a <code>ParseState</code> value
     * @exception HeclException
     *                if an error occurs
     */
    protected void parseText(ParseState state) throws HeclException {
        char ch;
        while (true) {
            ch = state.nextchar();
            if (state.done()) {
                return;
            }
            switch (ch) {
                case '\\' :
                    ch = state.nextchar();
                    if (state.done()) {
                        return;
                    }
                    appendToCurrent(ch);
                    break;
                case '[' :
                    addCommand();
                    break;
                case '$' :
                    addDollar(true);
                    break;
                case '&' :
                    addDollar(false);
                    break;
                case '"' :
                    return;
                default :
                    appendToCurrent(ch);
                    break;
            }
        }
    }

    /**
     * <code>parseWord</code> parses a regular word not in quotes.
     * 
     * @param state
     *            a <code>ParseState</code> value
     * @exception HeclException
     *                if an error occurs
     */
    protected void parseWord(ParseState state) throws HeclException {
        char ch;
        while (true) {
            ch = state.nextchar();
            if (state.done()) {
                return;
            }
            switch (ch) {
                case '[' :
                    addCommand();
                    break;
                case '$' :
                    addDollar(true);
                    break;
                case '&' :
                    addDollar(false);
                    break;
                /* This isn't special here, we can ignore it? */
                /*
                 * case '"': addCurrent(); parseText(state); appendCurrent();
                 * break;
                 */
                case ' ' :
                    return;
                case '\r' :
                    state.eoc = true;
                    return;
                case '\n' :
                    state.eoc = true;
                    return;
                case ';' :
                    state.eoc = true;
                    return;
                case '\\' :
                    ch = state.nextchar();
                    if (state.done()) {
                        return;
                    }
                    appendToCurrent(ch);
                    break;
                default :
                    appendToCurrent(ch);
                    //		    out.appendString(state.chars[state.idx]);
                    break;
            }
        }
    }
}
