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
import mwt.EventListener;
import mwt.Font;
import org.hecl.ClassCommandInfo;
import org.hecl.HeclException;
import org.hecl.IntThing;
import org.hecl.Interp;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.Thing;
import org.hecl.mwtgui.ext.TextBox;

/**
 *
 * @author donus
 */
public class TextBoxCmd extends ButtonCmd {

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
        Font f = new Font(0xA1C632, Font.FACE_SYSTEM, Font.SIZE_MEDIUM, Font.STYLE_PLAIN);
        if (p.existsProp("-font")) {
            Vector v = ListThing.get(p.getProp("-font"));
            if (2 != v.size()) {
                throw new HeclException("-font property, expect a list of {button_style font} pair");
            }

            f = (Font) ObjectThing.get((Thing) v.elementAt(1));
        }
        int actionType = 0;
        if (p.existsProp("-actiontype")) {
            actionType = IntThing.get(p.getProp("-actiontype"));
        }

        TextBox textbox = new TextBox(0, 0, 0, 0, "", (EventListener) MwtManager.getManager(), actionType, f);
        setPropertys(textbox, p, prop);
        return ObjectThing.create(textbox);
    }

    private Thing handlecmd(Interp ip, Object target, String subcmd, Thing[] argv, int i) throws HeclException {
        TextBox textbox = (TextBox) target;

        if (argv.length < 2) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "Object " + subcmd + " [arg...]");
        }

        if (subcmd.equals("cset")) {
            cset(textbox, argv);
        } else if (subcmd.equals("cget")) {
            return cget(textbox, argv);
        }

        return null;
    }

    public static void load(Interp ip) {
        ip.addCommand(CMDNAME, cmd);
        ip.addClassCmd(TextBox.class, cmd);
    }

    public static void unload(Interp ip) {
        ip.removeCommand(CMDNAME);
        ip.removeClassCmd(TextBox.class);
    }
    private static TextBoxCmd cmd = new TextBoxCmd();
    private static final String CMDNAME = "mwt.textbox";

    private void cset(TextBox textbox, Thing[] argv) throws HeclException {
        if (argv.length < 3) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "<hg> cset [arg]");
        }
        Properties p = new Properties();
        p.setProps(argv, 2);
        setPropertys(textbox, p, argv);
    }

    private Thing cget(TextBox textbox, Thing[] argv) throws HeclException {
        if (argv.length < 3) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "<hg> cget [arg]");
        }
        
        if (argv[2].toString().trim().equals("-scheduletime")) {
            return IntThing.create(textbox.getTimerScheduleTime());
        }
        return super.cget((Button) textbox, argv);
    }

    private void setPropertys(TextBox textbox, Properties p, Thing[] prop) throws HeclException {
        super.setPropertys(textbox, p, prop);
        for (int i = 0; i < prop.length; i++) {
            if (prop[i].toString().trim().equals("-cursorcolor")) {
                Vector v = ListThing.get(p.getProp("-cursorcolor"));
                int[] color = new int[v.size()];
                for (int j = 0; j < v.size(); j++) {
                    color[j] = Integer.parseInt(((Thing) v.elementAt(j)).toString(), 16);
                }
                textbox.setCursorColor(color);
            }
            if (prop[i].toString().trim().equals("-scheduletime")) {
                textbox.setScheduleTime(IntThing.get(p.getProp("-scheduletime")));
            }

        }
    }
}

