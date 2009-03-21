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

import javax.microedition.lcdui.Graphics;
import mwt.Button;
import mwt.Component;
import org.hecl.HeclException;

/**
 *
 * @author donus
 */
public interface EventGetWay {
    
    public void execHeclCmd(String sender) throws HeclException;
    public void execHecl(Graphics g) throws HeclException;

    public void execHeclCmd(int arg0, Component arg1, Object[] arg2);
    public void execHeclCmd(int arg0, Button arg1, Object[] arg2);
    public void keyPressed(int keyCode) throws HeclException;
    public void keyReleased(int keyCode) throws HeclException;

}
