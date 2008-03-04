/* Copyright 2008 David N. Welton - DedaSys LLC - http://www.dedasys.com

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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

class AndroidBuilder {

    public static void main(String[] args) throws IOException, ParseException {
	String androiddir = null;

	Options opts = new Options();

	/* Define some command line options. */
	opts.addOption("android", true, "android SDK location");
	opts.addOption("class", true, "New class name");
	opts.addOption("package", true, "New package name, like bee.bop.foo.bar");
	opts.addOption("label", true, "Label");

	CommandLineParser parser = new PosixParser();
	CommandLine cmd = parser.parse(opts, args);

	/* Get the android directory, or fail if it's not given. */
	if(cmd.hasOption("android")) {
	    androiddir = cmd.getOptionValue("android");
	} else {
	    usage(opts);
	}
	String aapt = androiddir + "/tools/aapt";
	String dx = androiddir + "/tools/dx";
	String androidjar = androiddir + "/android.jar";

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

	/* Calculate some other stuff based on the informatin we have. */
	String tmpdir = System.getProperty("java.io.tmpdir");
	String dirname = tmpdir + "/" + appclass + "-" + System.currentTimeMillis();
	String manifest = dirname + "/" + "AndroidManifest.xml";
	String tmppackage = dirname + "/" + "Temp.apk";
	String hecljar = dirname + "/" + "Hecl.jar";
	String heclapk = dirname + "/" + "Hecl.apk";


	/* The AndroidManifest.xml template. */
	String xmltemplate =
	    "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\" \n" +
	    "package=\"" + packagename + "\">\n" +
	    "<application>\n" +
	    "<activity android:name=\"" + appclass + "\" android:label=\"" + appname + "\">\n" +
	    "<intent-filter>\n" +
	    "<action android:name=\"android.intent.action.MAIN\" />\n" +
	    "<category android:name=\"android.intent.category.LAUNCHER\" />\n" +
	    "</intent-filter>\n" +
	    "</activity>\n" +
	    "<activity android:name=\"" + "Sub" + appclass +"\" android:label=\"SubHecl\">\n" +
	    "<intent-filter>\n" +
	    "<action android:name=\"android.intent.action.MAIN\" />\n" +
	    "</intent-filter>\n" +
	    "</activity>\n" +
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

	/* Now, we run aapt to generate a new, compressed .xml file... */
	runProcess(aapt, "package", "-f", "-c", "-M", manifest, "-I", androidjar, tmppackage);

	/* Then we extract it, overwriting AndroidManifest.xml*/
	ZipFile zipfile = new ZipFile(tmppackage);
	ZipEntry newmanifest = zipfile.getEntry("AndroidManifest.xml");
	System.out.println("newmanifest is " + newmanifest);
	InputStream is = zipfile.getInputStream(newmanifest);
	FileOutputStream fos = new FileOutputStream(manifest);
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
	    packagedir += "/" + s;
	    jarpackagedir += s + "/";
	}
	(new File(packagedir)).mkdirs();

	String mainJava = packagedir + "/" + appclass + ".java";
	String subJava = packagedir + "/Sub" + appclass + ".java";
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
	runProcess("javac", mainJava, subJava, "-cp", hecljar + ":" + androidjar);
	/* Stash them in the .jar. */
	runProcess("jar", "uf", hecljar, "-C", dirname, mainClass);
	runProcess("jar", "uf", hecljar, "-C", dirname, subClass);

	/* Run the dx program to turn them into Android dex stuff. */
	String dexfile = dirname + "/" + "classes.dex";
	runProcess(dx, "-JXmx384M", "--dex", "--output=" + dexfile,
		   "--locals=full",  "--positions=lines", hecljar);

	/* Replace the apk's AndroidManifest.xml and classes.dex */
	runProcess("zip", "-j", "-r", heclapk, manifest);
	runProcess("zip", "-j", "-r", heclapk, dexfile);

	/* Finally, rename the whole business. */
	(new File(heclapk)).renameTo(new File(dirname + "/" + appclass + ".apk"));
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
	for(String line;(line=inputStreamReader.readLine())!=null;) {
	    output.append(line);
	}
	for(String line;(line=errStreamReader.readLine())!=null;) {
	    error.append(line);
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