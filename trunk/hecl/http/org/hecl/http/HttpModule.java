/*
 * Created on 2005-03-07
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hecl.http;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.StringThing;
import org.hecl.Thing;

/**
 * @author zoro
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class HttpModule {

    public static void loadModule(Interp interp) throws HeclException {
        interp.commands.put("http", new HttpCommand());
    }

    public static void unloadModule(Interp interp) throws HeclException {
        interp.commands.remove("http");
    }
}
