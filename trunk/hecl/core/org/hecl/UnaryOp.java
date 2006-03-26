package org.hecl;

public abstract class UnaryOp extends Operator {
    UnaryOp(int cmdcode) {
	super(cmdcode,1,1);
    }
}
