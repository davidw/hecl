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
import mwt.Skin;
import org.hecl.ClassCommand;
import org.hecl.ClassCommandInfo;
import org.hecl.HeclException;
import org.hecl.IntThing;
import org.hecl.Interp;
import org.hecl.ListThing;
import org.hecl.ObjectThing;
import org.hecl.Thing;

/**
 *
 * @author donus
 */
public class SkinCmd implements ClassCommand, org.hecl.Command {

    public Thing method(Interp ip, ClassCommandInfo conetxt, Thing[] argv) throws HeclException {
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
        if (argv.length == 2) {
            Vector v = ListThing.get(argv[1]);
            int[] colors = new int[v.size()];
            for (int i = 0; i < v.size(); i++) {
                colors[i] = Integer.parseInt(((Thing) v.elementAt(i)).toString(), 16);
            }
            Skin skin = new Skin(colors);
            return ObjectThing.create(skin);
        } else if (argv.length == 3) {
            Vector v = ListThing.get(argv[1]);
            int newSize = IntThing.get(argv[2]);
            Image[] img = new Image[9];
            for (int i = 0; i < v.size(); i++) {
                img[i] = (Image) ObjectThing.get((Thing) v.elementAt(i));
            }
            return ObjectThing.create(new Skin(img, newSize));
        } else {
            throw HeclException.createWrongNumArgsException(argv, 2,
                    "mwt.skin expect either <color[]> or <image[], size>");
        }
    }

    private Thing handlecmd(Interp ip, Object target, String subcmd, Thing[] argv, int i) throws HeclException {
        Skin skin = (Skin) target;

        if (argv.length < 2) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "Object " + subcmd + " [arg...]");
        }

        return null;
    }

    public static Skin initSkin(String prefix, int skinSize) {
        Image[] si = new Image[9];
        for (int i = 0; i < 9; i++) {
            try {
                si[i] = Image.createImage("/" + prefix + i + ".png");
            } catch (Exception e) {
                System.out.println("Unknown Skin File " + "/skin" + i + ".png");
            }
        }
        return new Skin(si, skinSize);
    }

    public static void load(Interp ip) {
        ip.addCommand(CMDNAME, cmd);
        ip.addClassCmd(Skin.class, cmd);
    }

    public static void unload(Interp ip) {
        ip.removeCommand(CMDNAME);
        ip.removeClassCmd(Skin.class);
    }
    private static SkinCmd cmd = new SkinCmd();
    private static final String CMDNAME = "mwt.skin";
}
