/*
 * Created on 2004-12-05
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hecl.http;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 * @author zoro
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class HttpRequestMicro implements HttpRequestClass {
    private static final int BUFFERSIZE = 4096;
    String url = null;
    String queryType = "application/x-www-form-urlencoded";
    
    String errorMessage = null;
    
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
        this.url = url;
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
    
    /*
     * (non-Javadoc)
     * 
     * @see org.kocjan.http.HttpRequestClass#getErrorMessage()
     */
    public String getErrorMessage() {
        return null;
    }
    
}