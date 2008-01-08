/* Copyright 2005-2006 David N. Welton

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

package org.hecl;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The <code>Properties</code> class is used to parse command line
 * arguments.  Its basic usage pattern is like so: a new Properties is
 * instantiated with default properties and values, then setProps is
 * called with argv.  At that point the rest of the command can go on,
 * and for every prop that's needed, it can be fetched with getProp.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class Properties {
    protected Hashtable props;

    /**
     * Creates a new <code>Properties</code> instance with no default
     * properties.
     *
     */
    public Properties () {
	props = new Hashtable();
    }

    /**
     * Creates a new <code>Properties</code> instance with default
     * properties and their values.
     *
     * @param defaultprops an <code>Object[]</code> value
     */
    public Properties (Object [] defaultprops) {
	props = new Hashtable();
	for (int i = 0; i < defaultprops.length; i+=2) {
	    props.put((String)defaultprops[i], (Thing)defaultprops[i+1]);
	}
    }

    /**
     * The <code>setProps</code> method sets properties with their
     * values from the command line argv.  The number of Things
     * handled must be even.
     *
     * @param argv a <code>Thing[]</code> value
     * @param offset an <code>int</code> value
     * @exception HeclException if an error occurs
     */
    public void setProps(Thing[] argv, int offset)
	throws HeclException {

	if ((argv.length - offset) % 2 != 0) {
	    throw new HeclException("Properties must be name-value pairs");
	}

	for(int i = offset; i < argv.length; i +=2) {
	    setProp(argv[i].toString(), argv[i+1]);
	}
    }

    /**
     * The <code>setProp</code> method sets a single property to some
     * value.
     *
     * @param name a <code>String</code> value
     * @param val a <code>Thing</code> value
     */
    public void setProp(String name, Thing val) {
	props.put(name.toLowerCase(), (Object)val);
    }

    /**
     * The <code>getProp</code> method fetches the value of a
     * property.
     *
     * @param name a <code>String</code> value
     * @return a <code>Thing</code> value
     */
    public Thing getProp(String name) {
	return (Thing)props.get(name);
    }


    /**
     * The <code>existsProp</code> method is used to determine if a
     * property exists or not.
     *
     * @param name a <code>String</code> value
     * @return a <code>boolean</code> value
     */
    public boolean existsProp(String name) {
	return props.containsKey(name);
    }


    /**
     * <code>delProp</code> removes a property from the property set.
     *
     * @param name a <code>String</code> value
     */
    public void delProp(String name) {
	props.remove(name);
    }

    /**
     * <code>getProps</code> converts the properties back into an
     * array of <code>Thing</code>s.
     *
     * @return an array of <code>Thing</code>s, being property name
     * and property value.
     */
    public Thing[] getProps() {
	int n = props.size();
	Thing[] t = new Thing[2*n];

	Enumeration names = props.keys();
	Enumeration vals = props.elements();
	int i = 0;
	while(names.hasMoreElements()) {
	    t[i++] = new Thing((String)names.nextElement());
	    t[i++] = (Thing)vals.nextElement();
	}
	return t;
    }
}
