/* Copyright 2006 Wolfgang S. Kechel

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
 * The <code>MathCmds</code> class implements a variety of math
 * commands, depending on which version of Java Hecl is compiled for.
 *
 * @version 1.0
 */
public class MathCmds extends org.hecl.Operator {
//#if javaversion >= 1.5 || cldc > 1.0
    public static final Thing E = new Thing(new DoubleThing(Math.E));
    public static final Thing PI = new Thing(new DoubleThing(Math.PI));
//#endif

    public static final int CASTINT = 1;
    public static final int CASTLONG = 2;
    public static final int CASTFLOAT = 3;
    public static final int CASTDOUBLE = 4;
    public static final int TODEGREES = 5;
    public static final int TORADIANS = 6;
    public static final int ABS = 7;
    public static final int SQRT = 8;
    public static final int LOG = 9;

    public static final int SIN = 10;
    public static final int COS = 11;
    public static final int TAN = 12;
    public static final int ASIN = 13;
    public static final int ACOS = 14;
    public static final int ATAN = 15;
    public static final int EXP = 16;
    public static final int FLOOR = 17;
    public static final int CEIL = 18;
    public static final int POW = 19;
    public static final int RANDOM = 20;
    public static final int ROUND = 21;
    public static final int MIN = 22;
    public static final int MAX = 23;

    // valid only for java 1.5
    public static final int SIGNUM = 30;
    public static final int CBRT = 31;
    public static final int LOG10 = 32;
    public static final int LOG1P = 33;
    public static final int SINH = 34;
    public static final int COSH = 35;
    public static final int TANH = 36;
    public static final int EXPM1 = 37;
    public static final int HYPOT = 38;
    // end valid for java 1.5

    public static final int INCR = 50;
    public static final int DECR = 51;
    public static final int TRUE = 52;
    public static final int FALSE = 53;

    public static final int NOT = 80;
    public static final int AND = 81;
    public static final int OR = 82;

    public static final int EQ = 90;
    public static final int NEQ = 91;
    public static final int LT = 92;
    public static final int LE = 93;
    public static final int GT = 94;
    public static final int GE = 95;

    public static final int BINADD = 100;
    public static final int BINSUB = 101;
    public static final int BINMUL = 102;
    public static final int BINDIV = 103;
    public static final int MOD = 104;
    public static final int PLUS = 105;
    public static final int MINUS = 106;
    public static final int MUL = 107;

    // Comparison
    public static int compare(Thing a,Thing b) {
	return compare(NumberThing.asNumber(a),NumberThing.asNumber(b));
    }

    public static int compare(NumberThing a,NumberThing b) {
//#if javaversion >= 1.5 || cldc > 1.0
	if(a.isIntegral() && b.isIntegral()) {
//#endif
	    if(((IntegralThing)a).isLong() || ((IntegralThing)b).isLong()) {
		return compare(a.longValue(),b.longValue());
	    }
	    return compare(a.intValue(),b.intValue());
//#if javaversion >= 1.5 || cldc > 1.0
	}
	return compare(a.doubleValue(),b.doubleValue());
//#endif
    }

