package org.hecl;


class StringCmds {

    public static final int APPEND = 1;
    public static final int SLEN = 2;
    public static final int SINDEX = 3;

    static void dispatch(int cmd, Interp interp, Thing[] argv) throws HeclException {
	String str = argv[1].toString();

	switch (cmd) {

	    case APPEND:
		Thing result = argv[1];
		StringThing.get(result);
		StringThing st = (StringThing) result.val;
		for (int i = 2; i < argv.length; i++) {
		    st.append(argv[i].toString());
		}
		interp.setResult(result);
		break;

	    case SLEN:
		interp.setResult(str.length());
		break;

	    case SINDEX:
		int idx = IntThing.get(argv[2]);
		try {
		    char chars[] = new char[1];
		    chars[0] = str.charAt(idx);
		    interp.setResult(new String(chars));
		} catch (StringIndexOutOfBoundsException e) {
		    interp.setResult("");
		}
		break;
	}
    }

}
