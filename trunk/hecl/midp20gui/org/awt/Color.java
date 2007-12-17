/*
 * Copyright (C) 2005, 2006 data2c GmbH (www.data2c.com)
 *
 * Author: Wolfgang S. Kechel - wolfgang.kechel@data2c.com
 *
 * J2ME version of java.awt.Color. 
 */
//#ifndef j2se

package org.awt;

/**
 * The <code>Color</code> class is used encapsulate colors in the default
 * sRGB color space or colors in arbitrary color spaces identified by a
 * {@link ColorSpace}.  Every color has an implicit alpha value of 1.0 or
 * an explicit one provided in the constructor.  The alpha value
 * defines the transparency of a color and can be represented by
 * a float value in the range 0.0&nbsp;-&nbsp;1.0 or 0&nbsp;-&nbsp;255.
 * An alpha value of 1.0 or 255 means that the color is completely
 * opaque and an alpha value of 0 or 0.0 means that the color is 
 * completely transparent.
 * When constructing a <code>Color</code> with an explicit alpha or
 * getting the color/alpha components of a <code>Color</code>, the color
 * components are never premultiplied by the alpha component.
 * <p>
 * The default color space for the Java 2D(tm) API is sRGB, a proposed
 * standard RGB color space.  For further information on sRGB,
 * see <A href="http://www.w3.org/pub/WWW/Graphics/Color/sRGB.html">
 * http://www.w3.org/pub/WWW/Graphics/Color/sRGB.html
 * </A>.
 * <p>
 */
public class Color implements Transparency /*Paint, java.io.Serializable*/ {
    
    /**
     * The color white.  In the default sRGB space.
     */
    public final static Color white = new Color(255, 255, 255);
    public final static Color WHITE = white;

    /**
     * The color light gray.  In the default sRGB space.
     */
    public final static Color lightGray = new Color(192, 192, 192);
    public final static Color LIGHT_GRAY = lightGray;

    /**
     * The color gray.  In the default sRGB space.
     */
    public final static Color gray = new Color(128, 128, 128);
    public final static Color GRAY = gray;

    /**
     * The color dark gray.  In the default sRGB space.
     */
    public final static Color darkGray = new Color(64, 64, 64);
    public final static Color DARK_GRAY = darkGray;

    /**
     * The color black.  In the default sRGB space.
     */
    public final static Color black = new Color(0, 0, 0);
    public final static Color BLACK = black;
    
    /**
     * The color red.  In the default sRGB space.
     */
    public final static Color red = new Color(255, 0, 0);
    public final static Color RED = red;

    /**
     * The color pink.  In the default sRGB space.
     */
    public final static Color pink = new Color(255, 175, 175);
    public final static Color PINK = pink;

    /**
     * The color orange.  In the default sRGB space.
     */
    public final static Color orange = new Color(255, 200, 0);
    public final static Color ORANGE = orange;

    /**
     * The color yellow.  In the default sRGB space.
     */
    public final static Color yellow = new Color(255, 255, 0);
    public final static Color YELLOW = yellow;

    /**
     * The color green.  In the default sRGB space.
     */
    public final static Color green = new Color(0, 255, 0);
    public final static Color GREEN = green;

    /**
     * The color magenta.  In the default sRGB space.
     */
    public final static Color magenta = new Color(255, 0, 255);
    public final static Color MAGENTA = magenta;

    /**
     * The color cyan.  In the default sRGB space.
     */
    public final static Color cyan = new Color(0, 255, 255);
    public final static Color CYAN = cyan;

    /**
     * The color blue.  In the default sRGB space.
     */
    public final static Color blue = new Color(0, 0, 255);
    public final static Color BLUE = blue;

    /**
     * The color value.
     * @serial
     * @see #getRGB
     */
    int value;

    /**
     * Checks the color integer components supplied for validity.
     * Throws an {@link IllegalArgumentException} if the value is out of
     * range.
     * @param r the Red component
     * @param g the Green component
     * @param b the Blue component
     **/
    private static void testColorValueRange(int r, int g, int b, int a) {
        boolean rangeError = false;
	String badComponentString = "";
	
	if ( a < 0 || a > 255) {
	    rangeError = true;
	    badComponentString = badComponentString + " Alpha";
	}
        if ( r < 0 || r > 255) {
	    rangeError = true;
	    badComponentString = badComponentString + " Red";
	}
	if ( g < 0 || g > 255) {
	    rangeError = true;
	    badComponentString = badComponentString + " Green";
	}
	if ( b < 0 || b > 255) {
	    rangeError = true;
	    badComponentString = badComponentString + " Blue";
	}
	if ( rangeError == true ) {
	throw new IllegalArgumentException("Color parameter outside of expected range:"
					   + badComponentString);
	}
    }


