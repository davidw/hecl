/* Copyright 2005 Wojciech Kocjan

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

package org.hecl.http;

import org.hecl.Command;
import org.hecl.HeclException;
import org.hecl.Interp;
import org.hecl.StringThing;
import org.hecl.Thing;

public class HttpCommand implements Command {
    public void cmdCode(Interp interp, Thing[] argv) throws HeclException {
        String url;
	String data = null;
	String type = null;
        if ((argv.length < 2) || (argv.length > 4)) {
            throw HeclException.createWrongNumArgsException(argv, 1,
                    "url ?postData? ?type?");
        }
        url = argv[1].toString();
        if (argv.length > 2) {
            data = argv[2].toString();
        }

        if (argv.length > 3) {
            type = argv[3].toString();
        }

        data = HttpCommand.executeQuery(url, data, type);
        interp.result = new Thing(new StringThing(data));
    }

    public static String executeQuery(String url, String data, String type)
    	throws HeclException {
        StringBuffer outbuf;
        HttpRequestClass req;
        byte[] out;
        int i;

        req = new HttpRequest();
	req.setUrl(url);
        if (req == null)
            throw new HeclException("Unable to initialize http connection");

        if (data != null) {
            out = req.executeQuery(data.getBytes());
	} else {
            out = req.executeQuery();
	}
        if (out == null) {
            throw new HeclException("Http request error: "
                    + req.getErrorMessage());
	}
        req = null;
        outbuf = new StringBuffer(out.length);
        outbuf.setLength(out.length);
        for (i = 0; i < out.length; i++) {
            outbuf.setCharAt(i, (char) out[i]);
        }
        return outbuf.toString();
    }
}
