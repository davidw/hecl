/* Copyright 2005 David N. Welton

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

import javax.microedition.lcdui.*;

import java.util.Hashtable;
import java.util.Vector;

import org.hecl.Eval;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.Thing;


/**
 * <code>GUICmds</code> implements the high level lcdui commands.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class GUICmds implements org.hecl.Command, CommandListener {
    public Display display;
    public Interp interp;

    /* These are callbacks used to implement Commands. */
    private static Hashtable callbacks = new Hashtable();
    private static Hashtable widgets = new Hashtable();
    /* This is a temporary hash that is used for configuring/creating
     * widgets. */
    private static Hashtable properties;
    private static int uniqueid = 0;

    private static Screen screen;
    private static Item item;

    /* no offset */
    static final int TEXTFIELD = 0;
    static final int STRINGITEM = 1;
    static final int COMMAND = 2;
    static final int TEXTBOX = 3;

    /* These are the parameter types that describe different GUI elements. */
    static final int TEXT = 0;		/* Element's text */
    static final int LABEL = 1 << 8;	/* Label or title */
    static final int CODE = 2 << 8;	/* Code to run */
    static final int LEN = 3 << 8;	/* Maximum length, or fetch length. */
    static final int TYPE = 4 << 8;	/* Type or constraints of thing */

    /* get or set? */
    static final int GETPROP = 0x1000000;
    static final int SETPROP = 0x0000000;


    /**
     * The <code>setProps</code> method takes the list of properties
     * passed to a command and sets them.
     *
     * @param interp an <code>Interp</code> value
     * @param widgetid an <code>int</code> value
     * @param argv a <code>Thing[]</code> value
     * @param offset an <code>int</code> value
     * @exception HeclException if an error occurs
     */

    private void setProps(Interp interp, int widgetid, Thing[] argv, int offset)
	throws HeclException {
	setProps(interp, Integer.toString(widgetid), argv, offset);
    }

    private void setProps(Interp interp, String widgetid, Thing[] argv, int offset)
	throws HeclException {

	for(int i = offset; i < argv.length; i +=2) {
	    widgetGetSet(interp, widgetid, argv[i].toString(), argv[i+1], SETPROP);
	}
    }


    /**
     * The <code>stdLabel</code> method adds a standard default label.
     *
     * @param type a <code>String</code> value
     */
    private void stdLabel(String type) {
	properties.put("label", new Thing(type + uniqueid)); /* default  */
    }


    /**
     * The <code>cmdCode</code> method implements the commands
     * themselves.
     *
     * @param interp an <code>Interp</code> value
     * @param argv a <code>Thing[]</code> value
     * @exception HeclException if an error occurs
     */
    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {

        String cmdname = argv[0].getStringRep();
	properties = new Hashtable();

	/* Each widget has a unique id. */
	uniqueid ++;

	/* Set the standard widget label. */
	stdLabel(cmdname);

	if (cmdname.equals("form")) {
	    /* The 'form' command.  Creates a form and evaluates its code. */

	    /* In this and other commands, we first set up default
	     * properties in the hash table, so that they are not
	     * empty when the widget is created. */
	    properties.put("code", new Thing("")); /* default  */

	    /* Then we set the properties passed to us on the command line. */
	    setProps(interp, uniqueid, argv, 1);
	    Form f = new Form(((Thing)properties.get("label")).toString());
	    f.setCommandListener(this);
	    /* We make sure that this screen is the default when we
	     * evaluate the code.  This doesn't mean it's displayed,
	     * though. */
	    screen = (Screen)f;
	    Eval.eval(interp, (Thing)properties.get("code"));
	    /* Return the unique ID so that other widgets can find this one. */
	    returnId(interp, f);
	} else if (cmdname.equals("textbox")) {
	    /* The 'textbox' command.  Creates a textbox and evaluates its code. */
	    properties.put("len", IntThing.create(400)); /* default  */
	    properties.put("text", new Thing("")); /* default  */
	    properties.put("code", new Thing("")); /* default  */
	    setProps(interp, uniqueid, argv, 1);
	    TextBox tb = new TextBox(((Thing)properties.get("label")).toString(),
				     ((Thing)properties.get("text")).toString(),
				     IntThing.get((Thing)properties.get("len")), TextField.ANY);
	    tb.setCommandListener(this);
	    screen = (Screen)tb;
	    Eval.eval(interp, (Thing)properties.get("code"));
	    returnId(interp, tb);
	} else if (cmdname.equals("textfield")) {
	    /* The 'textfield' command. */
	    properties.put("text", new Thing("")); /* default  */
	    properties.put("len", IntThing.create(50)); /* default  */
	    setProps(interp, uniqueid, argv, 1);
	    TextField tf = new TextField(((Thing)properties.get("label")).toString(),
					 ((Thing)properties.get("text")).toString(),
					 IntThing.get((Thing)properties.get("len")), 0);
	    ((Form)screen).append(tf);
	    returnId(interp, tf);
	} else if (cmdname.equals("stringitem")) {
	    /* The 'stringitem' command. Differs from a plain string
	     * in that it can be modified, and it has both a label and
	     * text. */
	    setProps(interp, uniqueid, argv, 1);
	    StringItem si = new StringItem(((Thing)properties.get("label")).toString(),
					   ((Thing)properties.get("text")).toString());
	    ((Form)screen).append(si);
	    returnId(interp, si);
	} else if (cmdname.equals("string")) {
	    /* The 'string' command. Plain old string to append to a
	     * form. */
	    ((Form)screen).append(argv[1].toString());
	} else if (cmdname.equals("cmd")) {
	    /* The 'cmd' command.  Adds a command to the current
	     * screen (form, textbox and the like). */
	    properties.put("code", new Thing(""));
	    properties.put("type", new Thing("screen"));
	    setProps(interp, uniqueid, argv, 1);
	    String label = ((Thing)properties.get("label")).toString();
	    Command c = new Command(label, getCmdType(((Thing)properties.get("type")).toString()), 0);
	    screen.addCommand(c);
	    callbacks.put(label, properties.get("code"));
	} else if (cmdname.equals("getprop")) {
	    /* The 'getprop' command. Get a particular property of a
	     * widget. */
	    widgetGetSet(interp, argv[1].toString(), argv[2].toString(), null, GETPROP);
	} else if (cmdname.equals("setprop")) {
	    /* The 'setprop' command. Set a particular property of a
	     * widget. */
	    widgetGetSet(interp, argv[1].toString(), argv[2].toString(), argv[3], SETPROP);
	} else if (cmdname.equals("setcurrent")) {
	    /* The 'setcurrent' command.  Set the current widget to be
	     * the displayed widget.  Used with form, textbox and the
	     * like. */
	    Displayable widget = (Displayable)widgets.get(argv[1].toString());
	    display.setCurrent(widget);
	    screen = (Screen)widget;
	}

	properties = null;
    }

    /**
     * The <code>returnId</code> method stores a reference to the
     * widget in the widgets hash table, and returns the widget's
     * unique id, so that it is accessible later.
     *
     * @param interp an <code>Interp</code> value
     * @param obj an <code>Object</code> value
     */
    private void returnId(Interp interp, Object obj) {
	widgets.put(new Integer(uniqueid).toString(), obj);
	interp.setResult(IntThing.create(uniqueid));
    }

    /**
     * The <code>getWidgetType</code> method returns an integer
     * describing the widget type.
     *
     * @param widget an <code>Object</code> value
     * @return an <code>int</code> value
     */
    private int getWidgetType (Object widget) {
	if (widget instanceof TextField) {
	    return TEXTFIELD;
	} else if (widget instanceof Command) {
	    return COMMAND;
	} else if (widget instanceof TextBox) {
	    return TEXTBOX;
	} else if (widget instanceof StringItem) {
	    return STRINGITEM;
	}
	/* FIXME  */
	return 0;
    }

    /**
     * The <code>getCmdType</code> method returns one of the types
     * that a command may be.
     *
     * @param type a <code>String</code> value
     * @return an <code>int</code> value
     */
    private int getCmdType(String type) {
	if (type.equals("back")) {
	    return Command.BACK;
	} else if (type.equals("cancel")) {
	    return Command.CANCEL;
	} else if (type.equals("exit")) {
	    return Command.EXIT;
	} else if (type.equals("help")) {
	    return Command.HELP;
	} else if (type.equals("item")) {
	    return Command.ITEM;
	} else if (type.equals("ok")) {
	    return Command.OK;
	} else if (type.equals("screen")) {
	    return Command.SCREEN;
	} else if (type.equals("stop")) {
	    return Command.STOP;
	}
	return -1;
    }

    /**
     * The <code>getProp</code> method returns a numeric for the
     * property we are interested in.
     *
     * @param prop a <code>String</code> value
     * @return an <code>int</code> value
     */
    private int getProp(String prop) {
	if (prop.equals("text")) {
	    return TEXT;
	} else if (prop.equals("len")) {
	    return LEN;
	} else if (prop.equals("code")) {
	    return CODE;
	} else if (prop.equals("label")) {
	    return LABEL;
	} else if (prop.equals("type")) {
	    return TYPE;
	}
	return -1;
    }

    /**
     * The <code>widgetGetSet</code> method is key to setting up
     * widgets under Hecl.  It does two things: 1) When setting
     * properties, it sets them in a hash table so that they can be
     * retrieved to instantiate a widget.  2) It also attempts to get
     * or set the widget attributes directly.  Perhaps these two
     * functions could be separated...
     *
     * @param interp an <code>Interp</code> value
     * @param widgetid a <code>String</code> value
     * @param propname a <code>String</code> value
     * @param propval a <code>Thing</code> value
     * @param getset an <code>int</code> value
     * @exception HeclException if an error occurs
     */
    private void widgetGetSet(Interp interp, String widgetid, String propname,
			     Thing propval, int getset)
	throws HeclException {

	Object widget = widgets.get(widgetid);
	Thing result = null;
	int widgettype = getWidgetType(widget);
	int property = getProp(propname.toLowerCase());
	/* Ok, make a composite int to switch on. */
	int index = property + widgettype + getset;
	/* Use this to indicate that nothing has been set. */
	boolean ok = false;

	if (getset == SETPROP) {
	    ok = true;
	    switch(property) {
		/* These are here just to make sure it's one of them. */
		case LABEL:
		case TYPE:
		case TEXT:
		case CODE:
		case LEN:
		    properties.put(propname, propval);
		    break;
		default:
		    /* Nothing set here... fall through to the options below. */
		    ok = false;
	    }
	}

	switch (index) {
	    case TEXTBOX + TEXT + SETPROP:
		((TextBox)widget).setString(propval.toString());
		break;
	    case TEXTBOX + TEXT + GETPROP:
		result = new Thing(((TextBox)widget).getString());
		break;
	    case TEXTFIELD + TEXT + GETPROP:
		result = new Thing(((TextField)widget).getString());
		break;
	    case STRINGITEM + TEXT + GETPROP:
		result = new Thing(((StringItem)widget).getText());
		break;
	    default:
		if (!ok) {
		    throw new HeclException("Bad " +
					    (getset == GETPROP ? "(get)" : "(set)") + " argument: " +
					    widgetid + " " + index);
		}
	}

	if (result == null) {
	    result = new Thing("");
	}
	interp.setResult(result);
    }

    /**
     * The <code>commandAction</code> method is called when commands
     * are dispatched to.
     *
     * @param c a <code>Command</code> value
     * @param s a <code>Displayable</code> value
     */
    public void commandAction(Command c, Displayable s) {
	Thing code = (Thing)callbacks.get(c.getLabel());
	try {
	    Eval.eval(interp, code);
	} catch (Exception e) {
	    /* FIXME - perhaps we could call a 'bgerror' command if
	     * it's defined, like in Tk? */
//	    Hecl.displayError(e.toString());
	}
    }
}