    public static Thing unary(int cmdcode,Interp ip,NumberThing a)
	throws HeclException {
	switch(cmdcode) {
	  case CASTINT:
	    return IntThing.create(a.intValue());
	  case CASTLONG:
	    return LongThing.create(a.longValue());
//#if javaversion >= 1.5 || cldc > 1.0
	  case CASTFLOAT:
	  case CASTDOUBLE:
	    return DoubleThing.create(a.doubleValue());
	  case TODEGREES:
	    return DoubleThing.create(Math.toDegrees(a.doubleValue()));
	  case TORADIANS:
	    return DoubleThing.create(Math.toRadians(a.doubleValue()));
//#endif
	  case ABS:
//#if javaversion >= 1.5 || cldc > 1.0
	    if(a.isIntegral()) {
//#endif
		IntegralThing i = (IntegralThing)a;
		if(i.isLong())
		    return LongThing.create(Math.abs(i.longValue()));
		return IntThing.create(Math.abs(i.intValue()));
//#if javaversion >= 1.5 || cldc > 1.0
	    }
	    return DoubleThing.create(Math.abs(a.doubleValue()));
//#endif
//#if javaversion >= 1.5 || cldc > 1.0
	  case SIN:
	    return DoubleThing.create(Math.sin(a.doubleValue()));
	  case COS:
	    return DoubleThing.create(Math.cos(a.doubleValue()));
	  case TAN:
	    return DoubleThing.create(Math.tan(a.doubleValue()));
	  case SQRT:
	    return DoubleThing.create(Math.sqrt(a.doubleValue()));
	  case FLOOR:
	    return DoubleThing.create(Math.floor(a.doubleValue()));
	  case CEIL:
	    return DoubleThing.create(Math.ceil(a.doubleValue()));
	  case ROUND:
	    if(a.isIntegral()) {
		return new Thing(a.deepcopy());
	    } else {
		long l = (long)
		//#ifdef j2se
		    Math.round(a.doubleValue())
		//#else
		    Math.floor(.5+a.doubleValue())
		//#endif
		    ;
		if(l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
		    return IntThing.create((int)l);
		}
		return LongThing.create(l);
	    }
//#endif
	  case INCR:
	    return binary(BINADD,ip,a,IntThing.ONE);
	  case DECR:
	    return binary(BINSUB,ip,a,IntThing.ONE);
	  case NOT:
	    return boolres(!(a.intValue() != 0));
//#ifdef j2se
	  case LOG:
	    return DoubleThing.create(Math.log(a.doubleValue()));
	  case ASIN:
	    return DoubleThing.create(Math.asin(a.doubleValue()));
	  case ACOS:
	    return DoubleThing.create(Math.acos(a.doubleValue()));
	  case ATAN:
	    return DoubleThing.create(Math.atan(a.doubleValue()));
	  case EXP:
	    return DoubleThing.create(Math.exp(a.doubleValue()));
//#if javaversion >= 1.5
	  case SIGNUM:
	    return DoubleThing.create(Math.signum(a.doubleValue()));
	  case CBRT:
	    return DoubleThing.create(Math.cbrt(a.doubleValue()));
	  case LOG10:
	    return DoubleThing.create(Math.log10(a.doubleValue()));
	  case LOG1P:
	    return DoubleThing.create(Math.log1p(a.doubleValue()));
	  case SINH:
	    return DoubleThing.create(Math.sinh(a.doubleValue()));
	  case COSH:
	    return DoubleThing.create(Math.cosh(a.doubleValue()));
	  case TANH:
	    return DoubleThing.create(Math.tanh(a.doubleValue()));
	  case EXPM1:
	    return DoubleThing.create(Math.expm1(a.doubleValue()));
//#endif
//#endif
	}
	throw new HeclException("Unknown unary mathcmdcode '"+cmdcode+"'.");
    }


