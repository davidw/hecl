import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.Enumeration;
import java.util.Hashtable;

public class Dumper {
    static Hashtable unknownTypes;

    public static void main(String[] args) {
	unknownTypes = new Hashtable();

	try {
	    Class c = Class.forName("android.widget.TextView");
//	    Class c = Class.forName("android.widget.Button");
	    Method ms[] = c.getDeclaredMethods();
	    for (int i = 0; i < ms.length; i++) {
		String sig =  ms[i].getReturnType().getSimpleName() + " "  + ms[i].getName();
		Class params[] = ms[i].getParameterTypes();
		for (int j = 0; j < params.length; j++) {
		    sig += " " + params[j].getSimpleName();
		}
		System.out.println("	" + sig + " :");
		gencode(ms[i]);
	    }

	    System.out.println("UNKNOWN TYPES:");
	    for (Enumeration e = unknownTypes.keys() ; e.hasMoreElements() ;) {
		System.out.println(e.nextElement());
	    }
	} catch (Exception e) {
	    System.err.println(e.toString());
	    e.printStackTrace();
	}
    }

    public static void gencode(Method m) {
	Class params[] = m.getParameterTypes();
	String args = "";
	if (params.length > 0) {
	    int i;
	    for (i = 0; i < params.length - 1; i++) {
		args += genArg(params[i], i) + ", ";
	    }
	    args += genArg(params[i], i);
	}
	System.out.println(genretval(m.getReturnType().getSimpleName()) + "view." + m.getName() + "(" + args + ") )");
    }

    public static String genretval(String retval) {
	if (retval.equals("int") || retval.equals("boolean")) {
	    return "Thing retval = IntThing.create( ";
	} else if (retval.equals("void")) {
	    return "Thing retval = null; (";
	}
	return "";
    }

    public static String genArg(Class param, int pos) {
	pos += 2;
	String p = param.getSimpleName();
	if (p.equals("int")) {
	    return "IntThing.get(argv[" + pos + "])";
	} else {
	    unknownTypes.put(p, 1);
	}
	return p;
    }
}
