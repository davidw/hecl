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
import mwt.Window;

/**
 *
 * @author donus
 */
public class HeclWindow extends Window {
    
    public HeclWindow(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
    public int getFocusAction(long key) {
		switch((int) key) {
		case Canvas.LEFT:
		case Canvas.UP: return FOCUSACTION_PREV;
		case Canvas.RIGHT:
		case Canvas.DOWN: return FOCUSACTION_NEXT;
		case Canvas.FIRE: return FOCUSACTION_FIRE;
		default: return FOCUSACTION_NONE;
		}
	}

}
