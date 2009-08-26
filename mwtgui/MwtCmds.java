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

import javax.microedition.midlet.MIDlet;
import org.hecl.HeclException;
import org.hecl.Interp;

/**
 *
 * @author donus
 */
public class MwtCmds {

    public static void load(Interp interp, MIDlet midelt) throws HeclException {
        org.hecl.mwtgui.MwtManagerCmd.load(interp, midelt);
        org.hecl.mwtgui.ComponentCmd.load(interp);
        org.hecl.mwtgui.WindowCmd.load(interp);
        org.hecl.mwtgui.ButtonCmd.load(interp);
        org.hecl.mwtgui.FontCmd.load(interp);
        org.hecl.mwtgui.SkinCmd.load(interp);
        org.hecl.mwtgui.LabelCmd.load(interp);
        org.hecl.mwtgui.ScrollerCmd.load(interp);
        org.hecl.mwtgui.TextBoxCmd.load(interp);
        org.hecl.mwtgui.ImageButtonCmd.load(interp);
    }

    public static void unload(Interp interp) throws HeclException {
        org.hecl.mwtgui.MwtManagerCmd.unload(interp);
        org.hecl.mwtgui.ComponentCmd.unload(interp);
        org.hecl.mwtgui.WindowCmd.unload(interp);
        org.hecl.mwtgui.ButtonCmd.unload(interp);
        org.hecl.mwtgui.FontCmd.unload(interp);
        org.hecl.mwtgui.SkinCmd.unload(interp);
        org.hecl.mwtgui.LabelCmd.unload(interp);
        org.hecl.mwtgui.ScrollerCmd.unload(interp);
        org.hecl.mwtgui.TextBoxCmd.unload(interp);
        org.hecl.mwtgui.ImageButtonCmd.unload(interp);
    }
}
