/*
 * Created on 2005-03-07
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hecl.http;

import org.hecl.Command;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.StringThing;
import org.hecl.Thing;

/**
 * @author zoro
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class HttpCommand implements Command {
    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
        String url, data, type;
        if ((argv.length < 2) || (argv.length > 4)) {
            throw HeclException.createWrongNumArgsException(argv, 1,
                    "url ?postData? ?type?");
        }
        url = argv[1].getStringRep();
        if (argv.length > 2)
            data = argv[2].getStringRep();
        else
            data = null;
        if (argv.length > 3)
            type = argv[3].getStringRep();
        else
            type = null;
        data = HttpCommand.executeQuery(url, data, type);
        interp.result = new Thing(new StringThing(data));
    }
    public static String executeQuery(String url, String data, String type)
    	throws HeclException {
        StringBuffer outbuf;
        HttpRequestClass req;
        byte[] out;
        int i;
        req = HttpRequest.initializeRequest(url);
        if (req == null)
            throw new HeclException("Unable to initialize http connection");

        if (data != null)
            out = req.executeQuery(data.getBytes());
        else
            out = req.executeQuery();
        if (out == null)
            throw new HeclException("Http request error: "
                    + req.getErrorMessage());
        req = null;
        outbuf = new StringBuffer(out.length);
        outbuf.setLength(out.length);
        for (i = 0; i < out.length; i++) {
            outbuf.setCharAt(i, (char) out[i]); 
        }
        return outbuf.toString();
    }    
}
