/*
 * Created on 2005-03-07
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hecl;

/**
 * @author zoro
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ClassThing implements Command, RealThing {
    static int classCounter = 0;
    public ClassThing() {
    }
    /**
     * Creates a copy of a class.
     */
    public RealThing deepcopy() throws HeclException {
        throw new HeclException("Unable to create a deep copy of a class");
    }
    public String getStringRep() throws HeclException {
        throw new HeclException(
                "Unable to create a string representation of a class");
    }
    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
        if ((argv.length % 2) != 0) {
            throw HeclException.createWrongNumArgsException(argv, 1,
                    "objectName ??name? ?value? ?...??");
        }
    }
}