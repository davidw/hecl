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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import mwt.Component;
import mwt.Window;


/**
 *
 * @author donus
 */
public class Scroller extends HeclWindow {

    public int vertical = 1;	// x axis offset
    public int horizontal = 1;	// y axis offset
    
    public int dv = 1;	// x axis offset
    public int dh = 1;	// y axis offset
    public  Component button; // this button enables and disables the scroller
    public boolean on; // when is true "arrow" keys will scroll

    public Scroller(int x, int y, int width, int height) {
        super(x, y, width, height);

        // create the button behavior
        button = new Component(0, 0, 8, 8, false) {

          

            protected boolean keyEvent(long key, Window window) {
                if ((key >> 32) == 0 && window.getFocusAction(key) == Window.FOCUSACTION_FIRE) {
                    on = !on;
                } else if (on) {
                    switch ((int) key) {
                        case Canvas.UP:
                            vertical -= dv;
                            break;
                        case Canvas.DOWN:
                            vertical += dv;
                            break;
                        case Canvas.LEFT:
                            horizontal -= dh;
                            break;
                        case Canvas.RIGHT:
                            horizontal += dh;
                            break;
                    }
                } else {
                    return false;
                }
                return true;
            }

            protected void paint(Graphics g, Window window) {
                if (on) {
                    g.setColor(0xCFFF40);
                } else {
                    g.setColor(window.getFocus() == this ? 0xA1C632 : 0xC6C6C6);
                }
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(0);
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        };
        add(button);
    }
    // The paint method moves all child components generating a scrolling effect
    protected void paint(Graphics g, Window window) {
        super.setFocusable(true);
        getSkin(isHierarchyEnabled()? 0 : 1).paint(this,g);
        for (int i = 0; i < getChildCount(); i++) {
            final Component c = getChild(i);
            if (c != button) {
                c.setX(c.getX() + horizontal);
                c.setY(c.getY() + vertical);
            }
        }
        paintChilds(g, window);
        for (int i = 0; i < getChildCount(); i++) {
            final Component c = getChild(i);
            if (c != button) {
                c.setX(c.getX() - horizontal);
                c.setY(c.getY() - vertical);
            }
        }
        g.setColor(0);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
    // prevent removing the button
    public void add(Component c, int index) {
        if (c != button && index == getChildCount()) {
            index--;
        }
        super.add(c, index);
    }

    public void remove(int index) {
        if (getChild(index) != button) {
            super.removeChild(index);
        }
    }

    public void setHorizontal(int horizontal) {
        this.horizontal = horizontal;
    }

    public void setVertical(int vertical) {
        this.vertical = vertical;
    }

    public int getHorizontal() {
        return horizontal;
    }

    public int getVertical() {
        return vertical;
    }
    
    public void setXScroll(int dv) {
        this.dv = dv;
    }
    
    public void setYScroll(int dh) {
        this.dh = dh;
    }
    
    public int getXScroll() {
        return dv;
    }
    
    public int getYScroll() {
        return dh;
    }
}
