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

package jarhack;

import java.io.IOException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;

import java.util.Iterator;
import java.util.Set;

import java.util.jar.*;

/**
 * <code>JarHack</code> -- this class provides several static methods
 * that can be used to create .jar and .jad files from a template
 * Hecl.jar and some user supplied information, such as the output
 * file, and the name of the new application.
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
     * @param infile a <code>FileInputStream</code> value
     * @param outfile a <code>String</code> value
     * @param newname a <code>String</code> value
     * @exception IOException if an error occurs
     */
    public static void substHecl(InputStream infile, String outfile,
				 String newname, String scriptfile)
	throws IOException {

	JarInputStream jif = new JarInputStream(infile);
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
	int read;
        while ((entry = jif.getNextJarEntry()) != null) {

	    /* Don't copy the manifest file. */
            if ("META-INF/MANIFEST.MF".equals(entry.getName())) continue;

	    /* Insert our own copy of the script file. */
	    if ("script.hcl".equals(entry.getName())) {
		jof.putNextEntry(new JarEntry("script.hcl"));
		FileInputStream inf = new FileInputStream(scriptfile);
		while ((read = inf.read(buf)) != -1) {
		    jof.write(buf, 0, read);
		}
		inf.close();
	    } else {
		/* Otherwise, just copy the entry. */
		jof.putNextEntry(entry);
		while ((read = jif.read(buf)) != -1) {
		    jof.write(buf, 0, read);
		}
	    }

            jof.closeEntry();
        }

        jof.flush();
        jof.close();
        jif.close();
    }

    /**
     * The <code>createJadForJar</code> method creates a new .jad file
     * that matches the .jar file passed to it.  appname is the name
     * of the new application.
     *
     * @param jarfile a <code>String</code> value
     * @param appname a <code>String</code> value
     * @exception IOException if an error occurs
     */
    public static void createJadForJar(String jarfile, String appname) throws IOException {
	File jf = new File(jarfile);
	String parent = jf.getParent();
	File jadfile = new File(parent + File.separatorChar + appname + ".jad");
	FileWriter of = new FileWriter(jadfile);
	of.write("MIDlet-1: "+appname+", Hecl.png, Hecl" + "\n" +
		 "MIDlet-Info-URL: http://www.hecl.org" + "\n" +
		 "MIDlet-Jar-Size: " + jf.length() + "\n" +
		 "MIDlet-Jar-URL: "+appname+".jar" + "\n" +
		 "MIDlet-Name: " + appname + "\n" +
		 "MIDlet-Vendor: DedaSys" + "\n" +
		 "MIDlet-Version: 1.0" + "\n" +
		 "MicroEdition-Profile: MIDP-1.0" + "\n" +
		 "MicroEdition-Configuration: CLDC-1.0");
	of.close();
    }

    public static void main(String[] args) {

	String newfn = "/tmp/" + args[1] + ".jar";

	try {
	    FileInputStream infile = new FileInputStream(args[0]);
	    substHecl(infile, newfn, args[1], args[2]);
	} catch (Exception e) {
	    System.err.println("Error: " + e);
	}
    }
}
