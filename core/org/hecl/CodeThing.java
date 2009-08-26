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

    public String thingclass() {
	return "code";
    }

    /**
     * The <code>setCodeFromAny</code> method makes the Thing passed to it
     * into a CodeThing representation.
     * 
     * @param interp an <code>Interp</code> value
     * @param thing a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    private static void setCodeFromAny(Interp interp, Thing thing)
            throws HeclException {
        RealThing realthing = thing.getVal();
	CodeThing newthing;
        /* FIXME - SubstThing? */

        if (realthing instanceof CodeThing) {
	    return;
	} else if (realthing instanceof ListThing) {
	    newthing = new CodeThing();
	    Vector v = ListThing.get(thing);
	    int sz = v.size();
	    Thing []argv = new Thing[sz];
	    for (int i = 0; i < sz; i++) {
		argv[i] = (Thing)v.elementAt(i);
	    }
	    /* FIXME - addstanza lineno */
	    newthing.addStanza(interp, argv, -1);
	} else {
	    Parse hp = new Parse(interp, thing.toString());
            newthing = hp.parseToCode();
        }
	thing.setVal(newthing);
    }

    /**
     * <code>get</code> returns a CodeThing object from any kind of Thing - or
     * returns an error.
     * 
     * @param interp an <code>Interp</code> value
     * @param thing a <code>Thing</code> value
     * @return a <code>CodeThing</code> value
     * @exception HeclException
     *                if an error occurs
     */
    public static CodeThing get(Interp interp, Thing thing)
            throws HeclException {
        setCodeFromAny(interp, thing);
        return (CodeThing)thing.getVal();
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
            Stanza s = (Stanza)e.nextElement();
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
     * @param interp an <code>Interp</code> value
     * @param thing a <code>Thing</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException
     *                if an error occurs
     */
    protected static Thing doCodeSubst(Interp interp, Thing thing)
            throws HeclException {
        RealThing realthing = thing.getVal();

        return (((CodeThing)realthing).marksubst) ? interp.eval(thing) : thing;
    }

    /**
     * <code>doSubstSubst</code> runs substitutions on things of the
     * SubstThing type, which means $foo or &foo in Hecl.
     * 
     * @param interp an <code>Interp</code> value
     * @param thing a <code>Thing</code> value
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
     * @param interp an <code>Interp</code> value
     * @param thing a <code>Thing</code> value
     * @return a <code>Thing</code> value
     * @exception HeclException if an error occurs
     */
    protected static Thing doGroupSubst(Interp interp, Thing thing)
            throws HeclException {
        RealThing realthing = thing.getVal();
        StringBuffer result = new StringBuffer("");
        Vector v = GroupThing.get(thing);

	Thing t = null;

	for (Enumeration e = v.elements(); e.hasMoreElements();) {
	    t = (Thing) e.nextElement();

	    realthing = t.getVal();
	    if (realthing instanceof GroupThing) {
		result.append(doGroupSubst(interp, t).toString());
	    } else if (realthing instanceof SubstThing) {
		result.append(doSubstSubst(interp, t).toString());
		/* System.out.println("result is " + result);  */
	    } else if (realthing instanceof CodeThing) {
		result.append(doCodeSubst(interp, t).toString());
	    } else {
		result.append(t.toString());
	    }
	}

        return new Thing(result.toString());
    }

    /**
     * The <code>addStanza</code> method adds a new command and its arguments.
     * 
     * @param interp <code>Interp</code> value
     * @param argv <code>Thing[]</code> value
     */
    public void addStanza(Interp interp, Thing []argv, int lineno) {
	stanzas.addElement(new Stanza(null, argv, lineno));
    }

    /**
     * The <code>run</code> method runs the CodeThing.
     * 
     * @param interp <code>Interp</code> value
     * @exception HeclException if an error occurs
     */
    public synchronized Thing run(Interp interp) throws HeclException {
 	//++level;
	//System.err.println("starting CodeThing run" + level);
        //System.out.println("RUNNING: " + this.getStringRep() +"</RUNNING>");
	Thing res = null;
	for (Enumeration e = stanzas.elements(); e.hasMoreElements();) {
	    Stanza s = (Stanza) e.nextElement();
	    res = s.run(interp);
	}
	if(res == null)
	    res = Thing.emptyThing();
	//System.err.println("ending CodeThing run" + level);
	//--level;
	return res;
    }

    /**
     * The <code>getStringRep</code> method returns a String representation of
     * the commands it represents.
     *
     * @return a <code>String</code> value.
     */
    public String getStringRep() {
	int i = 0;
        StringBuffer out = new StringBuffer();

        for (Enumeration e = stanzas.elements(); e.hasMoreElements();) {
            Stanza s = (Stanza) e.nextElement();
	    /* Simulate 'join'ing the stanzas. */
	    if (i > 0) {
		out.append("\n");
	    } else {
		i ++;
	    }
            out.append(s.toString());
        }
        return out.toString();
    }
}
