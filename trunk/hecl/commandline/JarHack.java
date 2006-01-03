/* Copyright 2006 David N. Welton

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

import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.Iterator;
import java.util.Set;

import java.util.jar.*;



/**
 * <code>JarHack</code> 
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class JarHack {

    /**
     * The <code>substHecl</code> method takes the filenames of two
     * .jar's - one as input, the second as output, in addition to the
     * name of the application.  Where it counts, the old name (Hecl,
     * usually) is overridden with the new name, and the new .jar file
     * is written to the specified outfile.
     *
     * @param infile a <code>String</code> value
     * @param outfile a <code>String</code> value
     * @param newname a <code>String</code> value
     * @exception IOException if an error occurs
     */
    public static void substHecl(String infile, String outfile, String newname) throws IOException {
	JarInputStream jif = new JarInputStream(new FileInputStream(infile));
	Manifest mf = jif.getManifest();
	Attributes attrs = mf.getMainAttributes();

	Set keys = attrs.keySet();
	Iterator it = keys.iterator();
	while (it.hasNext()) {
	    Object key = it.next();
	    Object value = attrs.get(key);
	    String keyname = key.toString();

	    /* These are the three cases that interest us in
	     * particular, where we need to make changes. */
	    if (keyname.equals("MIDlet-Name")) {
		attrs.putValue(keyname, newname);
	    } else if (keyname.equals("MIDlet-1")) {
		/* FIXME  */
		String valuestr = value.toString();
		String properties[] = (valuestr.split(", "));
		attrs.putValue(keyname, newname + ", " + properties[1] + ", " + properties[2]);
	    } else if (keyname.equals("MIDlet-Jar-URL")) {
		attrs.put(key, newname + ".jar");
	    }
	}

        JarOutputStream jof = new JarOutputStream(new FileOutputStream(outfile), mf);

        byte[] buf = new byte[4096];

	/* Go through the various entries. */
        JarEntry entry;
        while ((entry = jif.getNextJarEntry()) != null) {

	    /* Don't copy the manifest file. */
            if ("META-INF/MANIFEST.MF".equals(entry.getName())) continue;

            jof.putNextEntry(entry);
            int read;
            while ((read = jif.read(buf)) != -1) {
                jof.write(buf, 0, read);
            }

            jof.closeEntry();
        }

        jof.flush();
        jof.close();
        jif.close();
    }

    public static void main(String[] args) {

	String newfn = "/tmp/" + args[1] + ".jar";

	try {
	    substHecl(args[0], newfn, args[1]);
	} catch (Exception e) {
	    System.err.println("Error: " + e);
	}
    }
}
