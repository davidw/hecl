package com.dedasys.hecl;

import java.util.*;

/**
 * <code>HashCmd</code> implements the "hash", "hget" and "hset"
 * commands.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class HashCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	String cmdname = argv[0].toString();

	if (cmdname.equals("hash")) {
	    Thing result = argv[1];
	    result.toHash();
	    interp.setResult(result);
	} else if (cmdname.equals("hget")) {
	    Hashtable hash = argv[1].toHash();
	    String key = argv[2].toString();
	    interp.setResult((Thing)hash.get(key));
	} else if (cmdname.equals("hset")) {
	    Hashtable hash = argv[1].toHash();
	    String key = argv[2].toString();
	    Thing val = argv[3];
	    hash.put(key, val);
	    interp.setResult(val);
	}
	return;
    }
}
