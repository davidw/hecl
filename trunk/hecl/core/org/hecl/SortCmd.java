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

import java.util.*;

/**
 * <code>SortCmd</code> implements the "sort" command.
 * 
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton </a>
 * @version 1.0
 */

class SortCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
        Vector v = ListThing.get(argv[1]);
        v = quicksort(v, 0, v.size() - 1);
        interp.setResult(ListThing.create(v));
    }

    /* FIXME - speeding this up a bit wouldn't hurt things. */

    /**
     * <code>quicksort</code> implementation.
     * 
     * @param a
     *            a <code>Vector</code> value
     * @param lo
     *            an <code>int</code> value
     * @param hi
     *            an <code>int</code> value
     * @return a <code>Vector</code> value
     */
    private Vector quicksort(Vector a, int lo, int hi) {
        //  lo is the lower index, hi is the upper index
        //  of the region of array a that is to be sorted
        int i = lo, j = hi;
        Thing x = (Thing) a.elementAt((lo + hi) / 2);
        Thing h;
        //  partition
        do {
//            while (((Thing) a.elementAt(i)).compare(x) < 0) {
            while (Compare.compare((Thing)a.elementAt(i), x) < 0) {
                i++;
            }
//            while (((Thing) a.elementAt(j)).compare(x) > 0) {
            while (Compare.compare((Thing)a.elementAt(j), x) > 0) {
                j--;
            }
            if (i <= j) {
                h = (Thing) a.elementAt(i);
                a.setElementAt(a.elementAt(j), i);
                a.setElementAt(h, j);
                i++;
                j--;
            }
        } while (i <= j);
        //  recursion
        if (lo < j)
            a = quicksort(a, lo, j);
        if (i < hi)
            a = quicksort(a, i, hi);
        return a;
    }
}
