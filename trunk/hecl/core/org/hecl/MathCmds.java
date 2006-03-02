/* Copyright 2006 David N. Welton

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


class MathCmds {
    static final Thing trueval = IntThing.create(true);

    public static final int ADD = 1;
    public static final int SUB = 2;
    public static final int MUL = 3;
    public static final int DIV = 4;
    public static final int MOD = 5;

    public static final int AND = 6;
    public static final int NOT = 7;
    public static final int OR =  8;

    public static final int EQ =  9;
    public static final int NE =  10;
    public static final int GT =  11;
    public static final int LT =  12;
    public static final int STREQ = 13;
    public static final int STRNE = 14;

    public static final int INCR = 15;
    public static final int TRUE = 16;

    static void dispatch(int cmd, Interp interp, Thing[] argv) throws HeclException {
	int res = 0;
	int n = argv.length;
        switch (cmd) {
	    case ADD:
		res = 0;
		for (int i = 1; i < n; ++i) {
		    res += IntThing.get(argv[i]);
		}
		break;
	    case SUB:
		if (n == 2) {
		    res = -IntThing.get(argv[1]);
		} else {
		    for (int i = 1; i < n; ++i) {
			res -= IntThing.get(argv[i]);
		    }
		}
		break;
	    case MUL:
		res = 1;
		for (int i = 1; i < n; ++i) {
		    res *= IntThing.get(argv[i]);
		}
		break;
	    case DIV:
		if (n < 3) {
		    throw HeclException.createWrongNumArgsException(
			argv, 1, "/ arg arg ?arg ...?");
		}
		res = IntThing.get(argv[1]);
		for (int i = 2; i < argv.length; ++i) {
		    res /= IntThing.get(argv[i]);
		}
		break;
	    case MOD:
		if (n != 3) {
		    throw HeclException.createWrongNumArgsException(
			argv, 1, "% arg arg");
		}
		res = IntThing.get(argv[1]) % IntThing.get(argv[2]);
		break;

	    case AND:
		res = IntThing.get(argv[1]);
		for (int i = 2; i < argv.length; i ++) {
		    res &= IntThing.get(argv[i]);
		}
		break;
	    case NOT:
		res = IntThing.get(argv[1]);
		if (res == 0) {
		    res = 1;
		} else {
		    res = 0;
		}
		break;
	    case OR:
		for (int i = 1; i < argv.length; i ++) {
		    res |= IntThing.get(argv[i]);
		}
		break;
            case EQ:
                res = IntThing.compare(argv[1], argv[2]) == 0 ? 1 : 0;
                break;
            case GT:
                res = IntThing.get(argv[1]) > IntThing.get(argv[2]) == true ? 1 : 0;
                break;
            case LT:
                res = IntThing.get(argv[1]) < IntThing.get(argv[2]) == true ? 1 : 0;
                break;
            case STREQ:
                res = Compare.compareString(argv[1], argv[2]) == 0 ? 1 : 0;
		break;
     	    case NE:
		res = IntThing.compare(argv[1], argv[2]) == 0 ? 0 : 1;
		break;
     	    case STRNE:
		res = Compare.compareString(argv[1], argv[2]) == 0 ? 0 : 1;
		break;

	    case INCR:
		int x = IntThing.get(argv[1]);
		int y = 1;
		if (argv.length > 2) {
		    y = IntThing.get(argv[2]);
		}
		((IntThing) argv[1].val).set(x + y);
		interp.result = argv[1];
		return;

	    case TRUE:
		interp.result = trueval;
		return;
	}
	interp.setResult(res);
    }
}