    /**
     * Creates an opaque sRGB color with the specified red, green, 
     * and blue values in the range (0 - 255).  
     * The actual color used in rendering depends
     * on finding the best match given the color space 
     * available for a given output device.  
     * Alpha is defaulted to 255.
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @see #getRed
     * @see #getGreen
     * @see #getBlue
     * @see #getRGB
     */
    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    /**
     * Creates an sRGB color with the specified red, green, blue, and alpha
     * values in the range (0 - 255).
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @param a the alpha component
     * @see #getRed
     * @see #getGreen
     * @see #getBlue
     * @see #getAlpha
     * @see #getRGB
     */
    public Color(int r, int g, int b, int a) {
        value = ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF) << 0);
	testColorValueRange(r,g,b,a);
    }

    /**
     * Creates an opaque sRGB color with the specified combined RGB value
     * consisting of the red component in bits 16-23, the green component
     * in bits 8-15, and the blue component in bits 0-7.  The actual color
     * used in rendering depends on finding the best match given the
     * color space available for a particular output device.  Alpha is
     * defaulted to 255.
     * @param rgb the combined RGB components
     * @see java.awt.image.ColorModel#getRGBdefault
     * @see #getRed
     * @see #getGreen
     * @see #getBlue
     * @see #getRGB
     */
    public Color(int rgb) {
        value = 0xff000000 | rgb;
    }

    /**
     * Creates an sRGB color with the specified combined RGBA value consisting
     * of the alpha component in bits 24-31, the red component in bits 16-23,
     * the green component in bits 8-15, and the blue component in bits 0-7.
     * If the <code>hasalpha</code> argument is <code>false</code>, alpha
     * is defaulted to 255.
     * @param rgba the combined RGBA components
     * @param hasalpha <code>true</code> if the alpha bits are valid;
     * <code>false</code> otherwise
     * @see java.awt.image.ColorModel#getRGBdefault
     * @see #getRed
     * @see #getGreen
     * @see #getBlue
     * @see #getAlpha
     * @see #getRGB
     */
    public Color(int rgba, boolean hasalpha) {
        if (hasalpha) {
            value = rgba;
        } else {
            value = 0xff000000 | rgba;
        }
    }


    /**
     * Returns the red component in the range 0-255 in the default sRGB
     * space.
     * @return the red component.
     * @see #getRGB
     */
    public int getRed() {
	return (getRGB() >> 16) & 0xFF;
    }

    /**
     * Returns the green component in the range 0-255 in the default sRGB
     * space.
     * @return the green component.
     * @see #getRGB
     */
    public int getGreen() {
	return (getRGB() >> 8) & 0xFF;
    }

    /**
     * Returns the blue component in the range 0-255 in the default sRGB
     * space.
     * @return the blue component.
     * @see #getRGB
     */
    public int getBlue() {
	return (getRGB() >> 0) & 0xFF;
    }

    /**
     * Returns the alpha component in the range 0-255.
     * @return the alpha component.
     * @see #getRGB
     */
    public int getAlpha() {
        return (getRGB() >> 24) & 0xff;
    }

    /**
     * Returns the RGB value representing the color in the default sRGB
     * {@link ColorModel}.
     * (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are
     * blue).
     * @return the RGB value of the color in the default sRGB
     *         <code>ColorModel</code>.
     * @see java.awt.image.ColorModel#getRGBdefault
     * @see #getRed
     * @see #getGreen
     * @see #getBlue
     * @since JDK1.0
     */
    public int getRGB() {
	return value;
    }

    private static final double FACTOR = 0.7;

    /**
     * Creates a new <code>Color</code> that is a brighter version of this
     * <code>Color</code>.
     * <p>
     * This method applies an arbitrary scale factor to each of the three RGB 
     * components of this <code>Color</code> to create a brighter version
     * of this <code>Color</code>. Although <code>brighter</code> and
     * <code>darker</code> are inverse operations, the results of a
     * series of invocations of these two methods might be inconsistent
     * because of rounding errors. 
     * @return     a new <code>Color</code> object that is  
     *                 a brighter version of this <code>Color</code>.
     * @see        java.awt.Color#darker
     * @since      JDK1.0
     */
    public Color brighter() {
        int r = getRed();
        int g = getGreen();
        int b = getBlue();

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int)(1.0/(1.0-FACTOR));
        if ( r == 0 && g == 0 && b == 0) {
           return new Color(i, i, i);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        return new Color(Math.min((int)(r/FACTOR), 255),
                         Math.min((int)(g/FACTOR), 255),
                         Math.min((int)(b/FACTOR), 255));
    }

    /**
     * Creates a new <code>Color</code> that is a darker version of this
     * <code>Color</code>.
     * <p>
     * This method applies an arbitrary scale factor to each of the three RGB 
     * components of this <code>Color</code> to create a darker version of
     * this <code>Color</code>.  Although <code>brighter</code> and
     * <code>darker</code> are inverse operations, the results of a series 
     * of invocations of these two methods might be inconsistent because
     * of rounding errors. 
     * @return  a new <code>Color</code> object that is 
     *                    a darker version of this <code>Color</code>.
     * @see        java.awt.Color#brighter
     * @since      JDK1.0
     */
    public Color darker() {
	return new Color(Math.max((int)(getRed()  *FACTOR), 0), 
			 Math.max((int)(getGreen()*FACTOR), 0),
			 Math.max((int)(getBlue() *FACTOR), 0));
    }

    /**
     * Computes the hash code for this <code>Color</code>.
     * @return     a hash code value for this object.
     * @since      JDK1.0
     */
    public int hashCode() {
	return value;
    }

    /**
     * Determines whether another object is equal to this 
     * <code>Color</code>.
     * <p>
     * The result is <code>true</code> if and only if the argument is not 
     * <code>null</code> and is a <code>Color</code> object that has the same 
     * red, green, blue, and alpha values as this object. 
     * @param       obj   the object to test for equality with this
     *				<code>Color</code>
     * @return      <code>true</code> if the objects are the same; 
     *                             <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public boolean equals(Object obj) {
        return obj instanceof Color && ((Color)obj).value == this.value;
    }

    /**
     * Returns a string representation of this <code>Color</code>. This
     * method is intended to be used only for debugging purposes.  The 
     * content and format of the returned string might vary between 
     * implementations. The returned string might be empty but cannot 
     * be <code>null</code>.
     * 
     * @return  a string representation of this <code>Color</code>.
     */
    public String toString() {
        return getClass().getName() + "[r=" + getRed() + ",g=" + getGreen() + ",b=" + getBlue() + "]";
    }

    /**
     * Converts a <code>String</code> to an integer and returns the 
     * specified opaque <code>Color</code>. This method handles string
     * formats that are used to represent octal and hexidecimal numbers.
     * @param      nm a <code>String</code> that represents 
     *                            an opaque color as a 24-bit integer
     * @return     the new <code>Color</code> object.
     * @see        java.lang.Integer#decode
     * @exception  NumberFormatException  if the specified string cannot
     *                      be interpreted as a decimal, 
     *                      octal, or hexidecimal integer.
     * @since      JDK1.1
     */
    public static Color decode(String nm) throws NumberFormatException {
	Integer intval = Integer.valueOf(nm);
	int i = intval.intValue();
	return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
    }


