/*
 * Created on 2005-03-07
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hecl;

import java.util.Hashtable;

/**
 * @author zoro
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
class Ensemble implements Command {
    Hashtable subcommands = new Hashtable();
    protected void addSubcommand(String name, Command cmd) {
        subcommands.put(name, cmd);
    }
    protected void removeSubcommand(String name) {
        subcommands.remove(name);
    }
    public Thing cmdCode(Interp interp, Thing[] argv) throws HeclException {
        if (argv.length < 2) {
            throw HeclException.createWrongNumArgsException(argv, 1,
                    "subcommand ?params");
        }
	return null;
    }
}
