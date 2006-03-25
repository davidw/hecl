/* Copyright 2005 David N. Welton

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.hecl.load;

import org.hecl.Command;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.ListThing;
import org.hecl.Thing;

import org.hecl.files.HeclFile;
import org.hecl.HeclModule;

import java.net.URI;
import java.net.URLClassLoader;
import java.net.URL;

import java.util.Vector;

/**
 * <code>LoadCmd</code> implements the "load" command.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class LoadCmd implements Command {

    /**
     * The <code>cmdCode</code> method implements the 'load' command.
     *
     * @param interp an <code>Interp</code> value
     * @param argv a <code>Thing[]</code> value
     * @exception HeclException if an error occurs
     */
    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
	try {
	    /* The the list of URI's where code might be located. */
	    Vector urlv = ListThing.get(argv[2]);
	    int sz = urlv.size();
	    URL[] urls = new URL[sz];

	    for (int i = 0; i < sz; i++ ) {
		/* Apparently the URI's must be absolute. */
		URI uri = new URI(((Thing)urlv.elementAt(i)).toString());
		if (!uri.isAbsolute()) {
		    URI current = new URI("file://" + HeclFile.currentFile);
		    uri = current.resolve(uri.normalize());
		}
		urls[i] = uri.toURL();
	    }

	    /* Actually load the module. */
	    URLClassLoader ucl = new URLClassLoader(urls);
	    Class c = ucl.loadClass (argv[1].toString());
	    HeclModule module = (HeclModule) c.newInstance();
	    module.loadModule(interp);
	} catch (Exception e) {
	    throw new HeclException(e.toString());
	}
    }
}