//#ifdef notdef
    /**
     * Finds a color in the system properties. 
     * <p>
     * The argument is treated as the name of a system property to 
     * be obtained. The string value of this property is then interpreted 
     * as an integer which is then converted to a <code>Color</code>
     * object. 
     * <p>
     * If the specified property is not found or could not be parsed as 
     * an integer then <code>null</code> is returned. 
     * @param    nm the name of the color property
     * @return   the <code>Color</code> converted from the system 
     * 		property.
     * @see      java.lang.System#getProperty(java.lang.String)
     * @see      java.lang.Integer#getInteger(java.lang.String)
     * @see      java.awt.Color#Color(int)
     * @since    JDK1.0
     */
    public static Color getColor(String nm) {
	return getColor(nm, null);
    }


    /**
     * Finds a color in the system properties. 
     * <p>
     * The first argument is treated as the name of a system property to 
     * be obtained. The string value of this property is then interpreted 
     * as an integer which is then converted to a <code>Color</code>
     * object. 
     * <p>
     * If the specified property is not found or cannot be parsed as 
     * an integer then the <code>Color</code> specified by the second
     * argument is returned instead. 
     * @param    nm the name of the color property
     * @param    v    the default <code>Color</code>
     * @return   the <code>Color</code> converted from the system
     *		property, or the specified <code>Color</code>.
     * @see      java.lang.System#getProperty(java.lang.String)
     * @see      java.lang.Integer#getInteger(java.lang.String)
     * @see      java.awt.Color#Color(int)
     * @since    JDK1.0
     */
    public static Color getColor(String nm, Color v) {
	Integer intval = Integer.getInteger(nm);
	if (intval == null) {
	    return v;
	}
	int i = intval.intValue();
	return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
    }

    /**
     * Finds a color in the system properties. 
     * <p>
     * The first argument is treated as the name of a system property to 
     * be obtained. The string value of this property is then interpreted 
     * as an integer which is then converted to a <code>Color</code>
     * object. 
     * <p>
     * If the specified property is not found or could not be parsed as 
     * an integer then the integer value <code>v</code> is used instead, 
     * and is converted to a <code>Color</code> object.
     * @param    nm  the name of the color property
     * @param    v   the default color value, as an integer
     * @return   the <code>Color</code> converted from the system
     *		property or the <code>Color</code> converted from
     *		the specified integer.
     * @see      java.lang.System#getProperty(java.lang.String)
     * @see      java.lang.Integer#getInteger(java.lang.String)
     * @see      java.awt.Color#Color(int)
     * @since    JDK1.0
     */
    public static Color getColor(String nm, int v) {
	Integer intval = Integer.getInteger(nm);
	int i = (intval != null) ? intval.intValue() : v;
	return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, (i >> 0) & 0xFF);
    }
