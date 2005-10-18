/* Copyright 2004-2005 David N. Welton

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
import java.util.Vector;

/**
 * The <code>CodeThing</code> class implements a chunk of "compiled" code
 * including multiple "Stanzas", or individual commands.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

public class CodeThing implements RealThing {
    /* The number of lines of commands. */
    private Vector stanzas;

    /* Mark this for substitution or not. */
    public boolean marksubst = false;

    CodeThing() {
        stanzas = new Vector();
    }

    CodeThing(Vector newstanzas) {
	stanzas = newstanzas;
    }

    /**
     * The <code>setCodeFromAny</code> method makes the Thing passed to it
     * into a CodeThing representation.
     * 
     * @param interp
     *            an <code>Interp</code> value
     * @param thing
     *            a <code>Thing</code> value
     * @exception HeclException
     *                if an error occurs
     */
    private static void setCodeFromAny(Interp interp, Thing thing)
            throws HeclException {
        RealThing realthing = thing.val;

        /* FIXME - SubstThing? */

        if (!(realthing instanceof CodeThing)) {
            CodeThing newthing = null;
            Parse hp = new Parse(interp, thing.getStringRep());
            newthing = hp.parseToCode();
            thing.setVal(newthing);
        }
    }

    /**
     * <code>get</code> returns a CodeThing object from any kind of Thing - or
     * returns an error.
     * 
     * @param interp
     *            an <code>Interp</code> value
     * @param thing
     *            a <code>Thing</code> value
     * @return a <code>CodeThing</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public static CodeThing get(Interp interp, Thing thing)
            throws HeclException {
        setCodeFromAny(interp, thing);
        return (CodeThing) thing.val;
    }


    /**
     * CodeThing's <code>deepcopy</code> method makes a copy of all
     * the stanzas, which in turn copy all their objects.
     *
     * @return a <code>RealThing</code> value
     * @exception HeclException if an error occurs
     */
    public RealThing deepcopy() throws HeclException {
	Vector deststanzas = new Vector();

	for (Enumeration e = stanzas.elements(); e.hasMoreElements();) {
            Stanza s = (Stanza) e.nextElement();
            deststanzas.addElement(s.deepcopy());
        }
        return new CodeThing(deststanzas);
    }

    /**
     * <code>doCodeSubst</code> takes a code Thing and runs it, returning the
     * result. This is used for substitution in situations like this: "foo [bar]
     * baz", where the substitution needs to be run every time, but the block
     * can't be broken up. doCodeSubst operates on the [bar] word in the above
     * case.
     * 
     * @param interp
     *            an <code>Interp</code> value
     * @param thing
     *            a <code>Thing</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException
     *                if an error occurs
     */
    protected static Thing doCodeSubst(Interp interp, Thing thing)
            throws HeclException {
        RealThing realthing = thing.val;
        Thing newthing = null;

        if (((CodeThing) realthing).marksubst) {
            Eval.eval(interp, thing);
            newthing = interp.result;
        } else {
            newthing = thing;
        }
        return newthing;
    }

    /**
     * <code>doSubstSubst</code> runs substitutions on things of the
     * SubstThing type, which means $foo or &foo in Hecl.
     * 
     * @param interp
     *            an <code>Interp</code> value
     * @param thing
     *            a <code>Thing</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException
     *                if an error occurs
     */
    protected static Thing doSubstSubst(Interp interp, Thing thing)
            throws HeclException {
        return SubstThing.get(interp, thing);
    }

    /**
     * <code>doGroupSubst</code> runs substitutions on 'groups' of things,
     * such as "foo $foo [foo]". The group can't be broken up, so it needs to be
     * substituted together by subst'ing the individual components.
     * 
     * @param interp
     *            an <code>Interp</code> value
     * @param thing
     *            a <code>Thing</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException
     *                if an error occurs
     */
    protected static Thing doGroupSubst(Interp interp, Thing thing)
            throws HeclException {
        RealThing realthing = thing.val;
        StringBuffer result = new StringBuffer("");
        Vector v = GroupThing.get(thing);

        /*
         * As a special case, one element groups get turned into regular things.
         */
        if (v.size() == 1) {
            StringThing.get(thing);
            return thing;
        } else {
            for (Enumeration e = v.elements(); e.hasMoreElements();) {
                Thing t = (Thing) e.nextElement();

                realthing = t.val;
                if (realthing instanceof GroupThing) {
                    result.append(doGroupSubst(interp, t).getStringRep());
                } else if (realthing instanceof SubstThing) {
                    result.append(doSubstSubst(interp, t).getStringRep());
                } else if (realthing instanceof CodeThing) {
                    result.append(doCodeSubst(interp, t).getStringRep());
                } else {
                    result.append(t.getStringRep());
                }
            }
        }
        return new Thing(new StringThing(result));
    }

    /**
     * The <code>addStanza</code> method adds a new command and its arguments.
     * 
     * @param interp
     *            <code>Interp</code> value
     * @param argv
     *            <code>Thing[]</code> value
     */
    public void addStanza(Interp interp, Thing []argv) {
	stanzas.addElement(
	    new Stanza((Command)interp.commands.get(argv[0].toString()),
		       argv));
    }

    /**
     * The <code>run</code> method runs the CodeThing.
     * 
     * @param interp
     *            <code>Interp</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public void run(Interp interp) throws HeclException {
        //System.out.println("RUNNING: " + this.getStringRep() + "</RUNNING>");
        for (Enumeration e = stanzas.elements(); e.hasMoreElements();) {
            Stanza s = (Stanza) e.nextElement();
            s.run(interp);
        }
    }

    /**
     * The <code>getStringRep</code> method returns a String representation of
     * the commands it represents.
     * 
     * @return a <code>String</code> value.
     */
    public String getStringRep() {
        StringBuffer out = new StringBuffer();

        for (Enumeration e = stanzas.elements(); e.hasMoreElements();) {
            Stanza s = (Stanza) e.nextElement();
            //	    out.append("[");
            out.append(s.toString() + ";\n");
            //	    out.append("]\n");
        }
        return out.toString();
    }
}
