/*
 * Copyright 2007-2008 Martin Mainusch 
 * 
 * Author: Martin Mainusch donus@gmx.net
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
package org.hecl.kxml;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.ObjectThing;
import org.hecl.IntThing;
import org.hecl.Operator;
import org.hecl.Thing;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParser;

/**
 * The <code>KXMLCmd</code> class implements the <a href="http://kxml.sourceforge.net/"> kXML-2 </a> functionality.
 * The KXML-2 implements the <a href= "http://xmlpull.org/"> XmlPull API </a>.
 * 
 * @author <a href="mailto:donus@gmx.net">Martin Mainusch</a>
 * @version 0.1Beta
 */
public class KXMLCmd extends Operator {
	
	public static final int CREATE_PARSER = 1;
	public static final int SET_INPUT = 2;
	public static final int NEXT_TAG = 3;
	public static final int NEXT = 4;
	public static final int PARSER_REQUIRE_START = 5;
	public static final int PARSER_REQUIRE_END = 6;
	public static final int GET_TEXT = 7;
	public static final int NEXT_TEXT = 8;
	public static final int ATTR_COUNT = 9;
	public static final int ATTR_NAME = 10;
	public static final int ATTR_VALUE = 11;
	public static final int ATTR_NAMESPACE = 12;
	public static final int ATTR_TYPE = 13;
	public static final int EVENT_TYPE =14;
	public static final int GET_NAME = 15;
	
	protected KXMLCmd(int cmdcode,int minargs,int maxargs) {
		super(cmdcode,minargs,maxargs);
	    }

	public Thing operate(int cmdCode, Interp interp, Thing[] argv)
			throws HeclException {
		
		KXmlParser parser = null;
		if (cmdCode != CREATE_PARSER) {
			parser = (KXmlParser) ObjectThing.get(argv[1]);
		}
		
		String name = argv.length>1?argv[1].toString() : null;
		switch (cmdCode) {
		case CREATE_PARSER:
			 parser = new KXmlParser();
			 return new ObjectThing().create(parser);
	
		case SET_INPUT:
			if (argv.length ==3) {
				try {
					parser.setInput(new ByteArrayInputStream(argv[2].toString().getBytes()),"ISO-8859-1");
					return new Thing("OK");
				} catch (XmlPullParserException e) {
					throw new HeclException(e.toString());
				} 
			}
			break;
		case NEXT_TAG:
			if (argv.length == 2) {
				try {
					return IntThing.create(parser.nextTag());
				} catch (Exception e) {
					throw new HeclException(e.toString());
				}
			}
			break;
		case NEXT:
			if (argv.length == 2) {
				try {
					return IntThing.create(parser.next());
				} catch (Exception e) {
					throw new HeclException(e.toString());
				}
			}
			break;
		case PARSER_REQUIRE_START:
			if (argv.length == 3) {	
				try {
					parser.require(XmlPullParser.START_TAG, null, argv[2].toString());
					return new Thing(argv[2].toString());
				} catch (Exception e) {
					throw new HeclException(e.toString());
				}
			}
			break;
		case PARSER_REQUIRE_END:
			if (argv.length == 3) {
				try {
					parser.require(XmlPullParser.END_TAG, null, argv[2].toString());
					return new Thing(argv[2].toString());
				} catch (Exception e) {
					throw new HeclException(e.toString());
				}
			}
			break;
		case NEXT_TEXT:
			if (argv.length == 2) {	
				try {
					return new Thing(parser.nextText());
				} catch (Exception e) {
					throw new HeclException(e.toString());
				}
			}
			break;
		case GET_TEXT:
			if (argv.length == 2) {	
				return new Thing(parser.getText());
			}
			break;
		case ATTR_COUNT:
			if (argv.length == 2) {	
				return IntThing.create(parser.getAttributeCount());
			}
			break;
		case ATTR_NAME:
			if (argv.length == 3) {	
				System.out.println(parser.getAttributeName(IntThing.get(argv[2])));
				return new Thing(parser.getAttributeName(IntThing.get(argv[2])));
			}
			break;
		case ATTR_VALUE:
			if (argv.length == 3) {
				return new Thing(parser.getAttributeValue(IntThing.get(argv[2])));
			}
			break;
		case ATTR_NAMESPACE:
			if (argv.length == 3) {
				return new Thing(parser.getAttributeNamespace(IntThing.get(argv[2])));
			}
			break;
		case ATTR_TYPE:
			if (argv.length == 3) {
				return new Thing(parser.getAttributeType(IntThing.get(argv[2])));
			}
			break;
		case EVENT_TYPE:
			if (argv.length == 2) {
				try {
					return IntThing.create(parser.getEventType());
				} catch (XmlPullParserException e) {
					throw new HeclException(e.toString());
				}
			}
			break;
		case GET_NAME:
			if (argv.length == 2) {	
				return new Thing(parser.getName());	
			}
			break;
		default:
			break;
		}
		return null;
	}
	
	public static void load(Interp ip) throws HeclException {
		Operator.load(ip,cmdtable);
	    }

	    public static void unload(Interp ip) throws HeclException {
		Operator.unload(ip,cmdtable);
	    }

	    private static Hashtable cmdtable = new Hashtable();

	    static {
	        cmdtable.put("kxml.create", new KXMLCmd(CREATE_PARSER,0,0));
	        cmdtable.put("kxml.input", new KXMLCmd(SET_INPUT,1,2));
	        cmdtable.put("kxml.nexttag", new KXMLCmd(NEXT_TAG,1,2));
	        cmdtable.put("kxml.next", new KXMLCmd(NEXT,1,2));
	        cmdtable.put("kxml.requirestart", new KXMLCmd(PARSER_REQUIRE_START,1,2));
	        cmdtable.put("kxml.requireend", new KXMLCmd(PARSER_REQUIRE_END,1,2));
	        cmdtable.put("kxml.gettext", new KXMLCmd(GET_TEXT,1,2));
	        cmdtable.put("kxml.nexttext", new KXMLCmd(NEXT_TEXT,1,2));
	        cmdtable.put("kxml.attrcount", new KXMLCmd(ATTR_COUNT,1,2));
	        cmdtable.put("kxml.attrname", new KXMLCmd(ATTR_NAME,1,3));
	        cmdtable.put("kxml.attrvalue", new KXMLCmd(ATTR_VALUE,1,3));
	        cmdtable.put("kxml.attrnamespace", new KXMLCmd(ATTR_NAMESPACE,1,3));
	        cmdtable.put("kxml.attrtype", new KXMLCmd(ATTR_TYPE,1,3));
	        cmdtable.put("kxml.event", new KXMLCmd(EVENT_TYPE,1,2));
	        cmdtable.put("kxml.getname", new KXMLCmd(GET_NAME,1,2));
	    }

}