    public static Thing binary(int cmdcode, Interp ip, NumberThing a, NumberThing b)
	throws HeclException {

	switch(cmdcode) {
	  case BINADD:
//#if javaversion >= 1.5 || cldc > 1.0
	    if(a.isIntegral() && b.isIntegral()) {
//#endif
		if(((IntegralThing)a).isLong()
		   || ((IntegralThing)b).isLong()) {
		    return LongThing.create(a.longValue()+b.longValue());
		}
		return IntThing.create(a.intValue()+b.intValue());
//#if javaversion >= 1.5 || cldc > 1.0
	    }
	    return DoubleThing.create(a.doubleValue()+b.doubleValue());
//#endif
	  case BINSUB:
//#if javaversion >= 1.5 || cldc > 1.0
	    if(a.isIntegral() && b.isIntegral()) {
//#endif
		if(((IntegralThing)a).isLong()
		   || ((IntegralThing)b).isLong()) {
		    return LongThing.create(a.longValue()-b.longValue());
		}
		return IntThing.create(a.intValue()-b.intValue());
//#if javaversion >= 1.5 || cldc > 1.0
	    }
	    return DoubleThing.create(a.doubleValue()-b.doubleValue());
//#endif
	  case BINMUL:
//#if javaversion >= 1.5 || cldc > 1.0
	    if(a.isIntegral() && b.isIntegral()) {
//#endif
		if(((IntegralThing)a).isLong()
		   || ((IntegralThing)b).isLong()) {
		    return LongThing.create(a.longValue()*b.longValue());
		}
		return IntThing.create(a.intValue()*b.intValue());
//#if javaversion >= 1.5 || cldc > 1.0
	    }
	    return DoubleThing.create(a.doubleValue()*b.doubleValue());
//#endif
	  case BINDIV:
//#if javaversion >= 1.5 || cldc > 1.0
	    if(a.isIntegral() && b.isIntegral()) {
//#endif
		if(((IntegralThing)a).isLong()
		   || ((IntegralThing)b).isLong()) {
		    return LongThing.create(a.longValue()/b.longValue());
		}
		return IntThing.create(a.intValue()/b.intValue());
//#if javaversion >= 1.5 || cldc > 1.0
	    }
	    return DoubleThing.create(a.doubleValue()/b.doubleValue());
//#endif
	  case MOD:
//#if javaversion >= 1.5 || cldc > 1.0
	    if(a.isIntegral() && b.isIntegral()) {
//#endif
		if(((IntegralThing)a).isLong()
		   || ((IntegralThing)b).isLong()) {
		    return LongThing.create(a.longValue()%b.longValue());
		}
		return IntThing.create(a.intValue()%b.intValue());
//#if javaversion >= 1.5 || cldc > 1.0
	    }
	    return DoubleThing.create(a.doubleValue()%b.doubleValue());
//#endif
	  case AND:
//#if javaversion >= 1.5 || cldc > 1.0
	    if(a.isIntegral() && b.isIntegral()) {
//#endif
		if(((IntegralThing)a).isLong()
		   || ((IntegralThing)b).isLong()) {
		    return LongThing.create(a.longValue() & b.longValue());
		}
		return IntThing.create(a.intValue() & b.intValue());
//#if javaversion >= 1.5 || cldc > 1.0
	    }
	    throw new HeclException("Integral argument required.");
//#endif
	  case OR:
//#if javaversion >= 1.5 || cldc > 1.0
	    if(a.isIntegral() && b.isIntegral()) {
//#endif
		if(((IntegralThing)a).isLong()
		   || ((IntegralThing)b).isLong()) {
		    return LongThing.create(a.longValue() | b.longValue());
		}
		return IntThing.create(a.intValue() | b.intValue());
//#if javaversion >= 1.5 || cldc > 1.0
	    }
	    throw new HeclException("Integral argument required.");
//#endif

//#ifdef ant:j2se
	  case POW:
	    return DoubleThing.create(Math.pow(a.doubleValue(), b.doubleValue()));
//#if javaversion >= 1.5
	  case HYPOT:
	    return DoubleThing.create(Math.hypot(a.doubleValue(), b.doubleValue()));
//#endif
//#endif
	  case EQ:
	    return boolres(compare(a,b) == 0);
	  case NEQ:
	    return boolres(compare(a,b) != 0);
	  case LT:
	    return boolres(compare(a,b) < 0);
	  case LE:
	    return boolres(compare(a,b) <= 0);
	  case GT:
	    return boolres(compare(a,b) > 0);
	  case GE:
	    return boolres(compare(a,b) >= 0);
	}
	throw new HeclException("Unknown binary mathcmdcode '"+cmdcode+"'.");
    }

    private static Thing boolres(boolean res) {
	return new Thing(res ? IntThing.ONE : IntThing.ZERO);
    }
    
