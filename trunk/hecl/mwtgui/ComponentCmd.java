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
import org.hecl.ClassCommand;
import org.hecl.ClassCommandInfo;
import org.hecl.HeclException;
import org.hecl.IntThing;
import org.hecl.Interp;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.Thing;
import org.hecl.mwtmisc.HeclUtils;

/**
 *
 * @author donus
 */
public class ComponentCmd implements ClassCommand, org.hecl.Command {

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
        boolean isContainer = false;
        if (p.existsProp("-container")) {
            isContainer = HeclUtils.thing2bool(p.getProp("-container"));
        }
                
        Component component = new Component(0, 0, 0, 0, isContainer);
        setPropertys(component, p, prop);
        return ObjectThing.create(component);
    }

    private Thing handlecmd(Interp ip, Object target, String subcmd, Thing[] argv, int i) throws HeclException {
        Component component = (Component) target;

        if (argv.length < 2) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "Object " + subcmd + " [arg...]");
        }
        if (subcmd.equals("cset")) {
            cset(component, argv);
        } else if (subcmd.equals("cget")) {
            return cget(component, argv);
        } else if (subcmd.equals("add")) {
            component.add((Component) ObjectThing.get(argv[2]));
        } else if (subcmd.equals("removechild")) {
            component.removeChild(IntThing.get(argv[2]));
        } 
        return null;
    }

    private void cset(Component component, Thing[] argv) throws HeclException {
        if (argv.length < 3) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "<hg> cset [arg]");
        }
        Properties p = new Properties();
        p.setProps(argv, 2);
        setPropertys(component, p, argv);
    }

    public void setPropertys(Component component, Properties p, Thing[] prop) throws HeclException {
        for (int i = 0; i < prop.length; i++) {
            if (prop[i].toString().trim().equals("-doublebuffered")) {
                component.setDoubleBuffered(HeclUtils.thing2bool(p.getProp("-doublebuffered")));
            } else if (prop[i].toString().trim().equals("-enable")) {
                component.setEnabled(HeclUtils.thing2bool(p.getProp("-enable")));
            } else if (prop[i].toString().trim().equals("-focusable")) {
                component.setFocusable(HeclUtils.thing2bool(p.getProp("-focusable")));
            } else if (prop[i].toString().trim().equals("-height")) {
                component.setHeight(IntThing.get(p.getProp("-height")));
            } else if (prop[i].toString().trim().equals("-id")) {
                component.setId((p.getProp("-id").toString()));
            } else if (prop[i].toString().trim().equals("-visible")) {
                component.setVisible(HeclUtils.thing2bool(p.getProp("-visible")));
            } else if (prop[i].toString().trim().equals("-width")) {
                component.setWidth(IntThing.get(p.getProp("-width")));
            } else if (prop[i].toString().trim().equals("-x")) {
                component.setX(IntThing.get(p.getProp("-x")));
            } else if (prop[i].toString().trim().equals("-y")) {
                component.setY(IntThing.get(p.getProp("-y")));
            }
        }
    }

    protected Thing cget(Component component, Thing[] argv) throws HeclException {
        if (argv.length < 3) {
            throw HeclException.createWrongNumArgsException(argv, 3,
                    "<hg> cget [arg]");        }
        if (argv[2].toString().trim().equals("-acceptsfocus")) {
            return IntThing.create(component.acceptsFocus());
        } else if (argv[2].toString().trim().equals("-child")) {
            if (argv.length == 4) {
                return ObjectThing.create(component.getChild(IntThing.get(argv[3])));
            } else if (argv.length == 5) {
                 return ObjectThing.create(component.getChild(argv[3].toString(), HeclUtils.thing2bool(argv[4])));
            } else {
                throw new HeclException("mwt.Component propertie has wrong number of parameter! " + argv[2].toString());
            }
        } else if (argv[2].toString().trim().equals("-childindex")) {
            return IntThing.create(component.getChild((Component) ObjectThing.get(argv[3])));
        } else if (argv[2].toString().trim().equals("-childcount")) {
            return IntThing.create(component.getChildCount());
        } else if (argv[2].toString().trim().equals("-height")) {
            return IntThing.create(component.getHeight());
        } else if (argv[2].toString().trim().equals("-id")) {
            return new Thing(component.getId());
        } else if (argv[2].toString().trim().equals("-parent")) {
            return ObjectThing.create(component.getParent());
        } else if (argv[2].toString().trim().equals("-width")) {
            return IntThing.create(component.getWidth());
        } else if (argv[2].toString().trim().equals("-x")) {
            return IntThing.create(component.getX());
        } else if (argv[2].toString().trim().equals("-y")) {
            return IntThing.create(component.getY());
        } else if (argv[2].toString().trim().equals("-iscontainer")) {
            return IntThing.create(component.isContainer());
        } else if (argv[2].toString().trim().equals("-isdoublebuffered")) {
            return new Thing(String.valueOf(component.isDoubleBuffered()));
        } else if (argv[2].toString().trim().equals("-isenabled")) {
            return new Thing(String.valueOf(component.isEnabled()));
        } else if (argv[2].toString().trim().equals("-isfocusable")) {
            return new Thing(String.valueOf(component.isFocusable()));
        } else if (argv[2].toString().trim().equals("-ishierarchyenabled")) {
            return new Thing(String.valueOf(component.isHierarchyEnabled()));
        } else if (argv[2].toString().trim().equals("-isHierarchyVisible")) {
            return new Thing(String.valueOf(component.isHierarchyVisible()));
        } else if (argv[2].toString().trim().equals("-isVisible")) {
            return new Thing(String.valueOf(component.isVisible()));
        } else {
            throw new HeclException("Unknown mwt.Component propertie! " + argv[2].toString());
        }
    }

    public static void load(Interp ip) {
        ip.addCommand(CMDNAME, cmd);
        ip.addClassCmd(Component.class, cmd);
    }

    public static void unload(Interp ip) {
        ip.removeCommand(CMDNAME);
        ip.removeClassCmd(Component.class);
    }
    private static ComponentCmd cmd = new ComponentCmd();
    private static final String CMDNAME = "mwt.component";
}
