/* Copyright 2007-2008 David N. Welton - DedaSys LLC - http://www.dedasys.com

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
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

class AndroidBuilder {

    /* FIXME - these two must come from somewhere else.  */

    private static String aapt = "/opt/android-sdk_m5-rc14_linux-x86/tools/aapt";
    private static String dx = "/opt/android-sdk_m5-rc14_linux-x86/tools/dx";
    private static String androidjar = "/opt/android-sdk_m5-rc14_linux-x86/android.jar";

    /* FIXME - get these from elsewhere.  */
    private static String appname = "Hackle";
    private static String appclass = "Wackle";
    private static String packagename = "foo.bar.baz";

    private static String xmltemplate =
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

    private static String mainClassTemplate =
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

    private static String subClassTemplate =
	"package " + packagename + ";\n" +
	"import org.hecl.android.SubHecl;\n" +
	"public class Sub" + appclass + " extends SubHecl {}\n";


    public static void main(String[] args) throws IOException {
	String tmpdir = System.getProperty("java.io.tmpdir");
	String dirname = tmpdir + "/" + appname + "-" + System.currentTimeMillis();
	String manifest = dirname + "/" + "AndroidManifest.xml";
	String tmppackage = dirname + "/" + "Temp.apk";
	String hecljar = dirname + "/" + "Hecl.jar";
	String heclapk = dirname + "/" + "Hecl.apk";

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

	System.err.println("jpd: " + jarpackagedir);

	String mainJava = packagedir + "/" + appclass + ".java";
	String subJava = packagedir + "/Sub" + appclass + ".java";
	String mainClass = jarpackagedir + appclass + ".class";
	String subClass = jarpackagedir + "Sub" + appclass + ".class";

	fos = new FileOutputStream(mainJava);
	fos.write(mainClassTemplate.getBytes());
	fos.close();

	fos = new FileOutputStream(subJava);
	fos.write(subClassTemplate.getBytes());
	fos.close();

	runProcess("javac", mainJava, subJava, "-cp", hecljar + ":" + androidjar);
	runProcess("jar", "uf", hecljar, "-C", dirname, mainClass);
	runProcess("jar", "uf", hecljar, "-C", dirname, subClass);

	String dexfile = dirname + "/" + "classes.dex";
	runProcess(dx, "-JXmx384M", "--dex", "--output=" + dexfile,
		   "--locals=full",  "--positions=lines", hecljar);

	runProcess("zip", "-j", "-r", heclapk, manifest);
	runProcess("zip", "-j", "-r", heclapk, dexfile);

	(new File(heclapk)).renameTo(new File(dirname + "/" + appclass + ".apk"));

	/* ... and put the new, updated Android.xml file in it. */

    }

    private static void copyFileStream(InputStream is, OutputStream os)
	throws IOException {

	int c = 0;
	while ((c = is.read()) != -1) {
	    os.write(c);
	}
	os.close();
	is.close();
    }

    private static void runProcess(String ... args)
	throws IOException {

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

	System.out.println(args[0] + " output = " + output);
	System.out.println(args[0] + " error = " + error);
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
}