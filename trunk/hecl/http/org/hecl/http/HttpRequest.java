package org.hecl.http;
/*
 * Created on 2004-12-04
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
/**
 * @author zoro
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class HttpRequest {
    public static HttpRequestClass initializeRequest(String url) {
        HttpRequestClass rc = null;
        Class c = null;
        if (c == null) {
            try {
                /*
                 * If a class responsible for HttpURLConnection is there, use
                 * that class for communication.
                 */
                c = Class.forName("java.net.HttpURLConnection");
                c = Class.forName("com.dedasys.hecl.http.HttpRequestJavaNet");
            } catch (ClassNotFoundException exception) {
                c = null;
            }
        }
        if (c == null) {
            try {
                /*
                 * If a class responsible for J2ME HttpConnection is there, use
                 * that class for communication.
                 */
                c = Class.forName("javax.microedition.io.HttpConnection");
                c = Class.forName("com.dedasys.hecl.http.HttpRequestMicro");
            } catch (ClassNotFoundException exception) {
                c = null;
            }
        }
        try {
            rc = (HttpRequestClass) c.newInstance();
        } catch (Exception e) {
            rc = null;
        }
        if (rc != null) {
            if (!rc.setUrl(url))
                return null;
        }
        return rc;
    }
}