    public Thing operate(int cmdcode,Interp ip,Thing[] argv)
	throws HeclException {
	NumberThing num = null;

	if(1 == minargs && 1 == maxargs)
	    return unary(cmdcode,ip,NumberThing.asNumber(argv[1]));
	if(2 == minargs && 2 == maxargs)
	    return binary(cmdcode,ip,
			  NumberThing.asNumber(argv[1]),
			  NumberThing.asNumber(argv[2]));

	switch(cmdcode) {
	  case PLUS:
	    num = IntThing.ZERO;
	    for(int i=1; i<argv.length; ++i) {
		num = (NumberThing)binary(BINADD, ip, num,
					  NumberThing.asNumber(argv[i])).getVal();
	    }
	    return new Thing(num);
	  case MINUS:
	    switch(argv.length) {
	      case 1:
		return new Thing(IntThing.ZERO);
	      case 2:
		return binary(BINSUB,ip,IntThing.ZERO,NumberThing.asNumber(argv[1]));
	      default:
		num = NumberThing.asNumber(argv[1]);
		for(int i=2; i<argv.length; ++i) {
		    num = (NumberThing)binary(BINSUB,ip, num,
					      NumberThing.asNumber(argv[i])).getVal();
		}
		return new Thing(num);
	    }
	  case MUL:
	    num = IntThing.ONE;
	    for(int i=1; i<argv.length; ++i) {
		num = (NumberThing)binary(BINMUL,ip,num,
					  NumberThing.asNumber(argv[i])).getVal();
	    }
	    return new Thing(num);
//#ifdef j2se
	  case RANDOM:
	    return DoubleThing.create(Math.random());
//#endif
	  case TRUE:
	    return new Thing(IntThing.ONE);
	  case FALSE:
	    return new Thing(IntThing.ZERO);
	  case AND:
	    num = NumberThing.asNumber(argv[1]);
	    for(int i=2; i<argv.length; ++i) {
		num = (NumberThing)binary(AND,ip,num,
					  NumberThing.asNumber(argv[i])).getVal();
	    }
	    return new Thing(num);
	  case OR:
	    num = NumberThing.asNumber(argv[1]);
	    for(int i=2; i<argv.length; ++i) {
		num = (NumberThing)binary(OR,ip,num,
					  NumberThing.asNumber(argv[i])).getVal();
	    }
	    return new Thing(num);
	  case INCR:
	      num = NumberThing.asNumber(argv[1]);
	      if(!num.isIntegral()) {
		  throw new HeclException("Argument '" + argv[1].toString()
					  + "' not an integer.");
	      }
	      NumberThing offset = argv.length > 2 ?
		  NumberThing.asNumber(argv[2]) : IntThing.ONE;
	      if(((IntegralThing)num).isLong()) {
		  num = new LongThing(num.longValue() + offset.longValue());
	      } else {
		  num = new IntThing(num.intValue() + offset.intValue());
	      }
	      argv[1].setCopyVal(num);
	      return new Thing(num);
	  default:
	    /*
	    Command c = extensions.get(cmdcode);
	    if(c != null) {
		c.
	    }

	    if(extensions.get(cmdcode)) {
	    }
	    */
	}
	throw new HeclException("Unknown math operator '"
				+ argv[0].toString() + "' with code '"
				+ cmdcode + "'.");
    }


    public static void load(Interp ip) throws HeclException {
	Enumeration e = vars.keys();
	while(e.hasMoreElements()) {
	    String k = (String)e.nextElement();
	    ip.setVar(k,(Thing)vars.get(k));
	}
	Operator.load(ip,cmdtable);
    }


    public static void unload(Interp ip) throws HeclException {
	Operator.unload(ip,cmdtable);
 	Enumeration e = vars.keys();
	while(e.hasMoreElements()) {
	    ip.unSetVar((String)e.nextElement());
	}
   }


    protected MathCmds(int cmdcode,int minargs,int maxargs) {
	super(cmdcode,minargs,maxargs);
    }


    protected static int compare(int v1,int v2) {
	return v1 < v2 ? -1 : (v1 == v2) ? 0 : 1;
    }


    protected static int compare(long v1,long v2) {
	return v1 < v2 ? -1 : (v1 == v2) ? 0 : 1;
    }


    protected static int compare(double v1,double v2) {
	return v1 < v2 ? -1 : (v1 == v2) ? 0 : 1;
    }


    private static int nextop = 1000;
    private static Hashtable vars = new Hashtable();
    private static Hashtable extensions = new Hashtable();

