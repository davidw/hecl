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

import mwt.Component;
import org.hecl.ClassCommandInfo;
import org.hecl.HeclException;
import org.hecl.IntThing;
import org.hecl.Interp;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.Thing;
import org.hecl.mwtgui.ext.HeclWindow;
import org.hecl.mwtgui.ext.Scroller;

/**
 *
 * @author donus
 */
public class ScrollerCmd extends WindowCmd {

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

    public Thing cmdCode(Interp ip, Thing[] argv) throws HeclException {
        Properties p = new Properties();
        p.setProps(argv, 1);
        Thing[] prop = p.getProps();
        Scroller scroller = new Scroller(0, 0, 0, 0);
        setPropertys(scroller, p, prop);
        return ObjectThing.create(scroller);
    }

    private Thing handlecmd(Interp ip, Object target, String subcmd, Thing[] argv, int i) throws HeclException {
        Scroller scroller = (Scroller) target;

        if (argv.length < 2) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "Object " + subcmd + " [arg...]");
        }

        if (subcmd.equals("cset")) {
            cset(scroller, argv);
        } else if (subcmd.equals("cget")) {
            return cget(scroller, argv);
        } else if (subcmd.equals("add")) {
            scroller.add((Component) ObjectThing.get(argv[2]));
        } else if (subcmd.equals("remove")) {
            scroller.remove(IntThing.get(argv[2]));
        }

        return null;
    }

    public static void load(Interp ip) {
        ip.addCommand(CMDNAME, cmd);
        ip.addClassCmd(Scroller.class, cmd);
    }

    public static void unload(Interp ip) {
        ip.removeCommand(CMDNAME);
        ip.removeClassCmd(Scroller.class);
    }
    private static ScrollerCmd cmd = new ScrollerCmd();
    private static final String CMDNAME = "mwt.scroller";

    private void cset(Scroller scroller, Thing[] argv) throws HeclException {
        if (argv.length < 3) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "<hg> cset [arg]");
        }
        Properties p = new Properties();
        p.setProps(argv, 2);
        setPropertys(scroller, p, argv);
    }

    private Thing cget(Scroller scroller, Thing[] argv) throws HeclException {
        if (argv.length < 3) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "<hg> cget [arg]");
        }

        if (argv[2].toString().trim().equals("-vertical")) {
            return IntThing.create(scroller.getVertical());
        } else if (argv[2].toString().trim().equals("-horizontal")) {
            return IntThing.create(scroller.getHorizontal());
        } else if (argv[2].toString().trim().equals("-xscroll")) {
            return IntThing.create(scroller.getXScroll());
        } else if (argv[2].toString().trim().equals("-yscroll")) {
            return IntThing.create(scroller.getYScroll());
        } else if (argv[2].toString().trim().equals("-on")) {
            return IntThing.create(scroller.on);
        }
        return super.cget((HeclWindow) scroller, argv);
    }

    private void setPropertys(Scroller scroller, Properties p, Thing[] prop) throws HeclException {
        super.setPropertys((HeclWindow) scroller, p, prop);
        for (int i = 0; i < prop.length; i++) {
            if (prop[i].toString().trim().equals("-vertical")) {
                scroller.setVertical(IntThing.get(p.getProp("-vertical")));
            }
            if (prop[i].toString().trim().equals("-horizontal")) {
                scroller.setHorizontal(IntThing.get(p.getProp("-horizontal")));
            }
            if (prop[i].toString().trim().equals("-xscroll")) {
                scroller.setXScroll(IntThing.get(p.getProp("-xscroll")));
            }
            if (prop[i].toString().trim().equals("-yscroll")) {
                scroller.setYScroll(IntThing.get(p.getProp("-yscroll")));
            }
        }
    }
}
