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
public interface HttpRequestClass {
    /**
     * Sets query type for POST requests
     * 
     * @param type new MIME type
     */
    public abstract void setQueryType(String type);
    /**
     * Sets new URL of the request
     * 
     * @param url new URL
     * @return whether setting the url has succeeded
     */
    public abstract boolean setUrl(String url);
    /**
     * Executes a GET query
     * 
     * @return HTTP body as byte array
     */
    public abstract byte[] executeQuery();
    /**
     * Executes a POST query
     * 
     * @param data POST data
     * @return HTTP body as byte array
     */
    public abstract byte[] executeQuery(byte[] data);
    public abstract String getErrorMessage();
}