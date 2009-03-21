/*
 * Copyright 2008-2009 Martin Mainusch 
 * 
 * Author: Martin Mainusch donus@gmx.net
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

package org.hecl.mwtgui;

import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;
import mwt.Button;
import mwt.Component;
import org.hecl.ClassCommand;
import org.hecl.ClassCommandInfo;
import org.hecl.HeclException;
import org.hecl.IntThing;
import org.hecl.Interp;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.StringThing;
import org.hecl.Thing;
//import org.hecl.hecltk.graphics.Drawable;
import org.hecl.mwtmisc.HeclUtils;
import org.hecl.mwtgui.ext.HeclWindow;

/**
 *
 * @author donus
 */
public class MwtManagerCmd implements EventGetWay, ClassCommand, org.hecl.Command {

    static protected MIDlet themidlet = null;
    private Interp ip;
    Thread th;

    public Thing method(Interp ip, ClassCommandInfo context, Thing[] argv) throws HeclException {
        if (argv.length > 1) {
            String subcmd = argv[1].toString().toLowerCase();
            Object target = ObjectThing.get(argv[0]);
            return handlecmd(ip, target, subcmd, argv, 2);
        } else {
            throw HeclException.createWrongNumArgsException(argv, 2,
                    "Object method [arg...]");
        }
    }

    public Thing cmdCode(Interp interp, Thing[] argv) throws HeclException {
        ip = interp;
        MwtManager htm = MwtManager.getManager();
        htm.setSelectionCmd(this);
        getDisplay().setCurrent(htm);
        return ObjectThing.create(htm);
    }

    private Display getDisplay() {
        return Display.getDisplay(themidlet);
    }

    private Thing handlecmd(Interp ip, Object target, String subcmd, Thing[] argv, int i) throws HeclException {
        MwtManager htm = (MwtManager) target;

        if (argv.length < 2) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "Object " + subcmd + " [arg...]");
        }

        if (subcmd.equals("cset")) {
        //cset(gp, argv);
        } else if (subcmd.equals("main")) {
            htm.main = (HeclWindow) ObjectThing.get(argv[2]);
        } else if (subcmd.equals("add")) {
            htm.main.add((Component) ObjectThing.get(argv[2]));
        } else if (subcmd.equals("getmain")) {
            return ObjectThing.create(htm.main);
        } else if (subcmd.equals("width")) {
            return new Thing(Integer.toString(htm.getWidth()));
        } else if (subcmd.equals("height")) {
            return new Thing(Integer.toString(htm.getHeight()));
        } else if (subcmd.equals("run")) {
            th = new Thread(htm);
            th.start();
        } else if (subcmd.equals("dialogopen")) {
            htm.main.dialogOpen((HeclWindow) ObjectThing.get(argv[2]));
        } else if (subcmd.equals("dialogclose")) {
            htm.main.dialogClose();
        } else if (subcmd.equals("stop")) {
            htm.setExit(HeclUtils.thing2bool(argv[2]));
        } else if (subcmd.equals("initfont")) {
            return ObjectThing.create(FontCmd.initFont(argv[2].toString()));
        } else if (subcmd.equals("initskin")) {
            if (4 != argv.length) {
                throw HeclException.createWrongNumArgsException(
                        argv, 4, "initskin <prefix> <size>");
            }
            return ObjectThing.create(SkinCmd.initSkin(argv[2].toString(), IntThing.get(argv[3])));
        } else if (subcmd.equals("initadvancefont")) {   
            if (5 != argv.length) {
                throw HeclException.createWrongNumArgsException(
                        argv, 5, "initadvancefont <imagename> <charset> <width>");
            }
             Vector v = ListThing.get(argv[4]);
                int[] widths = new int[v.size()];
                for (int j = 0; j < v.size(); j++) {
                    widths[j] = Integer.parseInt(((Thing) v.elementAt(j)).toString());
                }
            return ObjectThing.create(FontCmd.initFontAdvance(argv[2].toString(),argv[3].toString(), widths)); 
        } else if (subcmd.equals("bgimage")) {
            htm.setBgImage((Image) ObjectThing.get(argv[2]));
        } else if (subcmd.equals("repaint")) {
            htm.repaint();
        }
        return null;
    }

    public void execHeclCmd(String sender) throws HeclException {
        ip.eval(new Thing(sender));
    }

    public static void load(Interp ip, MIDlet m) {
        themidlet = m;
        ip.addCommand(CMDNAME, cmd);
        ip.addClassCmd(MwtManager.class, cmd);
    }

    public static void unload(Interp ip) {
        ip.removeCommand(CMDNAME);
        ip.removeClassCmd(MwtManager.class);
    }
    private static MwtManagerCmd cmd = new MwtManagerCmd();
    private static final String CMDNAME = "mwt.manager";

    public void execHecl(Graphics g) throws HeclException {
//        Vector v = new Vector();
//        v.addElement(StringThing.create("paint"));
//        v.addElement(ObjectThing.create(new Drawable(g, g.getClipWidth(), g.getClipHeight())));
//        ip.evalIdle(ListThing.create(v));
    }

    public void keyPressed(int keyCode) throws HeclException {
        ip.eval(new Thing("keypressed " + keyCode));
    }

    public void keyReleased(int keyCode) throws HeclException {
        ip.eval(new Thing("keyreleased " + keyCode));
    }

    public void execHeclCmd(int arg0, Component arg1, Object[] arg2) {
        Vector v = new Vector();
        v.addElement(StringThing.create("processevent"));
        v.addElement(IntThing.create(arg0));
        v.addElement(ObjectThing.create(arg1));
        ip.evalIdle(ListThing.create(v));
    }

    public void execHeclCmd(int arg0, Button arg1, Object[] arg2) {
        Vector v = new Vector();
        v.addElement(StringThing.create("processevent"));
        v.addElement(IntThing.create(arg0));
        v.addElement(ObjectThing.create(arg1));
        ip.evalIdle(ListThing.create(v));
    }
}
