package com.dedasys.hecl;

class ProcCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	Proc proc = new Proc(argv[2], argv[3]);
	interp.addCommand(argv[1].toString(), proc);
    }
}
