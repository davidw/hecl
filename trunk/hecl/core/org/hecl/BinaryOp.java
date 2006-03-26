package org.hecl;

public abstract class BinaryOp extends Operator {
    BinaryOp(int cmdcode) {
	super(cmdcode,2,2);
    }
}
