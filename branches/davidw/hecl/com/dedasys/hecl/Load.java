package com.dedasys.hecl;

abstract public class Load {
    public Thing getscript() throws HeclException {
	StringBuffer input = new StringBuffer();
	return new Thing(input);
    }
}