    private static Hashtable cmdtable = new Hashtable();

    static {

	cmdtable.put("true",new MathCmds(TRUE,0,0));
	cmdtable.put("false",new MathCmds(FALSE,0,0));
	cmdtable.put("and",new MathCmds(AND,1,-1));
	cmdtable.put("or",new MathCmds(OR,1,-1));

	cmdtable.put("1+",new MathCmds(INCR,1,2));
	cmdtable.put("1-",new MathCmds(DECR,1,2));
	cmdtable.put("incr",new MathCmds(INCR,1,2));

	// cast operators
	cmdtable.put("int",new MathCmds(CASTINT,1,1));
	cmdtable.put("long",new MathCmds(CASTLONG,1,1));

	// unary operators
	cmdtable.put("abs",new MathCmds(ABS,1,1));
	cmdtable.put("not",new MathCmds(NOT,1,1));

	// binary operators
	cmdtable.put("+",new MathCmds(PLUS,-1,-1));
	cmdtable.put("-",new MathCmds(MINUS,-1,-1));
	cmdtable.put("*",new MathCmds(MUL,-1,-1));
	cmdtable.put("/", new MathCmds(BINDIV,2,2));
	cmdtable.put("%", new MathCmds(MOD,2,2));

	// comparison
	cmdtable.put("=",new MathCmds(EQ,2,2));
	cmdtable.put("!=",new MathCmds(NEQ,2,2));
	cmdtable.put("<",new MathCmds(LT,2,2));
	cmdtable.put("<=",new MathCmds(LE,2,2));
	cmdtable.put(">",new MathCmds(GT,2,2));
	cmdtable.put(">=",new MathCmds(GE,2,2));

	// stuff not available in cldc 1.0
//#if javaversion >= 1.5 || cldc > 1.0
	vars.put("pi",PI);
	vars.put("e",E);
	cmdtable.put("sqrt",new MathCmds(SQRT,1,1));
	cmdtable.put("sin",new MathCmds(SIN,1,1));
	cmdtable.put("cos",new MathCmds(COS,1,1));
	cmdtable.put("tan",new MathCmds(TAN,1,1));
	cmdtable.put("floor",new MathCmds(FLOOR,1,1));
	cmdtable.put("ceil",new MathCmds(CEIL,1,1));
	cmdtable.put("float",new MathCmds(CASTFLOAT,1,1));
	cmdtable.put("double",new MathCmds(CASTDOUBLE,1,1));
	cmdtable.put("toDegrees",new MathCmds(TODEGREES,1,1));
	cmdtable.put("toRadians",new MathCmds(TORADIANS,1,1));
//#endif

	// Stuff available only in j2se
//#if javaversion >= 1.5
	cmdtable.put("random",new MathCmds(RANDOM,0,0));
	cmdtable.put("pow", new MathCmds(POW,2,2));
	cmdtable.put("log",new MathCmds(LOG,1,1));
	cmdtable.put("asin",new MathCmds(ASIN,1,1));
	cmdtable.put("acos",new MathCmds(ACOS,1,1));
	cmdtable.put("atan",new MathCmds(ATAN,1,1));
	cmdtable.put("exp",new MathCmds(EXP,1,1));
	cmdtable.put("round",new MathCmds(ROUND,1,1));

//#if javaversion > 1.5
	cmdtable.put("signum",new MathCmds(SIGNUM,1,1));
	cmdtable.put("cbrt",new MathCmds(CBRT,1,1));
	cmdtable.put("log10",new MathCmds(LOG10,1,1));
	cmdtable.put("log1p",new MathCmds(LOG1P,1,1));
	cmdtable.put("sinh",new MathCmds(SINH,1,1));
	cmdtable.put("cosh",new MathCmds(COSH,1,1));
	cmdtable.put("tanh",new MathCmds(TANH,1,1));
	cmdtable.put("expm1",new MathCmds(EXPM1,1,1));
	cmdtable.put("hypot", new MathCmds(HYPOT,2,2));
//#endif

//#endif
    }
}
