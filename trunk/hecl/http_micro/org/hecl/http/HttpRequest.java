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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 * <code>HttpRequest</code> implements HttpRequestClass for J2ME.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class HttpRequest implements HttpRequestClass {
    private static final int BUFFERSIZE = 4096;
    String url = null;
    String queryType = "application/x-www-form-urlencoded";

    String errorMessage = null;
    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }
    public boolean setUrl(String url) {
        this.url = url;
        return true;
    }
    public byte[] executeQuery() {
        return executeQuery(null);
    }
    public byte[] executeQuery(byte[] data) {
        byte[] buffer = new byte[BUFFERSIZE];
        ByteArrayOutputStream bos = null;
        OutputStream os = null;
        InputStream is = null;
        byte[] rc = null;
        HttpConnection conn;
        int code, len, bytesRead;
        try {
            conn = (HttpConnection) Connector.open(url);

            if (data != null) {
                conn.setRequestMethod(HttpConnection.POST);
                conn.setRequestProperty("Content-Type",
                queryType);
                conn.setRequestProperty("Content-Length", Integer
                        .toString(data.length));
                
                os = conn.openOutputStream();
                os.write(data);
                os.flush();
            }  else  {
                conn.setRequestMethod(HttpConnection.GET);
                os = conn.openOutputStream();
                os.flush();
            }

            code = conn.getResponseCode();
            if (code == HttpConnection.HTTP_OK) {
                is = conn.openInputStream();
                bos = new ByteArrayOutputStream();
                while ((bytesRead = is.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                rc = bos.toByteArray();
            }
        } catch (ClassCastException io) {
            errorMessage = "Unknown protocol";
        } catch (IOException io) {
	    /* FIXME  */
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException ioe) {
            }
            try {
                if (os != null)
                    os.close();
            } catch (IOException ioe) {
            }
            try {
                if (bos != null)
                    bos.close();
            } catch (IOException ioe) {
            }
        }
        return rc;
    }
    public String getErrorMessage() {
        return null;
    }
}
