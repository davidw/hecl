package com.dedasys.hecl;

/**
 * The <code>Command</code> interface is the template for all commands
 * implemented in Hecl.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

public interface Command {

    /**
     * The <code>cmdCode</code> method takes an interpreter and an
     * array of Things, performs some calculations, and calls
     * <code>interp.setResult()</code>, if it needs to, to set the
     * result.
     *
     * @param interp an <code>Interp</code> value
     * @param argv a <code>Thing[]</code> value
     * @exception HeclException if an error occurs
     */

    void cmdCode(Interp interp, Thing[] argv)
	throws HeclException;
}
