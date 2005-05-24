/*
 * Created on 2005-03-07
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hecl.http;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.ResHandle;
import org.hecl.StringThing;
import org.hecl.Thing;

/**
 * @author zoro
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class HeclModule implements org.hecl.HeclModule, ResHandle {

    public int getPriority() {
        return 0;
    }

    public Thing getRes(String resourcename) throws HeclException {
        return new Thing(
	    new StringThing(
		HttpCommand.executeQuery(resourcename, null, null)));
    }

    public boolean handleRes(String resourcename) {
        System.out.println("HANDLE - "+resourcename);
        if (resourcename.length() < 8)
            return false;
        System.out.println("N="+resourcename.substring(0, 7));
        if (resourcename.substring(0, 7).equals("http://"))
            return true;
        else
            return false;
    }

    public void loadModule(Interp interp) throws HeclException {
        interp.addResourceGetter(this);
        interp.commands.put("http", new HttpCommand());
    }

    public void unloadModule(Interp interp) throws HeclException {
        interp.removeResourceGetter(this);
        interp.commands.remove("http");
    }
}
