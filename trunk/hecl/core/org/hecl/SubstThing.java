/* Copyright 2004-2006 David N. Welton

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

/* $Id$ */

package org.hecl;

/**
 * The <code>SubstThing</code> class represents things that must be
 * substituted - $foo or &foo for example.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */
public class SubstThing implements RealThing {
    public long cacheversion = -1;

    private Thing val = null;

    private String varName = null;

    public SubstThing() {
    }

    /**
     * Creates a new <code>SubstThing</code> instance from a string,
     * which is the variable name to reference.
     *
     * @param s a <code>String</code> value
     */
    public SubstThing(String s) {
        varName = s;
    }

    public String thingclass() {
	return "subst";
    }

    /**
     * <code>setSubstFromAny</code> creates a Subst object from another type.
     *
     * @param interp an <code>Interp</code> value
     * @param thing a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    private static void setSubstFromAny(Interp interp, Thing thing)
            throws HeclException {
        RealThing realthing = thing.getVal();

        if (realthing instanceof SubstThing) {
            /* Don't need to modify it. */
        } else {
            thing.setVal(new SubstThing(thing.toString()));
        }
    }

    /**
     * <code>get</code> returns the *value* of a SubstThing - in other words,
     * the Thing that its varName is pointing to. We use a cacheing mechanism
     * devised by Salvatore Sanfilippo to avoid unnecessary lookups.
     *
     * @param interp an <code>Interp</code> value
     * @param thing a <code>Thing</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    public static Thing get(Interp interp, Thing thing) throws HeclException {
        setSubstFromAny(interp, thing);
        SubstThing getcopy = (SubstThing)thing.getVal();


        if (getcopy.cacheversion != interp.cacheversion) {
            getcopy.cacheversion = interp.cacheversion;
            getcopy.val = interp.getVar(getcopy.varName);
        } /* else {
	    System.out.println("CACHE HIT");
	}  */

	if (getcopy.val.copy) {
	    /* If the Thing value of the substthing is something
	     * that should be copied, we copy it so that we don't
	     * mess up the original.  See the set-3 test, for
	     * example. */
	    Thing copy = getcopy.val.deepcopy();
	    copy.copy = false;
	    interp.setVar(getcopy.varName, copy);
	    return copy;
	}
	return getcopy.val;
    }

    /**
     * <code>deepcopy</code> returns a copy of the SubstThing.
     *
     * @return a <code>RealThing</code> value
     */
    public RealThing deepcopy() {
        return new SubstThing(varName);
    }

    /**
     * <code>getStringRep</code> returns a string representation of the
     * SubstThing.
     *
     * @return a <code>String</code> value
     */
    public String getStringRep() {
	// Tricky: make wtkpreprocess happy by splitting the $ and the {
	return "$"+"{" + varName + "}";
    }
}