//#endif

    /**
     * Converts the components of a color, as specified by the HSB 
     * model, to an equivalent set of values for the default RGB model. 
     * <p>
     * The <code>saturation</code> and <code>brightness</code> components
     * should be floating-point values between zero and one
     * (numbers in the range 0.0-1.0).  The <code>hue</code> component
     * can be any floating-point number.  The floor of this number is
     * subtracted from it to create a fraction between 0 and 1.  This
     * fractional number is then multiplied by 360 to produce the hue
     * angle in the HSB color model.
     * <p>
     * The integer that is returned by <code>HSBtoRGB</code> encodes the 
     * value of a color in bits 0-23 of an integer value that is the same 
     * format used by the method {@link #getRGB() <code>getRGB</code>}.
     * This integer can be supplied as an argument to the
     * <code>Color</code> constructor that takes a single integer argument. 
     * @param     hue   the hue component of the color
     * @param     saturation   the saturation of the color
     * @param     brightness   the brightness of the color
     * @return    the RGB value of the color with the indicated hue, 
     *                            saturation, and brightness.
     * @see       java.awt.Color#getRGB()
     * @see       java.awt.Color#Color(int)
     * @see       java.awt.image.ColorModel#getRGBdefault()
     * @since     JDK1.0
     */
    public static int HSBtoRGB(float hue, float saturation, float brightness) {
	int r = 0, g = 0, b = 0;
    	if (saturation == 0) {
	    r = g = b = (int) (brightness * 255.0f + 0.5f);
	} else {
	    float h = (hue - (float)Math.floor(hue)) * 6.0f;
	    float f = h - (float)java.lang.Math.floor(h);
	    float p = brightness * (1.0f - saturation);
	    float q = brightness * (1.0f - saturation * f);
	    float t = brightness * (1.0f - (saturation * (1.0f - f)));
	    switch ((int) h) {
	    case 0:
		r = (int) (brightness * 255.0f + 0.5f);
		g = (int) (t * 255.0f + 0.5f);
		b = (int) (p * 255.0f + 0.5f);
		break;
	    case 1:
		r = (int) (q * 255.0f + 0.5f);
		g = (int) (brightness * 255.0f + 0.5f);
		b = (int) (p * 255.0f + 0.5f);
		break;
	    case 2:
		r = (int) (p * 255.0f + 0.5f);
		g = (int) (brightness * 255.0f + 0.5f);
		b = (int) (t * 255.0f + 0.5f);
		break;
	    case 3:
		r = (int) (p * 255.0f + 0.5f);
		g = (int) (q * 255.0f + 0.5f);
		b = (int) (brightness * 255.0f + 0.5f);
		break;
	    case 4:
		r = (int) (t * 255.0f + 0.5f);
		g = (int) (p * 255.0f + 0.5f);
		b = (int) (brightness * 255.0f + 0.5f);
		break;
	    case 5:
		r = (int) (brightness * 255.0f + 0.5f);
		g = (int) (p * 255.0f + 0.5f);
		b = (int) (q * 255.0f + 0.5f);
		break;
	    }
	}
	return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
    }

    /**
     * Returns the transparency mode for this <code>Color</code>.  This is
     * required to implement the <code>Paint</code> interface.
     * @return this <code>Color</code> object's transparency mode.
     * @see Transparency
     * @see #createContext
     */
    public int getTransparency() {
        int alpha = getAlpha();
        if (alpha == 0xff) {
            return Transparency.OPAQUE;
        }
        else if (alpha == 0) {
            return Transparency.BITMASK;
        }
        else {
            return Transparency.TRANSLUCENT;
        }
    }

}
//#endif
