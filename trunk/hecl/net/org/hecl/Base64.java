/* Copyright 2005-2006 by data2c.com

Authors:
Wolfgang S. Kechel - wolfgang.kechel@data2c.com
Jörn Marcks - joern.marcks@data2c.com

Wolfgang S. Kechel, Jörn Marcks

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

package org.hecl.net;

import java.io.UnsupportedEncodingException;

public class Base64 {
    static private int lineLength = 72; // max chars per line, a multiple of 4
    static private final char fillchar = '=';
    static private char[] vc = new char[64];
    static private int[] cv = new int[256];

    static {
	// build translate vc table
	// 0..25 -> 'A'..'Z'
	for (int i = 0; i <= 25; i++)
	    vc[i] = (char)('A' + i);
	// 26..51 -> 'a'..'z'
	for (int i = 0; i <= 25; i++)
	    vc[i + 26] = (char)('a' + i);
	// 52..61 -> '0'..'9'
	for (int i = 0; i <= 9; i++)
	    vc[i + 52] = (char)('0' + i);
	
	vc[62] = '+';
	vc[63] = '/';
	
	// build translate cv table
	for (int i = 0; i < 256; i++) {
	    cv[i] = -1;
	}
	
	for (int i = 0; i < 64; i++) {
	    cv[vc[i]] = i;
	}
	
	cv[fillchar] = -2;

	vc = vc;
	cv = cv;
    }

    
    static public String encode(byte[] b) {
	return encode2sb(b).toString();
    }
    
    static public StringBuffer encode2sb(byte[] b) {
	// Each group or partial group of 3 bytes becomes four chars
	// covered quotient
	int outputLength = ((b.length + 2) / 3) * 4;

	// account for trailing newlines, on all but the very last line
	if (lineLength != 0) {
	    int lines =  (outputLength + lineLength - 1) / lineLength - 1;
	    if (lines > 0) {
		outputLength += lines;
	    }
	}

	// must be local for recursion to work.
	StringBuffer sb = new StringBuffer(outputLength);

	// must be local for recursion to work.
	int linePos = 0;

	// first deal with even multiples of 3 bytes.
	int len = (b.length / 3) * 3;
	int leftover = b.length - len;
	for (int i = 0; i < len; i += 3) {
	    // Start a new line if next 4 chars won't fit on the current line
	    // We can't encapsulete the following code since the variable need to
	    // be local to this incarnation of encode.
	    linePos += 4;
	    if (linePos > lineLength) {
		linePos = 4;
	    }

	    // get next three bytes in unsigned form lined up,
	    // in big-endian order
	    int combined = b[i] & 0xff;
	    combined <<= 8;
	    combined |= b[i + 1] & 0xff;
	    combined <<= 8;
	    combined |= b[i + 2] & 0xff;

	    // break those 24 bits into a 4 groups of 6 bits,
	    // working LSB to MSB.
	    int c3 = combined & 0x3f;
	    combined >>>= 6;
	    int c2 = combined & 0x3f;
	    combined >>>= 6;
	    int c1 = combined & 0x3f;
	    combined >>>= 6;
	    int c0 = combined & 0x3f;

	    // Translate into the equivalent alpha character
	    // emitting them in big-endian order.
	    sb.append(vc[c0]);
	    sb.append(vc[c1]);
	    sb.append(vc[c2]);
	    sb.append(vc[c3]);
	}

	// deal with leftover bytes
	switch (leftover) {
	case 0:
	default:
            break;

	case 1:
            // One leftover byte generates xx==
            // Start a new line if next 4 chars won't fit on the current line
            linePos += 4;
            if (linePos > lineLength) {
		linePos = 4;
	    }

            // Handle this recursively with a faked complete triple.
            // Throw away last two chars and replace with ==
            sb.append(encode(new byte[] {b[len], 0, 0}).substring(0, 2));
            sb.append(fillchar);
            sb.append(fillchar);
            break;

	case 2:
            // Two leftover bytes generates xxx=
            // Start a new line if next 4 chars won't fit on the current line
            linePos += 4;
            if (linePos > lineLength) {
		linePos = 4;
	    }
            // Handle this recursively with a faked complete triple.
            // Throw away last char and replace with =
            sb.append(encode(new byte[] {b[len], b[len+1], 0}).substring(0, 3));
            sb.append(fillchar);
            break;

	}

	if (outputLength != sb.length()) {
	    //System.out.println("oops: minor program flaw: output length mis-estimated");
	    //System.out.println("estimate:" + outputLength);
	    //System.out.println("actual:" + sb.length());
	}
	return sb;
    }

    /**
     * decode a well-formed complete Base64 string back into an array of bytes.
     * It must have an even multiple of 4 data characters (not counting \n),
     * padded out with = as needed.
     */
    static public byte[] decode(String s) {
	// estimate worst case size of output array, no embedded newlines.
	byte[] b = new byte[(s.length() / 4) * 3];

	// tracks where we are in a cycle of 4 input chars.
	int cycle = 0;

	// where we combine 4 groups of 6 bits and take apart as 3 groups of 8.
	int combined = 0;

	// how many bytes we have prepared.
	int j = 0;
	// will be an even multiple of 4 chars, plus some embedded \n
	int len = s.length();
	int dummies = 0;
	for (int i = 0; i < len; i++) {
	    int c = s.charAt(i);
	    int value  = (c <= 255) ? cv[c] : -1;
	    switch (value) {
	    case -1:
		break;
		
	    case -2:
		value = 0;
		dummies++;
		// fallthrough
	    default:
		/* regular value character */
		switch (cycle) {
		case 0:
		    combined = value;
		    cycle = 1;
		    break;
		    
		case 1:
		    combined <<= 6;
		    combined |= value;
		    cycle = 2;
		    break;
		    
		case 2:
		    combined <<= 6;
		    combined |= value;
		    cycle = 3;
		    break;
		    
		case 3:
		    combined <<= 6;
		    combined |= value;
		    // we have just completed a cycle of 4 chars.
		    // the four 6-bit values are in combined in big-endian order
		    // peel them off 8 bits at a time working lsb to msb
		    // to get our original 3 8-bit bytes back
		    b[j + 2] = (byte)combined;
		    combined >>>= 8;
		    b[j + 1] = (byte)combined;
		    combined >>>= 8;
		    b[j] = (byte)combined;
		    j += 3;
		    cycle = 0;
		    break;
		}
		break;
	    }
	}

	j -= dummies;
	if (b.length != j) {
	    byte[] b2 = new byte[j];
	    System.arraycopy(b, 0, b2, 0, j);
	    b = b2;
	}
	return b;
    }

    public static String decode(String s,String encoding) throws UnsupportedEncodingException {
	return new String(decode(s),encoding);
    }

    static public void setLineLength(int length) {
	lineLength = (length / 4) * 4;
    }
}
