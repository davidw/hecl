/*
 * Copyright 2005-2006
 * Wolfgang S. Kechel, data2c GmbH (www.data2c.com)
 * 
 * Author: Wolfgang S. Kechel - wolfgang.kechel@data2c.com
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

package org.hecl.midp20.lcdui;

import java.util.Hashtable;

import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import org.hecl.Thing;
import org.hecl.Interp;

public class HeclCanvas extends GameCanvas {
    public HeclCanvas(boolean suppressKeyEvents) {
	super(suppressKeyEvents);
	owner = null;
	nokeyevents = !suppressKeyEvents;
	inpaint = false;
    }
    

    public void setOwner(CanvasEvent.Callback newowner) {
	owner = newowner;
    }
    
    
    public Graphics getGraphics() {
	return super.getGraphics();
    }
    

    public boolean isPainting() {
	return inpaint;
    }
    

    public void paint(Graphics g) {
	inpaint = true;
	super.paint(g);
	callcb(new CanvasEvent(this,CanvasEvent.E_PAINT,0,0,getWidth(),getHeight(),0));
	inpaint = false;
    }


    public void pointerPressed(int x,int y) {
	callcb(new CanvasEvent(this,CanvasEvent.E_PPRESS,x,y,getWidth(),getHeight(),0));
    }


    public void pointerReleased(int x,int y) {
	callcb(new CanvasEvent(this,CanvasEvent.E_PRELEASE,x,y,getWidth(),getHeight(),0));
    }
    

    public void pointerDragged(int x,int y) {
	callcb(new CanvasEvent(this,CanvasEvent.E_PDRAG,x,y,getWidth(),getHeight(),0));
    }
    

    public void keyPressed(int keycode) {
	callcb(new CanvasEvent(this,CanvasEvent.E_KPRESS,0,0,getWidth(),getHeight(),keycode));
    }
    

    public void keyReleased(int keycode) {
	callcb(new CanvasEvent(this,CanvasEvent.E_KRELEASE,0,0,getWidth(),getHeight(),keycode));
    }
    

    public void keyRepeated(int keycode) {
	callcb(new CanvasEvent(this,CanvasEvent.E_KREPEAT,0,0,getWidth(),getHeight(),keycode));
    }
    

    public void hideNotify() {
	callcb(new CanvasEvent(this,CanvasEvent.E_HIDE,0,0,getWidth(),getHeight(),0));
    }
    

    public void showNotify() {
	callcb(new CanvasEvent(this,CanvasEvent.E_SHOW,0,0,getWidth(),getHeight(),0));
    }
    

    public void sizeChanged(int w,int h) {
	callcb(new CanvasEvent(this,CanvasEvent.E_RESIZE,0,0,w,h,0));
    }


    void callcb(CanvasEvent e) {
	System.out.println(e.asString());
	if(owner != null)
	    owner.call(e);
    }
    
    protected boolean nokeyevents;
    protected boolean inpaint;
    protected CanvasEvent.Callback owner;

}
