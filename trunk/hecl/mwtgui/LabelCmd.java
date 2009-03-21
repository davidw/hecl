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
import javax.microedition.lcdui.Image;
import mwt.Component;
import mwt.Font;
import mwt.Label;
import org.hecl.ClassCommandInfo;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.Thing;

/**
 *
 * @author donus
 */
public class LabelCmd extends ComponentCmd {

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
        Properties p = new Properties();
        p.setProps(argv, 1);

        Thing[] prop = p.getProps();
        Label label = new Label(0, 0, 0, 0, "");
        setPropertys(label, p, prop);
        return ObjectThing.create(label);
    }

    private Thing handlecmd(Interp ip, Object target, String subcmd, Thing[] argv, int i) throws HeclException {
        Label label = (Label) target;

        if (argv.length < 2) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "Object " + subcmd + " [arg...]");
        }
        if (subcmd.equals("cset")) {
            cset(label, argv);
        } else if (subcmd.equals("cget")) {
            return cget(label, argv);
        }

        return null;
    }

    private void setPropertys(Label label, Properties p, Thing[] prop) throws HeclException {
        super.setPropertys(label, p, prop);
        for (int i = 0; i < prop.length; i++) {
            if (prop[i].toString().trim().equals("-text")) {
                label.setText(p.getProp("-text").toString());
            } else if (prop[i].toString().trim().equals("-image")) {
                label.setImage((Image) ObjectThing.get(p.getProp("-image")));
            } else if (prop[i].toString().trim().equals("-align")) {
                label.setTextAlign(MwtWidgetInfo.toComponentAlign(p.getProp("-align")));
            } else if (prop[i].toString().trim().equals("-font")) {
                Vector v = ListThing.get(p.getProp("-font"));
                if (2 != v.size()) {
                    throw new HeclException("-font property, expect a list of {button_style font} pair");
                }
                label.setFont(MwtWidgetInfo.toLabelStyle((Thing) v.elementAt(0)), (Font) ObjectThing.get((Thing) v.elementAt(1)));
            } else if (prop[i].toString().trim().equals("-defaultfont")) {
                Vector v = ListThing.get(p.getProp("-font"));
                if (2 != v.size()) {
                    throw new HeclException("-font property, expect a list of {button_style font} pair");
                }
                Label.setDefaultFont(MwtWidgetInfo.toLabelStyle((Thing) v.elementAt(0)), (Font) ObjectThing.get((Thing) v.elementAt(1)));
            }
        }
    }

    private void cset(Label label, Thing[] argv) throws HeclException {
        if (argv.length < 3) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "<hg> cset [arg]");
        }
        Properties p = new Properties();
        p.setProps(argv, 2);
        setPropertys(label, p, argv);
    }

    private Thing cget(Label label, Thing[] argv) throws HeclException {
        if (argv.length < 3) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "<hg> cget [arg]");
        }
        if (argv[2].toString().trim().equals("-image")) {
            return ObjectThing.create(label.getImage());
        } else if (argv[2].toString().trim().equals("-defaultfont")) {
            if (4 != argv.length) {
                throw HeclException.createWrongNumArgsException(
                        argv, 4, "font");
            }
            return ObjectThing.create(Label.getDefaultFont(MwtWidgetInfo.toLabelStyle(argv[3])));
        } else if (argv[2].toString().trim().equals("-font")) {
            if (4 != argv.length) {
                throw HeclException.createWrongNumArgsException(
                        argv, 4, "font");
            }
            return ObjectThing.create(label.getFont(MwtWidgetInfo.toLabelStyle(argv[3])));
        } else if (argv[2].toString().trim().equals("-text")) {
            return new Thing(label.getText());
        } else if (argv[2].toString().trim().equals("-align")) {
            return MwtWidgetInfo.fromComponentAlign(label.getTextAlign());
        }
        return super.cget((Component) label, argv);
    }

    public static void load(Interp ip) {
        ip.addCommand(CMDNAME, cmd);
        ip.addClassCmd(Label.class, cmd);
    }

    public static void unload(Interp ip) {
        ip.removeCommand(CMDNAME);
        ip.removeClassCmd(Label.class);
    }
    private static LabelCmd cmd = new LabelCmd();
    private static final String CMDNAME = "mwt.label";
}
