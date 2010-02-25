/* Copyright 2008-2010 David N. Welton - DedaSys LLC - http://www.dedasys.com

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

package org.hecl.androidbuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

class AndroidBuilder {
    private static final String sep = File.separator;
    private static final String eol = System.getProperty("line.separator");
    private static final String pathsep = File.pathSeparator;

    public static void main(String[] args) throws IOException, ParseException {
	String androiddir = null;

	Options opts = new Options();

	/* Define some command line options. */
	opts.addOption("android", true, "android SDK location");
	opts.addOption("class", true, "New class name");
	opts.addOption("package", true, "New package name, like bee.bop.foo.bar");
	opts.addOption("label", true, "Label");
	opts.addOption("permissions", true, "Android Permissions");
	opts.addOption("intentfilter", true, "Intent Filter File");
	opts.addOption("extraclass", true, "Extra class");
	opts.addOption("script", true, "Script file");

	CommandLineParser parser = new PosixParser();
	CommandLine cmd = parser.parse(opts, args);

	/* Get the android directory, or fail if it's not given. */
	if(cmd.hasOption("android")) {
	    androiddir = cmd.getOptionValue("android");
	} else {
	    usage(opts);
	}
	String aapt = androiddir + sep + "tools" + sep + "aapt";
	String dx = androiddir + sep + "tools" + sep + "dx";
	if (sep == "\\") {
	    /* It's windows  */
	    dx += ".bat";
	}

	String androidjar = androiddir + sep + "android.jar";

	/* Get the application's class name.  */
	String appclass = "Hackle";
 	if(cmd.hasOption("class")) {
	    appclass = cmd.getOptionValue("class");
	}

	/* Get the application's label. */
	String appname = "Hecl Hackle";
 	if(cmd.hasOption("label")) {
	    appname = cmd.getOptionValue("label");
	}

	/* Get the fake package name. */
	String packagename = "bee.bop.doo.wah";
 	if(cmd.hasOption("package")) {
	    packagename = cmd.getOptionValue("package");
	}

	String perms = "";
 	if(cmd.hasOption("permissions")) {
	    for(String p : cmd.getOptionValue("permissions").split(",")) {
		perms += "<uses-permission android:name=\"android.permission." + p + "\" />\n";
	    }
	}

	boolean hasextraClass = false;
	String extraClass = "";
 	if(cmd.hasOption("extraclass")) {
	    hasextraClass = true;
	    extraClass = cmd.getOptionValue("extraclass");
	}

	String intentfilterFile = "";
 	if(cmd.hasOption("intentfilter")) {
	    intentfilterFile = cmd.getOptionValue("intentfilter");
	}

	String scriptFilename = null;
 	if(cmd.hasOption("script")) {
	    scriptFilename = cmd.getOptionValue("script");
	}

	/* Calculate some other stuff based on the informatin we have. */
	String tmpdir = System.getProperty("java.io.tmpdir");
	File dirnamefile = new File(tmpdir, appclass + "-" + System.currentTimeMillis());
	String dirname = dirnamefile.toString();
	String manifest = dirname + sep + "AndroidManifest.xml";
	String tmppackage = dirname + sep + "Temp.apk";
	String hecljar = dirname + sep + "Hecl.jar";
	String heclapk = dirname + sep + "Hecl.apk";
	String resdir = dirname + sep + "res";
	String icondir = resdir + sep + "drawable";
	String iconfile = (new File(icondir, "aicon.png")).toString();


	String intentreceiver = "";
	/* If we have an intent filter .xml file, read it and add its
	 * contents. */
	if (!intentfilterFile.equals("")) {
	    StringBuffer sb = new StringBuffer("");
	    FileInputStream fis = new FileInputStream(intentfilterFile);
	    int c = 0;
	    while ((c = fis.read()) != -1) {
		sb.append((char)c);
	    }
	    fis.close();
	    intentreceiver =sb.toString();
	}

	/* The AndroidManifest.xml template. */
	String xmltemplate =
	    "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\" \n" +
	    "package=\"" + packagename + "\">\n" +
	    perms +
	    "<application android:icon=\"@drawable/aicon\">\n" +
	    /* Main activity */
	    "<activity android:name=\"" + appclass + "\" android:label=\"" + appname + "\">\n" +
	    "<intent-filter>\n" +
	    "<action android:name=\"android.intent.action.MAIN\" />\n" +
	    "<category android:name=\"android.intent.category.LAUNCHER\" />\n" +
	    "</intent-filter>\n" +
	    "</activity>\n" +
	    /* SubHecl */
	    "<activity android:name=\"" + "Sub" + appclass +"\" android:label=\"SubHecl\">\n" +
	    "<intent-filter>\n" +
	    "<action android:name=\"android.intent.action.MAIN\" />\n" +
	    "</intent-filter>\n" +
	    "</activity>\n" +

	    /* Intent Receiver. */

	    intentreceiver +

	    "</application>\n" +
	    "</manifest>\n";

	/* Template for the main .java file. */
	String mainClassTemplate =
	    "package " + packagename + ";\n" +
	    "import org.hecl.android.Hecl;\n" +
	    "import org.hecl.HeclException;\n" +
	    "import org.hecl.Interp;\n" +
	    "import org.hecl.java.JavaCmd;\n" +
	    "public class " + appclass + " extends Hecl {\n" +
	    "protected void createCommands(Interp i) throws HeclException {\n" +
	    "JavaCmd.load(interp, \"" + packagename + "." + appclass + "\", \"hecl\");\n" +
	    "JavaCmd.load(interp, \"" + packagename + ".Sub" + appclass + "\", \"subhecl\");\n" +
	    "}\n" +
	    "}\n";

	/* Template for the sub file. */
	String subClassTemplate =
	    "package " + packagename + ";\n" +
	    "import org.hecl.android.SubHecl;\n" +
	    "public class Sub" + appclass + " extends SubHecl {}\n";


	/* First we write out the AndroidManifest.xml file.  */
	(new File(dirname)).mkdir();
        FileWriter outputstream = null;
	try {
            outputstream = new FileWriter(manifest);
	    outputstream.write(xmltemplate);
	} catch (IOException e) {
	    System.err.println("Couldn't write to " + manifest + " : " + e.toString());
	    System.exit(1);
	} finally {
	    if (outputstream != null) {
                outputstream.close();
            }
	}

	InputStream is = null;
	FileOutputStream fos = null;

	/* Make a directory for the icon. */
	(new File(icondir)).mkdirs();

	copyFileStream(AndroidBuilder.class.getResourceAsStream("/android/res/drawable/aicon.png"),
		       new FileOutputStream(iconfile));

	/* Now, we run aapt to generate a new, compressed .xml file... */
	runProcess(aapt, "package", "-f", "-M", manifest,
		   "-S", resdir, "-I", androidjar, "-F", tmppackage);

	/* Then we extract it, overwriting AndroidManifest.xml*/
	ZipFile zipfile = new ZipFile(tmppackage);
	ZipEntry newmanifest = zipfile.getEntry("AndroidManifest.xml");
	System.out.println("newmanifest is " + newmanifest);
	is = zipfile.getInputStream(newmanifest);
	fos = new FileOutputStream(manifest);
	copyFileStream(is, fos);

	/* Now, we copy in Hecl.jar  ...  */
	is = AndroidBuilder.class.getResourceAsStream("/android/Hecl.jar");
	fos = new FileOutputStream(hecljar);
	copyFileStream(is, fos);

	/* ... and the Hecl.apk. */
	is = AndroidBuilder.class.getResourceAsStream("/android/bin/Hecl.apk");
	fos = new FileOutputStream(heclapk);
	copyFileStream(is, fos);

	/* Now, we can create some Java classes ...  */
	String packagedir = dirname;
	String jarpackagedir = ""; /* The name inside the jar file. */
	for (String s : packagename.split("\\.")) {
	    packagedir += sep + s;
	    jarpackagedir += s + sep;
	}
	(new File(packagedir)).mkdirs();

	String mainJava = packagedir + sep + appclass + ".java";
	String subJava = packagedir + sep + "Sub" + appclass + ".java";
	String mainClass = jarpackagedir + appclass + ".class";
	String subClass = jarpackagedir + "Sub" + appclass + ".class";


	/* Output a new 'main' class. */
	fos = new FileOutputStream(mainJava);
	fos.write(mainClassTemplate.getBytes());
	fos.close();

	/* Output a new 'sub' class. */
	fos = new FileOutputStream(subJava);
	fos.write(subClassTemplate.getBytes());
	fos.close();

	/* Compile the new classes. */
	runProcess("javac", mainJava, subJava, "-cp", hecljar + pathsep + androidjar);

	/* Stash them in the .jar. */
	runProcess("jar", "uf", hecljar, "-C", dirname, mainClass);
	runProcess("jar", "uf", hecljar, "-C", dirname, subClass);

	/* If there is an extra class, move it into the .jar */
	if (hasextraClass) {
	    File ec = new File(extraClass);
	    is = new FileInputStream(ec);
	    String outfile = dirname + sep + jarpackagedir + ec.getName();
	    System.out.println("Moving " + extraClass + " to " + outfile);
	    fos = new FileOutputStream(outfile);
	    copyFileStream(is, fos);
	    runProcess("jar", "uf", hecljar, "-C", dirname, jarpackagedir + ec.getName());
	}


	/* Run the dx program to turn them into Android dex stuff. */
	String dexfile = dirname + sep + "classes.dex";
	runProcess(dx, "-JXmx384M", "--dex", "--output=" + dexfile,
		   "--positions=lines", hecljar);

	/* Finally, rename the whole business back to the calling
	 * directory.  We copy the whole thing across as a .zip
	 * archive in order to replace the script.hcl file. */

	String newfilename = System.getProperty("user.dir") + sep + appclass + ".apk";
	if (scriptFilename == null) {
	    /* Just move it over. */
	    (new File(heclapk)).renameTo(new File(newfilename));
	} else {
	    /* Copy it bit by bit, and replace the script.hcl file. */
	    ZipInputStream zif = new ZipInputStream(new FileInputStream(heclapk));
	    ZipOutputStream zof = new ZipOutputStream(new FileOutputStream(newfilename));

	    int read;
	    byte[] buf = new byte[4096];
	    ZipEntry ze = zif.getNextEntry();
	    while (ze != null) {
		zof.putNextEntry(new ZipEntry(ze.getName()));
		if ("res/raw/script.hcl".equals(ze.getName())) {
		    FileInputStream inf = new FileInputStream(scriptFilename);
		    while ((read = inf.read(buf)) != -1) {
			zof.write(buf, 0, read);
		    }
		    inf.close();
		    /* Replace the apk's AndroidManifest.xml ... */
		} else if ("AndroidManifest.xml".equals(ze.getName())) {
		    FileInputStream inf = new FileInputStream(manifest);
		    while ((read = inf.read(buf)) != -1) {
			zof.write(buf, 0, read);
		    }
		    inf.close();
		    /* ... and classes.dex  */
		} else if ("classes.dex".equals(ze.getName())) {
		    FileInputStream inf = new FileInputStream(dexfile);
		    while ((read = inf.read(buf)) != -1) {
			zof.write(buf, 0, read);
		    }
		    inf.close();
		} else {
		    while ((read = zif.read(buf)) != -1) {
			zof.write(buf, 0, read);
		    }
		}
		ze = zif.getNextEntry();
	    }

	    zif.close();
	    zof.close();
	}

	/* FIXME - we should probably destroy the temporary directory,
	 * but it's very useful for debugging purposes.  */
    }

    /**
     * The <code>copyFileStream</code> method copies one file stream
     * into another.
     *
     * @param is an <code>InputStream</code> value
     * @param os an <code>OutputStream</code> value
     * @exception IOException if an error occurs
     */
    private static void copyFileStream(InputStream is, OutputStream os)
	throws IOException {

	int c = 0;
	while ((c = is.read()) != -1) {
	    os.write(c);
	}
	os.close();
	is.close();
    }

    /**
     * The <code>runProcess</code> method runs an external program,
     * prints its output and waits for it to exit.
     *
     * @exception IOException if an error occurs
     */
    private static void runProcess(String ... args)
	throws IOException {

	String cmdline = "";
	for (String c : args) {
	    cmdline += c + " ";
	}
	System.err.println(cmdline);

	ProcessBuilder pb = new ProcessBuilder(args);
	Process process = pb.start();
	BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	BufferedReader errStreamReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
	StringBuffer output = new StringBuffer();
	StringBuffer error = new StringBuffer();
	for(String line;(line=errStreamReader.readLine())!=null;) {
	    error.append(line + eol);
	}
	for(String line;(line=inputStreamReader.readLine())!=null;) {
	    output.append(line + eol);
	}

	if (output.length() > 0) {
	    System.out.println(output);
	}
	if (error.length() > 0) {
	    System.out.println("error: '" + error + "'");
	}
	try {
	    process.waitFor();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	    System.exit(1);
	} finally {
	    inputStreamReader.close();
	    errStreamReader.close();
	}
    }

    private static void usage(Options opts) {
	HelpFormatter formatter = new HelpFormatter();
	formatter.printHelp("AndroidBuilder", opts);
	System.exit(1);
    }

}