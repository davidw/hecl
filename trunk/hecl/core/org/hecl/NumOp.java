package org.hecl;

public class NumOp extends Operator {
    public NumOp(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }
    
    public RealThing operate(int cmdcode,Interp interp,Thing[] argv)
	throws HeclException {
	return MathCmds.operate(cmdcode,interp,argv);
    }
}
