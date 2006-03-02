package org.hecl;

import java.util.Hashtable;

class HashCmds {

    public static final int HASH = 1;
    public static final int HGET = 2;
    public static final int HSET = 3;

    static void dispatch(int cmd, Interp interp, Thing[] argv) throws HeclException {
	Thing result = null;
	Hashtable hash;
	String key;

	switch (cmd) {

	    case HASH:
		result = argv[1];
		HashThing.get(result);
		break;

	    case HGET:
		hash = HashThing.get(argv[1]);
		key = argv[2].toString();
		result = (Thing) hash.get(key);
		break;

	    case HSET:
		hash = HashThing.get(argv[1]);
		key = argv[2].toString();
		result = argv[3];
		hash.put(key, result);
		break;
	}
	interp.setResult(result);
    }
}
