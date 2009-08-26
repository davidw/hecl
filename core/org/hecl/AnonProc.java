package org.hecl;

public class AnonProc implements ClassCommand {

    public Thing method(Interp interp, ClassCommandInfo context, Thing[] argv)
	throws HeclException {
	Thing[] newargv = new Thing[argv.length];
	newargv[0] = new Thing("anonproc");
	for (int i = 1; i < argv.length; i++) {
	    newargv[i] = argv[i];
	}

	RealThing rt = argv[0].getVal();
	Proc proc = (Proc)((ObjectThing)rt).get();
	return proc.cmdCode(interp, newargv);
    }

}
