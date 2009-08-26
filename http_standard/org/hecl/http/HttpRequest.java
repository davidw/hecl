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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <code>HttpRequest</code> is the J2SE http request class.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class HttpRequest implements HttpRequestClass {
    private static final int BUFFERSIZE = 16384;

    URL url;
    String queryType = "application/x-www-form-urlencoded";

    String errmsg = null;

    public String getErrorMessage() {
        return errmsg;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public boolean setUrl(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException exception) {
            return false;
        }
        return true;
    }

    public byte[] executeQuery() {
        return executeQuery(null);
    }

    public byte[] executeQuery(byte[] data) {
        byte[] buffer = new byte[BUFFERSIZE];
        HttpURLConnection conn;
        OutputStream os;
        InputStream is;
        ByteArrayOutputStream bos;
        int code;
	int bytesRead;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            if (data != null) {
                conn.setRequestProperty("Content-Type",
                        queryType);
                conn.setRequestProperty("Content-Length", Integer
                        .toString(data.length));
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");

                os = conn.getOutputStream();
                os.write(data);
                os.flush();
                os.close();
            } else {
                conn.setDoOutput(false);
                conn.setRequestMethod("GET");
            }

            is = conn.getInputStream();
            code = conn.getResponseCode();

            bos = new ByteArrayOutputStream();
            while ((bytesRead = is.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            is.close();
            conn.disconnect();
            buffer = bos.toByteArray();
            bos.close();
            return buffer;
        } catch (IOException exception) {
            System.out.println("Error - " + exception.getMessage());
            errmsg = exception.getMessage();
        }
        return null;
    }
}
