package com.dedasys.hecl;

import java.util.*;

/**
 * <code>SortCmd</code> implements the "sort" command.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */

class SortCmd implements Command {

    public void cmdCode(Interp interp, Thing[] argv)
	throws HeclException {
	Vector v = argv[1].toList();
	v = quicksort(v, 0, v.size() - 1);
	interp.setResult(new Thing(v));
    }

    /* FIXME - speeding this up a bit wouldn't hurt things. */

    private Vector quicksort (Vector a, int lo, int hi) {
        //  lo is the lower index, hi is the upper index
        //  of the region of array a that is to be sorted
        int i = lo, j = hi;
        Thing x = (Thing)a.elementAt((lo + hi) / 2);
	Thing h;
        //  partition
        do
        {
	    while (((Thing)a.elementAt(i)).compare(x) < 0) {
		i++;
	    }
	    while (((Thing)a.elementAt(j)).compare(x) > 0) {
		j--;
	    }
	    if (i <= j)
	    {
		h = (Thing)a.elementAt(i);
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
