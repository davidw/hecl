/* Copyright 2005-2006 David N. Welton

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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.LongThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.Thing;

/**
 * <code>GUI</code> implements the high level lcdui commands.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class GUI implements CommandListener, Runnable, ItemStateListener {
    public Display display;
    public Interp interp;
    public Hecl midlet;

    /* These are callbacks used to implement Commands. */
    private static Hashtable callbacks = new Hashtable();

    /* And these are for item state changes. */
    private static Hashtable itemcallbacks = new Hashtable();

    /* This is a temporary hash that is used for configuring/creating
     * widgets. */
    private static Properties properties;
    private static int uniqueid = 0;

    private static Screen screen;

//#ifdef sms
    private static boolean smsloaded = false;
    private static boolean smspresent = false;
//#endif

    /* Command types  */
    public static final int ALERTCMD = 0;
    public static final int CHOICEGROUPCMD = 1;
    public static final int CMDCMD = 2;
    public static final int DATEFIELDCMD = 3;
    public static final int FORMCMD = 4;
    public static final int GAUGECMD = 5;
    public static final int LISTBOXCMD = 6;
    public static final int STRINGCMD = 7;
    public static final int STRINGITEMCMD = 8;
    public static final int TEXTBOXCMD = 9;
    public static final int TEXTFIELDCMD = 10;

    public static final int GETPROPCMD = 11;
    public static final int SETPROPCMD = 12;
    public static final int GETINDEXCMD = 13;
    public static final int SETINDEXCMD = 14;
    public static final int SETCURRENTCMD = 15;
    public static final int NOSCREENCMD = 16;
    public static final int SCREENAPPENDCMD = 17;
    public static final int EXITCMD = 18;

//#ifdef sms
    public static final int SMSCMD = 20;
