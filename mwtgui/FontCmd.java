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

import java.io.IOException;
import javax.microedition.lcdui.Image;
import mwt.Font;
import org.hecl.ClassCommand;
import org.hecl.ClassCommandInfo;
import org.hecl.HeclException;
import org.hecl.IntThing;
import org.hecl.Interp;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.Thing;

/**
 *
 * @author donus
 */
public class FontCmd implements ClassCommand, org.hecl.Command {

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

    public Thing cmdCode(Interp arg0, Thing[] argv) throws HeclException {
//        if (argv.length == 1) {
            Properties p = new Properties();
            p.setProps(argv, 1);
            Thing[] prop = p.getProps();
            Font font = new Font(0, 0, 0, 0);
            setPropertys(font, p, prop);
            return ObjectThing.create(font);
//        } else if (argv.length == 5) {
//            Image img = (Image) ObjectThing.get((argv[1]));
//            Vector v_char = ListThing.get(argv[2]);
//            char[] ch = new char[v_char.size()];
//            for(int i=0; i < v_char.size(); i++) {
//                ch[i] = ((Thing) v_char.elementAt(i)).toString().charAt(0);
//            }
//            
//            Vector v_widths = ListThing.get(argv[3]);
//            int[] widths = new int[v_widths.size()];
//            for(int i=0; i < v_widths.size(); i++) {
//                widths[i] = IntThing.get((Thing) v_widths.elementAt(i));
//            }
//            return ObjectThing.create(new Font(img, ch, widths, IntThing.get(argv[4])));
//            
//        } else if (argv.length == 4) {
//            Vector v_img = ListThing.get(argv[1]);
//            Image[] img = new Image[v_img.size()];
//            for(int i=0; i < v_img.size(); i++) {
//                img[i] =(Image) ObjectThing.get((Thing)v_img.elementAt(i));
//            }
//            Vector v_char = ListThing.get(argv[2]);
//            char[] ch = new char[v_char.size()];
//            for(int i=0; i < v_char.size(); i++) {
//                ch[i] = ((Thing) v_char.elementAt(i)).toString().charAt(0);
//            }
//            return ObjectThing.create(new Font(img, ch, IntThing.get(argv[3])));
//        } else {
//             throw HeclException.createWrongNumArgsException(argv, 2,
//                    "mwt.font");
//        }
    }

    private Thing handlecmd(Interp ip, Object target, String subcmd, Thing[] argv, int i) throws HeclException {
        Font font = (Font) target;
        if (argv.length < 2) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "Object " + subcmd + " [arg...]");
        }
        if (subcmd.equals("cset")) {
            cset(font, argv);
        } else if (subcmd.equals("cget")) {
            return cget(font, argv);
        } else {
            throw new HeclException("Unknown command name: " + subcmd);
        }
        return null;
    }

    private void cset(Font font, Thing[] argv) throws HeclException {
        if (argv.length < 3) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "<hg> cset [arg]");
        }

        Properties p = new Properties();
        p.setProps(argv, 2);
        setPropertys(font, p, argv);
    }

    private void setPropertys(Font font, Properties p, Thing[] prop) throws HeclException {
        for (int i = 0; i < prop.length; i++) {
            if (prop[i].toString().trim().equals("-color")) {
                font.setColor(Integer.parseInt(p.getProp("-color").toString(), 16));
            } else if (prop[i].toString().trim().equals("-size")) {
                font.setSize(MwtWidgetInfo.toFontSize(p.getProp("-size")));
            } else if (prop[i].toString().trim().equals("-face")) {
                font.setSize(MwtWidgetInfo.toFontFace(p.getProp("-face")));
            } else if (prop[i].toString().trim().equals("-style")) {
                font.setStyle(MwtWidgetInfo.toFontStyle(p.getProp("-syle")));
            }
        }
    }

    protected Thing cget(Font font, Thing[] argv) throws HeclException {
        if (argv.length < 3) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "<hg> cget [arg]");
        }
        if (argv[2].toString().trim().equals("-charsetlength")) {
            return IntThing.create(font.getCharsetLength());
        } else if (argv[2].toString().trim().equals("-color")) {
            return IntThing.create(font.getColor());
        } else if (argv[2].toString().trim().equals("-face")) {
            if (font.isBitmapFont()) {
                return IntThing.create(font.getFace());
            } else {
                return MwtWidgetInfo.fromFontFace(font.getFace());
            }
        } else if (argv[2].toString().trim().equals("-height")) {
            return IntThing.create(font.getHeight());
        } else if (argv[2].toString().trim().equals("-size")) {
            if (font.isBitmapFont()) {
                return IntThing.create(font.getSize());
            } else {
                return MwtWidgetInfo.fromFontSize(font.getSize());
            }
        } else if (argv[2].toString().trim().equals("-style")) {
            return MwtWidgetInfo.fromFontStyle(font.getStyle());
        } else if (argv[2].toString().trim().equals("-type")) {
            return MwtWidgetInfo.fromFontType(font.getType());
        } else if (argv[2].toString().trim().equals("-width")) {
            if (4 != argv.length) {
                throw HeclException.createWrongNumArgsException(
                        argv, 4, "string");
            }
            return IntThing.create(font.getWidth(argv[3].toString()));
        } else if (argv[2].toString().trim().equals("-isbitmapfont")) {
            return IntThing.create(font.isBitmapFont());
        } else {
            throw new HeclException("Unknown mwt.Font propertie! " + argv.toString());
        }
    }

    public static Font initFont(String charArray) throws HeclException {
        final char[] charset = charArray.toCharArray();
        final Image[] images = new Image[charset.length];
        for (int i = 0; i < charset.length; i++) {
            try {
                images[i] = Image.createImage("/" + ((int) charset[i]) + ".png");
            } catch (IOException e) {
                System.out.println("Unknown Font image file or wrong file format: " + e.getMessage());
            }
        }
        return new Font(images, charset, -3);
    }
    
      public static Font initFontAdvance(String imageName, String charArray, int[] widths) throws HeclException {
        final char[] charset = charArray.toCharArray();
        if(charset.length != widths.length) {
            throw new HeclException("wrong length of widths array.");
        }
        Image image = null;
        try {
            image = Image.createImage("/" + imageName);
        } catch (IOException e) {
            throw new HeclException("Unknown Font image file or wrong file format: " + e.getMessage());
        }
        return new Font(image, charset, widths, -3);
    }

    public static void load(Interp ip) {
        ip.addCommand(CMDNAME, cmd);
        ip.addClassCmd(Font.class, cmd);
    }

    public static void unload(Interp ip) {
        ip.removeCommand(CMDNAME);
        ip.removeClassCmd(Font.class);
    }
    private static FontCmd cmd = new FontCmd();
    private static final String CMDNAME = "mwt.font";
}
