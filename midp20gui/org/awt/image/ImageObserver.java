/*
 * Copyright (C) 2005, 2006 data2c GmbH (www.data2c.com)
 *
 * Author: Wolfgang S. Kechel - wolfgang.kechel@data2c.com
 * 
 * J2ME version of java.awt.image.ImageObserver
 */

package org.awt.image;

//#ifndef j2se
import javax.microedition.lcdui.Image;

public interface ImageObserver {
    public static final int WIDTH = 1;
    public static final int HEIGHT = 2;
    public static final int PROPERTIES = 4;
    public static final int SOMEBITS = 8;
    public static final int FRAMEBITS = 16;
    public static final int ALLBITS = 32;
    public static final int ERROR = 64;
    public static final int ABORT = 128;
    
    boolean imageUpdate(Image img,
			int infoflags,
			int x,
			int y,
			int width,
			int height);
}
//#endif
