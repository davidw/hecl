/* Copyright 2007 David N. Welton - DedaSys LLC - http://www.dedasys.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package org.hecl.android;

import android.app.Activity;

import android.widget.Button;

import org.hecl.ClassCommand;
import org.hecl.ClassCommandInfo;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ObjectThing;
import org.hecl.Properties;
import org.hecl.StringThing;
import org.hecl.Thing;


public class ButtonCmd implements ClassCommand, org.hecl.Command {
    private static ButtonCmd cmd = new ButtonCmd();
    private static final String CMDNAME = "button";
    private static Activity activity = null;

    public Thing cmdCode(Interp interp, Thing[] argv) throws HeclException {
	Button button = null;
	Properties p = new Properties();
	p.setProps(argv, 1);
	if (p.existsProp("-id")) {
	    button = (Button) activity.findViewById(IntThing.get(p.getProp("-id")));
	} else {
	    button = new Button(activity);
	}

	return ObjectThing.create(button);
    }

    private ButtonCmd() {}

    public Thing method(Interp ip, ClassCommandInfo context, Thing[] argv)
	throws HeclException {
	if(argv.length > 1) {
	    String subcmd = argv[1].toString().toLowerCase();
	    Object target = ObjectThing.get(argv[0]);



	    return new Thing("");

	}
	throw HeclException.createWrongNumArgsException(argv, 2, "Object method [arg...]");
    }

    public static void load(Interp ip, Activity a) {
	activity = a;
	ip.addCommand(CMDNAME, cmd);
	ip.addClassCmd(Button.class, cmd);
    }
    public static void unload(Interp ip) {
	activity = null;
	ip.removeCommand(CMDNAME);
	ip.removeClassCmd(ButtonCmd.class);
    }
}
