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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import mwt.Button;
import mwt.Component;
import mwt.EventListener;
import org.hecl.HeclException;
import org.hecl.mwtgui.ext.HeclWindow;

/**
 *
 * @author donus
 */
public class MwtManager extends Canvas implements Runnable, EventListener {

    private boolean exit = false;
    HeclWindow main = new HeclWindow(10, 10, getWidth() - 10, getHeight() - 10);
    private static MwtManager htm = null;
    EventGetWay event;
    Image bgImage = null;

    static MwtManager getManager() {
        if (htm == null) {
            htm = new MwtManager();
        }
        return htm;
    }
// notify input
    protected void keyPressed(int keyCode) {
        try {
            if (keyCode > 0) {
                event.keyPressed(keyCode);
            } else {
                event.keyPressed(getGameAction(keyCode));
            }
        } catch (HeclException ex) {
            ex.printStackTrace();
        }
    }

    protected void keyReleased(int keyCode) {
        try {
            if (keyCode > 0) {
                event.keyReleased(keyCode);
            } else {
                event.keyReleased(getGameAction(keyCode));
            }
        } catch (HeclException ex) {
            ex.printStackTrace();
        }
    }

    public void processEvent(int arg0, Component arg1, Object[] arg2) {
        if (arg1 instanceof Button) {
            event.execHeclCmd(((Button) arg1).getActionType(), (Button) arg1, arg2);
        } else {
            event.execHeclCmd(arg0, arg1, arg2);
        }
    }

    protected void paint(Graphics g) {
        g.fillRect(0, 0, getWidth(), getHeight());
//        try {
//            event.execHecl(g);
//        } catch (HeclException ex) {
//            
//        }
        if (bgImage != null) {
            g.drawImage(bgImage, getWidth() / 2, getHeight() / 2, Graphics.VCENTER | Graphics.HCENTER);
        }
        if (main != null) {
            main.paint(g);
        }
    }

    void setBgImage(Image image) {
        bgImage = image;
    }

    void setExit(boolean thing2bool) {
        exit = thing2bool;
    }

    void setSelectionCmd(MwtManagerCmd eventProvider) {
        event = eventProvider;
    }

    public void run() {
        while (!exit) { // main loop
            main.repeatKeys(true);
            repaint();
            serviceRepaints();
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