//#endif

    /* Commands are run in a new thread so that if they block, they
     * don't block the whole system. */
    private Thread cmdThread;

    /* no offset */
    static final int TEXTFIELD = 0;
    static final int STRINGITEM = 1;
    static final int COMMAND = 2;
    static final int TEXTBOX = 3;
    static final int LISTBOX = 4;
    static final int FORM = 5;
    static final int ALERT = 6;
    static final int CHOICEGROUP = 7;
    static final int GAUGE = 8;
    static final int DATEFIELD = 9;

    /* These are the parameter types that describe different GUI elements. */
    static final int TEXT = 0;		/* Element's text */
    static final int LABEL = 1 << 8;	/* Label or title */
    static final int CODE = 2 << 8;	/* Code to run */
    static final int LEN = 3 << 8;	/* Maximum length, or fetch length. */
    static final int TYPE = 4 << 8;	/* Type or constraints of thing */
    static final int SELECTED = 5 << 8;	/* Which index is selected */
    static final int INDEX = 6 << 8;	/* Item index */
    static final int LIST = 7 << 8;	/* List of items for Choice's */
    static final int VAL = 8 << 8;	/* Value - for gauges */
    static final int DATE = 9 << 8;	/* Date - for datefields */
    static final int CALLBACK = 10 << 8;	/* Callback - callback code */

    /* get or set? */
    static final int GETPROP = 0x1000000;
    static final int SETPROP = 0x0000000;

    /**
     * The <code>stdLabel</code> method adds a standard default label.
     *
     * @param type a <code>String</code> value
     */
    private void stdLabel(String type) {
	properties.setProp("label", new Thing(type + uniqueid)); /* default  */
    }

    /**
     * The <code>setItemCallback</code> method sets an itemcallback
     * for the item in question, if the callback property has been
     * defined.  This is used in calls to the itemStateChanged method.
     *
     * @param item an <code>Item</code> value
     */
    private void setItemCallback(Object item) {
	if (properties.existsProp("callback")) {
	    itemcallbacks.put(item, properties.getProp("callback"));
	}
    }

    /**
     * The <code>choicesFromList</code> method takes the 'list'
     * property of a widget and turns into an array of strings
     * suitable for passing to instantiate a new Choice derived widget
     * with.
     *
     * @return a <code>String[]</code> value
     * @exception HeclException if an error occurs
     */
    private String []choicesFromList() throws HeclException {
	Vector v = ListThing.get(properties.getProp("list"));
	int sz = v.size();
	String []choices = new String[sz];
	for (int i = 0; i < sz; i++) {
	    choices[i] = ((Thing)v.elementAt(i)).toString();
	}
	return choices;
    }

    public Thing dispatch(int cmd, Thing[] argv)
	throws HeclException {

	properties = new Properties();

	/* Each widget has a unique id. */
	uniqueid ++;

	/* Set the standard widget label. */
	stdLabel(argv[0].toString());

	Thing res = new Thing("");
	String []choices;
	Object widget;
	int idx;

	switch (cmd) {

	    case FORMCMD:
		/* The 'form' command.  Creates a form and evaluates its code. */

		/* In this and other commands, we first set up default
		 * properties in the hash table, so that they are not
		 * empty when the widget is created. */
		properties.setProp("code", new Thing("")); /* default  */

		/* Then we set the properties passed to us on the command line. */
		properties.setProps(argv, 1);
		Form f = new Form((properties.getProp("label")).toString());
		f.setCommandListener(this);
		f.setItemStateListener(this);
		/* We make sure that this screen is the default when we
		 * evaluate the code.  This doesn't mean it's displayed,
		 * though. */
		screen = (Screen)f;
		interp.eval(properties.getProp("code"));
		res = ObjectThing.create(f);
		break;

	    case ALERTCMD:
		/* The Alert command. */
		properties.setProp("text", new Thing("")); /* default */
		properties.setProp("type", new Thing("info")); /* default */

		properties.setProps(argv, 1);
		Alert a = new Alert(properties.getProp("label").toString(),
				    properties.getProp("text").toString(),
				    null,
				    getAlertType(properties.getProp("type").toString()));
		screen = (Screen)a;

		if (properties.existsProp("time")) {
		    int tm = IntThing.get(properties.getProp("time"));
		    if (tm < 0) {
			tm = Alert.FOREVER;
		    }
		    a.setTimeout(tm);
		}
		res = ObjectThing.create(a);
		break;

	    case LISTBOXCMD:
		/* These are actually called 'Lists', but that name is
		 * already taken in Hecl.  */
		properties.setProp("type", new Thing("exclusive")); /* default */
		properties.setProp("code", new Thing("")); /* default  */
		properties.setProp("list", new Thing("")); /* default */

		properties.setProps(argv, 1);

		choices = choicesFromList();

		int type = getChoiceType((properties.getProp("type")).toString());
		ListBox lb = new ListBox((properties.getProp("label")).toString(),
					 type, choices);
		lb.setCommandListener(this);
		lb.setItemStateListener(this);
		setItemCallback(lb.cg);
		screen = (Screen)lb;
		interp.eval(properties.getProp("code"));
		res = ObjectThing.create(lb);
		break;

	    case CHOICEGROUPCMD:
		properties.setProp("type", new Thing("exclusive")); /* default */
		properties.setProp("list", new Thing("")); /* default */
		properties.setProps(argv, 1);

		/* This is redundant with the code for listbox above. */
		choices = choicesFromList();

		ChoiceGroup cg = new ChoiceGroup(
		    properties.getProp("label").toString(),
		    getChoiceType((properties.getProp("type")).toString()),
		    choices, null);

		setItemCallback(cg);
		if (screen != null) {
		    ((Form)screen).append(cg);
		}
		res = ObjectThing.create(cg);
		break;

	    case TEXTBOXCMD:
		/* The 'textbox' command.  Creates a textbox and evaluates its code. */
		properties.setProp("len", IntThing.create(400)); /* default  */
		properties.setProp("text", new Thing("")); /* default  */
		properties.setProp("code", new Thing("")); /* default  */
		properties.setProp("type", new Thing("any")); /* default  */
		properties.setProps(argv, 1);
		TextBox tb;
		try {
		    tb = new TextBox((properties.getProp("label")).toString(),
				     (properties.getProp("text")).toString(),
				     IntThing.get(properties.getProp("len")),
				     getTextType((properties.getProp("type")).toString()));
		} catch (IllegalArgumentException e) {
		    throw new HeclException("textbox can't hold a string that big");
		}

		tb.setCommandListener(this);
		screen = (Screen)tb;
		interp.eval(properties.getProp("code"));
		res = ObjectThing.create(tb);
		break;

	    case TEXTFIELDCMD:
		/* The 'textfield' command. */
		properties.setProp("text", new Thing("")); /* default  */
		properties.setProp("len", IntThing.create(50)); /* default  */
		properties.setProp("type", new Thing("any")); /* default  */
		properties.setProps(argv, 1);

		TextField tf =
		    new TextField((properties.getProp("label")).toString(),
				  (properties.getProp("text")).toString(),
				  IntThing.get(properties.getProp("len")),
				  getTextType((properties.getProp("type")).toString()));

		setItemCallback(tf);
		if (screen != null) {
		    ((Form)screen).append(tf);
		}
		res = ObjectThing.create(tf);
		break;

	    case GAUGECMD:
		properties.setProp("maxval", IntThing.create(10)); /* default  */
		properties.setProp("val", IntThing.create(0)); /* default  */
		properties.setProp("interactive", IntThing.create(1)); /* default  */
		properties.setProps(argv, 1);

		Gauge g = new Gauge((properties.getProp("label")).toString(),
				    IntThing.get(properties.getProp("interactive")) == 1,
				    IntThing.get(properties.getProp("maxval")),
				    IntThing.get(properties.getProp("val")));

		setItemCallback(g);
		if (screen != null) {
		    ((Form)screen).append(g);
		}
		res = ObjectThing.create(g);
		break;

	    case DATEFIELDCMD:
		/* The datefield command. */
		properties.setProp("type", new Thing("date_time"));
		properties.setProps(argv, 1);

		DateField df = new DateField(
		    (properties.getProp("label")).toString(),
		    getDateFieldType((properties.getProp("type")).toString()));

		setItemCallback(df);
		if (screen != null) {
		    ((Form)screen).append(df);
		}
		res = ObjectThing.create(df);
		break;

	    case STRINGITEMCMD:
		/* The 'stringitem' command. Differs from a plain string
		 * in that it can be modified, and it has both a label and
		 * text. */
		properties.setProp("text", new Thing("")); /* default  */
		properties.setProps(argv, 1);
		StringItem si = new StringItem((properties.getProp("label")).toString(),
					       (properties.getProp("text")).toString());
		if (screen != null) {
		    ((Form)screen).append(si);
		}
		res = ObjectThing.create(si);
		break;

	    case STRINGCMD:
		/* The 'string' command. Plain old string to append to a
		 * form. */
		String s = argv[1].toString();
		if (screen != null) {
		    if (screen instanceof Form) {
			((Form)screen).append(s);
		    } else if (screen instanceof List) {
			((List)screen).append(s, null);
		    }
		}
		res = ObjectThing.create(s);
		break;

	    case CMDCMD:
		/* The 'cmd' command.  Adds a command to the current
		 * screen (form, textbox and the like). */
		properties.setProp("code", new Thing(""));
		properties.setProp("type", new Thing("screen"));
		properties.setProps(argv, 1);
		String label = (properties.getProp("label")).toString();
		Command c = new Command(label,
					getCmdType((properties.getProp("type")).toString()), 0);
		if (screen != null) {
		    screen.addCommand(c);
		}
		callbacks.put(label, properties.getProp("code"));
		res = ObjectThing.create(c);
		break;

	    case GETPROPCMD:
		/* The 'getprop' command. Get a particular property of a
		 * widget. */
		res = widgetGetSet(interp, argv[1], argv[2].toString(), null, GETPROP);
		break;

	    case SETPROPCMD:
		/* The 'setprop' command. Set a particular property of a
		 * widget. */
		res = widgetGetSet(interp, argv[1], argv[2].toString(), argv[3], SETPROP);
		break;

	    case SETINDEXCMD:
		widget = ObjectThing.get(argv[1]);
		idx = IntThing.get(argv[2]);
		if (widget instanceof Form) {
		    ((Form)widget).set(idx, (Item)ObjectThing.get(argv[3]));
		} else if (widget instanceof Choice) {
		    ((Choice)widget).set(idx, argv[3].toString(), null);
		}
		break;

	    case GETINDEXCMD:
		widget = ObjectThing.get(argv[1]);
		idx = IntThing.get(argv[2]);
		if (widget instanceof Form) {
		    res = ObjectThing.create(((Form)widget).get(idx));
		} else if (widget instanceof Choice) {
		    res = new Thing(((Choice)widget).getString(idx));
		}
		break;

	    case SCREENAPPENDCMD:
		widget = ObjectThing.get(argv[1]);
		Object item = ObjectThing.get(argv[2]);
		if (widget instanceof Form) {
		    if (item instanceof String) {
			((Form)widget).append(item.toString());
		    } else {
			((Form)widget).append((Item)item);
		    }
		} else if (widget instanceof Choice) {
		    ((Choice)widget).append(item.toString(), null);
		}
		break;

	    case NOSCREENCMD:
		/* Run without a screen so that we can set indexes and
		 * stuff like that. */
		Screen oldscreen = screen;
		screen = null;
		interp.eval(argv[1]);
		screen = oldscreen;
		break;

	    case SETCURRENTCMD:
		/* The 'setcurrent' command.  Set the current widget to be
		 * the displayed widget.  Used with form, textbox and the
		 * like. */
		Displayable disp = (Displayable)ObjectThing.get(argv[1]);
		display.setCurrent(disp);
		screen = (Screen)disp;
		break;

	    case EXITCMD:
		midlet.exitApp();
		break;

//#ifdef sms
	    case SMSCMD:
		try {
		    Class.forName("javax.wireless.messaging.MessageConnection");
		    SMS.send("sms://" + argv[1].toString(), argv[2].toString());
		} catch (ClassNotFoundException cnf) {
		    throw new HeclException("This phone doesn't support Java SMS!");
		}
		break;
//#endif
	}
	properties = null;
	return res;
    }

    /**
     * The <code>getWidgetType</code> method returns an integer
     * describing the widget type.
     *
     * @param widget an <code>Object</code> value
     * @return an <code>int</code> value
     */
    private int getWidgetType (Object widget) {
	if (widget instanceof TextField)
	    return TEXTFIELD;
	if (widget instanceof Command)
	    return COMMAND;
	if (widget instanceof ListBox)
	    /* Comes before FORM because it's more specific. */
	    return LISTBOX;
	if (widget instanceof Form)
	    return FORM;
	if (widget instanceof TextBox)
	    return TEXTBOX;
	if (widget instanceof StringItem)
	    return STRINGITEM;
	if (widget instanceof ChoiceGroup)
	    return CHOICEGROUP;
	if (widget instanceof Alert)
	    return ALERT;
	if (widget instanceof Gauge)
	    return GAUGE;
	if (widget instanceof DateField)
	    return DATEFIELD;
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
	if (type.equals("back"))
	    return Command.BACK;
	if (type.equals("cancel"))
	    return Command.CANCEL;
	if (type.equals("exit"))
	    return Command.EXIT;
	if (type.equals("help"))
	    return Command.HELP;
	if (type.equals("item"))
	    return Command.ITEM;
	if (type.equals("ok"))
	    return Command.OK;
	if (type.equals("screen"))
	    return Command.SCREEN;
	if (type.equals("stop"))
	    return Command.STOP;
	return -1;
    }


    /**
     * The <code>getChoiceType</code> method returns a numeric type
     * that corresponds with the string that we pass it.
     *
     * @param type a <code>String</code> value
     * @return an <code>int</code> value
     */
    private int getChoiceType(String type) {
	if (type.equals("exclusive")) {
	    return Choice.EXCLUSIVE;
	} else if (type.equals("implicit")) {
	    return Choice.IMPLICIT;
	} else if (type.equals("multiple")) {
	    return Choice.MULTIPLE;
	}
	return 0;
    }


    /**
     * The <code>getDateFieldType</code> method returns a DateField
     * type, given a string describing the type.
     *
     * @param type a <code>String</code> value
     * @return an <code>int</code> value
     */
    private int getDateFieldType(String type) {
	if (type.equals("date")) {
	    return DateField.DATE;
	} else if (type.equals("date_time")) {
	    return DateField.DATE_TIME;
	} else if (type.equals("time")) {
	    return DateField.TIME;
	}
	return 0;
    }

    /**
     * The <code>getAlertType</code> method returns an AlertType for
     * the string passed in.
     *
     * @param type a <code>String</code> value
     * @return an <code>AlertType</code> value
     */
    private AlertType getAlertType(String type) {
	if (type.equals("alarm")) {
	    return AlertType.ALARM;
	} else if (type.equals("confirmation")) {
	    return AlertType.CONFIRMATION;
	} else if (type.equals("error")) {
	    return AlertType.ERROR;
	} else if (type.equals("info")) {
	    return AlertType.INFO;
	} else if (type.equals("warning")) {
	    return AlertType.WARNING;
	}
	return null;
    }

    /**
     * The <code>getTextType</code> method returns the type of text to
     * use, given a string.  Used in textfields and textboxes.
     *
     * @param type a <code>String</code> value
     * @return an <code>int</code> value
     */
    private int getTextType(String type) {
	if (type.equals("any")) {
	    return TextField.ANY;
	} else if (type.equals("emailaddr")) {
	    return TextField.EMAILADDR;
	} else if (type.equals("numeric")) {
	    return TextField.NUMERIC;
	} else if (type.equals("phonenumber")) {
	    return TextField.PHONENUMBER;
	} else if (type.equals("password")) {
	    return TextField.PASSWORD;
	} else if (type.equals("url")) {
	    return TextField.URL;
	}
	return TextField.ANY;
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
	} else if (prop.equals("callback")) {
	    return CALLBACK;
	} else if (prop.equals("code")) {
	    return CODE;
	} else if (prop.equals("label")) {
	    return LABEL;
	} else if (prop.equals("selected")) {
	    return SELECTED;
	} else if (prop.equals("type")) {
	    return TYPE;
	} else if (prop.equals("list")) {
	    return LIST;
	} else if (prop.equals("val")) {
	    return VAL;
	} else if (prop.equals("date")) {
	    return DATE;
	} else if (prop.equals("index")) {
	    return INDEX;
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
    private Thing widgetGetSet(Interp interp, Thing widgetthing, String propname,
			       Thing propval, int getset)
	throws HeclException {

//	Object widget = widgets.get(widgetid);
	Object widget = ObjectThing.get(widgetthing);
	Thing result = new Thing("");
	int widgettype = getWidgetType(widget);
	int property = getProp(propname.toLowerCase());
	/* Ok, make a composite int to switch on. */
	int index = property + widgettype + getset;
	/* Use this to indicate that nothing has been set. */
	boolean ok = false;

	/* The commented out options below are not available in the
	 * stock lcdui. */

	switch (index) {
	    case ALERT + LABEL + SETPROP:
	    case FORM + LABEL + SETPROP:
	    case LISTBOX + LABEL + SETPROP:
	    case TEXTBOX + LABEL + SETPROP:
		((Screen)widget).setTitle(propval.toString());
		break;
	    case ALERT + LABEL + GETPROP:
	    case FORM + LABEL + GETPROP:
	    case LISTBOX + LABEL + GETPROP:
	    case TEXTBOX + LABEL + GETPROP:
		result = new Thing(((Screen)widget).getTitle());
		break;
	    case TEXTBOX + TEXT + SETPROP:
		try {
		    ((TextBox)widget).setString(propval.toString());
		} catch (IllegalArgumentException e) {
		    throw new HeclException("textbox can only hold " +
					    ((TextBox)widget).getMaxSize() + " chars");
		}
		break;
	    case TEXTBOX + TEXT + GETPROP:
		result = new Thing(((TextBox)widget).getString());
		break;
	    case LISTBOX + SELECTED + SETPROP:
	    case CHOICEGROUP + SELECTED + SETPROP:
	    {
		Choice c;
		if (widgettype == LISTBOX) {
		    c = (Choice)((ListBox)widget).cg;
		} else {
		    c = (Choice)widget;
		}
		int sz = c.size();
		Vector v = ListThing.get(propval);
		boolean []flags = new boolean[sz];
		int i;
		for (i = 0; i < sz; i++) {
		    flags[i] = false;
		}
		for (Enumeration e = v.elements(); e.hasMoreElements();) {
		    i = IntThing.get((Thing)e.nextElement());
		    flags[i] = true;
		}
		c.setSelectedFlags(flags);
		break;
	    }
	    case LISTBOX + SELECTED + GETPROP:
	    case CHOICEGROUP + SELECTED + GETPROP:
	    {
		Choice c;
		if (widgettype == LISTBOX) {
		    c = (Choice)((ListBox)widget).cg;
		} else {
		    c = (Choice)widget;
		}
		int sz = c.size();
		Vector v = new Vector();
		boolean []flags = new boolean[sz];
		c.getSelectedFlags(flags);
		for (int i = 0; i < sz; i++) {
		    if (flags[i] == true) {
			v.addElement(IntThing.create(i));
		    }
		}
		result = ListThing.create(v);
		break;
	    }
	    case TEXTFIELD + TEXT + GETPROP:
		result = new Thing(((TextField)widget).getString());
		break;
	    case TEXTFIELD + TEXT + SETPROP:
		((TextField)widget).setString(propval.toString());
		break;
	    case CHOICEGROUP + LABEL + SETPROP:
	    case DATEFIELD + LABEL + SETPROP:
	    case GAUGE + LABEL + SETPROP:
	    case STRINGITEM + LABEL + SETPROP:
	    case TEXTFIELD + LABEL + SETPROP:
		((Item)widget).setLabel(propval.toString());
		break;
	    case CHOICEGROUP + LABEL + GETPROP:
	    case DATEFIELD + LABEL + GETPROP:
	    case GAUGE + LABEL + GETPROP:
	    case STRINGITEM + LABEL + GETPROP:
	    case TEXTFIELD + LABEL + GETPROP:
		result = new Thing(((Item)widget).getLabel());
		break;
	    case STRINGITEM + TEXT + GETPROP:
		result = new Thing(((StringItem)widget).getText());
		break;
	    case STRINGITEM + TEXT + SETPROP:
		((StringItem)widget).setText(propval.toString());
		break;
	    case ALERT + TEXT + SETPROP:
		((Alert)widget).setString(propval.toString());
		break;
	    case ALERT + TEXT + GETPROP:
		result = new Thing(((Alert)widget).getString());
		break;
	    case GAUGE + VAL + SETPROP:
		((Gauge)widget).setValue(IntThing.get(propval));
		break;
	    case GAUGE + VAL + GETPROP:
		result = IntThing.create(((Gauge)widget).getValue());
		break;
	    case DATEFIELD + DATE + GETPROP:
		result = LongThing.create(
		    (int)(((DateField)widget).getDate().getTime()));
		break;
	    case DATEFIELD + DATE + SETPROP:
		((DateField)widget).setDate(new java.util.Date(LongThing.get(propval)));
		break;

	    case CALLBACK + LISTBOX + SETPROP:
	    case CALLBACK + CHOICEGROUP + SETPROP:
	    case CALLBACK + GAUGE + SETPROP:
	    case CALLBACK + TEXTFIELD + SETPROP:
	    case CALLBACK + DATEFIELD + SETPROP:
		itemcallbacks.put(widget, propval);
		break;

	    case CALLBACK + LISTBOX + GETPROP:
	    case CALLBACK + CHOICEGROUP + GETPROP:
	    case CALLBACK + GAUGE + GETPROP:
	    case CALLBACK + TEXTFIELD + GETPROP:
	    case CALLBACK + DATEFIELD + GETPROP:
		result = (Thing)itemcallbacks.get(widget);
		break;

	    case COMMAND + CODE + SETPROP:
		String label = ((Command)widget).getLabel();
		callbacks.put(label, propval);
		break;
	    case COMMAND + CODE + GETPROP:
		result = (Thing)callbacks.get(((Command)widget).getLabel());
		break;

	    default:
		if (!ok) {
		    throw new HeclException("Bad " +
					    (getset == GETPROP ?
					     "(get)" : "(set)") + " argument: " +
					    widgetthing.toString() + " " + index);
		}
	}
	return result;
    }

    /**
     * The <code>commandAction</code> method is called when commands
     * are dispatched to.
     *
     * @param c a <code>Command</code> value
     * @param s a <code>Displayable</code> value
     */
    public void commandAction(Command c, Displayable s) {
	synchronized (code) {
	    code = (Thing)callbacks.get(c.getLabel());
	}
	/* I guess we could use some kind of queue instead of just
	 * popping off a new thread each time... FIXME */
	cmdThread = new Thread(this);
	cmdThread.start();
    }

    /* This is to share between threads... */
    Thing code = new Thing("");


    /**
     * The <code>run</code> method is where a 'cmd's callback is
     * evaluated.
     *
     */
    public void run() {
	try {
	    synchronized (code) {
		interp.eval(code);
	    }
	} catch (Exception e) {
	    System.err.println(e.toString());
	    e.printStackTrace();
	    /* FIXME - perhaps we could call a 'bgerror' command if
	     * it's defined, like in Tk? */
	    // Hecl.displayError(e.toString());
	}
	try {
	    /* It's done evaluating - time to go away. */
	    cmdThread.join();
	} catch (InterruptedException e) {
	    System.err.println(e.toString());
	}
    }

    /**
     * The <code>itemStateChanged</code> method runs callbacks for
     * items that have defined them when their state changes.
     *
     * @param item an <code>Item</code> value
     */
    public void itemStateChanged(Item item) {
	if (itemcallbacks.isEmpty()) {
	    return;
	}
	if (itemcallbacks.containsKey(item)) {
	    Thing code = (Thing)itemcallbacks.get(item);
	    try {
		interp.eval(code);
	    } catch (Exception e) {
		System.err.println(e.toString());
	    }
	}
    }
}
