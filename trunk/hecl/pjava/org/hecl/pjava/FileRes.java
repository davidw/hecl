/* Copyright 2004-2005 David N. Welton

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

package org.hecl.pjava;

import java.io.*;
import org.hecl.*;

/**
 * <code>FileGetter</code> implements the Load interface. It is used to load
 * files from the local file system.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

public class FileRes implements ResHandle {
    int priority = -10;
    public FileRes() {
    }
    public FileRes(int pri) {
        priority = pri;
    }
    public boolean handleRes(String str) {
        return true;
    }
    /**
     * <code>getscript</code> returns the data contained in a file.
     * 
     * @param flname
     *            a <code>String</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public Thing getRes(String flname) throws HeclException {
        StringBuffer input = null;
        FileInputStream fin = null;
        int c = 0;

        try {
            fin = new FileInputStream(flname);
            File fl = new File(flname);
            DataInputStream in = new DataInputStream(fin);
            int len = (int) fl.length();
            byte[] b = new byte[len];
            in.readFully(b);
            input = new StringBuffer(new String(b));
            b = null;
        } catch (Exception e) {
            throw new HeclException("Error reading from file: " + e);
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception ex) {
                    throw new HeclException(ex.toString());
                }
            }
        }
        return new Thing(input);
    }
    public int getPriority() {
        return priority;
    }
}
