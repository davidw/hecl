package com.dedasys.hecl;

/**
 * The <code>Load</code> interface is the template for loading
 * additional 
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

abstract public class Load {
    public Thing getscript(String resourcename)
	throws HeclException {
	StringBuffer input = new StringBuffer();
	return new Thing(input);
    }
}
