/*
 * Created on 2004-12-04
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
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
 * @author zoro
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class HttpRequestJavaNet implements HttpRequestClass {
    private static final int BUFFERSIZE = 16384;

    URL url;
    String queryType = "application/x-www-form-urlencoded";

    String errmsg = null;
    
    /*
     * (non-Javadoc)
     * 
     * @see org.kocjan.http.HttpRequestClass#getErrorMessage()
     */
    public String getErrorMessage() {
        return errmsg;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kocjan.http.HttpRequestClass#setQueryType(java.lang.String)
     */
    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.kocjan.http.HttpRequestClass#setUrl(java.lang.String)
     */
    public boolean setUrl(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException exception) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kocjan.http.HttpRequestClass#executeQuery()
     */
    public byte[] executeQuery() {
        return executeQuery(null);
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.kocjan.http.HttpRequestClass#executeQuery(byte[])
     */
    public byte[] executeQuery(byte[] data) {
        byte[] buffer = new byte[BUFFERSIZE];
        HttpURLConnection conn;
        OutputStream os;
        InputStream is;
        ByteArrayOutputStream bos;
        int code, bytesRead;
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