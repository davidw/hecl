package com.dedasys.hecl;

/**
 * The <code>Load</code> interface is the template for loading
 * additional 
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

abstract public class Load {
    public Thing getscript() throws HeclException {
	StringBuffer input = new StringBuffer();
	return new Thing(input);
    }
}
