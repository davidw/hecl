package com.dedasys.hecl;

public class StringThing implements RealThing {

    private StringBuffer val;

    public StringThing() {
	val = new StringBuffer("");
    }

    public StringThing(String s) {
	val = new StringBuffer(s);
    }

    public StringThing(StringBuffer sb) {
	val = sb;
    }

    private static void setStringFromAny(Thing thing) {
	RealThing realthing = thing.val;

	if (!(realthing instanceof StringThing)) {
	    thing.val = new StringThing(thing.toString());
	}
    }

    public static String get(Thing thing) {
	setStringFromAny(thing);
	return thing.toString();
    }

    public RealThing deepcopy() {
	StringBuffer newsb = new StringBuffer();
	newsb.append(val.toString());
	return new StringThing(newsb);
    }

    public String toString() {
	return val.toString();
    }

    public void append(char ch) {
	val.append(ch);
    }

    public void append(String str) {
	val.append(str);
    }
}
