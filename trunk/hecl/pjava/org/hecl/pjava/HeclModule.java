/*
 * Created on 2005-03-04
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hecl.pjava;

import org.hecl.HeclException;
import org.hecl.Interp;

/**
 * @author zoro
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class HeclModule implements org.hecl.HeclModule {
    public void loadModule(Interp interp) throws HeclException {
        interp.addResourceGetter(new FileRes());
    }
    public void unloadModule(Interp interp) throws HeclException {
        interp.removeResourceGetter(new FileRes());
    }
}