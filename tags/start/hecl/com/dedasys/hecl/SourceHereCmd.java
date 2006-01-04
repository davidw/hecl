package com.dedasys.hecl;

import java.util.*;
import java.io.*;

class SourceHereCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	String filename = argv[1].toString();
	Eval eval = new Eval();
	File fl = new File(interp.getScriptName());
	File newfl = new File(fl.getParent(), filename);
	eval.eval(interp, interp.getscript(newfl.toString()));
    }
}
