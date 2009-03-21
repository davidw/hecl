/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hecl.mwtgui.ext;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import mwt.Button;
import mwt.EventListener;
import mwt.Window;

/**
 *
 * @author donus
 */
public class ImageButton extends Button {
    
    private Image image;
    
    public ImageButton(int x, int y, int width, int height, Image image, EventListener action, int actionType) {
        super(x,y,width,height,"",action,actionType);
        this.image = image;
    }

    public void setImage(Image image) {
        this.image=image;
    }
    
    public Image getImage() {
        return image;
    }
    
    // overrides
	protected void paint(Graphics g, Window window) {
                super.paint(g, window);
		if(image != null) {
                    
			g.drawImage(image,(this.getWidth()-image.getWidth())/2,(this.getHeight()-image.getHeight())/2,0);
			return;
		}
	}

}
