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
import mwt.Component;
import mwt.Skin;
import org.hecl.ClassCommandInfo;
import org.hecl.HeclException;
import org.hecl.IntThing;
import org.hecl.Interp;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.Thing;
import org.hecl.mwtmisc.HeclUtils;
import org.hecl.mwtgui.ext.HeclWindow;

/**
 *
 * @author donus
 */
public class WindowCmd extends ComponentCmd {

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

    public Thing cmdCode(Interp iterp, Thing[] argv) throws HeclException {
        Properties p = new Properties();
        p.setProps(argv, 1);
        Thing[] prop = p.getProps();
        HeclWindow win = new HeclWindow(0, 0, 0, 0);
        setPropertys(win, p, prop);
        return ObjectThing.create(win);
    }

    private Thing handlecmd(Interp ip, Object target, String subcmd, Thing[] argv, int i) throws HeclException {
        HeclWindow win = (HeclWindow) target;

        if (argv.length < 2) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "Object " + subcmd + " [arg...]");
        }

        if (subcmd.equals("cset")) {
            cset(win, argv);
        } else if (subcmd.equals("cget")) {
            return cget(win, argv);
        } else if (subcmd.equals("add")) {
            win.add((Component) ObjectThing.get(argv[2]));
        } else if (subcmd.equals("removechild")) {
            win.removeChild(IntThing.get(argv[2]));
        } else if (subcmd.equals("dialogclose")) {
            win.dialogClose();
        } else if (subcmd.equals("dialogopen")) {
            win.dialogOpen((HeclWindow) ObjectThing.get(argv[2]));
        } else if (subcmd.equals("setkeystate")) {
            win.setKeyState(IntThing.get(argv[2]),MwtWidgetInfo.toKeyState(argv[3]), HeclUtils.thing2bool(argv[4]));
        } else {
            throw new HeclException("Unknown command name: " + subcmd);
        }
        return null;
    }

    public void cset(HeclWindow win, Thing[] argv) throws HeclException {
        if (argv.length < 3) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "<hg> cset [arg]");
        }

        Properties p = new Properties();
        p.setProps(argv, 2);
        setPropertys(win, p, argv);
    }

    public Thing cget(HeclWindow win, Thing[] argv) throws HeclException {
        if (argv.length < 3) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "<hg> cget [arg]");
        }
        if (argv[2].toString().trim().equals("-dialog")) {
            return ObjectThing.create(win.getDialog());
        } else if (argv[2].toString().trim().equals("-focus")) {
            System.out.println("f " + win.getFocus());
            return ObjectThing.create(win.getFocus());
        } else if (argv[2].toString().trim().equals("-skin")) {
            if (4 != argv.length) {
                throw HeclException.createWrongNumArgsException(
                        argv, 4, "skin");
            }
            return ObjectThing.create(win.getSkin(MwtWidgetInfo.toButtonStyle(argv[3])));
        }
        return super.cget((Component) win, argv);
    }

    public void setPropertys(HeclWindow win, Properties p, Thing[] prop) throws HeclException {
        super.setPropertys(win, p, prop);
        for (int i = 0; i < prop.length; i++) {
            if (prop[i].toString().trim().equals("-defaultskin")) {
                Vector v = ListThing.get(p.getProp("-defaultskin"));
                if (2 != v.size()) {
                    throw new HeclException("-defaultskin property, expect a list of {skin window_style} pair");
                }
                Skin sk = (Skin) ObjectThing.get((Thing) v.elementAt(0));
                HeclWindow.setDefaultSkin(MwtWidgetInfo.toWindowSyle((Thing) v.elementAt(1)), sk);
            } else if (prop[i].toString().trim().equals("-focus")) {
                win.setFocus((Component) ObjectThing.get(p.getProp("-focus")));
            } else if (prop[i].toString().trim().equals("-skin")) {
                Vector v = ListThing.get(p.getProp("-skin"));
                if (2 != v.size()) {
                    throw new HeclException("-skin property, expect a list of {skin window_style} pair");
                }
                Skin sk = (Skin) ObjectThing.get((Thing) v.elementAt(0));
                win.setSkin(MwtWidgetInfo.toWindowSyle((Thing) v.elementAt(1)), sk);
            } else if (prop[i].toString().trim().equals("-focusfirst")) {
                win.setFocusFirst();
            } else if (prop[i].toString().trim().equals("-focusnext")) {
                win.setFocusNext();
            } else if (prop[i].toString().trim().equals("-focusprev")) {
                win.setFocusPrevious();
            }
        }
    }

    public static void load(Interp ip) {
        ip.addCommand(CMDNAME, cmd);
        ip.addClassCmd(HeclWindow.class, cmd);
    }

    public static void unload(Interp ip) {
        ip.removeCommand(CMDNAME);
        ip.removeClassCmd(HeclWindow.class);
    }
    private static WindowCmd cmd = new WindowCmd();
    private static final String CMDNAME = "mwt.window";
}
