/* Copyright 2009 DedaSys LLC - www.dedasys.com

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

import java.io.IOException;
import java.io.InputStream;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <code>BBJDEVersion</code> fetches a file, app.version from the
 * rapc.jar distributed with the Blackberry JDE
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class BBJDEVersion {

    public static void main(String[] args) {
	try {
	    if (args.length == 0) {
		throw new Exception("Need a jar file to process!");
	    }

	    JarFile jf = new JarFile(args[0]);
	    JarEntry entry = jf.getJarEntry("app.version");
	    InputStream is = jf.getInputStream(entry);
	    /* This is more than enough to contain the version. */
	    byte[] buf = new byte[4096];
	    is.read(buf);
	    String sversion = (new String(buf)).trim();

	    /* Only print out the major/minor version numbers. */
	    String[] res = sversion.split("\\.");
	    System.out.println(res[0] + "." + res[1]);
	    jf.close();
	} catch (Exception e) {
	    System.err.println("BBJDEVersion Exception: " + e.toString());
	    System.exit(-1);
	}
    }
}