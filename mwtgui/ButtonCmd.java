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
import mwt.Button;
import mwt.Component;
import mwt.EventListener;
import mwt.Font;
import mwt.Skin;
import org.hecl.ClassCommandInfo;
import org.hecl.HeclException;
import org.hecl.IntThing;
import org.hecl.Interp;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.Thing;

/**
 *
 * @author donus
 */
public class ButtonCmd extends ComponentCmd {

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
        int actionType = 0;
        if (p.existsProp("-actiontype")) {
            actionType = IntThing.get(p.getProp("-actiontype"));
        }
        Button button = new Button(0, 0, 0, 0, "", (EventListener) MwtManager.getManager(), actionType);
        setPropertys(button, p, prop);
        return ObjectThing.create(button);
    }

    private Thing handlecmd(Interp ip, Object target, String subcmd, Thing[] argv, int i) throws HeclException {
        Button button = (Button) target;

        if (argv.length < 2) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "Object " + subcmd + " [arg...]");
        }

        if (subcmd.equals("cset")) {
            cset(button, argv);
        } else if (subcmd.equals("cget")) {
            return cget(button, argv);
        }

        return null;
    }

    public static void load(Interp ip) {
        ip.addCommand(CMDNAME, cmd);
        ip.addClassCmd(Button.class, cmd);
    }

    public static void unload(Interp ip) {
        ip.removeCommand(CMDNAME);
        ip.removeClassCmd(Button.class);
    }
    private static ButtonCmd cmd = new ButtonCmd();
    private static final String CMDNAME = "mwt.button";

    protected void cset(Button button, Thing[] argv) throws HeclException {
        if (argv.length < 3) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "<hg> cset [arg]");
        }
        Properties p = new Properties();
        p.setProps(argv, 2);
        setPropertys(button, p, argv);
    }

    protected Thing cget(Button button, Thing[] argv) throws HeclException {
        if (argv.length < 3) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "<hg> cget [arg]");
        }
        if (argv[2].toString().trim().equals("-defaultfont")) {
            if (4 != argv.length) {
                throw HeclException.createWrongNumArgsException(
                        argv, 4, "font");
            }
            return ObjectThing.create(Button.getDefaultFont(MwtWidgetInfo.toButtonStyle(argv[3])));
        } else if (argv[2].toString().trim().equals("-font")) {
            if (4 != argv.length) {
                throw HeclException.createWrongNumArgsException(
                        argv, 4, "font");
            }
            return ObjectThing.create(button.getFont(MwtWidgetInfo.toButtonStyle(argv[3])));
        } else if (argv[2].toString().trim().equals("-text")) {
            return new Thing(button.getText());
        } else if (argv[2].toString().trim().equals("-align")) {
            return MwtWidgetInfo.fromComponentAlign(button.getTextAlign());
        } else if (argv[2].toString().trim().equals("-skin")) {
            if (4 != argv.length) {
                throw HeclException.createWrongNumArgsException(
                        argv, 4, "skin");
            }
            return ObjectThing.create(button.getSkin(MwtWidgetInfo.toButtonStyle(argv[3])));
        }
        return super.cget((Component) button, argv);
    }

    protected void setPropertys(Button button, Properties p, Thing[] prop) throws HeclException {
        super.setPropertys(button, p, prop);
        for (int i = 0; i < prop.length; i++) {
            if (prop[i].toString().trim().equals("-text")) {
                button.setText(p.getProp("-text").toString());
            } else if (prop[i].toString().trim().equals("-align")) {
                button.setTextAlign(MwtWidgetInfo.toComponentAlign(p.getProp("-align")));
            } else if (prop[i].toString().trim().equals("-font")) {
                Vector v = ListThing.get(p.getProp("-font"));
                if (2 != v.size()) {
                    throw new HeclException("-font property, expect a list of {button_style font} pair");
                }
                button.setFont(MwtWidgetInfo.toButtonStyle((Thing) v.elementAt(0)), (Font) ObjectThing.get((Thing) v.elementAt(1)));
            } else if (prop[i].toString().trim().equals("-defaultfont")) {
                Vector v = ListThing.get(p.getProp("-font"));
                if (2 != v.size()) {
                    throw new HeclException("-font property, expect a list of {button_style font} pair");
                }
                Button.setDefaultFont(MwtWidgetInfo.toButtonStyle((Thing) v.elementAt(0)), (Font) ObjectThing.get((Thing) v.elementAt(1)));
            } else if (prop[i].toString().trim().equals("-skin")) {
                Vector v = ListThing.get(p.getProp("-skin"));
                if (2 != v.size()) {
                    throw new HeclException("-skin property, expect a list of {skin window_style} pair");
                }
                Skin sk = (Skin) ObjectThing.get((Thing) v.elementAt(0));
                button.setSkin(MwtWidgetInfo.toButtonStyle((Thing) v.elementAt(1)), sk);
            }
        }
    }
}
