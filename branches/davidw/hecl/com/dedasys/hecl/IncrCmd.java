package com.dedasys.hecl;

class IncrCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {

	Integer im = argv[1].toInteger();
	int m = im.intValue();
	int n = 1;
	if (argv.length > 2) {
	    n = argv[2].toInt();
	}
	int r = m + n;
	argv[1] = new Thing(r);
	interp.setResult(new Thing(r));
    }
}
