/*
 * Copyright 2005-2007
 * Wolfgang S. Kechel, data2c GmbH (www.data2c.com)
 * 
 * Author: Wolfgang S. Kechel - wolfgang.kechel@data2c.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hecl.midp20.lcdui;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.game.GameCanvas;

import org.awt.Color;
import org.awt.Rectangle;
import org.graphics.Drawable;

import org.hecl.midp20.MidletCmd;

public class HeclCanvas extends GameCanvas implements CommandListener {
    // We treat the left/right softkey on our own and provide two additional
    // gamekeys to simplify the programming.
    public static final int GAME_LEFT_SK = -1;
    public static final int GAME_RIGHT_SK = -2;
    
    // Fallback keycodes, valid for:
    // Nokia, Samsung (SGH-D500) and SonyEricsson (K608i, K700i and V800)
    // and probably many more
    public static int KEYCODE_LEFT_SK = -6;
    public static int KEYCODE_RIGHT_SK = -7;

    public HeclCanvas(boolean suppressKeyEvents) {
	super(suppressKeyEvents);
	this.nokeyevents = !suppressKeyEvents;

	//de.enough.polish.ui.game.GameCanvas has no getGraphics method
//#ifdef polish.blackberry 
	this.mygraphics = super.getPolishGraphics();
//#else
	this.mygraphics = super.getGraphics();
//#endif
	calcScreenWidth();
	this.drawable = new Drawable(this.mygraphics,this.drawwidth,this.drawheight);
	// Force initial draw
	showCommands(mygraphics);
	flushGraphics();
    }
    

    public void addCommand(Command cmd) {
	for (int i=0; i<this.cmds.size(); i++) {
	    if (cmd == (Command)this.cmds.elementAt(i)) {
		// Its the same just return
		return;
	    }
	}
	if(!isfullscreen || SettingsCmd.cvkeepcmdsinfullscreen)
	    super.addCommand(cmd);

	// Now insert it in order (according to priority)
	boolean inserted = false;
	for (int i=0; i<this.cmds.size(); i++) {
	    if (cmd.getPriority() < ((Command)this.cmds.elementAt(i)).getPriority()) {
		this.cmds.insertElementAt(cmd,i);
		inserted = true;
		break;
	    }
	}
	if (inserted == false) {
	    // Not inserted just place it at the end
	    this.cmds.addElement(cmd);
	}
	updateCommands();
    }

    public void commandAction(Command c,Displayable d) {
	// command handler for the menu
	if(c == CMD_BACK) {
	    MidletCmd.getDisplay().setCurrent(this);
	    return;
	}
	if(c == List.SELECT_COMMAND) {
	    int n = menulist.getSelectedIndex();
	    if(n == -1)
		return;
	    ++n;
	    if(n >= 1 && n < cmds.size()) {
		Command mycmd = (Command)cmds.elementAt(n);
		MidletCmd.getDisplay().setCurrent(this);
		handleCommand(mycmd);
	    }
	}
    }
    
    public void handleCommand(Command c) {
	if(savecmdlistener != null)
	    savecmdlistener.commandAction(c,this);
    }
    
    public void flushGraphics() {
//#ifdef DEBUG
	Rectangle r = new Rectangle(mygraphics.getClipX(),
				    mygraphics.getClipY(),
				    mygraphics.getClipWidth(),
				    mygraphics.getClipHeight());
	System.err.println("HeclCanvas.flushgraphics()");
	System.err.println("clip is: "+r.x
			   +", "+r.y
			   +", "+r.width
			   +", "+r.height);
	mygraphics.setClip(0,0,240,309);
//#endif
	super.flushGraphics();
//#ifdef DEBUG
	mygraphics.setClip(r.x,r.y,r.width,r.height);
//#endif
    }


    public Color getCmdBgColor() {return cmdbgcolor;}

    public Color getCmdFgColor() {return cmdfgcolor;}
    
    public Vector getCommands() {return cmds;}

    public int getFullWidth() {return fullwidth;}
    
    public int getFullHeight() {return fullheight;}
    
    public int getDrawWidth() {return drawwidth;}
    
    public int getDrawHeight() {return drawheight;}

    public int getGameAction(int keyCode) {
	if(keyCode == KEYCODE_LEFT_SK)
	    return GAME_LEFT_SK;
	if(keyCode == KEYCODE_RIGHT_SK)
	    return GAME_RIGHT_SK;
	return super.getGameAction(keyCode);
    }


    public boolean getFullScreenMode() {return isfullscreen;}

    public Graphics getGraphics() {return mygraphics;}
    

    public int getKeyCode(int gameAction) {
	if(gameAction == GAME_LEFT_SK)
	    return KEYCODE_LEFT_SK;
	if(gameAction == GAME_RIGHT_SK)
	    return KEYCODE_RIGHT_SK;
	return super.getKeyCode(gameAction);
    }

    
    public String getKeyName(int keyCode) {
	if(keyCode == KEYCODE_LEFT_SK)
	    return "LEFT_SK";
	if(keyCode == KEYCODE_RIGHT_SK)
	    return "RIGHT_SK";
	return super.getKeyName(keyCode);
    }
    
    
    public void hideNotify() {
	callEventHandler(CanvasEvent.E_HIDE,0,0,drawwidth,drawheight,0);
    }
    

    public void setCmdBgColor(Color c) {
	cmdbgcolor = c;
	showCommands(mygraphics);
    }


    public void setCmdFgColor(Color c) {
	cmdfgcolor = c;
	showCommands(mygraphics);
    }
    

    public void setCommandListener(CommandListener l) {
	// we need to take care of the listener here since we override the
	// default behavior in fullscreen mode!
	cmdlistener = l;
	if(!isfullscreen) {
	    // Just do it for non-fullscreen
	    super.setCommandListener(cmdlistener);
	} else {
	    // do not set, this will be done when fullscreen mode is turned off
	    savecmdlistener = cmdlistener;
	}
    }
    

    public void setEventHandler(EventHandler eventHandler) {
	evh = eventHandler;
    }
    
    public void setFullScreenMode(boolean b) {	
	// Commented out due to problem with WTK2.5.2 that forces us to
	// resitore fullscreen mode of canvas after performing setcurrent to a
	// non-canvas displayable.
	//
	//if(b == isfullscreen)
	//    return;

	// ignore request for fullscreen canvas when disabled
	if(b && !SettingsCmd.cvallowfullscreen)
	    return;

	isfullscreen = b;
	super.setFullScreenMode(isfullscreen);

	if(SettingsCmd.cvkeepcmdsinfullscreen)
	    return;

	int n = cmds.size();
	if(isfullscreen) {
	    // disable commands
	    savecmdlistener = cmdlistener;
	    super.setCommandListener(null);
	    for(int i=0; i<n; ++i) {
		super.removeCommand((Command)cmds.elementAt(i));
	    }
	    showCommands(mygraphics);
	} else {
	    // enable commands
	    for(int i=0; i<n; ++i) {
		super.addCommand((Command)cmds.elementAt(i));
	    }
	    cmdlistener = savecmdlistener;
	    super.setCommandListener(cmdlistener);
	}
    }

    protected void showCommands(Graphics g) {
//#ifdef DEBUG
	System.err.println("showCommands, fullscreen="+isfullscreen+", isshown="+isShown());
//#endif
	// Unfortunately, isShown does not work on some emulators, therefor we
	// simply draw the commands in any case here.
	if(SettingsCmd.cvdocmds && isfullscreen && cmds.size() > 0 && isShown()) {
	    int oldcol = g.getColor();
	    Font oldfont = g.getFont();
	    int oldcx = g.getClipX();
	    int oldcy = g.getClipY();
	    int oldcw = g.getClipWidth();
	    int oldch = g.getClipHeight();
	    
	    // Nokia 6630 fullscreen bug
	    calcScreenWidth();      
//#ifdef DEBUG
	    System.err.println("drawing rect: y="+drawheight+", h="+CMDBARHEIGHT);

	    System.err.println("size - w="+getWidth()+" h="+getHeight());
	    System.err.println("draw - w"+getDrawWidth()+" h="+getDrawHeight());
	    System.err.println("oldclip("+oldcx+","+oldcy+","+oldcw+","+oldch+")");
	    System.err.println("setclip(0,"+drawheight+","+drawwidth+","+CMDBARHEIGHT+")");
	    g.setColor(0xff0000);
	    g.drawLine(0,0,getWidth(),getHeight());
//#endif

	    // set clipping region to area where commands are shown
	    g.setClip(0,drawheight,drawwidth,CMDBARHEIGHT);

	    // clear rect in bg color
	    g.setColor(cmdbgcolor.getRGB());
	    g.fillRect(0,drawheight,drawwidth,CMDBARHEIGHT);
	    //g.drawLine(0,drawheight,drawwidth-1,drawheight+3);
	    
	    // Draw the labels
	    g.setColor(cmdfgcolor.getRGB());
	    g.setFont(CMDFONT);
	    int ypos = drawheight+1;
	    Command c = buttons[0].getCommand();
	    if(c != null) {
		String l = c.getLabel();
		g.drawString(l != null ? l : "Left",
			     1,ypos,Graphics.TOP|Graphics.LEFT);
	    }
	    c = buttons[1].getCommand();
	    if(c != null) {
		String l = c.getLabel();
		g.drawString(l != null ? l : "Right",
			     drawwidth-1,ypos,Graphics.TOP|Graphics.RIGHT);
	    }
	    c = buttons[2].getCommand();
	    if(c != null) {
		String l = c.getLabel();
		g.drawString(l != null ? l : "Center",
			     drawwidth/2,ypos,Graphics.TOP|Graphics.HCENTER);
	    }
	    g.setColor(oldcol);
	    g.setFont(oldfont);
	    //#ifdef DEBUG
	    System.err.println("flushing: 0, "+drawheight +", "+drawwidth+", "+CMDBARHEIGHT);
	    //#endif
	    flushGraphics(0,drawheight,drawwidth,CMDBARHEIGHT);
	    g.setClip(oldcx,oldcy,oldcw,oldch);
	} else {
	    //System.err.println("no commands");
	}
    }


    public void paint(Graphics g) {
	//#ifdef notdef
	System.err.println("HeclCanvas.paint called");
	if(this.drawable != null)
	    System.err.println("NEEDSFLUSH="+this.drawable.needsFlush());
	//#endif
	callEventHandler(CanvasEvent.E_PAINT,0,0,drawwidth,drawheight,0);
	if(this.drawable != null && this.drawable.needsFlush()) {
	    showCommands(mygraphics);
	    flushGraphics();
	}
	super.paint(g);
    }


    public void pointerPressed(int x,int y) {
	callEventHandler(CanvasEvent.E_PPRESS,x,y,drawwidth,drawheight,0);
    }


    public void pointerReleased(int x,int y) {
	callEventHandler(CanvasEvent.E_PRELEASE,x,y,drawwidth,drawheight,0);
    }
    

    public void pointerDragged(int x,int y) {
	callEventHandler(CanvasEvent.E_PDRAG,x,y,drawwidth,drawheight,0);
    }
    

    protected boolean isCommandKey(int keycode) {
	if(isfullscreen && cmds.size() > 0) {
	    switch(getGameAction(keycode)) {
	      case GAME_LEFT_SK:
	      case GAME_RIGHT_SK:
		return true;
	      case FIRE:
		return cmds.size() == 3;
	      default:
		break;
	    }
	}
	return false;
    }
    
    public void keyPressed(int keycode) {
	if(isfullscreen && cmds.size() > 0) {
	    Command c = findCommand(keycode);
	    if(c != null) {
		if(c == CMD_MENU) {
		    menulist.setCommandListener(this);
		    menulist.addCommand(CMD_BACK);
		    MidletCmd.getDisplay().setCurrent(menulist);
		    return;
		}
		handleCommand(c);
		return;
	    }
	}
	callEventHandler(CanvasEvent.E_KPRESS,0,0,drawwidth,drawheight,keycode);
    }
    

    public void keyReleased(int keycode) {
	if(isfullscreen && cmds.size() > 0) {
	    // ignore when command is selected
	    if(findCommand(keycode) != null)
		return;
	}
	callEventHandler(CanvasEvent.E_KRELEASE,0,0,drawwidth,drawheight,keycode);
    }
    

    public void keyRepeated(int keycode) {
	if(isfullscreen && cmds.size() > 0) {
	    // ignore when command is selected
	    if(findCommand(keycode) != null)
		return;
	}
	callEventHandler(CanvasEvent.E_KREPEAT,0,0,drawwidth,drawheight,keycode);
    }
    

    public void removeCommand(Command cmd) {
	super.removeCommand(cmd);
	cmds.removeElement(cmd);
	cmds.trimToSize();
	// update display
	updateCommands();
    }


    public void showNotify() {
	callEventHandler(CanvasEvent.E_SHOW,0,0,drawwidth,drawheight,0);
    }

//#ifdef polish.blackberry

//#ifdef polish.usePolishGui
    //de.enough.polish.ui.AccessibleCanvas sizeChanged is public
    public
//#else
    protected
//#endif

//#else
    protected
//#endif
	void sizeChanged(int w,int h) {

//#ifdef DEBUG
	System.err.println("size changed, w="+w+", h="+h);
	System.err.println("size changed, w="+super.getWidth()+", h="+super.getHeight());
//#endif
	// go calculate. remind: some nokia devices have a bug that cause
	// wrong values to be passed as arguments to this function.
	calcScreenWidth();
	if(this.drawable != null) {
	    this.drawable.resize(drawwidth,drawheight);
	}
	try {
	    callEventHandler(CanvasEvent.E_RESIZE,0,0,drawwidth,drawheight,0);
	}
	catch(Exception e) {
	    e.printStackTrace();
	}
	showCommands(mygraphics);
    }


    protected void callEventHandler(int reason,int x,int y,
				    int width,int height,int keycode) {
	if(evh != null) {
	    try {
		//System.err.println("*** calling evh, reason="+reason);
		CanvasEvent ce = new CanvasEvent(this,reason,x,y,width,height,keycode);
		//System.err.println(ce.toString());
		evh.handleEvent(ce);
		//System.err.println("*** evh done");
	    }
	    catch(Exception e) {
		//System.err.println("**** CANVASEVENT PROBLEM");
		e.printStackTrace();
	    }
	}
    }

    public Drawable getDrawable() {return this.drawable;}
    

    protected boolean isSoftKey(int keycode) {
	return (keycode == KEYCODE_LEFT_SK || keycode == KEYCODE_RIGHT_SK);
    }


    private Command findCommand(int keyCode) {
	for(int i=0; i < buttons.length; ++i) {
	    Command c = buttons[i].getCommand();
	    if(c == null)
		continue;
	    try {
		if(buttons[i].gamecode == getGameAction(keyCode)) {
		    return c;
		}
	    }
	    catch(IllegalArgumentException illgl) {
	    }
	}
	return null;
    }
    
    
    private void updateCommands() {
	if(!SettingsCmd.cvdocmds)
	    return;
	
	for(int i=0; i<buttons.length; ++i)
	    buttons[i].setCommand(null);
	
	Vector commandsTable = new Vector();
	for (int i = 0; i < cmds.size(); i++) {
	    commandsTable.addElement(null);
	}
	
	// Sort commands using priority
	Enumeration en = cmds.elements();
	while (en.hasMoreElements()) {
	    Command commandToSort = (Command)en.nextElement();
	    
	    for (int i = 0; i < commandsTable.size(); i++) {
		if (commandsTable.elementAt(i) == null) {
		    commandsTable.setElementAt(commandToSort, i);
		    break;
		}
		if (commandToSort.getPriority() < ((Command)commandsTable.elementAt(i)).getPriority()) {
		    for (int j = commandsTable.size() - 1; j > i; j--) {
			if (commandsTable.elementAt(j - 1) != null) {
			    commandsTable.setElementAt(commandsTable.elementAt(j - 1), j);
			}
		    }
		}
	    }			
	}
	if(commandsTable.size() > buttons.length) {
	    // Menu is needed
	    commandsTable.insertElementAt(CMD_MENU, 0);
	}
	assignCommands(commandsTable);			
	showCommands(mygraphics);
    }
    
    private void assignCommands(Vector commandsTable) {
	if(commandsTable.size() <= buttons.length) {
	    // we have one button for each command
	    for (int i = 0; i < commandsTable.size(); i++) {
		for(int j=0; j<buttons.length; ++j) {
		    Command c = (Command)commandsTable.elementAt(i);
		    if (buttons[j].getCommand() == null && buttons[j].isPreferred(c)) {
			buttons[j].setCommand(c);
			commandsTable.removeElementAt(i);
			i--;
			break;
		    }
		}
	    }
	    for (int i = 0; i < commandsTable.size(); i++) {
		for(int j=0; j<buttons.length; ++j) {
		    if (buttons[j].getCommand() == null) {
			buttons[j].setCommand((Command)commandsTable.elementAt(i));
			commandsTable.removeElementAt(i);
			i--;
			break;
		    }
		}
	    }
	} else {
	    // more cmds than buttons
	    if(buttons.length > 0) {
		buttons[0].setCommand((Command)commandsTable.firstElement());
		commandsTable.removeElementAt(0);
	    }
	    if(buttons.length > 1) {
		for(int i=0; i<commandsTable.size(); ++i) {
		    Command c = (Command)commandsTable.elementAt(i);
		    if(null == buttons[1].getCommand() && buttons[1].isPreferred(c)) {
			buttons[1].setCommand(c);
			commandsTable.removeElementAt(i);
		    }
		}
	    }
	    for(int i=2; i<buttons.length; ++i)
		buttons[i].setCommand(null);
	    
	    menulist.deleteAll();
	    menucommands = new Vector();
	    for (int i = 0; i < commandsTable.size(); i++) {
		Command c = (Command)commandsTable.elementAt(i);
		menucommands.addElement(c);
		menulist.append(WidgetInfo.commandLabel(c,false), null);
	    }
	}
    }

    
    private void calcScreenWidth() {
	this.fullwidth = super.getWidth();
	this.fullheight = super.getHeight();
	this.drawwidth = fullwidth;
	this.drawheight = getFullScreenMode() && !SettingsCmd.cvkeepcmdsinfullscreen ?
	    this.fullheight-CMDBARHEIGHT : this.fullheight;
    }

    public boolean getAutoFlushMode() {
	return this.autoflush;
    }
    

    public void setAutoFlushMode(boolean b) {
	this.autoflush = b;
    }
    
    protected boolean nokeyevents;
    protected EventHandler evh = null;
    protected Vector cmds = new Vector();
    protected CommandListener cmdlistener = null;
    protected CommandListener savecmdlistener = null;
    private Graphics mygraphics = null;
    private boolean isfullscreen = false;
    private Drawable drawable;
    private SoftButton buttons[] = SoftButton.makeSoftButtons();
    private Vector menucommands = null;
    private List menulist = new List("Menu", List.IMPLICIT);
    private int fullwidth = 1;
    private int fullheight = 1;
    private int drawwidth = 1;
    private int drawheight = 1;
    private Color cmdbgcolor = SettingsCmd.cvcmdbgcolor;
    private Color cmdfgcolor = SettingsCmd.cvcmdfgcolor;
    private boolean autoflush = true;

    static class SoftButton {
	private static SoftButton[] makeSoftButtons() {
	    SoftButton[] v = new SoftButton[3];
	    
	    v[0] = new SoftButton(KEYCODE_LEFT_SK,GAME_LEFT_SK,
				  new int[]{Command.OK,Command.ITEM,Command.SCREEN});
	    v[1] = new SoftButton(KEYCODE_RIGHT_SK,GAME_RIGHT_SK,
				  new int[]{Command.CANCEL,Command.EXIT,
						Command.BACK,Command.STOP});
	    v[2] = new SoftButton(0,Canvas.FIRE,new int[]{Command.SCREEN});
	    return v;
	}
	    
	public SoftButton(int keyCode,int gameCode,int [] ptypes) {
	    keycode = keyCode;
	    gamecode = gameCode;
	    preferredtype = ptypes;
	}

	public Command getCommand() {
	    return cmd;
	}
	
	public void setCommand(Command c) {
	    cmd = c;
	}
	
	public boolean isPreferred(Command c) {
	    int cmdtype = c.getCommandType();
	    if(preferredtype != null) {
		for(int i=0; i<preferredtype.length; ++i) {
		    if(cmdtype == preferredtype[i])
			return true;
		}
	    }
	    return false;
	}
	
	public int keycode = 0;
	public int gamecode = 0;
	protected Command cmd = null;
	public int[] preferredtype = null;
    }

    private static final Command CMD_MENU = new Command("Optionen", Command.SCREEN, 0);
    private static final Command CMD_BACK = new Command("Zurück", Command.BACK, 0);
    private static final Command CMD_SELECT = new Command("Auswahl", Command.OK, 0);
    private static Font CMDFONT = Font.getFont(Font.FONT_STATIC_TEXT);
    //private static int CMDFONTHEIGHT = CMDFONT.getHeight();
    private static int CMDBARHEIGHT = CMDFONT.getHeight()+2;
    
    static {
	//Globals.set("isblackberry", new Boolean(false));
	try {
	    // Siemens (S65)
	    Class.forName("com.siemens.mp.lcdui.Image");
	    KEYCODE_LEFT_SK = -1;
	    KEYCODE_RIGHT_SK = -4;
	} catch (ClassNotFoundException e1) {
	}
	
	try {
	    // Blackberry
	    Class.forName("net.rim.device.api.system.Application");
	    KEYCODE_LEFT_SK = 0x80000;
	    KEYCODE_RIGHT_SK = 0x1b0000;
	    //Globals.set("isblackberry", new Boolean(true));
	} catch (ClassNotFoundException e2) {
	}
    }
}

// Variables:
// mode:java
// coding:utf-8
// End:
