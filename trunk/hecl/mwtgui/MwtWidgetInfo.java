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

import mwt.Button;
import mwt.Component;
import mwt.EventListener;
import mwt.Font;
import mwt.Label;
import mwt.Window;
import org.hecl.HeclException;
import org.hecl.Thing;

/**
 *
 * @author donus
 */
public class MwtWidgetInfo {

    //mwt.Component
    static String alignnames[] = {"bottom_center", "bottom_left", "bottom_right",
        "middle_center", "middle_left", "middle_right",
        "top_center", "top_left", "top_right"
    };
    static int alignvals[] = {Component.ALIGN_BOTTOM_CENTER, Component.ALIGN_BOTTOM_LEFT, Component.ALIGN_BOTTOM_RIGHT,
        Component.ALIGN_MIDDLE_CENTER, Component.ALIGN_MIDDLE_LEFT, Component.ALIGN_MIDDLE_RIGHT,
        Component.ALIGN_TOP_CENTER, Component.ALIGN_TOP_LEFT, Component.ALIGN_TOP_RIGHT
    };

    //mwt.Font
    static String fontsizenames[] = {"size_large", "size_medium", "size_small"};
    static int fontsizevals[] = {Font.SIZE_LARGE, Font.SIZE_MEDIUM, Font.SIZE_SMALL};
    static String fontstylenames[] = {"style_plain", "style_bold", "style_italic", "style_underlined"};
    static int fontstylevals[] = {Font.STYLE_PLAIN, Font.STYLE_BOLD, Font.STYLE_ITALIC, Font.STYLE_UNDERLINED};
    static String fontfacenames[] = {"face_system", "face_monospace", "face_proportional"};
    static int fontfacevals[] = {Font.FACE_SYSTEM, Font.FACE_MONOSPACE, Font.FACE_PROPORTIONAL};
    static String fonttypenames[] = {"type_system", "type_mapped", "type_strip"};
    static int fonttypevals[] = {Font.TYPE_SYSTEM, Font.TYPE_MAPPED, Font.TYPE_STRIP};
    //mwt.Button
    static String buttonstylenames[] = {"style_default", "style_disable", "style_focused", "style_pressed"};
    static int buttonstylevals[] = {Button.STYLE_DEFAULT, Button.STYLE_DISABLED, Button.STYLE_FOCUSED, Button.STYLE_PRESSED};
     //mwt.Label
    static String labelstylenames[] = {"style_default", "style_disable"};
    static int labelstylevals[] = {Label.STYLE_DEFAULT, Label.STYLE_DISABLED};
    //mwt.EventType
    static String eventtypenames[] = {"event_action", "event_undefined"};
    static int eventtypevals[] = {EventListener.EVENT_ACTION, EventListener.EVENT_UNDEFINED};
    static String windowstylenames[] = {"style_default", "style_disable"};
    static int windowstylevals[] = {Window.STYLE_DEFAULT, Window.STYLE_DISABLED};

    static String keystatenames[] = {"keystate_released", "keystate_pressed"};
    static int keystatevals[] = {Window.KEYSTATE_RELEASED, Window.KEYSTATE_PRESSED};
    
    public static int toComponentAlign(Thing t) throws HeclException {
        return t2int(t, alignnames, alignvals, "mwt.component align");
    }

    public static Thing fromComponentAlign(int t) throws HeclException {
        return int2t(t, alignnames, alignvals, "mwt.component align");
    }

    public static int toFontSize(Thing t) throws HeclException {
        return t2int(t, fontsizenames, fontsizevals, "mwt.font size");
    }

    public static Thing fromFontSize(int t) throws HeclException {
        return int2t(t, fontsizenames, fontstylevals, "mwt.font size");
    }

    public static int toFontStyle(Thing t) throws HeclException {
        return t2int(t, fontstylenames, fontstylevals, "mwt.font style");
    }

    public static Thing fromFontStyle(int t) throws HeclException {
        return int2t(t, fontstylenames, fontsizevals, "mwt.font style");
    }

    public static int toFontFace(Thing t) throws HeclException {
        return t2int(t, fontfacenames, fontfacevals, "mwt.font face");
    }

    public static Thing fromFontFace(int t) throws HeclException {
        return int2t(t, fontfacenames, fontfacevals, "mwt.font face");
    }

    public static int toFontType(Thing t) throws HeclException {
        return t2int(t, fonttypenames, fonttypevals, "mwt.font type");
    }

    public static Thing fromFontType(int t) throws HeclException {
        return int2t(t, fonttypenames, fonttypevals, "mwt.font type");
    }

    public static int toButtonStyle(Thing t) throws HeclException {
        return t2int(t, buttonstylenames, buttonstylevals, "mwt.button style");
    }

    public static Thing fromButtonStyle(int t) throws HeclException {
        return int2t(t, buttonstylenames, buttonstylevals, "mwt.button style");
    }
    
    public static int toLabelStyle(Thing t) throws HeclException {
        return t2int(t, labelstylenames, labelstylevals, "mwt.label style");
    }

    public static Thing fromLabelStyle(int t) throws HeclException {
        return int2t(t, labelstylenames, labelstylevals, "mwt.label style");
    }

    public static int toEventType(Thing t) throws HeclException {
        return t2int(t, eventtypenames, eventtypevals, "mwt.event type");
    }

    public static Thing fromEventType(int t) throws HeclException {
        return int2t(t, eventtypenames, eventtypevals, "mwt.event type");
    }

    public static int toWindowSyle(Thing t) throws HeclException {
        return t2int(t, windowstylenames, windowstylevals, "mwt.window style");
    }

    public static Thing fromWindowStyle(int t) throws HeclException {
        return int2t(t, windowstylenames, windowstylevals, "mwt.window style");
    }
    
     public static int toKeyState(Thing t) throws HeclException {
        return t2int(t, keystatenames, keystatevals, "mwt.window keystate");
    }

    public static Thing fromKeyState(int t) throws HeclException {
        return int2t(t, keystatenames, keystatevals, "mwt.component keystate");
    }

    protected static int t2int(Thing t, String nametab[], int valtab[], String emsg)
            throws HeclException {
        return s2int(t.toString().toLowerCase(), nametab, valtab, emsg);
    }

    protected static int s2int(String s, String nametab[], int valtab[], String emsg)
            throws HeclException {
        int l = nametab.length;
        for (int i = 0; i < l; ++i) {
            if (s.equals(nametab[i])) {
                return valtab[i];
            }
        }
        throw new HeclException("Invalid " + emsg + " '" + s + "'.");
    }

    protected static Thing int2t(int v, String nametab[], int valtab[], String emsg)
            throws HeclException {
        return new Thing(int2s(v, nametab, valtab, emsg));
    }

    protected static String int2s(int v, String nametab[], int valtab[], String emsg)
            throws HeclException {
        int l = valtab.length;
        for (int i = 0; i < l; ++i) {
            if (v == valtab[i]) {
                return nametab[i];
            }
        }
        throw new HeclException("Invalid " + emsg + " value '" + v + "'.");
    }
}
