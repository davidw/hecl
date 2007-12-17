/*
 * Copyright 2005-2007
 * Wolfgang S. Kechel, data2c GmbH (www.data2c.com)
 * 
 * Author: Wolfgang S. Kechel - wolfgang.kechel@data2c.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hecl.midp20.lcdui;

import java.util.Vector;
import javax.microedition.lcdui.Font;

import org.hecl.Command;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.IntThing;
import org.hecl.ListThing;
import org.hecl.StringThing;
import org.hecl.Thing;

import org.hecl.misc.HeclUtils;

class FontMap implements org.hecl.Command {
    public static void load(Interp ip) {
	ip.addCommand(CMDNAME,cmd);
    }
    public static void unload(Interp ip) {
	ip.removeCommand(CMDNAME);
    }
    
    public Thing cmdCode(Interp interp,Thing[] argv) throws HeclException {
	int numargs = argv.length;
	
	if(numargs < 2)
	    throw HeclException.createWrongNumArgsException(
		argv, 1, "font names or font <font> command [args...]");
	
	String subcmd = argv[1].toString();
	if(numargs == 2) {
	    if(subcmd.equals("names")) {
		Vector allfonts = new Vector();
		
		for(int i=0; i<faces.length; ++i) {
		    for(int j=0; j<styles.length; ++j) {
			for(int k=0; k<sizes.length; ++k) {
			    Font f = Font.getFont(faces[i],styles[j],sizes[k]);
			    if(null != f) {
				String fontname = get(f);
					allfonts.addElement(new Thing(fontname));
			    }
			}
		    }
		}
		return ListThing.create(allfonts);
	    }
	} else if(numargs > 2) {
	    Font font = get(argv[1]);
	    
	    if(font == null)
		throw new HeclException("Invalid font '"
					+argv[1].toString()+"'.");
	    
	    subcmd = argv[2].toString().toLowerCase();
	    int n = 3;
	    if(subcmd.equals(WidgetInfo.NCGET)) {
		if(0 != HeclUtils.testArguments(argv,n+1,-1)) {
		    throw HeclException.createWrongNumArgsException(
			argv, n+1, "option");
		}
		String optname = argv[n].toString().toLowerCase();
		if(optname.equals("-face"))
		    return WidgetInfo.fromFontFace(font.getFace());
		if(optname.equals("-size"))
		    return WidgetInfo.fromFontSize(font.getSize());
		if(optname.equals("-plain"))
		    return IntThing.create(font.isPlain());
		if(optname.equals("-bold"))
		   return IntThing.create(font.isBold());
		if(optname.equals("-italic"))
		    return IntThing.create(font.isItalic());
		if(optname.equals("-underlined"))
		    return IntThing.create(font.isUnderlined());
		if(optname.equals(WidgetInfo.NHEIGHT))
		    return IntThing.create(font.getHeight());
		if(optname.equals("-baselineposition"))
		    return IntThing.create(font.getBaselinePosition());
		throw new HeclException("Unknown cget option '"+optname+"'");
	    }
	    /*
	      if(subcmd.equals("configure")) {
	      }
	    */
	    if(subcmd.equals("charwidth")) {
		// charwidth string [offset [len]]
		if(0 != HeclUtils.testArguments(argv,n+1,-1)) {
		    throw HeclException.createWrongNumArgsException(
			argv, n+1, "string [offset [len]]");
		}
		char[] thechars = argv[n++].toString().toCharArray();
		int offset = 0;
		int len = thechars.length;
		
		if(n < numargs) {
		    offset = IntThing.get(argv[n++]);
		    len = (n < numargs) ? IntThing.get(argv[n++]) : len - offset;
		}
		checkOffsetAndLength(offset,len,thechars.length);
		return IntThing.create(font.charsWidth(thechars,offset,len));
	    }
	    if(subcmd.equals("stringwidth")) {
		// stringwidth string [offset [len]]
		if(0 != HeclUtils.testArguments(argv,n+1,-1)) {
		    throw HeclException.createWrongNumArgsException(
			argv, n+1, "string [offset [len]]");
		}
		String s = argv[n++].toString();
		int offset = 0;
		int len = s.length();
		
		if(n < numargs) {
		    offset = IntThing.get(argv[n++]);
		    len = (n < numargs) ? IntThing.get(argv[n++]) : len - offset;
		}
		checkOffsetAndLength(offset,len,s.length());
		return IntThing.create(font.substringWidth(s,offset,len));
	    }
	}
	throw new HeclException("Invalid font command '"+subcmd+"'!");
    }
    
    
    public static Thing fontThing(Font f) throws HeclException {
	return StringThing.create(get(f));
    }
    
    
    public static Font get(Thing t) {
	return get(t.toString());
    }
    
    
    public static Font get(String pname) {
	String name = pname.toLowerCase();
	
	if(name.equals(deffontname))
	    return Font.getDefaultFont();

	int len = name.length();
	int face = Font.FACE_SYSTEM;
	int size = Font.SIZE_MEDIUM;
	int style = Font.STYLE_PLAIN;

	// face
	int first = 0;
	int e = name.indexOf(fontfieldseperator,first);
	if(e < 0 || len<4)
	    return null;
	try {
	    face = WidgetInfo.toFontFace(name.substring(first,e));
	} catch(HeclException he) {
	    return null;
	}

	// size
	first = e+1;
	if(first >= len)
	    return null;
	e = name.indexOf(fontfieldseperator,first);
	if(e < 0)
	    return null;
	try {
	    size = WidgetInfo.toFontSize(name.substring(first,e));
	} catch(HeclException he) {
	    return null;
	}
	
	// build style, or'ed bold, italic, underline
	first = e+1;
	if(first >= len)
	    return null;
	e = name.indexOf(fontfieldseperator,first);
	String s = name.substring(first,e);
	// bold
	if(s.equals("b"))
	    style |= Font.STYLE_BOLD;
	
	first = e+1;
	if(first >= len)
	    return null;
	
	e = name.indexOf(fontfieldseperator,first);
	s = name.substring(first,e);
	// italic
	if(s.equals("i"))
	    style |= Font.STYLE_ITALIC;

	first = e+1;
	if(first >= len)
	    return null;
	s = name.substring(first,len);
	// underline
	if(s.equals("u"))
	    style |= Font.STYLE_UNDERLINED;
	
	return Font.getFont(face, style, size);
    }

    public static String get(Font f) throws HeclException {
	if(f == Font.getDefaultFont())
	    return deffontname;
	return WidgetInfo.fromFontFace(f.getFace()).toString()
	    + "-" + WidgetInfo.fromFontSize(f.getSize()).toString()
	    + "-" + (f.isBold() ? "b" : "*")
	    + "-" + (f.isItalic() ? "i" : "*")
	    + "-" + (f.isUnderlined() ? "u" : "*")
	    ;
    }

    private static void checkOffsetAndLength(int offset,int len,int max) {
	if(offset < 0 || offset > len)
	    throw new ArrayIndexOutOfBoundsException("Invalid offset.");
	if(offset+len > max)
	    throw new ArrayIndexOutOfBoundsException("Invalid length.");
    }

    public static final char fontfieldseperator = '-';
    public static final String deffontname = "defaultfont";

    private static final int[] faces = {
	Font.FACE_SYSTEM,Font.FACE_MONOSPACE,Font.FACE_PROPORTIONAL
    };
    private static final int[] sizes = {
	Font.SIZE_SMALL,Font.SIZE_MEDIUM,Font.SIZE_LARGE
    };
    private static final int[] styles = {
	Font.STYLE_PLAIN,
	    Font.STYLE_BOLD,
	    Font.STYLE_ITALIC,
	    Font.STYLE_UNDERLINED,
	    Font.STYLE_BOLD|Font.STYLE_ITALIC,
	    Font.STYLE_BOLD|Font.STYLE_UNDERLINED,
	    Font.STYLE_ITALIC|Font.STYLE_UNDERLINED,
	    Font.STYLE_BOLD|Font.STYLE_ITALIC|Font.STYLE_UNDERLINED
    };

    private static FontMap cmd = new FontMap();
    private static final String CMDNAME = "lcdui.font";
}

// Variables:
// mode:java
// coding:utf-8
// End:
