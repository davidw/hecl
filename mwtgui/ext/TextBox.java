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


package org.hecl.mwtgui.ext;

import java.util.Timer;
import java.util.TimerTask;


import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import mwt.Button;
import mwt.Component;
import mwt.Font;
import mwt.Window;
import mwt.Component;
import mwt.EventListener;
import mwt.Font;
import mwt.Window;

/**
 *
 * @author donus
 */
public class TextBox extends Button {

    final StringBuffer text = new StringBuffer();
    public static final String[] keys = {"abc", "def", "ghi", "jkl", "mno",
        "pqrs", "tuv", "wxyz"
    };
    Timer keyTimer;
    int keyMajor = -1;
    int keyMinor;
    int cursor = 0;
    int cursorWait;
    int timewait = 500;
    static private int CURSOR_WAIT = 100;
    private int[] cursor_color = {0xA1C632, 0xCFFF40};
    int cursorColor;
    final Font font;

    public TextBox(int x, int y, int w, int h, String text, EventListener action, int type, Font font) {
        super(x, y, h, h, text, action, type);
        this.font = font;
    }

    public String getText() {
        return text.toString();
    }
    
    public void setCursorColor(int[] color) {
        this.cursor_color = color;
    }
    
    public int[] getCursorColor() {
        return this.cursor_color;
    }
    
    public void setScheduleTime(int time) {
        this.timewait = time;
    }
    
    public int getTimerScheduleTime() {
        return this.timewait;
    }
    
    public boolean keyEvent(long key, Window window) {
        if ((key >> 32) != 0) {
            return true;
        // if key is not released return
        } else if ((int) key == window.getKeyState(Canvas.FIRE)) {
            click();
        }
        if (cursor == text.length()) {
            text.append(' '); // new char (blank)
        }

        if (keyTimer != null) {
            keyTimer.cancel();
        }

        int index = ((int) key) - (Canvas.KEY_NUM2);
        if (index < 0 || index > keys.length) {
            keyMajor = -1;
        } else {
            if (index != keyMajor) {
                keyMinor = 0;
                keyMajor = index;
            } else {
                keyMinor++;
                if (keyMinor >= keys[keyMajor].length()) {
                    keyMinor = 0;
                }
            }

            keyTimer = new Timer();
            text.setCharAt(cursor, keys[keyMajor].charAt(keyMinor));
            keyTimer.schedule(new KeyConfirmer(this), timewait);
            return true;
        }
        switch ((int) key) {
            case Canvas.KEY_STAR:
                if (text.length() > 0) {
                    text.deleteCharAt(cursor);
                    if (cursor > 0) {
                        cursor--;
                    }
                }
                return true;
            case Canvas.KEY_NUM1:
                cursor++;
                return true;
        }

        switch ((int) key) { // move cursor
            case Canvas.RIGHT:
                cursor++;
                return true;
            case Canvas.LEFT:
                if (cursor > 0) {
                    cursor--;
                }
                return true;
            case Canvas.DOWN:
            case Canvas.FIRE:
                ((Window) getParent()).setFocusNext();
                return true;
            case Canvas.UP:
                ((Window) getParent()).setFocusPrevious();
                return true;
        }
        return true;
    }

    synchronized void keyConfirmed() {
        if (keyMajor != -1) {
            text.setCharAt(cursor, keys[keyMajor].charAt(keyMinor));
            keyMajor = -1;
            cursor++;
        }
    }

    class KeyConfirmer extends TimerTask {

        TextBox textbox;

        public KeyConfirmer(TextBox textbox) {
            this.textbox = textbox;
        }

        public void run() {
            textbox.keyConfirmed();
        }
    }

    protected void paint(Graphics g, Window window) {
        boolean focused = window.getFocus() == this;
        if (focused) {
            getSkin(1).paint(this, g);
            cursorWait++;
            if (cursorWait == CURSOR_WAIT) {
                cursorWait = 0;
                cursorColor = cursorColor == 0 ? 1 : 0;
            }
            g.setColor(cursor_color[cursorColor]);
            char cursorChar = (cursor == text.length()) ? ' ' : text.charAt(cursor);
            int x = font.getWidth(text.toString().substring(0, cursor));
            if (cursor > 0) {
                x -= 3;
            } // charspacing
            g.fillRect(x + 2, 3, font.getWidth("" + cursorChar), 14);
        } else {
            getSkin(0).paint(this, g);
        }
        font.write(g, text.toString(), 2, 0, getWidth() - 4, getHeight(),
                Component.ALIGN_BOTTOM_LEFT);
    }
}
