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

import javax.microedition.lcdui.Image;
import mwt.Button;
import mwt.EventListener;
import org.hecl.ClassCommandInfo;
import org.hecl.HeclException;
import org.hecl.IntThing;
import org.hecl.Interp;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.Thing;
import org.hecl.mwtgui.ext.ImageButton;

/**
 *
 * @author donus
 */
public class ImageButtonCmd extends ButtonCmd {
    
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
        ImageButton imagebutton = new ImageButton(0, 0, 0, 0, null, (EventListener) MwtManager.getManager(), actionType);
        setPropertys(imagebutton, p, prop);
        return ObjectThing.create(imagebutton);
    }
 
    private Thing handlecmd(Interp ip, Object target, String subcmd, Thing[] argv, int i) throws HeclException {
        ImageButton imagebutton = (ImageButton) target;

        if (argv.length < 2) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "Object " + subcmd + " [arg...]");
        }
				System.out.println(subcmd);
        if (subcmd.equals("cset")) {
            cset(imagebutton, argv);
        } else if (subcmd.equals("cget")) {
            return cget(imagebutton, argv);
        }
        return null;
    }

     protected void cset(ImageButton button, Thing[] argv) throws HeclException {
        if (argv.length < 3) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "<hg> cset [arg]");
        }
        Properties p = new Properties();
        p.setProps(argv, 2);
        setPropertys(button, p, argv);
    }

    protected Thing cget(ImageButton imagebutton, Thing[] argv) throws HeclException {
        if (argv.length < 3) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "<hg> cget [arg]");
        }
        for (int i=0; i<argv.length; i++) {
					System.out.println(argv[i]);
				} 
        if (argv[2].toString().trim().equals("-image")) {
            return ObjectThing.create(imagebutton.getImage());
        }
        return super.cget((Button) imagebutton, argv);
    }
    
     protected void setPropertys(ImageButton imagebutton, Properties p, Thing[] prop) throws HeclException {
        super.setPropertys(imagebutton, p, prop);
        for (int i = 0; i < prop.length; i++) {
            if (prop[i].toString().trim().equals("-image")) {
                imagebutton.setImage((Image)ObjectThing.get(p.getProp("-image")));
            }
        }
     }
    public static void load(Interp ip) {
        ip.addCommand(CMDNAME, cmd);
        ip.addClassCmd(ImageButton.class, cmd);
    }

    public static void unload(Interp ip) {
        ip.removeCommand(CMDNAME);
        ip.removeClassCmd(ImageButton.class);
    }
    private static ImageButtonCmd cmd = new ImageButtonCmd();
    private static final String CMDNAME = "mwt.imagebutton";

}
